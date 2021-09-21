package com.alexkron.dictionary.service;

import com.alexkron.dictionary.entity.Dictionary;

import java.io.File;

public interface DictionaryService {
    String get(String id);

    boolean set(String id, String value);

    boolean set(String id, String value, long ttl);

    String remove(String id);

    File dump();

    void load(File dictionary);
}
