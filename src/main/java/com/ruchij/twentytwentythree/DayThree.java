package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayThree implements JavaSolution {
    record Coordinate(int x, int y) {}
    @Override
    public Object solve(Stream<String> input) {
        LinkedHashMap<Coordinate, Character> map = parse(input);
        int total = 0;
        int current = 0;
        boolean include = false;

        for (Map.Entry<Coordinate, Character> entry : map.entrySet()) {
            if (Character.isDigit(entry.getValue())) {
                current = current * 10 + Character.digit(entry.getValue(), 10);
                if (!include) {
                    include = hasSymbolNearby(entry.getKey(), map);
                }
            } else {
                if (include) {
                    total += current;
                    include = false;
                }

                current = 0;
            }
        }

        if (include) {
            total += current;
        }

        return total;
    }

    private boolean hasSymbolNearby(Coordinate coordinate, LinkedHashMap<Coordinate, Character> map) {
        for (int x : new int[]{-1, 0, 1}) {
            for (int y : new int[] {-1, 0, 1}) {
                Character character = map.get(new Coordinate(coordinate.x + x, coordinate.y + y));

                if (character != null && !Character.isDigit(character) && character != '.') {
                    return true;
                }
            }
        }

        return false;
    }

    private LinkedHashMap<Coordinate, Character> parse(Stream<String> input) {
        LinkedHashMap<Coordinate, Character> result = new LinkedHashMap<>();
        List<String> lines = new ArrayList<>(input.toList());

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                result.put(new Coordinate(x, y), line.charAt(x));
            }
        }

        return result;
    }
}
