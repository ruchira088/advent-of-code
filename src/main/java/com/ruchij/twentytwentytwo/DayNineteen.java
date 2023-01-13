package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DayNineteen implements JavaSolution {
    enum Material {
        ORE, CLAY, OBSIDIAN, GEODE
    }

    interface Robot {
        Resources resourceRequirements();

        Material producedResource();
    }

    record OreRobot(int ore) implements Robot {
        @Override
        public Resources resourceRequirements() {
            return new Resources(ore, 0, 0, 0);
        }

        @Override
        public Material producedResource() {
            return Material.ORE;
        }
    }

    record ClayRobot(int ore) implements Robot {
        @Override
        public Material producedResource() {
            return Material.CLAY;
        }

        @Override
        public Resources resourceRequirements() {
            return new Resources(ore, 0, 0, 0);
        }
    }

    record ObsidianRobot(int ore, int clay) implements Robot {
        @Override
        public Resources resourceRequirements() {
            return new Resources(ore, clay, 0, 0);
        }

        @Override
        public Material producedResource() {
            return Material.OBSIDIAN;
        }
    }

    record GeodeRobot(int ore, int obsidian) implements Robot {
        @Override
        public Resources resourceRequirements() {
            return new Resources(ore, 0, obsidian, 0);
        }

        @Override
        public Material producedResource() {
            return Material.GEODE;
        }
    }

    record Blueprint(int id, OreRobot oreRobot, ClayRobot clayRobot, ObsidianRobot obsidianRobot,
                     GeodeRobot geodeRobot) {
    }

    record Resources(int ore, int clay, int obsidian, int geode) {
        Resources plus(Resources resources) {
            return new Resources(
                    ore + resources.ore,
                    clay + resources.clay,
                    obsidian + resources.obsidian,
                    geode + resources.geode
            );
        }

        Resources minus(Resources resources) {
            return new Resources(
                    ore - resources.ore,
                    clay - resources.clay,
                    obsidian - resources.obsidian,
                    geode - resources.geode
            );
        }

        Resources trim(Resources max) {
            return new Resources(
                    Math.min(ore, max.ore),
                    Math.min(clay, max.clay),
                    Math.min(obsidian, max.obsidian),
                    Math.min(geode, max.geode)
            );
        }
    }

    record Robots(int oreRobot, int clayRobot, int obsidianRobot, int geodeRobot) {
        Resources mine() {
            return new Resources(oreRobot, clayRobot, obsidianRobot, geodeRobot);
        }

        Robots add(Robot robot) {
            return switch (robot.producedResource()) {
                case ORE -> new Robots(oreRobot + 1, clayRobot, obsidianRobot, geodeRobot);
                case CLAY -> new Robots(oreRobot, clayRobot + 1, obsidianRobot, geodeRobot);
                case OBSIDIAN -> new Robots(oreRobot, clayRobot, obsidianRobot + 1, geodeRobot);
                case GEODE -> new Robots(oreRobot, clayRobot, obsidianRobot, geodeRobot + 1);
            };
        }
    }

    record State(int timestamp, Resources resources, Robots robots) {
        ProductionState productionState() {
            return new ProductionState(resources, robots);
        }

        State trim(Resources maxResources) {
            return new State(timestamp, resources.trim(maxResources), robots);
        }
    }

    record ProductionState(Resources resources, Robots robots) {
    }

    List<Blueprint> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        List<Blueprint> blueprints = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();
            Blueprint blueprint = parse(line);
            blueprints.add(blueprint);
        }

        return blueprints;
    }

    Blueprint parse(String input) {
        Pattern pattern = Pattern.compile("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            int groupId = Integer.parseInt(matcher.group(1));

            int oreRobotOre = Integer.parseInt(matcher.group(2));
            OreRobot oreRobot = new OreRobot(oreRobotOre);

            int clayRobotOre = Integer.parseInt(matcher.group(3));
            ClayRobot clayRobot = new ClayRobot(clayRobotOre);

            int obsidianRobotOre = Integer.parseInt(matcher.group(4));
            int obsidianRobotClay = Integer.parseInt(matcher.group(5));
            ObsidianRobot obsidianRobot = new ObsidianRobot(obsidianRobotOre, obsidianRobotClay);

            int geodeRobotOre = Integer.parseInt(matcher.group(6));
            int geodeRobotObsidian = Integer.parseInt(matcher.group(7));
            GeodeRobot geodeRobot = new GeodeRobot(geodeRobotOre, geodeRobotObsidian);

            return new Blueprint(groupId, oreRobot, clayRobot, obsidianRobot, geodeRobot);
        } else {
            throw new IllegalArgumentException("Unable to parse \"%s\"".formatted(input));
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        List<Blueprint> blueprints = parse(input).stream().limit(3).toList();
        ArrayList<Future<Integer>> futures = new ArrayList<>();
        int answer = 1;

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (Blueprint blueprint : blueprints) {
            Future<Integer> future = executorService.submit(() -> quality(blueprint, 32));
            futures.add(future);
        }

        for (Future<Integer> future : futures) {
            try {
                answer *= future.get();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        executorService.shutdown();

        return answer;
    }

    Resources max(Blueprint blueprint, int timestamp, int total) {
        int diff = total - timestamp;
        int maxOre = Math.max(blueprint.oreRobot.ore, Math.max(blueprint.clayRobot.ore, Math.max(blueprint.obsidianRobot.ore, blueprint.geodeRobot.ore)));

        return new Resources(maxOre * diff, blueprint.obsidianRobot.clay * diff, blueprint.geodeRobot.obsidian * diff, Integer.MAX_VALUE);
    }

    int quality(Blueprint blueprint, int minutes) {
        ArrayDeque<State> states = new ArrayDeque<>();
        HashSet<ProductionState> visited = new HashSet<>();
        Instant start = Instant.now();

        int maxGeodes = 0;
        int timestamp = 0;

        State initialState = new State(0, new Resources(0, 0, 0, 0), new Robots(1, 0, 0, 0));
        states.add(initialState);

        while (!states.isEmpty()) {
            State polledState = states.poll();
            State state = polledState.trim(max(blueprint, polledState.timestamp, minutes));

            if (timestamp != state.timestamp) {
                Instant end = Instant.now();
                System.out.println("Timestamp=%d Duration=%,dms".formatted(timestamp, end.toEpochMilli() - start.toEpochMilli()));
                start = end;

                visited.clear();
                timestamp = state.timestamp;
            }

            if (!visited.contains(state.productionState())) {
                visited.add(state.productionState());

                if (state.resources.geode >= maxGeodes) {
                    maxGeodes = state.resources.geode;
                }

                if (state.timestamp < minutes) {
                    for (State nextState : nextStates(state, blueprint)) {
                        if (!visited.contains(nextState.productionState())) {
                            states.add(nextState);
                        }
                    }
                }
            }
        }

        return maxGeodes;
    }

    Set<State> nextStates(State state, Blueprint blueprint) {
        Set<State> states = new HashSet<>();
        states.add(new State(state.timestamp + 1, state.resources.plus(state.robots.mine()), state.robots));

        Set<Robot> robots = new HashSet<>(Set.of(blueprint.geodeRobot));

        int maxOre =
                Stream.of(blueprint.oreRobot.ore, blueprint.clayRobot.ore, blueprint.obsidianRobot.ore, blueprint.geodeRobot.ore)
                        .max(Comparator.naturalOrder()).orElseThrow();

        if (state.robots.clayRobot < blueprint.obsidianRobot.clay) {
            robots.add(blueprint.clayRobot);
        }

        if (state.robots.obsidianRobot < blueprint.geodeRobot.obsidian) {
            robots.add(blueprint.obsidianRobot);
        }

        if (state.robots.oreRobot < maxOre) {
            robots.add(blueprint.oreRobot);
        }

        for (Robot robot : robots) {
            build(robot, state.resources)
                    .ifPresent(leftover -> {
                        State newState =
                                new State(
                                        state.timestamp + 1,
                                        leftover.plus(state.robots.mine()),
                                        state.robots.add(robot)
                                );
                        states.add(newState);
                    });
        }

        return states;
    }

    Optional<Resources> build(Robot robot, Resources resources) {
        Resources leftOver = resources.minus(robot.resourceRequirements());

        if (leftOver.ore >= 0 && leftOver.clay >= 0 && leftOver.obsidian >= 0 && leftOver.geode >= 0) {
            return Optional.of(leftOver);
        } else {
            return Optional.empty();
        }
    }
}
