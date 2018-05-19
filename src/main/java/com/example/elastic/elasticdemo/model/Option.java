package com.example.elastic.elasticdemo.model;

import com.example.elastic.elasticdemo.api.model.OptionDto;

import java.util.Objects;

public final class Option {

    private final String name;
    private final String value;

    public Option(String name, String value) {
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
        Option options = (Option) o;
        return Objects.equals(name, options.name) &&
                Objects.equals(value, options.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "Option{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static Option fromDto(OptionDto optionDto){
        return new Option(optionDto.getName(), optionDto.getValue());
    }
}
