package com.example.elastic.elasticdemo.api.model;

import com.example.elastic.elasticdemo.model.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionDto {

    private final String name;
    private final String value;

    @JsonCreator
    public OptionDto(@JsonProperty("name") String name,
                     @JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionDto optionDto = (OptionDto) o;
        return Objects.equals(name, optionDto.name) &&
                Objects.equals(value, optionDto.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "OptionDto{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static OptionDto toDto(Option option){
        return new OptionDto(option.getName(), option.getValue());
    }
}
