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
        List<Boolean> pixels = new ArrayList<>();

        long cycle = 1;
        long registerValue = 1;
        long pixel = 0;

        for (Instruction instruction : instructions) {
            long start = cycle;
            long end = start + instruction.cycles();
            long endPixel = pixel + instruction.cycles();

            for (long i = pixel; i < endPixel; i++) {
                boolean isLit = Set.of(registerValue - 1, registerValue, registerValue + 1).contains(pixel % 40);
                pixels.add(isLit);
                pixel++;
            }

            if (instruction instanceof Instruction.Add add) {
                registerValue = registerValue + add.value;
            }

            cycle = end;
        }

        screen(pixels);

        return "";
    }

    void screen(List<Boolean> pixels) {
        int index = 0;
        int size = pixels.size();

        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 40; x++) {
                if (size <= index) {
                    System.out.print('O');
                } else if (pixels.get(index)) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }

                index++;
            }

            System.out.println();
        }
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
