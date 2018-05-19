package com.example.elastic.elasticdemo.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class NgramDocumentResponseDto {

    private final String name;
    private final float score;

    @JsonCreator
    public NgramDocumentResponseDto(@JsonProperty("name") String name,
                                    @JsonProperty("score") float score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public float getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NgramDocumentResponseDto that = (NgramDocumentResponseDto) o;
        return Float.compare(that.score, score) == 0 &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }

    @Override
    public String toString() {
        return "NgramDocumentResponseDto{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
