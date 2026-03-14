package y2025.day6;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day6 extends AdventSolution {
  @Override
  public Object part1Solution() {
    var input = getInput()
            .map(this::parseLine)
            .toList();
    var sum = 0L;
    for (int i = 0 ; i < input.getFirst().size() ; i++) {
      var op = input.getLast().get(i);
      var finalI = i;
      var value = input.reversed().stream().skip(1).map(row -> row.get(finalI))
              .map(Long::valueOf)
              .reduce((a,b) -> op.equals("+") ? a + b : a * b )
              .get();
      sum += value;
    }
    return sum;
  }

  private List<String> parseLine(String line) {
    var trimmed = line.replaceAll("\\s+", " ").trim();
    return Arrays.asList(trimmed.split(" "));
  }

  @Override
  public Object part2Solution() {
    var input = getInput()
            .toList();
    var ops = input.getLast().replaceAll(" ", "");
    var numbersInput = input.subList(0, input.size() - 1);
    var numbers = new ArrayList<List<Long>>();
    var currentNumbers = new ArrayList<Long>();
    var maxLength = numbersInput.stream().mapToInt(String::length).max().getAsInt();
    for (int i = 0; i < maxLength; i++) {
      var numberBuilder = new StringBuilder();
      var finalI = i;
      numbersInput.stream()
              .map(line -> finalI >= line.length() ? ' ' : line.charAt(finalI))
              .forEach(numberBuilder::append);
      var numberStr = numberBuilder.toString().replaceAll(" ", "");

      if (!numberStr.isEmpty()) {
        var number = Long.parseLong(numberBuilder.toString().replaceAll(" ", ""));
        currentNumbers.add(number);
      } else {
        numbers.add(currentNumbers);
        currentNumbers = new ArrayList<>();
      }
    }
    numbers.add(currentNumbers);
    var sum = 0L;
    for (var i = 0 ; i < numbers.size() ; i++) {
      var finalI = i;
      var value = numbers.get(i).stream().reduce((a,b) -> ops.charAt(finalI) == '*' ? a* b : a + b).get();
      sum += value;
    }
    return sum;
  }

  public static void main(String[] args) {
    new Day6().solve();
  }
}
