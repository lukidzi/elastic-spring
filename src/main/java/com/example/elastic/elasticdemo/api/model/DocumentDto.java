package com.example.elastic.elasticdemo.api.model;

import com.example.elastic.elasticdemo.model.Document;
import com.example.elastic.elasticdemo.model.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDto {

    private final String name;
    private final List<OptionDto> options;

    @JsonCreator
    public DocumentDto(@JsonProperty("name") String name,
                       @JsonProperty("options") List<OptionDto> options) {
        this.name = name;
        this.options = Optional.ofNullable(options).map(Collections::unmodifiableList).orElse(Collections.emptyList());
    }

    public String getName() {
        return name;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentDto that = (DocumentDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, options);
    }

    @Override
    public String toString() {
        return "DocumentDto{" +
                "name='" + name + '\'' +
                ", options=" + options +
                '}';
    }

    public static DocumentDto toDto(Document document) {
        return new DocumentDto(document.getName(),
                document.getOptions().stream().map(OptionDto::toDto).collect(Collectors.toList()));
    }
}
