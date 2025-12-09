package y2025.day5;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day5 extends AdventSolution {
  private record Range(long start, long end) {
    public boolean contains(long value) {
      return start <= value && value <= end;
    }

    public boolean overlaps(Range other) {
      var firstEnd = start < other.start ? end : other.end;
      var secondStart = Math.max(start, other.start);
      return firstEnd >= secondStart;
    }

    public Range merge(Range other) {
      if (!overlaps(other)) {
        throw new IllegalArgumentException("Cannot merge non overlapping " + other + " with " + this);
      }
      return new Range(Math.min(start, other.start), Math.max(end, other.end));
    }

    public long size() {
      return end - start + 1;
    }

    public static Range fromString(String input) {
      var pattern = Pattern.compile("(\\d+)-(\\d+)");
      var matcher = pattern.matcher(input);
      if (!matcher.matches()) {
        throw new IllegalArgumentException(input);
      }
      var start = Long.parseLong(matcher.group(1));
      var end = Long.parseLong(matcher.group(2));
      return new Range(start, end);
    }
  }

  @Override
  public Object part1Solution() {
    var input = getInput().toList();
    var separator = input.indexOf("");
    var rangesInput = input.subList(0, separator);
    var freshRanges = rangesInput.stream().map(Range::fromString).toList();
    var ingredients = input.subList(separator + 1, input.size()).stream().map(Long::valueOf).toList();

    return ingredients.stream()
            .filter(ingredient -> freshRanges.stream().anyMatch(range -> range.contains(ingredient)))
            .count();
  }

  @Override
  public Object part2Solution() {
    var input = getInput().toList();
    var separator = input.indexOf("");
    var rangesInput = input.subList(0, separator);
    var freshRanges = rangesInput.stream()
            .map(Range::fromString)
            .toList();

    var nonOverlappingRanges = new HashSet<Range>();

    freshRanges.forEach(range -> {
      var overlappingRanges = nonOverlappingRanges.stream()
              .filter(r -> r.overlaps(range))
              .collect(Collectors.toSet());
      nonOverlappingRanges.removeAll(overlappingRanges);
      var rangeToAdd = overlappingRanges.stream()
              .reduce(range, Range::merge);
      nonOverlappingRanges.add(rangeToAdd);
    });
    return nonOverlappingRanges.stream().mapToLong(Range::size).sum();
  }

  public static void main(String[] args) {
    new Day5().solve();
  }
}
