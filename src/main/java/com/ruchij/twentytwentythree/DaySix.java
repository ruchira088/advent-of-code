package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.stream.Stream;

public class DaySix implements JavaSolution {
    record Race(long time, long record) {
    }

    @Override
    public Object solve(Stream<String> __) {
//        Race race = new Race(71530, 940200);
        Race race = new Race(55999793, 401148522741405L);
        return distances(race);
    }

    long distances(Race race) {
        long count = 0;

        for (long i = 0; i <= race.time; i++) {
            long speed = i;
            long distance = speed * (race.time - i);

            if (distance > race.record) {
                count++;
            }
        }

        return count;
    }
}
