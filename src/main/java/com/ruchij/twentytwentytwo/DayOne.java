package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class DayOne implements JavaSolution {

    @Override
    public Object solve(Stream<String> input) {
        PriorityQueue<Long> calorieCount = new PriorityQueue<>(Comparator.reverseOrder());
        long currentElfCalories = 0;

        Iterator<String> iterator = input.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next().trim();

            if (line.isEmpty()) {
                calorieCount.add(currentElfCalories);
                currentElfCalories = 0;
            } else {
                long foodItem = Long.parseLong(line);
                currentElfCalories += foodItem;
            }
        }

        calorieCount.add(currentElfCalories);

        long total = 0;

        for (int i = 0; i < 3; i++) {
            total += calorieCount.poll();
        }

        return total;
    }
}
