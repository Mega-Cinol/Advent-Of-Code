package y2024.day11;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Day11 extends AdventSolution {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    private record Stone(long value) {
        public List<Stone> next() {
            var digits = countDigits();
            if (value == 0) {
                return List.of(new Stone(1));
            }
            if (digits % 2 == 0) {
                var splitValue = (long) Math.pow(10, digits / 2);
                var left = value / splitValue;
                var right = value % splitValue;
                return List.of(new Stone(left), new Stone(right));
            } else {
                return List.of(new Stone(value * 2024));
            }
        }
        private int countDigits() {
            var currentValue = value;
            var digits = 0;
            while (currentValue != 0) {
                currentValue /= 10;
                digits++;
            }
            return digits;
        }
    }
    @Override
    public Object part1Solution() {
        var input = getInput().toList().get(0);
        var matcher = NUMBER_PATTERN.matcher(input);
        var stones = new ArrayList<Stone>();
        while (matcher.find()) {
            stones.add(new Stone(Long.parseLong(matcher.group())));
        }
        for (int i = 0; i < 25; i++) {
            var nextStones = new ArrayList<Stone>();
            stones.stream().map(Stone::next).forEach(nextStones::addAll);
            stones = nextStones;
        }
        return stones.size();
    }

    @Override
    public Object part2Solution() {
        var input = getInput().toList().get(0);
        var matcher = NUMBER_PATTERN.matcher(input);
        var stones = new HashMap<Stone, Long>();
        while (matcher.find()) {
            stones.merge(new Stone(Long.parseLong(matcher.group())), 1L, Long::sum);
        }
        for (int i = 0; i < 75; i++) {
            var nextStones = new HashMap<Stone, Long>();
            stones.forEach((key, value) -> {
                var nextStone = key.next();
                nextStone.forEach(stone -> nextStones.merge(stone, value, Long::sum));
            });
            stones = nextStones;
        }
        return stones.values().stream().reduce(0L, Long::sum);
    }

    public static void main(String[] args) {
        new Day11().solve();
    }
}
