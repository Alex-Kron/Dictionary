package com.alexkron.dictionary.service;

import com.alexkron.dictionary.entity.Dictionary;
import com.alexkron.dictionary.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.List;

@Service
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    DictionaryRepository dictionaryRepository;

    @Override
    public String get(String id) {
        return dictionaryRepository.existsById(id) ? dictionaryRepository.getById(id).getValue() : "Not found by id = " + id;
    }

    @Override
    public boolean set(String id, String value) {
        if (id.isEmpty() | value.isEmpty()) {
            return false;
        } else {
            if (dictionaryRepository.existsById(id)) {
                remove(id);
            }
            dictionaryRepository.save(new Dictionary(id, value));
            return true;
        }
    }

    @Override
    public boolean set(String id, String value, long ttl) {
        if (id.isEmpty() | value.isEmpty() | ttl <= 0) {
            return false;
        } else {
            if (dictionaryRepository.existsById(id)) {
                remove(id);
            }
            dictionaryRepository.save(new Dictionary(id, value, ttl * 1000));
            return true;
        }
    }


    @Override
    public String remove(String id) {
        if (dictionaryRepository.existsById(id)) {
            String value = get(id);
            dictionaryRepository.deleteById(id);
            return value;
        } else {
            return "Not found by id = " + id;
        }
    }

    @Override
    public File dump() {
        List<Dictionary> repo = dictionaryRepository.findAll();
        File file = new File("dump.txt");
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            if (repo.isEmpty()) {
                return file;
            } else {
                long time = System.currentTimeMillis();
                for (Dictionary dict :
                        repo) {
                    dict.setTtl(dict.getTtl() - time);
                    bufferedWriter.write(dict.toString());
                }
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error creating dump", e);
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.close();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "File closing error", e);
            }
        }
        return file;
    }

    @Override
    public void load(File dictionary) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(dictionary);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            dictionaryRepository.deleteAll();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                Dictionary dict = Dictionary.valueOf(line);
                if (dict == null) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "File read error");
                }
                dictionaryRepository.save(dict);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "File read error");
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "File closing error", e);
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    private void clearing() {
        List<Dictionary> expired = dictionaryRepository.findByTtlIsLessThan(System.currentTimeMillis());
        for (Dictionary dictionary :
                expired) {
            remove(dictionary.getId());
        }
    }
}
