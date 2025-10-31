package y2024.day25;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.List;

public class Day25 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var keys = new ArrayList<List<Integer>>();
        var locks = new ArrayList<List<Integer>>();
        var input = getInput().toList();
        for (var i = 0; i < input.size(); i += 8) {
            if (input.get(i).equals(".....")) {
                var pins = new ArrayList<Integer>();
                pins.add(0);
                pins.add(0);
                pins.add(0);
                pins.add(0);
                pins.add(0);
                for (var j = i + 5 ; j > i ; j--) {
                    for (var pos = 0 ; pos < 5 ; pos++) {
                        if (input.get(j).charAt(pos) == '#') {
                            pins.set(pos, pins.get(pos) + 1);
                        }
                    }
                }
                keys.add(pins);
            } else {
                var pins = new ArrayList<Integer>();
                pins.add(0);
                pins.add(0);
                pins.add(0);
                pins.add(0);
                pins.add(0);
                for (var j = i + 1 ; j < i + 7 ; j++) {
                    for (var pos = 0 ; pos < 5 ; pos++) {
                        if (input.get(j).charAt(pos) == '#') {
                            pins.set(pos, pins.get(pos) + 1);
                        }
                    }
                }
                locks.add(pins);
            }
        }
        var matching = 0L;
        for (var key : keys) {
            for (var lock : locks) {
                var overlaping = false;
                for (var pin = 0 ; pin < 5 ; pin++) {
                    if (lock.get(pin) + key.get(pin) > 5) {
                        overlaping = true;
                        break;
                    }
                }
                if (!overlaping) {
                    matching++;
                }
            }
        }
        return matching;
    }

    @Override
    public Object part2Solution() {
        return "N/A";
    }

    public static void main(String[] args) {
        new Day25().solve();
    }
}
