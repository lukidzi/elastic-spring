package com.example.elastic.elasticdemo.model;

import com.example.elastic.elasticdemo.api.model.DocumentDto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Document {

    private final String name;
    private final List<Option> options;

    public Document(String name, List<Option> options) {
        this.name = name;
        this.options = Optional.ofNullable(options).map(Collections::unmodifiableList).orElse(Collections.emptyList());
    }

    public String getName() {
        return name;
    }

    public List<Option> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(name, document.name) &&
                Objects.equals(options, document.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, options);
    }

    @Override
    public String toString() {
        return "Document{" +
                "name='" + name + '\'' +
                ", options=" + options +
                '}';
    }

    public static Document fromDto(DocumentDto documentDto) {
        return new Document(documentDto.getName(),
                documentDto.getOptions().stream().map(Option::fromDto).collect(Collectors.toList()));
    }
}
