package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayFifteen implements JavaSolution {
    record Coordinate(long x, long y) {
    }

    record Position(Coordinate sensor, Coordinate closestBeacon) {
    }

    @Override
    public Object solve(Stream<String> input) {
        List<Position> positions = parse(input);
        Set<Coordinate> beacons =
                positions.stream().map(Position::closestBeacon).collect(Collectors.toSet());
        Map<Coordinate, Long> distances = distances(positions);

        Set<Coordinate> coordinateSet = impossible(distances, 2_000_000);

        return coordinateSet.stream().filter(coordinate -> !beacons.contains(coordinate)).count();
    }

    Set<Coordinate> impossible(Map<Coordinate, Long> distances, long yAxis) {
        HashSet<Coordinate> coordinates = new HashSet<>();

        for (Map.Entry<Coordinate, Long> entry : distances.entrySet()) {
            if (entry.getKey().y == yAxis || (entry.getKey().y > yAxis && entry.getKey().y - entry.getValue() < yAxis) ||
                    (entry.getKey().y < yAxis && entry.getKey().y + entry.getValue() > yAxis)) {
                for (
                        int i = 0;
                        manhattanDistance(entry.getKey(), new Coordinate(entry.getKey().x + i, yAxis))
                                <= entry.getValue();
                        i++) {
                    coordinates.add(new Coordinate(entry.getKey().x + i, yAxis));
                    coordinates.add(new Coordinate(entry.getKey().x - i, yAxis));
                }
            }
        }

        return coordinates;
    }

    Map<Coordinate, Long> distances(List<Position> positions) {
        HashMap<Coordinate, Long> map = new HashMap<>();

        for (Position position : positions) {
            map.put(position.sensor, manhattanDistance(position.sensor, position.closestBeacon));
        }

        return map;
    }

    long manhattanDistance(Coordinate coordinateOne, Coordinate coordinateTwo) {
        return Math.abs(coordinateOne.x - coordinateTwo.x) + Math.abs(coordinateOne.y - coordinateTwo.y);
    }

    List<Position> parse(Stream<String> input) {
        ArrayList<Position> positions = new ArrayList<>();
        Iterator<String> iterator = input.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();

            Position position = parse(line);
            positions.add(position);
        }

        return positions;
    }

    Position parse(String line) {
        Pattern pattern = Pattern.compile("Sensor at x=([\\d|-]+), y=([\\d|-]+): closest beacon is at x=([\\d|-]+), y=([\\d|-]+)");
        Matcher matcher = pattern.matcher(line.trim());

        if (matcher.find()) {
            long sensorX = Long.parseLong(matcher.group(1));
            long sensorY = Long.parseLong(matcher.group(2));
            long beaconX = Long.parseLong(matcher.group(3));
            long beaconY = Long.parseLong(matcher.group(4));

            return new Position(new Coordinate(sensorX, sensorY), new Coordinate(beaconX, beaconY));
        } else {
            throw new IllegalArgumentException("Unable to parse \"%s\"".formatted(line));
        }
    }
}
