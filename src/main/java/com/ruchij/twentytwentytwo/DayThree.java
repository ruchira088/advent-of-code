package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayThree implements JavaSolution {
    record Group(List<Rucksack> rucksacks) {
        Set<Character> common() {
            Set<Character> characters = null;

            for (Rucksack rucksack: rucksacks) {
                if (characters == null) {
                    characters = rucksack.items;
                } else {
                    Set<Character> updated = new HashSet<>();

                    for (Character character : characters) {
                        if (rucksack.items.contains(character)) {
                            updated.add(character);
                        }
                    }

                    characters = updated;
                }
            }

            return characters;
        }
    }

    record Rucksack(Set<Character> items) {
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
        List<Group> groups = new ArrayList<>();

        int groupSize = 3;
        long total = 0;

        while (iterator.hasNext()) {
            List<Rucksack> rucksacks = new ArrayList<>();

            for (int i = 0; i < groupSize; i++) {
                String line = iterator.next();
                Rucksack rucksack = parseRucksack(line);
                rucksacks.add(rucksack);
            }

            groups.add(new Group(rucksacks));
        }

        for (Group group : groups) {
            for (Character character : group.common()) {
                total += priority(character);
            }
        }

        return total;
    }

    Rucksack parseRucksack(String line) {
        HashSet<Character> items = new HashSet<>();

        for (char character : line.toCharArray()) {
            items.add(character);
        }


        return new Rucksack(items);
    }
}
