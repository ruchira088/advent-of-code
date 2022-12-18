package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayEleven implements JavaSolution {
    record NextThrow(int mod, int divisible, int notDivisible) {
    }

    record Monkey(int id, List<Integer> startingItems, Function<Integer, Integer> operation, NextThrow nextThrow) {
    }

    @Override
    public Object solve(Stream<String> input) {
        List<Monkey> monkeys = parse(input);
        Map<Integer, Integer> inspectionCount = new HashMap<>();
        Map<Integer, Monkey> monkeyGroup = new HashMap<>();

        for (Monkey monkey : monkeys) {
            monkeyGroup.put(monkey.id, monkey);
        }

        for (int i = 0; i < 20; i++) {
            for (Monkey monkey : monkeys) {
                inspect(monkey, monkeyGroup, inspectionCount);
            }
        }

        List<Integer> itemInspections = new ArrayList<>(inspectionCount.values());
        Collections.sort(itemInspections, Comparator.reverseOrder());

        return itemInspections.get(0) * itemInspections.get(1);
    }

    void printMonkeys(List<Monkey> monkeys) {
        for (Monkey monkey : monkeys) {
            System.out.println("Monkey %s: %s".formatted(monkey.id, monkey.startingItems.stream().map(Object::toString).collect(Collectors.joining(", "))));
        }
    }

    void inspect(Monkey monkey, Map<Integer, Monkey> monkeyGroup, Map<Integer, Integer> inspectionCount) {
        for (Integer item : new ArrayList<>(monkey.startingItems)) {
            Integer worryLevel = monkey.operation.apply(item) / 3;

            inspectionCount.put(monkey.id, inspectionCount.getOrDefault(monkey.id, 0) + 1);

            monkey.startingItems.remove(item);

            if (worryLevel % monkey.nextThrow.mod == 0) {
                monkeyGroup.get(monkey.nextThrow.divisible).startingItems().add(worryLevel);
            } else {
                monkeyGroup.get(monkey.nextThrow.notDivisible).startingItems().add(worryLevel);
            }
        }
    }

    List<Monkey> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        ArrayList<Monkey> monkeys = new ArrayList<>();
        ArrayList<String> group = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();

            if (line.trim().isEmpty()) {
                monkeys.add(parse(group));
                group = new ArrayList<>();
            } else {
                group.add(line.trim());
            }
        }

        if (!group.isEmpty()) {
            monkeys.add(parse(group));
        }

        return monkeys;
    }

    Monkey parse(List<String> lines) {
        Matcher monkeyIdMatcher = Pattern.compile("Monkey (\\d+):").matcher(lines.get(0).trim());
        monkeyIdMatcher.find();
        int monkeyId = Integer.parseInt(monkeyIdMatcher.group(1).trim());

        Matcher startingItemsMatcher = Pattern.compile("Starting items: (.*)").matcher(lines.get(1).trim());
        startingItemsMatcher.find();
        List<Integer> itemsList = Arrays.stream(startingItemsMatcher.group(1).split(","))
                .filter(word -> !word.isEmpty())
                .map(item -> Integer.parseInt(item.trim()))
                .toList();

        Matcher operationMatcher = Pattern.compile("Operation: new = (\\S+) (\\S+) (\\S+)").matcher(lines.get(2).trim());
        operationMatcher.find();

        String first = operationMatcher.group(1).trim();
        Function<Integer, Integer> firstTerm =
                first.equalsIgnoreCase("old") ? x -> x : x -> Integer.parseInt(first);

        String operator = operationMatcher.group(2).trim();
        BiFunction<Integer, Integer, Integer> operationFunction =
                operator.equalsIgnoreCase("*") ? (x, y) -> x * y : (x, y) -> x + y;

        String second = operationMatcher.group(3).trim();
        Function<Integer, Integer> secondTerm =
                second.equalsIgnoreCase("old") ? x -> x : x -> Integer.parseInt(second);

        Function<Integer, Integer> operation =
                old -> operationFunction.apply(firstTerm.apply(old), secondTerm.apply(old));

        Matcher divisibleMatcher = Pattern.compile("Test: divisible by (\\d+)").matcher(lines.get(3).trim());
        divisibleMatcher.find();
        int divisible = Integer.parseInt(divisibleMatcher.group(1).trim());

        Matcher divisibleThrowMatcher = Pattern.compile("If true: throw to monkey (\\d+)").matcher(lines.get(4).trim());
        divisibleThrowMatcher.find();
        int divisibleThrow = Integer.parseInt(divisibleThrowMatcher.group(1).trim());

        Matcher indivisibleThrowMatcher = Pattern.compile("If false: throw to monkey (\\d+)").matcher(lines.get(5).trim());
        indivisibleThrowMatcher.find();
        int indivisibleThrow = Integer.parseInt(indivisibleThrowMatcher.group(1).trim());

        Monkey monkey = new Monkey(monkeyId, new ArrayList<>(itemsList), operation, new NextThrow(divisible, divisibleThrow, indivisibleThrow));

        return monkey;
    }
}
