package y2025.day12;

import common.AdventSolution;
import common.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Day12 extends AdventSolution {
  private static final Pattern PRESENT_HEADER_PATTERN = Pattern.compile("[0-9]*:");
  @Override
  public Object part1Solution() {
    var input = getInput().toList();
    var lineIdx = 0;
    var presents = new ArrayList<Set<Point>>();
    while (isPresentHeader(input.get(lineIdx))) {
      var present = new HashSet<Point>();
      for (var x = 0 ; x < 3 ; x++) {
        for (var y = 0 ; y < 3 ; y++) {
          if (input.get(lineIdx + y + 1).charAt(x) == '#') {
            present.add(new Point(x, y));
          }
        }
      }
      presents.add(present);
      lineIdx += 5;
    }
    var areas = input.subList(lineIdx, input.size())
            .stream()
            .map(Area::fromString)
            .toList();
    System.out.println(areas.stream().filter(Area::isClearlyPossible).count());
    System.out.println(areas.stream().filter(a -> a.isClearlyImpossible(presents)).count());
    System.out.println(areas.size());
    return null;
  }

  private record Area(int width, int height, List<Integer> presents) {
    public static Area fromString(String areaDesc) {
      var dimensions = areaDesc.split(":")[0].split("x");
      var width = Integer.parseInt(dimensions[0]);
      var height = Integer.parseInt(dimensions[1]);
      var presents = Arrays.stream(areaDesc.split(":")[1].split(" "))
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .map(Integer::parseInt)
              .toList();
      return new Area(width, height, presents);
    }
    public boolean isClearlyPossible() {
      return presents.stream().mapToInt(Integer::intValue).sum() <= (width / 3) * (height / 3);
    }
    public boolean isClearlyImpossible(List<Set<Point>> presentTemplates) {
      var requiredFields = 0;
      for (int i = 0; i < presentTemplates.size(); i++) {
        requiredFields += presentTemplates.get(i).size() * presents.get(i);
      }
      return requiredFields > width * height;
    }
  }

  private boolean isPresentHeader(String line) {
    return PRESENT_HEADER_PATTERN.matcher(line).matches();
  }

  @Override
  public Object part2Solution() {
    return null;
  }

  public static void main(String[] args) {
    new Day12().solve();
  }
}
