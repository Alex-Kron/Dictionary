package com.alexkron.dictionary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Clock;

@Entity
@Table(name = "Dictionary")
public class Dictionary {

    @Id
    @Column(name = "Key", nullable = false)
    private String key;

    @Column(name = "Value", nullable = false)
    private String value;

    @Column(name = "Ttl", nullable = false)
    private long ttl;

    private final static long MSINHOUR = 3600000;

    public Dictionary() {
    }

    public Dictionary(String key, String value) {
        this.key = key;
        this.value = value;
        Clock clock = Clock.systemDefaultZone();
        this.ttl = clock.millis() + MSINHOUR;
    }

    public Dictionary(String key, String value, long ttl) {
        this.key = key;
        this.value = value;
        Clock clock = Clock.systemDefaultZone();
        this.ttl = clock.millis() + ttl;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public long getTtl() {
        return ttl;
    }

    public void setKey(String id) {
        this.key = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return key + "__" + value + "__" + ttl + '\n';
    }

    public static Dictionary valueOf(String str) {
        String[] arr = str.split("__*\\n*");
        if (arr.length < 3) {
            return null;
        } else {
            return new Dictionary(arr[0], arr[1], Long.parseLong(arr[2]));
        }
    }
}
