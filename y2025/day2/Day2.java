package y2025.day2;

import common.AdventSolution;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day2 extends AdventSolution {
  @Override
  public Object part1Solution() {
    return Arrays.stream(getInput().findFirst().get().split(","))
            .mapToLong(this::addInvalidIds)
            .sum();
  }

  private long addInvalidIds(String range) {
    var rangeParts = range.split("-");
    var from = Long.parseLong(rangeParts[0]);
    var to = Long.parseLong(rangeParts[1]);
    return LongStream.rangeClosed(from, to).filter(this::isInvalid).sum();
  }

  private boolean isInvalid(long id) {
    var length = ("" + id).length();
    if (length % 2 != 0) {
      return false;
    }
    var mask = (long) Math.pow(10, length / 2);
    return id / mask == id % mask;
  }

  @Override
  public Object part2Solution() {
    return Arrays.stream(getInput().findFirst().get().split(","))
            .mapToLong(this::addInvalidIds2)
            .sum();
  }

  private long addInvalidIds2(String range) {
    var rangeParts = range.split("-");
    var from = Long.parseLong(rangeParts[0]);
    var to = Long.parseLong(rangeParts[1]);
    return LongStream.rangeClosed(from, to).filter(this::isInvalid2)
//            .peek(System.out::println)
            .sum();
  }

  private boolean isInvalid2(long id) {
    var length = ("" + id).length();
    return IntStream.rangeClosed(1, length / 2).filter(patternLength -> length % patternLength == 0)
            .anyMatch(patternLength -> isRepeated(id, patternLength));
  }

  private boolean isRepeated(long id, int patternLength) {
    var idTxt = "" + id;
    var pattern = idTxt.substring(0, patternLength);
    return idTxt.equals(pattern.repeat(idTxt.length() / patternLength));
  }

  public static void main(String[] args) {
    new Day2().solve();
  }
}
