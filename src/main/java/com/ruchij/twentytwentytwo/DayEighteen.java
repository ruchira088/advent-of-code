package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayEighteen implements JavaSolution {
    record Coordinate(int x, int y, int z) {
    }

    @Override
    public Object solve(Stream<String> input) {
        Set<Coordinate> coordinates = new HashSet<>(parse(input));

        int sides = 0;

        for (Coordinate coordinate : coordinates) {
            sides += exposedSides(coordinate, coordinates);
        }

        return sides;
    }

    int exposedSides(Coordinate coordinate, Set<Coordinate> coordinateSet) {
        return (int) neighbours(coordinate).stream()
                .filter(neighbour -> !coordinateSet.contains(neighbour))
                .count();
    }

    Set<Coordinate> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        Set<Coordinate> list = new HashSet<>();

        while (iterator.hasNext()) {
            String line = iterator.next();
            list.add(parse(line));
        }

        return list;
    }

    Set<Coordinate> neighbours(Coordinate coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;
        int z = coordinate.z;

        return Set.of(
                new Coordinate(x + 1, y, z),
                new Coordinate(x - 1, y, z),
                new Coordinate(x, y + 1, z),
                new Coordinate(x, y - 1, z),
                new Coordinate(x, y, z + 1),
                new Coordinate(x, y, z - 1)
        );
    }


    Coordinate parse(String line) {
        Pattern pattern = Pattern.compile("([\\d|-]+),([\\d|-]+),([\\d|-]+)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            return new Coordinate(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
            );
        } else {
            throw new IllegalArgumentException("Unable to parse \"%s\"".formatted(line));
        }
    }
}
