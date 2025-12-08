package y2025.day1;

import common.AdventSolution;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

public class Day1 extends AdventSolution {
  @Override
  public Object part1Solution() {
    var rotations = getInput().map(value -> {
              var number = Integer.parseInt(value.substring(1));
              return value.startsWith("L") ? -1 * number : number;
            })
            .toList();
    var current = 50;
    var zeroCount = 0;
    for (var rotation: rotations) {
      current += rotation + 100;
      current %= 100;
      if (current == 0) {
        zeroCount++;
      }
    }
    return zeroCount;
  }

  @Override
  public Object part2Solution() {
    var rotations = getInput().map(value -> {
              var number = Integer.parseInt(value.substring(1));
              return value.startsWith("L") ? -1 * number : number;
            })
            .collect(Collectors.toCollection(ArrayDeque::new));
    var current = 50;
    var zeroCount = 0;
    while (!rotations.isEmpty()) {
      var rotation = rotations.remove();
      var toRotate = rotation;
      if (current == 0) {
        if (Math.abs(rotation) >= 100) {
          toRotate = rotation > 0 ? 100 : -100;
          rotations.addFirst(rotation - toRotate);
        }
      } else if (rotation > 0) {
        if (100 - current < rotation) {
          toRotate = 100 - current;
          rotations.addFirst(rotation - toRotate);
        }
      } else {
        if (current + rotation < 0) {
          toRotate = -1 * current;
          rotations.addFirst(rotation - toRotate);
        }
      }
      if (toRotate == 0) {
        continue;
      }
      current += toRotate + 100;
      current %= 100;
      if (current == 0) {
        zeroCount++;
      }
    }
    return zeroCount;
  }

  public static void main(String[] args) {
    new Day1().solve();
  }
}
