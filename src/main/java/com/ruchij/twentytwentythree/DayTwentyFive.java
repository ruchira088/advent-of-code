package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayTwentyFive implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        Map<String, Set<String>> graph = parse(input);

        return graph.size();
    }

    Map<String, Set<String>> parse(Stream<String> input) {
        Map<String, Set<String>> graph = new HashMap<>();

        input.forEach(line -> {
            Map.Entry<String, Set<String>> parsedLine = parseLine(line);

            Set<String> values = graph.getOrDefault(parsedLine.getKey(), new HashSet<>());
            values.addAll(parsedLine.getValue());
            graph.put(parsedLine.getKey(), values);

            for (String current : parsedLine.getValue()) {
                Set<String> strings = graph.getOrDefault(current, new HashSet<>());
                strings.add(parsedLine.getKey());
                graph.put(current, strings);
            }
        });

        return graph;
    }

    Map.Entry<String, Set<String>> parseLine(String line) {
        String[] parts = line.split(":");
        String node = parts[0];
        Set<String> nodes = Arrays.stream(parts[1].split(" ")).map(String::trim).collect(Collectors.toSet());

        return Map.entry(node, nodes);
    }

}
