package com.alexkron.dictionary.service;

import com.alexkron.dictionary.entity.Dictionary;
import com.alexkron.dictionary.repository.DictionaryRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DictionaryServiceImplTest {
    @Autowired
    DictionaryService service;

    @MockBean
    private DictionaryRepository repository;


    @Test
    public void get() {
        String key = "1";
        String value = "value";
        Mockito.doReturn(true)
                .when(repository)
                .existsById(key);
        Mockito.doReturn(new Dictionary(key, value))
                .when(repository)
                .getById(key);
        String returnValue = service.get(key);
        Assert.assertEquals(value, returnValue);
    }

    @Test
    public void getFailed() {
        String key = "1";
        String returnValue = service.get(key);
        Assert.assertNull(returnValue);
    }

    @Test
    public void setTwoArgsTrue() {
        String key = "1";
        String value = "value";
        boolean isDictionarySet = service.set(key, value);
        Assert.assertTrue(isDictionarySet);
        Mockito.verify(repository, Mockito.times(1))
                .save(ArgumentMatchers.any(Dictionary.class));
    }

    @Test
    public void setTwoArgsFalse1() {
        boolean isDictionarySet = service.set(null, null);
        Assert.assertFalse(isDictionarySet);
        Mockito.verify(repository, Mockito.times(0))
                .save(ArgumentMatchers.any(Dictionary.class));
    }

    @Test
    public void setTwoArgsFalse2() {
        boolean isDictionarySet = service.set("", "");
        Assert.assertFalse(isDictionarySet);
        Mockito.verify(repository, Mockito.times(0))
                .save(ArgumentMatchers.any(Dictionary.class));
    }

    @Test
    public void setThreeArgsTrue() {
        String key = "1";
        String value = "value";
        long ttl = 1000;
        boolean isDictionarySet = service.set(key, value, ttl);
        Assert.assertTrue(isDictionarySet);
        Mockito.verify(repository, Mockito.times(1))
                .save(ArgumentMatchers.any(Dictionary.class));
    }

    @Test
    public void setThreeArgsFalse1() {
        boolean isDictionarySet = service.set(null, null, 0);
        Assert.assertFalse(isDictionarySet);
        Mockito.verify(repository, Mockito.times(0))
                .save(ArgumentMatchers.any(Dictionary.class));
    }

    @Test
    public void setThreeArgsFalse2() {
        boolean isDictionarySet = service.set("", "", 0);
        Assert.assertFalse(isDictionarySet);
        Mockito.verify(repository, Mockito.times(0))
                .save(ArgumentMatchers.any(Dictionary.class));
    }

    @Test
    public void setThreeArgsFalse3() {
        String key = "1";
        String value = "value";
        boolean isDictionarySet = service.set(key, value, 0);
        Assert.assertFalse(isDictionarySet);
        Mockito.verify(repository, Mockito.times(0))
                .save(ArgumentMatchers.any(Dictionary.class));
    }

    @Test
    public void remove() {
        String key = "1";
        String value = "value";
        Mockito.doReturn(true)
                .when(repository)
                .existsById(key);
        Mockito.doReturn(new Dictionary(key, value))
                .when(repository)
                .getById(key);
        String returnValue = service.remove(key);
        Mockito.verify(repository, Mockito.times(1))
                .deleteById(key);
        Assert.assertEquals(value, returnValue);
    }

    @Test
    public void removeFailed() {
        String key = "1";
        String returnValue = service.remove(key);
        Mockito.verify(repository, Mockito.times(0))
                .deleteById(key);
        Assert.assertNull(returnValue);
    }

    @Test
    public void dump() throws IOException {
        List<Dictionary> list = new ArrayList<Dictionary>();
        Dictionary dictionary1 = new Dictionary("1", "value1");
        Dictionary dictionary2 = new Dictionary("2", "value2");
        list.add(dictionary1);
        list.add(dictionary2);
        Mockito.doReturn(list).when(repository).findAll();
        File returnFile = service.dump();
        FileReader fileReader = new FileReader(returnFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        List<Dictionary> returnList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            returnList.add(Dictionary.valueOf(line));
        }
        Assert.assertEquals(list.get(0).getKey(), returnList.get(0).getKey());
        Assert.assertEquals(list.get(1).getValue(), returnList.get(1).getValue());
    }

    @Test
    public void dumpEmpty() {
        File returnFile = service.dump();
        Assert.assertEquals(0, returnFile.length());
    }

    @Test
    public void load() throws IOException {
        Dictionary dictionary1 = new Dictionary("1", "value1");
        Dictionary dictionary2 = new Dictionary("2", "value2");
        dictionary1.setTtl(1000);
        dictionary2.setTtl(2000);
        File file = new File("src/test/resources/load_test.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(dictionary1.toString());
        bufferedWriter.write(dictionary2.toString());
        bufferedWriter.flush();
        service.load(file);
        Mockito.verify(repository, Mockito.times(1))
                .deleteAll();
        Mockito.verify(repository, Mockito.times(2))
                .save(ArgumentMatchers.any(Dictionary.class));
    }
}