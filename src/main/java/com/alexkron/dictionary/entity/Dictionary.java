package com.alexkron.dictionary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Dictionary")
public class Dictionary {

    @Id
    @Column(name = "Id", nullable = false)
    private String id;

    @Column(name = "Value", nullable = false)
    private String value;

    @Column(name = "Ttl", nullable = false)
    private long ttl;

    private final static long msInHour = 3600000;

    public Dictionary() {
    }

    public Dictionary(String id, String value) {
        this.id = id;
        this.value = value;
        this.ttl = System.currentTimeMillis() + msInHour;
    }

    public Dictionary(String id, String value, long ttl) {
        this.id = id;
        this.value = value;
        this.ttl = System.currentTimeMillis() + ttl;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public long getTtl() {
        return ttl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return id + "__" + value + "__" + ttl + '\n';
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
