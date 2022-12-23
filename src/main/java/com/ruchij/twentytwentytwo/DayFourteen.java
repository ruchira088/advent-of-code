package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayFourteen implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    enum Content {
        ROCK, SAND
    }

    @Override
    public Object solve(Stream<String> input) {
        Coordinate sandPouringPoint = new Coordinate(500, 0);
        List<List<Coordinate>> rocks = parse(input);
        Map<Coordinate, Content> cave = cave(rocks);
        Coordinate rested = null;
        int floor = bottom(cave) + 2;
        int pours = 0;

        while (rested == null || !rested.equals(sandPouringPoint)) {
            pours++;
            rested = pourSand(cave, sandPouringPoint, floor);
        }

        return pours;
    }

    Coordinate pourSand(Map<Coordinate, Content> cave, Coordinate sand, int floor) {
        if (sand.y + 1 == floor) {
            return sand;
        }

        Coordinate nextMove = null;

        Coordinate moveDown = new Coordinate(sand.x, sand.y + 1);
        Coordinate moveLeftDown = new Coordinate(sand.x - 1, sand.y + 1);
        Coordinate moveRightDown = new Coordinate(sand.x + 1, sand.y + 1);

        if (cave.get(moveDown) == null) {
            nextMove = moveDown;
        } else if (cave.get(moveLeftDown) == null) {
            nextMove = moveLeftDown;
        } else if (cave.get(moveRightDown) == null) {
            nextMove = moveRightDown;
        }

        if (nextMove != null) {
            cave.remove(sand);
            cave.put(nextMove, Content.SAND);
            return pourSand(cave, nextMove, floor);
        }

        return sand;
    }

    int bottom(Map<Coordinate, Content> cave) {
        int bottom = 0;

        for (Coordinate coordinate : cave.keySet()) {
            if (coordinate.y > bottom) {
                bottom = coordinate.y;
            }
        }

        return bottom;
    }

    Map<Coordinate, Content> cave(List<List<Coordinate>> rocks) {
        Map<Coordinate, Content> cave = new HashMap<>();

        for (List<Coordinate> rockLine : rocks) {
            int size = rockLine.size();

            for (int i = 0; i < (size - 1); i++) {
                List<Coordinate> coordinates = coordinates(rockLine.get(i), rockLine.get(i + 1));

                for (Coordinate coordinate : coordinates) {
                    cave.put(coordinate, Content.ROCK);
                }
            }
        }

        return cave;
    }

    List<List<Coordinate>> parse(Stream<String> input) {
        List<List<Coordinate>> data = new ArrayList<>();
        Iterator<String> iterator = input.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();
            data.add(parse(line));
        }

        return data;
    }

    List<Coordinate> parse(String line) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        String[] split = line.split("->");

        for (String term : split) {
            String[] strings = term.trim().split(",");
            coordinates.add(new Coordinate(Integer.parseInt(strings[0]), Integer.parseInt(strings[1])));
        }

        return coordinates;
    }

    List<Coordinate> coordinates(Coordinate start, Coordinate end) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Coordinate coordinate = start;
        coordinates.add(coordinate);

        while (!coordinate.equals(end)) {

            if (end.x != coordinate.x) {
                coordinate = new Coordinate(coordinate.x + (end.x > coordinate.x ? 1 : -1), coordinate.y);
            }

            if (end.y != coordinate.y) {
                coordinate = new Coordinate(coordinate.x, coordinate.y + (end.y > coordinate.y ? 1 : -1));
            }


            coordinates.add(coordinate);
        }

        return coordinates;
    }
}
