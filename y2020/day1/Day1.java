package y2020.day1;

import java.io.IOException;
import java.util.NavigableSet;
import java.util.TreeSet;

import common.AdventSolution;
import common.Input;

public class Day1 extends AdventSolution {


    @Override
    public Object part1Solution() {
        var numbers = getInput().map(Integer::parseInt).toList();
        for (var x : numbers) {
            for (var y : numbers) {
                if (x + y == 2020) {
                    return x * y;
                }
            }
        }
        return "Not found";
    }

    @Override
    public Object part2Solution() {
        NavigableSet<Integer> numbers = new TreeSet<Integer>();
        Input.parseLines("y2020/day1/day1.txt", Integer::parseInt, numbers::add);
        for (int x : numbers) {
            int y = numbers.higher(x);
            while (x + y < 2020) {
                if (numbers.contains(2020 - x - y)) {
                    return (2020 - x - y) * x * y;
                }
                y = numbers.higher(y);
            }
        }
        return "Not found";
    }

    public static void main(String[] args) throws IOException {
        new Day1().solve();
    }
}
