package y2023.day18;

import common.AdventSolution;
import common.Area;
import common.Direction;
import common.Pair;
import common.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day18 extends AdventSolution {

	public static void main(String[] args) {
		new Day18().solve();
	}

  private final Area area = new Area();
	@Override
	public Object part1Solution() {
		var commands = getInput().map(DigCommand::fromString).toList();
		var edges = new HashSet<DigResult>();
		var current = new Point(0, 0);
		for (var command : commands) {
			var digResult = command.dig(current);
			current = digResult.endPosition();
			edges.add(digResult);
		}
    return getSubAreasArea(getSubAreas(edges));
  }

  private long getSubAreasArea(Set<Pair<Point>> subAreas) {
    return subAreas.stream()
            .mapToLong(subArea -> {
              var x = Math.abs(subArea.getFirst().getX() - subArea.getSecond().getX()) + 1;
              var y = Math.abs(subArea.getFirst().getY() - subArea.getSecond().getY()) + 1;
              return x * y;
            })
            .sum();
  }


	@Override
	public Object part2Solution() {
		var commands = getInput().map(DigCommand::fromString).toList();
		var edges = new HashSet<DigResult>();
		var current = new Point(0, 0);
		for (var command : commands) {
			var digResult = command.part2Converted().dig(current);
			current = digResult.endPosition();
			edges.add(digResult);
		}
    return getSubAreasArea(getSubAreas(edges));
  }

  private Set<Pair<Point>> getSubAreas(HashSet<DigResult> edges) {
    return area.getSubAreas(edges.stream().map(DigResult::toPointPair).collect(Collectors.toSet()));
  }

  private record DigResult(Point startPosition, Point endPosition) {
    public Pair<Point> toPointPair() {
      return Pair.of(startPosition, endPosition);
    }
	}

	private record DigCommand(Direction direction, long steps, String color) {
		public static DigCommand fromString(String commandDescription) {
			var digPattern = Pattern.compile("([RLUD]) (\\d+) \\(#([a-f0-9]{6})\\)");
			var digCommandMatcher = digPattern.matcher(commandDescription);
			if (!digCommandMatcher.matches()) {
				throw new IllegalArgumentException(commandDescription);
			}
			var direction = switch (digCommandMatcher.group(1)) {
			case "U" -> Direction.UP;
			case "D" -> Direction.DOWN;
			case "L" -> Direction.LEFT;
			case "R" -> Direction.RIGHT;
			default -> throw new IllegalArgumentException("Unsupported direction symbol " + digCommandMatcher.group(1));
			};
			return new DigCommand(direction, Long.parseLong(digCommandMatcher.group(2)), digCommandMatcher.group(3));
		}

		public DigCommand part2Converted() {
			var direction = switch (color().substring(5)) {
			case "0" -> Direction.RIGHT;
			case "1" -> Direction.DOWN;
			case "2" -> Direction.LEFT;
			case "3" -> Direction.UP;
			default -> throw new IllegalArgumentException(color());
			};
			var step = hexToDec(color().substring(0, 5));
			return new DigCommand(direction, step, "");
		}

		private long hexToDec(String hex) {
			var result = 0L;
			for (int i = 0; i < 5; i++) {
				var value = switch (hex.charAt(i)) {
				case '0' -> 0;
				case '1' -> 1;
				case '2' -> 2;
				case '3' -> 3;
				case '4' -> 4;
				case '5' -> 5;
				case '6' -> 6;
				case '7' -> 7;
				case '8' -> 8;
				case '9' -> 9;
				case 'a' -> 10;
				case 'b' -> 11;
				case 'c' -> 12;
				case 'd' -> 13;
				case 'e' -> 14;
				case 'f' -> 15;
				default -> throw new IllegalArgumentException("Unexpected value: " + hex.charAt(i));
				};
				result += (long) (Math.pow(16, 4 - i) * value);
			}
			return result;
		}

		public DigResult dig(Point from) {
			return new DigResult(from, from.move(direction, steps));
		}
	}
}
