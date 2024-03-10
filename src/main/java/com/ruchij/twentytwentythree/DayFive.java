package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayFive implements JavaSolution {
    record Mapping(long destinationStart, long sourceStart, long range) {}
    record Plan(List<Long> seeds, List<List<Mapping>> mappings) {}

    @Override
    public Object solve(Stream<String> input) {
        Plan plan = parse(input.toList());

        OptionalLong result = plan.seeds.stream()
                .mapToLong(seed -> route(seed, plan.mappings).getLast())
                .min();

        return result;
    }

    private LinkedList<Long> route(Long seed, List<List<Mapping>> allMappings) {
        LinkedList<Long> list = new LinkedList<>();
        list.add(seed);

        for (List<Mapping> mappings : allMappings) {
            long value = list.getLast();
            Long destination = null;

            for (Mapping mapping : mappings) {
                if (mapping.sourceStart <= value && value < (mapping.sourceStart + mapping.range)) {
                    long diff = value - mapping.sourceStart;
                    destination = mapping.destinationStart + diff;
                    break;
                }
            }

            if (destination == null) {
                destination = value;
            }

            list.add(destination);
        }

        return list;
    }

    private Plan parse(List<String> lines) {
        List<Long> seeds = parseNumbers(lines.get(0).split(":")[1]);
        List<List<Mapping>> mappings = new ArrayList<>();
        List<Mapping> mapping = null;

        for (String line : lines.subList(2, lines.size())) {
            if (line.contains(":")) {
                if (mapping != null) {
                    mappings.add(mapping);
                }

                mapping = new ArrayList<>();
            } else if (!line.trim().isBlank()) {
                mapping.add(parseMapping(line));
            }
        }

        mappings.add(mapping);

        Plan plan = new Plan(seeds, mappings);

        return plan;
    }

    private List<Long> parseNumbers(String line) {
        List<Long> numbers = Arrays.stream(line.split(" "))
                .map(String::trim)
                .filter(string -> !string.isBlank())
                .map(Long::parseLong)
                .toList();

        return numbers;
    }

    private Mapping parseMapping(String line) {
        List<Long> numbers = parseNumbers(line);

        Mapping mapping = new Mapping(numbers.get(0), numbers.get(1), numbers.get(2));

        return mapping;
    }
}
