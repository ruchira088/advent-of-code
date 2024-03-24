package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayEleven implements JavaSolution {
    record Coordinate(int x, int y) {}

    @Override
    public Object solve(Stream<String> input) {
        Set<Coordinate> coordinates = expand(parse(input));

        return diffs(coordinates);
    }

    Set<Coordinate> parse(Stream<String> input) {
        List<String> lines = input.filter(line -> !line.isEmpty()).toList();
        HashSet<Coordinate> coordinates = new HashSet<>();

        for (int y = 0; y < lines.size(); y++) {
            char[] charArray = lines.get(y).trim().toCharArray();

            for (int x = 0; x < charArray.length; x++) {
                if (charArray[x] == '#') {
                    Coordinate coordinate = new Coordinate(x, y);
                    coordinates.add(coordinate);
                }
            }
        }

        return coordinates;
    }

    Set<Coordinate> expand(Set<Coordinate> initial) {
        int width = initial.stream().mapToInt(Coordinate::x).max().getAsInt() + 1;
        int height = initial.stream().mapToInt(Coordinate::y).max().getAsInt() + 1;

        SortedSet<Integer> emptyRows = new TreeSet<>();
        SortedSet<Integer> emptyColumns = new TreeSet<>();

        for (int y = 0; y < height; y++) {
            boolean found = false;

            for (int x = 0; x < width; x++) {
                Coordinate coordinate = new Coordinate(x, y);

                if (initial.contains(coordinate)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                emptyRows.add(y);
            }
        }

        for (int x = 0; x < width; x++) {
            boolean found = false;

            for (int y = 0; y < height; y++) {
                Coordinate coordinate = new Coordinate(x, y);

                if (initial.contains(coordinate)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                emptyColumns.add(x);
            }
        }

        HashSet<Coordinate> expanded = new HashSet<>();

        for (Coordinate coordinate : initial) {
            expanded.add(transform(coordinate, emptyRows, emptyColumns));
        }

        return expanded;
    }

    Coordinate transform(Coordinate coordinate, SortedSet<Integer> emptyRows, SortedSet<Integer> emptyColumns) {
        int yDiff = emptyRows.headSet(coordinate.y()).size();
        int xDiff = emptyColumns.headSet(coordinate.x()).size();

        Coordinate transformed = new Coordinate(coordinate.x + xDiff, coordinate.y + yDiff);
        return transformed;
    }

    long diffs(Set<Coordinate> coordinates) {
        long length = 0;
        Coordinate[] coordinatesArray = coordinates.stream().toArray(Coordinate[]::new);

        for (int i = 0; i < coordinatesArray.length; i++) {
            Coordinate start = coordinatesArray[i];

            for (int j = i + 1; j < coordinatesArray.length; j++) {
                Coordinate end = coordinatesArray[j];
                long diff = Math.abs(end.x - start.x) + Math.abs(end.y - start.y);
                length += diff;
            }
        }

        return length;
    }
}
