package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class DayFour implements JavaSolution {
    record Range(long start, long end) {
        private static final String SEPARATOR = "-";

        static Range parse(String input) {
            String[] numbers = input.split(SEPARATOR);

            long start = Long.parseLong(numbers[0].trim());
            long end = Long.parseLong(numbers[1].trim());

            return new Range(start, end);
        }

        boolean contains(Range other) {
            return compare(this, other) || compare(other, this);
        }

        private static boolean compare(Range range, Range other) {
            return range.start <= other.start && range.end >= other.end;
        }
    }

    record Pair<A, B>(A first, B second) {}

    @Override
    public Object solve(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        ArrayList<Pair<Range, Range>> elfPairs = new ArrayList<>();
        long count = 0;

        while (iterator.hasNext()) {
            String line = iterator.next();

            Pair<Range, Range> pair = parse(line);

            if (pair.first.contains(pair.second)) {
                count++;
            }

        }

        return count;
    }

    private Pair<Range, Range> parse(String line) {
        String[] groups = line.split(",");

        return new Pair<>(Range.parse(groups[0].trim()), Range.parse(groups[1].trim()));
    }


}
