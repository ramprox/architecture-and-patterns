package ru.ramprox.server.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PropertyToCollectionConverter {
    public List<String> convertToList(String line) {
        return convert(line, Collectors.toList());
    }

    public Set<String> convertToSet(String line) {
        return convert(line, Collectors.toSet());
    }

    private <T> T convert(String line, Collector<String, ?, T> collector) {
        return Arrays.stream(line.split(";"))
                .peek(String::trim)
                .collect(collector);
    }
}
