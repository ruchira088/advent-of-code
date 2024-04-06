package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayFifteen implements JavaSolution {
    sealed interface Instruction {
        String label();
    }

    record Remove(String label) implements Instruction {}

    private final class Lens implements Instruction {
        private String label;
        private int focalLength;

        Lens(String label, int focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }

        @Override
        public String label() {
            return label;
        }

        public int focalLength() {
            return focalLength;
        }

        public void setFocalLength(int focalLength) {
            this.focalLength = focalLength;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Lens) obj;
            return Objects.equals(this.label, that.label) &&
                    this.focalLength == that.focalLength;
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, focalLength);
        }

        @Override
        public String toString() {
            return "Lens[" +
                    "label=" + label + ", " +
                    "focalLength=" + focalLength + ']';
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        Map<Integer, List<Lens>> boxes = new HashMap<>();

        input.flatMap(line -> Arrays.stream(line.split(",")))
                .map(this::parse)
                .forEach(instruction -> {
                    int boxId = hash(instruction.label());
                    List<Lens> lenses = boxes.computeIfAbsent(boxId, __ -> new ArrayList<>());
                    int index = -1;

                    for (int i = 0; i < lenses.size(); i++) {
                        Lens lens = lenses.get(i);

                        if (lens.label.equals(instruction.label())) {
                            index = i;
                            break;
                        }
                    }

                    boolean found = index != -1;

                    if (instruction instanceof Remove) {
                        if (found) {
                            lenses.remove(index);
                        }
                    } else if (instruction instanceof Lens) {
                        Lens lens = (Lens) instruction;

                        if (found) {
                            lenses.get(index).setFocalLength(lens.focalLength);
                        } else {
                            lenses.add(lens);
                        }
                    }
                });

        long focusingPower = 0;

        for (Map.Entry<Integer, List<Lens>> entry : boxes.entrySet()) {
            List<Lens> lenses = entry.getValue();
            for (int i = 0; i < lenses.size(); i++) {
                int power = (entry.getKey() + 1) * (i + 1) * lenses.get(i).focalLength;
                focusingPower += power;
            }
        }

        return focusingPower;
    }

    Instruction parse(String input) {
        if (input.contains("=")) {
            String[] strings = input.split("=");
            return new Lens(strings[0], Integer.parseInt(strings[1]));
        } else {
            return new Remove(input.substring(0, input.length() - 1));
        }
    }

    int hash(String input) {
        int value = 0;

        for (int c : input.toCharArray()) {
            value += c;
            value *= 17;
            value = value % 256;
        }

        return value;
    }
}
