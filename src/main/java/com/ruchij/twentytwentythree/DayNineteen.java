package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class DayNineteen implements JavaSolution {
    record Part(int x, int m, int a, int s) {
        int total() { return x + m + a + s; }
    }

    record Pair<A, B>(A first, B second) {}

    sealed interface Destination {
        default Rule toRule() {
            return new Rule(part -> null, __ -> true, this);
        }
    }

    enum Outcome implements Destination {
        ACCEPTED("A"), REJECTED("R");

        private final String symbol;

        Outcome(String symbol) {
            this.symbol = symbol;
        }
    }

    record WorkflowName(String name) implements Destination {
    }

    record Rule(Function<Part, Integer> valueFn, Function<Integer, Boolean> testFn,
                Destination destination) {
    }

    record Workflow(String name, List<Rule> rules) {
    }

    @Override
    public Object solve(Stream<String> input) {
        HashMap<String, Workflow> workflows = new HashMap<>();
        ArrayList<Part> parts = new ArrayList<>();

        boolean encounteredEmptyLine = false;

        for (String line : input.toList()) {
            if (encounteredEmptyLine) {
                Part part = parsePart(line);
                parts.add(part);
            } else if (line.trim().isEmpty()) {
                encounteredEmptyLine = true;
            } else {
                Workflow workflow = parseWorkflow(line);
                workflows.put(workflow.name(), workflow);
            }
        }

        Workflow start = workflows.get("in");

        long total = parts.stream()
                .filter(part -> determineOutcome(workflows, start, part) == Outcome.ACCEPTED)
                .mapToLong(part -> part.total())
                .sum();

        return total;
    }

    Outcome determineOutcome(Map<String, Workflow> workflows, Workflow workflow, Part part) {
        for (Rule rule : workflow.rules) {
            if (rule.testFn.apply(rule.valueFn.apply(part))) {
                Destination destination = rule.destination;
                if (destination instanceof WorkflowName) {
                    WorkflowName workflowName = (WorkflowName) destination;
                    return determineOutcome(workflows, workflows.get(workflowName.name), part);
                } else {
                    Outcome outcome = (Outcome) destination;
                    return outcome;
                }
            }
        }

        throw new RuntimeException("No outcome found for workflow=%s, part=%s".formatted(workflow.name, part));
    }


    Part parsePart(String input) {
        List<Integer> partsList = Arrays.stream(input.substring(1, input.length() - 1).split(","))
                .map(string -> Integer.parseInt(string.split("=")[1]))
                .toList();

        Part part = new Part(partsList.get(0), partsList.get(1), partsList.get(2), partsList.get(3));
        return part;
    }

    Workflow parseWorkflow(String input) {
        String[] split = input.split("\\{");
        String name = split[0];
        List<Rule> rules = Arrays.stream(split[1].substring(0, split[1].length() - 1).split(","))
                .map(this::parseRule)
                .toList();

        Workflow workflow = new Workflow(name, rules);
        return workflow;
    }

    Rule parseRule(String input) {
        if (input.contains(">") || input.contains("<")) {
            String[] split = input.split(":");

            String condition = split[0];
            Destination destination = parseDestination(split[1]);

            Function<Part, Integer> fn =
                    switch (condition.charAt(0)) {
                        case 'x' -> part -> part.x;
                        case 'm' -> part -> part.m;
                        case 'a' -> part -> part.a;
                        case 's' -> part -> part.s;
                        default -> throw new IllegalArgumentException("Invalid condition: " + condition);
                    };

            int boundaryValue = Integer.parseInt(condition.substring(2));

            Function<Integer, Boolean> testFn =
                    x -> input.contains(">") ? x > boundaryValue : x < boundaryValue;

            Rule rule = new Rule(fn, testFn, destination);
            return rule;
        } else {
            return parseDestination(input).toRule();
        }
    }

    Destination parseDestination(String input) {
        return Arrays.stream(Outcome.values())
                .filter(value -> value.symbol.equalsIgnoreCase(input))
                .map(outcome -> (Destination) outcome)
                .findFirst()
                .orElseGet(() -> new WorkflowName(input));
    }
}
