package uk.co.animandosolutions.mcdev.deathcomp.utils;

import java.util.List;
import java.util.stream.Collectors;

public class StatsUtils {


    public static Double computeMedian(List<Double> it) {
        int size = it.size();
        if (size == 0) {
            return Double.NaN;
        }
        var sorted = it.stream().sorted().toList();
        if (size % 2 == 1) {
            var midPoint = sorted.get((size - 1) / 2);
            return midPoint.doubleValue();
        } else {
            var midPointIdx = (size / 2);
            var middleTwo = sorted.subList(midPointIdx - 1, midPointIdx + 1);
            return middleTwo.stream().collect(Collectors.summingDouble(e -> e.doubleValue())).doubleValue() / 2;
        }
    }

}
