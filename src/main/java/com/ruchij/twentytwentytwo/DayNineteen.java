package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayNineteen implements JavaSolution {
    enum Material {
        ORE, CLAY, OBSIDIAN, GEODE;
    }

    interface Robot {
        Map<Material, Integer> cost();

        Material collectionType();
    }

    record OreRobot(int ore) implements Robot {
        @Override
        public Map<Material, Integer> cost() {
            return Map.of(Material.ORE, ore);
        }

        @Override
        public Material collectionType() {
            return Material.ORE;
        }
    }

    record ClayRobot(int ore) implements Robot {
        @Override
        public Map<Material, Integer> cost() {
            return Map.of(Material.ORE, ore);
        }

        @Override
        public Material collectionType() {
            return Material.CLAY;
        }
    }

    record ObsidianRobot(int ore, int clay) implements Robot {

        @Override
        public Map<Material, Integer> cost() {
            return Map.of(Material.ORE, ore, Material.CLAY, clay);
        }

        @Override
        public Material collectionType() {
            return Material.OBSIDIAN;
        }
    }

    record GeodeRobot(int ore, int obsidian) implements Robot {

        @Override
        public Map<Material, Integer> cost() {
            return Map.of(Material.ORE, ore, Material.OBSIDIAN, obsidian);
        }

        @Override
        public Material collectionType() {
            return Material.GEODE;
        }
    }

    record Blueprint(int id, OreRobot oreRobot, ClayRobot clayRobot, ObsidianRobot obsidianRobot, GeodeRobot geodeRobot) {
        Robot robot(Material material) {
            return switch (material) {
                case ORE -> oreRobot;
                case CLAY -> clayRobot;
                case OBSIDIAN -> obsidianRobot;
                case GEODE -> geodeRobot;
            };
        }
    }

    record State(int timestamp, Map<Material, Long> materials, Map<Robot, Long> robots) {}

    record Summary(Map<Material, Long> materials, Map<Robot, Long> robots) {}

    @Override
    public Object solve(Stream<String> input) {
        Blueprint blueprint = new Blueprint(1, new OreRobot(4), new ClayRobot(2), new ObsidianRobot(3, 14), new GeodeRobot(2, 7));

        return quality(blueprint, 5);
    }

    long quality(Blueprint blueprint, int minutes) {
        ArrayDeque<State> states = new ArrayDeque<>();
        Set<Summary> visited = new HashSet<>();

        long max = 0;

        State initialState = new State(0, Map.of(), Map.of(blueprint.oreRobot, 1L));
        states.add(initialState);

        while (!states.isEmpty()) {
            State state = states.poll();
            Summary summary = new Summary(state.materials, state.robots);

            if (state.timestamp == minutes) {
                System.out.println(state.timestamp);
                System.out.println(summary);
                Long geodes = state.materials.getOrDefault(Material.GEODE, 0L);

                if (max < geodes) {
                    max = geodes;
                }
            } else if (!visited.contains(summary)) {
                System.out.println(state.timestamp);
                System.out.println(summary);
                visited.add(summary);

                for (Summary next : permutations(blueprint, summary)) {
                    HashMap<Material, Long> resources = new HashMap<>();

                    for (Map.Entry<Robot, Long> entry : state.robots.entrySet()) {
                        Material material = entry.getKey().collectionType();
                        resources.put(material, entry.getValue() + next.materials.getOrDefault(material, 0L));
                    }

                    State nextState = new State(state.timestamp + 1, resources, next.robots);
                    Summary newSummary = new Summary(nextState.materials, nextState.robots);

                    if (!visited.contains(newSummary)) {
                        states.add(nextState);
                    }
                }
            }

        }

        return max * blueprint.id;
    }

    Set<Summary> permutations(Blueprint blueprint, Summary summary) {
        HashSet<Summary> summaries = new HashSet<>();
        summaries.add(summary);

        for (Material material : Material.values()) {
            for (Summary next : new HashSet<>(summaries)) {
                summaries.addAll(permutations(blueprint, next, material));
            }
        }

        return summaries;
    }

    Set<Summary> permutations(Blueprint blueprint, Summary summary, Material material) {
        HashSet<Summary> summaries = new HashSet<>();
        summaries.add(summary);

        Robot robot = blueprint.robot(material);

        boolean canAfford = true;

        while (canAfford) {
            for (Map.Entry<Material, Integer> cost : robot.cost().entrySet()) {
                Long wealth = summary.materials.getOrDefault(cost.getKey(), 0L);

                if (wealth < cost.getValue()) {
                    canAfford = false;
                } else {
                    HashMap<Material, Long> resources = new HashMap<>(summary.materials);
                    long remaining = resources.getOrDefault(cost.getKey(), 0L) - cost.getValue();
                    resources.put(cost.getKey(), remaining);
                    summary = new Summary(resources, summary.robots);
                }
            }

            if (canAfford) {
                Map<Robot, Long> robots = new HashMap<>(summary.robots);
                robots.put(robot, robots.getOrDefault(robot, 0L) + 1);

                summary = new Summary(summary.materials, robots);
                summaries.add(summary);
            }
        }

        return summaries;
    }


}
