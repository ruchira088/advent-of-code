package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.List;
import java.util.stream.Stream;

public class DaySix implements JavaSolution {
    record Race(int time, int record) {
    }

    @Override
    public Object solve(Stream<String> __) {
        List<Race> races =
                List.of(
//                        new Race(7, 9),
//                        new Race(15, 40),
//                        new Race(30, 200)
                        new Race(55, 401),
                        new Race(99, 1485),
                        new Race(97, 2274),
                        new Race(93, 1405)

                );

        long result = 1;

        for (Race race : races) {
            result = result * distances(race);
        }

        return result;
    }

    int distances(Race race) {
        int count = 0;

        for (int i = 0; i <= race.time; i++) {
            int speed = i;
            int distance = speed * (race.time - i);

            if (distance > race.record) {
                count++;
            }
        }

        return count;
    }
}
