package y2025.day3;

import common.AdventSolution;

public class Day3 extends AdventSolution {
  private record ValueAt(int index, int value) {
  }

  @Override
  public Object part1Solution() {
    return getInput()
            .mapToLong(batteries -> getMaxJoltage(batteries, 2)).sum();
  }

  public long getMaxJoltage(String batteries, int numberOfBatteries) {
    var joltage = 0L;
    var batteriesFound = 0;
    var startIdx = 0;
    while (batteriesFound < numberOfBatteries) {
      var maxBattery = getMaxBatteryAt(batteries, startIdx, batteries.length() - (numberOfBatteries - batteriesFound - 1));
      joltage *= 10L;
      joltage += maxBattery.value;
      startIdx = maxBattery.index + 1;
      batteriesFound++;
    }
    return joltage;
//    var maxIdx = getMaxBatteryIdx(batteries);
//    if (maxIdx.index == batteries.length() - 1) {
//      var firstMaxIdx = getMaxBatteryIdx(batteries, 0, batteries.length() - 1);
//      return firstMaxIdx.value * 10L + maxIdx.value;
//    } else {
//      var secondMaxIdx = getMaxBatteryIdx(batteries, maxIdx.index + 1);
//      return maxIdx.value * 10L + secondMaxIdx.value;
//    }
  }

  private ValueAt getMaxBatteryAt(String batteries, int startIndex, int endIndex) {
    int max = 0;
    int maxIdx = startIndex;
    for (int i = startIndex; i < endIndex; i++) {
      int batteryValue = valueAt(batteries, i);
      if (batteryValue > max) {
        max = batteryValue;
        maxIdx = i;
      }
    }
    return new ValueAt(maxIdx, max);
  }

  private int valueAt(String batteries, int idx) {
    return Integer.parseInt(batteries.substring(idx, idx + 1));
  }

  @Override
  public Object part2Solution() {
    return getInput()
            .mapToLong(batteries -> getMaxJoltage(batteries, 12)).sum();
  }

  public static void main(String[] args) {
    new Day3().solve();
  }
}
