package y2022.day9;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import common.Direction;
import common.Input;
import common.Point;

public class Day9 {

	public static void main(String[] args) {
		var rope = new Rope(2);
		Input.parseLines("y2022/day9/day9.txt", Command::fromString, rope::apply);
		System.out.println(rope.getVisitedCount());
		rope = new Rope(10);
		Input.parseLines("y2022/day9/day9.txt", Command::fromString, rope::apply);
		System.out.println(rope.getVisitedCount());

	}

	private static record Command(Direction direction, int times) {
		private static final Pattern CMD_PATTERN = Pattern.compile("([ULDR]) (\\d+)");

		public static Command fromString(String cmdStr) {
			var cmdMatcher = CMD_PATTERN.matcher(cmdStr);
			if (!cmdMatcher.matches()) {
				throw new IllegalArgumentException(cmdStr);
			}
			var direction = switch (cmdMatcher.group(1)) {
			case "U" -> Direction.NORTH;
			case "D" -> Direction.SOUTH;
			case "L" -> Direction.WEST;
			case "R" -> Direction.EAST;
			default -> throw new IllegalArgumentException("Unexpected value: " + cmdMatcher.group(1));
			};
			return new Command(direction, Integer.parseInt(cmdMatcher.group(2)));
		}
	}

	private static class Rope {
		private List<Point> knots = new ArrayList<>();

		private final Set<Point> visited = new HashSet<>();

		public Rope(int size) {
			IntStream.range(0, size).forEach(i -> knots.add(new Point(0, 0)));
			visited.add(knots.get(knots.size() - 1));
		}

		public void apply(Command command) {
			for (int i = 0; i < command.times(); i++) {
				knots.set(0, knots.get(0).move(command.direction()));
				for (int j = 1; j < knots.size(); j++) {
					if (!knots.get(j).getNeighbours().contains(knots.get(j - 1))) {
						var head = knots.get(j - 1);
						var tail = knots.get(j);
						tail = tail.move((long) Math.signum(head.getX() - tail.getX()),
								(long) Math.signum(head.getY() - tail.getY()));
						knots.set(j, tail);
					}
				}
				visited.add(knots.get(knots.size() - 1));
			}
		}

		public long getVisitedCount() {
			return visited.size();
		}
	}
}
