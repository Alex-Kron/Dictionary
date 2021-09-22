package com.alexkron.dictionary.service;

import com.alexkron.dictionary.entity.Dictionary;
import com.alexkron.dictionary.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.time.Clock;
import java.util.List;

@Service
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    DictionaryRepository dictionaryRepository;

    static Clock clock = Clock.systemDefaultZone();

    @Override
    public String get(String key) {
        return dictionaryRepository.existsById(key) ? dictionaryRepository.getById(key).getValue() : null;
    }

    @Override
    public boolean set(String key, String value) {
        if (key == null | value == null) {
            return false;
        } else {
            if (key.isEmpty() | value.isEmpty()) {
                return false;
            } else {
                if (dictionaryRepository.existsById(key)) {
                    remove(key);
                }
                dictionaryRepository.save(new Dictionary(key, value));
                return true;
            }
        }
    }

    @Override
    public boolean set(String key, String value, long ttl) {
        if (key == null | value == null) {
            return false;
        } else {
            if (key.isEmpty() | value.isEmpty() | ttl <= 0) {
                return false;
            } else {
                if (dictionaryRepository.existsById(key)) {
                    remove(key);
                }
                long msInSecond = 1000;
                dictionaryRepository.save(new Dictionary(key, value, ttl * msInSecond));
                return true;
            }
        }
    }


    @Override
    public String remove(String key) {
        if (dictionaryRepository.existsById(key)) {
            String value = dictionaryRepository.getById(key).getValue();
            dictionaryRepository.deleteById(key);
            return value;
        } else {
            return null;
        }
    }

    @Override
    public File dump() {
        List<Dictionary> repo = dictionaryRepository.findAll();
        File file = new File("src/main/resources/dump.txt");
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            if (repo.isEmpty()) {
                return file;
            } else {
                long time = clock.millis();
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
        Clock clock = Clock.systemDefaultZone();
        List<Dictionary> expired = dictionaryRepository.findByTtlIsLessThan(clock.millis());
        for (Dictionary dictionary :
                expired) {
            remove(dictionary.getKey());
        }
    }
}
