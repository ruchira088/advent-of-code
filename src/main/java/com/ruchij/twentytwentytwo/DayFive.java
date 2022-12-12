package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DayFive implements JavaSolution {
    private record Instruction(int itemCount, int from, int to) {
    }

    @Override
    public Object solve(Stream<String> input) {
        Iterator<String> iterator = input.iterator();

        List<Stack<Character>> stacks = parseStacks(iterator);
        List<Instruction> instructions = parseInstructions(iterator);

        for (Instruction instruction : instructions) {
            perform(stacks, instruction);
        }

        return top(stacks);
    }

    String top(List<Stack<Character>> stacks) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Stack<Character> stack : stacks) {
            stringBuilder.append(stack.pop());
        }

        return stringBuilder.toString();
    }

    List<Stack<Character>> parseStacks(Iterator<String> iterator) {
        List<Stack<Character>> stacks = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();

            if (!line.isEmpty()) {
                char[] chars = line.toCharArray();

                for (int i = 1, j = 0; i < chars.length; i += 4, j++) {
                    if (stacks.size() <= j) {
                        stacks.add(new Stack<>());
                    }

                    char character = chars[i];

                    if (Character.isDigit(character)) {
                        return stacks;
                    } else if (chars[i] != ' ') {
                        Stack<Character> stack = stacks.get(j);
                        stack.add(0, chars[i]);
                    }
                }
            }
        }

        return stacks;
    }

    List<Instruction> parseInstructions(Iterator<String> iterator) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();

            if (!line.isEmpty()) {
                instructions.add(parseInstruction(line));
            }
        }

        return instructions;
    }

    Instruction parseInstruction(String line) {
        String regex = "move (\\d+) from (\\d+) to (\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            int itemsCount = Integer.parseInt(matcher.group(1));
            int from = Integer.parseInt(matcher.group(2));
            int to = Integer.parseInt(matcher.group(3));

            return new Instruction(itemsCount, from, to);
        }

        throw new IllegalArgumentException("Unable to parse '%s' as Instruction".formatted(line));
    }

    void perform(List<Stack<Character>> stacks, Instruction instruction) {
        Stack<Character> from = stacks.get(instruction.from - 1);
        Stack<Character> to = stacks.get(instruction.to - 1);

        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < instruction.itemCount; i++) {
            stack.push(from.pop());
        }

        while (!stack.isEmpty()) {
            to.push(stack.pop());
        }
    }
}
