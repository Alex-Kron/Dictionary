package com.alexkron.dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DictionaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DictionaryApplication.class, args);
    }

}
