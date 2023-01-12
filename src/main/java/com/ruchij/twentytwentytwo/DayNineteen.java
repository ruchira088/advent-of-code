package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DayNineteen implements JavaSolution {
    enum Material {
        ORE, CLAY, OBSIDIAN, GEODE
    }

    interface Robot {
        Map<Material, Integer> resourceRequirements();

        Material producedResource();
    }

    record OreRobot(int ore) implements Robot {
        @Override
        public Map<Material, Integer> resourceRequirements() {
            return Map.of(Material.ORE, ore);
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
        public Map<Material, Integer> resourceRequirements() {
            return Map.of(Material.ORE, ore);
        }
    }

    record ObsidianRobot(int ore, int clay) implements Robot {
        @Override
        public Map<Material, Integer> resourceRequirements() {
            return Map.of(Material.ORE, ore, Material.CLAY, clay);
        }

        @Override
        public Material producedResource() {
            return Material.OBSIDIAN;
        }
    }

    record GeodeRobot(int ore, int obsidian) implements Robot {
        @Override
        public Map<Material, Integer> resourceRequirements() {
            return Map.of(Material.ORE, ore, Material.OBSIDIAN, obsidian);
        }

        @Override
        public Material producedResource() {
            return Material.GEODE;
        }
    }

    record Blueprint(int id, OreRobot oreRobot, ClayRobot clayRobot, ObsidianRobot obsidianRobot,
                     GeodeRobot geodeRobot) {
    }

    record State(int timestamp, Map<Material, Integer> resources, Map<Robot, Integer> robots) {
        ProductionState productionState() {
            return new ProductionState(resources, robots);
        }
    }

    record ProductionState(Map<Material, Integer> resources, Map<Robot, Integer> robots) {
    }

    Set<Blueprint> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        HashSet<Blueprint> blueprints = new HashSet<>();

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
        Set<Blueprint> blueprints = parse(input);

        return maxGeodes(blueprints.stream().toList().get(0), 21);
    }

    boolean belowMax(Map<Map<Robot, Integer>, Map<Material, Integer>> max, State state) {
        Map<Material, Integer> materials = max.get(state.robots);

        if (materials == null) {
            max.put(state.robots, state.resources);
            return false;
        } else {
            int count = 0;

            for (Material material : Material.values()) {
                if (materials.getOrDefault(material, 0) <= state.resources.getOrDefault(material, 0)) {
                    count++;
                }
            }

            if (count == 0) {
                return true;
            } else {
                if (count == 4) {
                    max.put(state.robots, state.resources);
                }

                return false;
            }
        }

    }

    int maxGeodes(Blueprint blueprint, int minutes) {
        ArrayDeque<State> states = new ArrayDeque<>();
        HashSet<ProductionState> visited = new HashSet<>();
        Map<Map<Robot, Integer>, Map<Material, Integer>> max = new HashMap<>();

        int maxGeodes = 0;
        int timestamp = 0;

        State initialState = new State(0, new HashMap<>(), new HashMap<>(Map.of(blueprint.oreRobot, 1)));
        states.add(initialState);

        while (!states.isEmpty()) {
            State state = states.poll();

            if (timestamp != state.timestamp) {
                visited.clear();
                timestamp = state.timestamp;
            }

            Integer geodes =
                    state.resources.getOrDefault(Material.GEODE, 0) +
                    state.robots.getOrDefault(blueprint.geodeRobot, 0);

            if (!belowMax(max, state) && !visited.contains(state.productionState())) {
                visited.add(state.productionState());

                maxGeodes = Math.max(maxGeodes, state.resources.getOrDefault(Material.GEODE, 0));

                if (state.timestamp < minutes) {
                    for (State nextState : nextStates(state, blueprint)) {
                        if (!belowMax(max, nextState) && !visited.contains(nextState.productionState())) {
                            states.add(nextState);
                        }
                    }
                }
            }
        }

        return maxGeodes;
    }

    Set<State> nextStates(State inputState, Blueprint blueprint) {
        Set<State> states = new HashSet<>();
        states.add(new State(inputState.timestamp, inputState.resources, inputState.robots));

        Set<Robot> robots = Set.of(blueprint.oreRobot, blueprint.clayRobot, blueprint.obsidianRobot, blueprint.geodeRobot);

        for (Robot robot : robots) {
            for (State state : new HashSet<>(states)) {
                Map<Material, Integer> resources = new HashMap<>(state.resources);
                int count = 0;

                while (canAfford(robot, resources)) {
                    resources = build(robot, resources);
                    count++;

                    HashMap<Robot, Integer> robotCount = new HashMap<>(state.robots);
                    robotCount.put(robot, robotCount.getOrDefault(robot, 0) + count);

                    states.add(new State(state.timestamp, resources, robotCount));
                }
            }
        }

        Set<State> nextStates = new HashSet<>();
        int maxOre = Math.max(blueprint.geodeRobot.ore, Math.max(blueprint.obsidianRobot.ore, Math.max(blueprint.oreRobot().ore, blueprint.clayRobot.ore)));

        Function<State, Boolean> isLogicalState =
                state -> {
                    Integer clay = state.resources.getOrDefault(Material.CLAY, 0);
                    Integer obsidian = state.resources.getOrDefault(Material.OBSIDIAN, 0);
                    Integer ore = state.resources.getOrDefault(Material.ORE, 0);

                    return (clay < blueprint.obsidianRobot.clay || ore < blueprint.obsidianRobot.ore) &&
                            (obsidian < blueprint.geodeRobot.obsidian || ore < blueprint.geodeRobot.ore) &&
                            ore <= maxOre;
                };

        for (State state : states) {
            if (isLogicalState.apply(state)) {
                nextStates.add(new State(state.timestamp + 1, addMinedResources(state.resources, inputState.robots), state.robots));
            }
        }

        return nextStates;
    }

    Map<Material, Integer> addMinedResources(Map<Material, Integer> resources, Map<Robot, Integer> robots) {
        HashMap<Material, Integer> updatedResources = new HashMap<>(resources);

        for (Map.Entry<Robot, Integer> entry : robots.entrySet()) {
            Material material = entry.getKey().producedResource();
            updatedResources.put(material, updatedResources.getOrDefault(material, 0) + entry.getValue());
        }

        return updatedResources;
    }

    boolean canAfford(Robot robot, Map<Material, Integer> resources) {
        for (Map.Entry<Material, Integer> materialRequirement : robot.resourceRequirements().entrySet()) {
            if (resources.getOrDefault(materialRequirement.getKey(), 0) < materialRequirement.getValue()) {
                return false;
            }
        }

        return true;
    }

    Map<Material, Integer> build(Robot robot, Map<Material, Integer> resources) {
        HashMap<Material, Integer> updatedResources = new HashMap<>(resources);

        for (Map.Entry<Material, Integer> materialRequirement : robot.resourceRequirements().entrySet()) {
            updatedResources.put(materialRequirement.getKey(), updatedResources.get(materialRequirement.getKey()) - materialRequirement.getValue());
        }

        return updatedResources;
    }


}
