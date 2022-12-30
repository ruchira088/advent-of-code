package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
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
        Map<Coordinate, Integer> exposingCoordinates = new HashMap<>();
        Set<Coordinate> allExposingCoordinates = new HashSet<>();

        for (Coordinate coordinate : coordinates) {
            for (Coordinate exposingCoordinate : exposingCoordinates(coordinate, coordinates)) {
                exposingCoordinates.put(
                        exposingCoordinate,
                        exposingCoordinates.getOrDefault(exposingCoordinate, 0) + 1
                );

                allExposingCoordinates.addAll(
                        neighbours(exposingCoordinate).stream()
                                .filter(value -> !coordinates.contains(value))
                                .collect(Collectors.toSet())
                );

                allExposingCoordinates.add(exposingCoordinate);
            }
        }

        Set<Set<Coordinate>> consolidated = consolidate(allExposingCoordinates);
        Coordinate rightMost = rightMost(coordinates);

        Set<Coordinate> outerCoordinates = consolidated.stream()
                .filter(set -> set.contains(new Coordinate(rightMost.x + 1, rightMost.y, rightMost.z)))
                .findFirst()
                .orElseThrow();

        int count = 0;

        for (Map.Entry<Coordinate, Integer> entry : exposingCoordinates.entrySet()) {
            if (outerCoordinates.contains(entry.getKey())) {
                count += entry.getValue();
            }
        }

        return count;
    }

    Coordinate rightMost(Set<Coordinate> coordinates) {
        Optional<Coordinate> max = coordinates.stream().max(Comparator.comparing(coordinate -> coordinate.x));

        return max.orElseThrow();
    }

    Set<Set<Coordinate>> consolidate(Set<Coordinate> coordinates) {
        Map<UUID, Set<Coordinate>> groups = new HashMap<>();
        boolean process = true;

        while (process) {
            process = false;

            for (Coordinate coordinate : coordinates) {
                Set<UUID> matchingUuids = new HashSet<>();

                Set<Coordinate> values = new HashSet<>(neighbours(coordinate));
                values.add(coordinate);

                for (Coordinate value : values) {
                    for (Map.Entry<UUID, Set<Coordinate>> entry : groups.entrySet()) {
                        if (entry.getValue().contains(value)) {
                            matchingUuids.add(entry.getKey());
                        }
                    }
                }

                if (matchingUuids.isEmpty()) {
                    process = true;
                    groups.put(UUID.randomUUID(), new HashSet<>(Set.of(coordinate)));
                } else {
                    List<UUID> uuids = matchingUuids.stream().toList();

                    UUID primary = uuids.get(0);
                    Set<Coordinate> coordinateSet = groups.get(primary);
                    coordinateSet.add(coordinate);

                    for (UUID uuid : uuids.subList(1, uuids.size())) {
                        process = true;
                        coordinateSet.addAll(groups.remove(uuid));
                    }
                }
            }
        }


        return new HashSet<>(groups.values());
    }


    Set<Coordinate> exposingCoordinates(Coordinate coordinate, Set<Coordinate> coordinateSet) {
        return neighbours(coordinate).stream()
                .filter(neighbour -> !coordinateSet.contains(neighbour))
                .collect(Collectors.toSet());
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
