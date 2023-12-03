package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class DayTwenty implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        List<Integer> numbers = parse(input);
        List<Integer> result = new ArrayList<>(numbers);

        int count = numbers.size();

        for (Integer number : numbers) {
            int index = result.indexOf(number);
            int offset = index + (number % (count - 1));
            int position = (offset >= count ? (offset + 1) : (offset >= 0 ? offset : (count + offset - 1))) % count;

            if (position < index) {
                List<Integer> integers = new ArrayList<>(result.subList(0, position));
                integers.add(number);
                integers.addAll(result.subList(position, index));
                integers.addAll(result.subList(index + 1, count));

                result = integers;
            } else if (position > index) {
                List<Integer> integers = new ArrayList<>(result.subList(0, index));
                integers.addAll(result.subList(index + 1, position + 1));
                integers.add(number);
                integers.addAll(result.subList(position + 1, count));

                result = integers;
            }

            System.out.println(number);
            System.out.println(result);
        }

        int zeroIndex = result.indexOf(0);
        int answer = 0;

        for (Integer position : Set.of(1000, 2000, 3000)) {
            answer += result.get((zeroIndex + position) % count);
        }

        return answer;
    }

    List<Integer> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        ArrayList<Integer> integers = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();
            int number = Integer.parseInt(line);
            integers.add(number);
        }

        return integers;
    }
}
