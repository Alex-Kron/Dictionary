package com.alexkron.dictionary.service;

import com.alexkron.dictionary.entity.Dictionary;

import java.io.File;

public interface DictionaryService {
    String get(String key);

    boolean set(String key, String value);

    boolean set(String key, String value, long ttl);

    String remove(String key);

    File dump();

    void load(File dictionary);
}
