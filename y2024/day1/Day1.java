package y2024.day1;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Day1 extends AdventSolution {
    private static final Pattern LINE_PATTERN = Pattern.compile("(\\d+)\\s*(\\d+)");

    @Override
    public Object part1Solution() {
        var list1 = new ArrayList<Integer>();
        var list2 = new ArrayList<Integer>();
        getInput().forEach(line -> {
            var matcher = LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(line);
            }
            var first = Integer.parseInt(matcher.group(1));
            var second = Integer.parseInt(matcher.group(2));
            list1.add(first);
            list2.add(second);
        });
        Collections.sort(list1);
        Collections.sort(list2);
        var diffSum = 0L;
        for (var i = 0 ; i < list1.size() ; i++) {
            diffSum += Math.abs(list1.get(i) - list2.get(i));
        }

        return diffSum;
    }

    @Override
    public Object part2Solution() {
        var list1 = new ArrayList<Long>();
        var occurencesList = new HashMap<Long, Long>();
        getInput().forEach(line -> {
            var matcher = LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(line);
            }
            var first = Long.parseLong(matcher.group(1));
            var second = Long.parseLong(matcher.group(2));
            list1.add(first);
            occurencesList.merge(second, 1L, Long::sum);
        });
        return list1.stream()
                .mapToLong(num -> num * occurencesList.getOrDefault(num, 0L))
                .sum();
    }

    public static void main(String[] args) {
        new Day1().solve();
    }
}
