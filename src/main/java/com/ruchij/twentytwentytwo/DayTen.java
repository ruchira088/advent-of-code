package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayTen implements JavaSolution {
    interface Instruction {
        int cycles();

        record Add(int value) implements Instruction {

            @Override
            public int cycles() {
                return 2;
            }
        }

        record Noop() implements Instruction {

            @Override
            public int cycles() {
                return 1;
            }
        }

        static Instruction parse(String line) {
            String[] strings = line.split(" ");

            if (strings.length == 1) {
                return new Noop();
            } else {
                return new Add(Integer.parseInt(strings[1]));
            }
        }
    }

    List<Long> range(long startInclusive, long endExclusive) {
        ArrayList<Long> numbers = new ArrayList<>();

        for (int i = 0; i < endExclusive - startInclusive; i++) {
            numbers.add(startInclusive + i);
        }

        return numbers;
    }

    @Override
    public Object solve(Stream<String> input) {
        List<Instruction> instructions = parse(input);
        List<Long> values = new ArrayList<>();

        long cycle = 1;
        long registerValue = 1;

        for (Instruction instruction : instructions) {
            long start = cycle;

            long end = cycle + instruction.cycles();

            long current = registerValue;

            range(start, end).stream()
                    .filter(number -> Set.of(20L, 60L, 100L, 140L, 180L, 220L).contains(number))
                    .findFirst()
                    .ifPresent(cycleNumber -> values.add(cycleNumber * current));

            if (instruction instanceof Instruction.Add add) {
                registerValue = registerValue + add.value;
            }

            cycle = end;
        }

        return values.stream().mapToLong(x -> x).sum();
    }

    List<Instruction> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        ArrayList<Instruction> instructions = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();
            instructions.add(Instruction.parse(line));
        }

        return instructions;
    }
}
