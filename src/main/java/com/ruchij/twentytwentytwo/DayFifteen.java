package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DayFifteen implements JavaSolution {
    record Coordinate(long x, long y) {
    }

    record Position(Coordinate sensor, Coordinate closestBeacon) {
    }

    @Override
    public Object solve(Stream<String> input) {
        long length = 4_000_000;
        List<Position> positions = parse(input);
        Map<Coordinate, Long> distances = distances(positions);

        long y = 0;

        while (y <= length) {
            long x = 0;

            while (x <= length) {
                Coordinate current = new Coordinate(x, y);
                Coordinate next = next(current, distances);

                if (next == null) {
                    return 4_000_000 * current.x + current.y;
                } else {
                    x = next.x;
                }
            }
            y++;
        }

        return null;
    }

    Coordinate next(Coordinate coordinate, Map<Coordinate, Long> distances) {
        for (Map.Entry<Coordinate, Long> entry : distances.entrySet()) {
            Coordinate sensor = entry.getKey();
            long modulus = entry.getValue();

            long distance = manhattanDistance(coordinate, sensor);

            if (distance <= modulus) {
                long x = sensor.x + modulus - Math.abs(sensor.y - coordinate.y);
                return new Coordinate(x + 1, coordinate.y);
            }
        }

        return null;
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
