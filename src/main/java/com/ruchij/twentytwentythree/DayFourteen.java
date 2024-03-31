package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayFourteen implements JavaSolution {
    record Coordinate(int x, int y) {}

    enum State {
        ROUND('O'), SQUARE('#'), EMPTY('.');

        final char symbol;

        State(char symbol) {
            this.symbol = symbol;
        }

        static State parse(char character) {
            return Arrays.stream(values())
                    .filter(state -> state.symbol == character)
                    .findFirst()
                    .orElseThrow();
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        Map<Coordinate, State> parsed = parse(input);
        Map<Integer, List<Map.Entry<Coordinate, State>>> listMap = parsed.entrySet().stream()
                .collect(Collectors.groupingBy(entry -> entry.getKey().x));

        long sum = order(listMap)
                .stream()
                .map(this::tiltNorth)
                .mapToLong(this::weight)
                .sum();

        return sum;
    }

    long weight(List<State> states) {
        long value = 0;
        int size = states.size();

        for (int i = 0; i < size; i++) {
            if (states.get(i) == State.ROUND) {
                value += size - i;
            }
        }

        return value;
    }

    List<State> tiltNorth(List<State> states) {
        ArrayList<State> arrayList = new ArrayList<>(states.size());

        int edge = 0;

        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            if (state == State.ROUND) {
                arrayList.add(edge, state);
                edge++;
            } else if (state == State.SQUARE) {
                edge = i;
                edge++;
                arrayList.add(state);
            } else {
                arrayList.add(state);
            }
        }

        return arrayList;
    }

    List<List<State>> order(Map<Integer, List<Map.Entry<Coordinate, State>>> map) {
        List<List<State>> lists = new ArrayList<>();

        Stream<Map.Entry<Integer, List<Map.Entry<Coordinate, State>>>> sorted = map.entrySet().stream().sorted(Map.Entry.comparingByKey());

        for (Map.Entry<Integer, List<Map.Entry<Coordinate, State>>> entry : sorted.toList()) {
            List<State> list = entry.getValue().stream()
                    .sorted(Comparator.comparingInt(coordinateStateEntry -> coordinateStateEntry.getKey().y))
                    .map(value -> value.getValue())
                    .toList();

            lists.add(list);
        }

        return lists;
    }

    Map<Coordinate, State> parse(Stream<String> input) {
        HashMap<Coordinate, State> coordinateStateHashMap = new HashMap<>();
        List<String> lines = input.toList();
        int row = 0;

        for (String line : lines) {
            char[] chars = line.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                State state = State.parse(chars[i]);
                Coordinate coordinate = new Coordinate(i, row);
                coordinateStateHashMap.put(coordinate, state);
            }

            row++;
        }

        return coordinateStateHashMap;
    }
}
