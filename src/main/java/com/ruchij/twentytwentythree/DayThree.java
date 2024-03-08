package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayThree implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    record Point(int value, int id) {}

    @Override
    public Object solve(Stream<String> input) {
        LinkedHashMap<Coordinate, Character> map = parse(input);

        Map<Coordinate, Point> numbers = numbers(map);

        return findGear(map).stream().mapToInt(coordinate -> gearValue(coordinate, numbers)).sum();
    }

    private int gearValue(Coordinate coordinate, Map<Coordinate, Point> numbers) {
        HashSet<Point> points = new HashSet<>();

        for (int x : new int[] {-1, 0, 1}) {
            for (int y : new int[] {-1, 0, 1}) {
                Coordinate current = new Coordinate(coordinate.x + x, coordinate.y + y);

                Point point = numbers.get(current);

                if (point != null) {
                    points.add(point);
                }
            }
        }

        if (points.size() != 2) {
            return 0;
        } else {
            int result = 1;

            for (Point point : points) {
                result = result * point.value;
            }

            return result;
        }
    }


    private Map<Coordinate, Point> numbers(Map<Coordinate, Character> map) {
        HashMap<Coordinate, Point> coordinatePointHashMap = new HashMap<>();

        int number = 0;
        int y = 0;

        Set<Coordinate> coordinates = new HashSet<>();

        for (Map.Entry<Coordinate, Character> entry : map.entrySet()) {
            if (Character.isDigit(entry.getValue()) && y == entry.getKey().y) {
                number = number * 10 + Character.digit(entry.getValue(), 10);
                coordinates.add(entry.getKey());
            } else  {
                int size = coordinatePointHashMap.size();

                for (Coordinate coordinate : coordinates) {
                    coordinatePointHashMap.put(coordinate, new Point(number, size));
                }

                coordinates = new HashSet<>();
                number = 0;

                if (Character.isDigit(entry.getValue())) {
                    number = Character.digit(entry.getValue(), 10);
                    coordinates.add(entry.getKey());
                }
            }

            y = entry.getKey().y;
        }

        return coordinatePointHashMap;
    }

    private Set<Coordinate> findGear(Map<Coordinate, Character> map) {
        HashSet<Coordinate> coordinates = new HashSet<>();

        for (Map.Entry<Coordinate, Character> entry : map.entrySet()) {
            if (entry.getValue() == '*') {
                coordinates.add(entry.getKey());
            }
        }

        return coordinates;
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
