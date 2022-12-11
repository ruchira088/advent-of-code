package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class DayThree implements JavaSolution {
    record Rucksack(List<Character> items, int size) {
        List<Character> one() {
            return items.subList(0, size / 2);
        }

        List<Character> two() {
            return items.subList(size / 2, size);
        }

        Set<Character> common() {
            Set<Character> two = Set.copyOf(two());
            Set<Character> result = new HashSet<>();

            for (Character character : one()) {
                if (two.contains(character)) {
                    result.add(character);
                }
            }

            return result;
        }
    }

    private int priority(char character) {
        if (character < 97) {
            return character - 38;
        } else {
            return character - 96;
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        long total = 0;

        while (iterator.hasNext()) {
            String line = iterator.next();

            Rucksack rucksack = parse(line);

            for (Character character : rucksack.common()) {
                total += priority(character);
            }
        }

        return total;
    }

    Rucksack parse(String line) {
        List<Character> characters = line.trim().chars()
                .mapToObj(character -> Character.valueOf((char) character))
                .toList();

        return new Rucksack(characters, characters.size());
    }
}
