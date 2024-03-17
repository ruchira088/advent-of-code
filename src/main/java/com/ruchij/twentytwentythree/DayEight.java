package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
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
        private String label;
        private Node left;
        private Node right;

        Node(String label) {
            this.label = label;
        }

        public void setLabel(String label) {
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

    record Game(List<Instruction> instructions, Node[] startNodes) {}

    @Override
    public Object solve(Stream<String> input) {
        Game game = parse(input);
        List<Long> cycles = cycles(game);

        System.out.println(cycles);

        return lcm(cycles);
    }

    private List<Long> cycles(Game game) {
        Map<Integer, Long> map = new HashMap<>();
        Node[] nodes = Arrays.copyOf(game.startNodes, game.startNodes.length);
        long count = 0;

        while (map.keySet().size() != nodes.length) {
            for (Instruction instruction : game.instructions) {
                for (int i = 0; i < nodes.length; i++) {
                    Node node = nodes[i];

                    if (node.getLabel().endsWith("Z") && !map.containsKey(i)) {
                        map.put(i, count);
                    } else if (!map.containsKey(i)) {
                        Node next = instruction == Instruction.LEFT ? node.left : node.right;
                        nodes[i] = next;
                    }
                }

                count++;
            }
        }

        return List.copyOf(map.values());
    }

    private Map<Long, Long> factors(long number) {
        HashMap<Long, Long> factors = new HashMap<>();
        double end = Math.ceil(Math.sqrt(number));
        long current = number;
        int i = 2;

        while (i <= Math.min(end, current)) {
            if (current % i == 0) {
                factors.put((long) i, factors.getOrDefault((long) i, 0L) + 1);
                current = current / i;
            } else {
                i++;
            }
        }

        if (current != 1) {
            factors.put(current, 1L);
        }

        return factors;
    }

    long lcm(List<Long> numbers) {
        long result = 1;
        Map<Long, Long> values = new HashMap<>();

        for (Long number : numbers) {
            Map<Long, Long> factors = factors(number);

            for (Map.Entry<Long, Long> entry : Set.copyOf(factors.entrySet())) {
                long value = Math.max(values.getOrDefault(entry.getKey(), 0L), entry.getValue());
                values.put(entry.getKey(), value);
            }
        }

        for (Map.Entry<Long, Long> entry : values.entrySet()) {
            result = result * ((long) Math.pow(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    private Game parse(Stream<String> input) {
        List<String> inputList = new ArrayList<>(input.toList());
        List<Instruction> instructions =
                Arrays.stream(inputList.getFirst().split("")).map(Instruction::parse).toList();

        inputList.removeFirst();
        inputList.removeFirst();

        HashMap<String, Node> nodeHashMap = new HashMap<>();
        ArrayList<Node> startNodes = new ArrayList<>();

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

                if (value.endsWith("A")) {
                    startNodes.add(node);
                }

                node.setLeft(leftNode);
                node.setRight(rightNode);
            } else {
                throw new RuntimeException("Invalid input: " + line);
            }
        }

        Game game = new Game(instructions, startNodes.toArray(size -> new Node[size]));

        return game;
    }
}
