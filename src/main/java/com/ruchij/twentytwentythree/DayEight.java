package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DayEight implements JavaSolution {
    enum Instruction {
        RIGHT("R"), LEFT("L");

        private final String label;
        private Instruction(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        static Instruction parse(String input) {
            return Arrays.stream(Instruction.values())
                    .filter(instruction -> instruction.label.equalsIgnoreCase(input))
                    .findAny()
                    .orElseThrow();
        }
    }

    class Node {
        private final String label;
        private Node left;
        private Node right;

        Node(String label) {
            this.label = label;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public String getLabel() {
            return label;
        }
    }

    record Game(List<Instruction> instructions, Node node) {}

    @Override
    public Object solve(Stream<String> input) {
        Game game = parse(input);
        long result = stepCount(game);

        return result;
    }

    private long stepCount(Game game) {
        long count = 0;

        Node node = game.node;

        while (true) {
            for (Instruction instruction : game.instructions()) {
                if (node.getLabel().equalsIgnoreCase("ZZZ")) {
                    return count;
                } else {
                    count++;

                    if (instruction == Instruction.LEFT) {
                        node = node.left;
                    } else {
                        node = node.right;
                    }
                }
            }
        }
    }

    private Game parse(Stream<String> input) {
        List<String> inputList = new ArrayList<>(input.toList());
        List<Instruction> instructions =
                Arrays.stream(inputList.getFirst().split("")).map(Instruction::parse).toList();

        inputList.removeFirst();
        inputList.removeFirst();

        HashMap<String, Node> nodeHashMap = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\S+) = \\((\\S+), (\\S+)\\)");

        for (String line : inputList) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String value = matcher.group(1);
                String left = matcher.group(2);
                String right = matcher.group(3);

                Node node = nodeHashMap.computeIfAbsent(value, __ -> new Node(value));
                Node leftNode = nodeHashMap.computeIfAbsent(left, __ -> new Node(left));
                Node rightNode = nodeHashMap.computeIfAbsent(right, __ -> new Node(right));

                node.setLeft(leftNode);
                node.setRight(rightNode);
            } else {
                throw new RuntimeException("Invalid input: " + line);
            }
        }

        Game game = new Game(instructions, nodeHashMap.get("AAA"));

        return game;
    }
}
