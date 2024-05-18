package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.stream.Stream;

public class DayTwentyFour implements JavaSolution {
    record Coordinate(double x, double y, double z) {}
    record HailStone(Coordinate position, Coordinate velocity) {}

    @Override
    public Object solve(Stream<String> input) {
        HailStone[] hailStones = parse(input);

        Coordinate start = new Coordinate(200000000000000L, 200000000000000L, 0);
        Coordinate end = new Coordinate(400000000000000L, 400000000000000L, 0);

        long count = 0;

        for (int i = 0; i < hailStones.length; i++) {
            HailStone hailStoneA = hailStones[i];
            for (int j = i + 1; j < hailStones.length; j++) {
                HailStone hailStoneB = hailStones[j];
                Coordinate coordinate = findIntersection(hailStoneA, hailStoneB);

                if (inRange(start, end, coordinate) && isValidTime(coordinate, hailStoneA) && isValidTime(coordinate, hailStoneB)) {
                    count++;
                }
            }
        }

        return count;
    }

    boolean inRange(Coordinate start, Coordinate end, Coordinate coordinate) {
        return coordinate.x >= start.x && coordinate.y >= start.y
                && coordinate.x <= end.x && coordinate.y <= end.y;
    }

    boolean isValidTime(Coordinate coordinate, HailStone hailStone) {
        return  (coordinate.x - hailStone.position.x) / hailStone.velocity.x >= 0;
    }

    Coordinate findIntersection(HailStone hailStoneA, HailStone hailStoneB) {
        Double x = solveX(hailStoneA, hailStoneB);

        if (x != null) {
            double y = calculateY(x, hailStoneA);
            Coordinate coordinate = new Coordinate(x, y, 0);
            return coordinate;
        }

        return null;
    }

    HailStone[] parse(Stream<String> input) {
        return input.map(this::parse).toArray(size -> new HailStone[size]);
    }

    HailStone parse(String line) {
        String[] strings = line.split("@");
        Coordinate position = parseCoordinate(strings[0].split(","));
        Coordinate velocity = parseCoordinate(strings[1].split(","));

        return new HailStone(position, velocity);
    }

    Coordinate parseCoordinate(String[] values) {
        return new Coordinate(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]));
    }

    Double solveX(HailStone hailStoneA, HailStone hailStoneB) {
        try {
            double top =
                    hailStoneA.position.y -
                            hailStoneB.position.y +
                            hailStoneB.velocity.y / hailStoneB.velocity.x * hailStoneB.position.x -
                            hailStoneA.velocity.y / hailStoneA.velocity.x * hailStoneA.position.x;

            double bottom =
                    hailStoneB.velocity.y / hailStoneB.velocity.x -
                            hailStoneA.velocity.y / hailStoneA.velocity.x;

            return top / bottom;
        } catch (Exception e) {
            return null;
        }
    }

    Double calculateY(double x, HailStone hailStone) {
        return hailStone.velocity.y / hailStone.velocity.x * (x - hailStone.position.x) + hailStone.position.y;
    }
}
