package y2025.day3;

import common.AdventSolution;

public class Day3 extends AdventSolution {
  @Override
  public Object part1Solution() {
    return getInput()
            .peek(System.out::println)
            .mapToInt(this::getMaxJoltage).sum();
  }

  public int getMaxJoltage(String batteries) {
    var maxIdx = getMaxBatteryIdx(batteries, 0);
    var secondMaxIdx = getMaxBatteryIdx(batteries, maxIdx+1);
    return valueAt(batteries, maxIdx) * 10 + valueAt(batteries, secondMaxIdx);
  }

  private int getMaxBatteryIdx(String batteries, int startIndex) {
    int max = 0;
    int maxIdx = startIndex;
    for (int i = startIndex; i < batteries.length(); i++) {
      int batteryValue = valueAt(batteries, i);
      if (batteryValue > max) {
        max = batteryValue;
        maxIdx = i;
      }
    }
    return maxIdx;
  }

  private int valueAt(String batteries, int idx) {
    return Integer.parseInt(batteries.substring(idx, idx + 1));
  }
  @Override
  public Object part2Solution() {
    return null;
  }

  public static void main(String[] args) {
    new Day3().solve();
  }
}
