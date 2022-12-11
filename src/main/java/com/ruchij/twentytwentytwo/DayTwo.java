package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class DayTwo implements JavaSolution {
    record Game(Shape opponent, Outcome outcome) {
        public Shape response() {
            if (outcome == Outcome.DRAW) {
                return opponent;
            }

            for (Shape shape : Shape.values()) {
                if (outcome == Outcome.WIN && shape.defeats(opponent)) {
                    return shape;
                }

                if (outcome == Outcome.LOSE && opponent.defeats(shape)) {
                    return shape;
                }
            }

            throw new RuntimeException("Unable to deduce response for %s".formatted(this));
        }
    }

    enum Outcome {
        WIN(6, 'Z'), DRAW(3, 'Y'), LOSE(0, 'X');

        private final int points;
        private final char character;

        Outcome(int points, char character) {
            this.points = points;
            this.character = character;
        }

        public int getPoints() {
            return points;
        }
    }

    enum Shape {
        ROCK(1, Set.of('A')), PAPER(2, Set.of('B')), SCISSORS(3, Set.of('C'));

        private final Set<Character> chars;
        private final int points;

        Shape(int points, Set<Character> chars) {
            this.points = points;
            this.chars = chars;
        }

        boolean defeats(Shape other) {
            return this == ROCK && other == SCISSORS ||
                    this == PAPER && other == ROCK ||
                    this == SCISSORS && other == PAPER;
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        long score = 0;

        while (iterator.hasNext()) {
            String line = iterator.next();
            Game game = parse(line);
            score += game.outcome().points;
            score += game.response().points;
        }

        return score;
    }

    private Game parse(String line) {
        char[] chars = line.trim().toCharArray();

        return new Game(parseShape(chars[0]), parseOutcome(chars[2]));
    }

    private Outcome parseOutcome(char character) {
        for (Outcome outcome : Outcome.values()) {
            if (outcome.character == character) {
                return outcome;
            }
        }

        throw new RuntimeException("Unable to parse '%s' as Outcome".formatted(character));
    }

    private Shape parseShape(char character) {
        for (Shape shape : Shape.values()) {
            if (shape.chars.contains(character)) {
                return shape;
            }
        }

        throw new RuntimeException("Unable to parse '%s' as Shape".formatted(character));
    }
}
