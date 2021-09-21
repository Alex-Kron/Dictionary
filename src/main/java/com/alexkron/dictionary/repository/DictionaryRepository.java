package com.alexkron.dictionary.repository;

import com.alexkron.dictionary.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DictionaryRepository extends JpaRepository<Dictionary, String> {
    List<Dictionary> findByTtlIsLessThan (long time);
}
