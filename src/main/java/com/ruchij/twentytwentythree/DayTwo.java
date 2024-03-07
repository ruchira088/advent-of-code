package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DayTwo implements JavaSolution {
    enum Color {
        BLUE, RED, GREEN,
    }

    record Game(int id, List<Map<Color, Integer>> cubes) {}

    @Override
    public Object solve(Stream<String> input) {
        int sum = input.map(this::parse)
                .map(this::minimumCubes)
                .mapToInt(colorIntegerMap ->
                        colorIntegerMap.values().stream().reduce(1, (x, y) -> x * y)
                )
                .sum();

        return sum;
    }

    private Map<Color, Integer> minimumCubes(Game game) {
        HashMap<Color, Integer> cubes = new HashMap<>();

        for (Map<Color, Integer> turn : game.cubes) {
            for (Map.Entry<Color, Integer> entry : turn.entrySet()) {
                int current = cubes.getOrDefault(entry.getKey(), 0);
                cubes.put(entry.getKey(), Math.max(current, entry.getValue()));
            }
        }

        return cubes;
    }

    private boolean isValid(Game game, Map<Color, Integer> bundle) {
        for (Map<Color, Integer> turn : game.cubes) {
            for (Map.Entry<Color, Integer> entry : turn.entrySet()) {
                if (bundle.get(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }

        return true;
    }

    private Game parse(String input) {
        String[] strings = input.split(":");

        if (strings.length != 2) {
            throw new RuntimeException("Invalid input: %s".formatted(input));
        }

        String[] cubesShownString = strings[1].split(";");


        ArrayList<Map<Color, Integer>> cubes = new ArrayList<>();

        for (String turn : cubesShownString) {
            Map<Color, Integer> cubesShown= new HashMap<>();

            for (String ball : turn.split(",")) {
                String[] info = ball.trim().split(" ");
                int count = Integer.parseInt(info[0].trim());
                Color color = Color.valueOf(info[1].trim().toUpperCase());
                cubesShown.put(color, count);
            }

            cubes.add(cubesShown);
        }

        int id = Integer.parseInt(strings[0].split(" ")[1].trim());

        Game game = new Game(id, cubes);

        return game;
    }
}
