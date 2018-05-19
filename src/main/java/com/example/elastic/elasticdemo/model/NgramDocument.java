package com.example.elastic.elasticdemo.model;

import java.util.Objects;

public final class NgramDocument {

    private final String name;

    public NgramDocument(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NgramDocument that = (NgramDocument) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "NgramDocument{" +
                "name='" + name + '\'' +
                '}';
    }
}
