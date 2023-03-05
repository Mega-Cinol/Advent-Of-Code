package y2022.day24;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Direction;
import common.Input;
import common.PathFinding;
import common.PathFinding.DistanceAndPrevious;
import common.Point;

public class Day24 {

	public static void main(String[] args) {
		var blizzardMap = Input.parseGrid("y2022/day24/day24.txt", Day24::direction);
		blizzardMap.entrySet().removeIf(e -> e.getValue() == null);
		var maxX = Point.maxX(blizzardMap.keySet());
		var minX = Point.minX(blizzardMap.keySet());
		var maxY = Point.maxY(blizzardMap.keySet());
		var minY = Point.minY(blizzardMap.keySet());
		var start = new Point(minX, minY - 1);
		var finish = new Point(maxX, maxY);

		class PossibleMovesCalculator implements Function<PlaceAndTime, Map<PlaceAndTime, Integer>>,
				Predicate<Map<PlaceAndTime, DistanceAndPrevious<PlaceAndTime>>> {
			private final Point target;
			private boolean targetVisited = false;

			public PossibleMovesCalculator(Point target) {
				this.target = target;
			}

			@Override
			public Map<PlaceAndTime, Integer> apply(PlaceAndTime t) {
				targetVisited = t.point().equals(target);
				var moves = Stream.concat(t.point().getNonDiagonalNeighbours().stream(), Stream.of(t.point()))
						.filter(p -> p.getX() >= minX).filter(p -> p.getX() <= maxX).filter(p -> p.getY() >= minY)
						.filter(p -> p.getY() <= maxY).map(place -> new PlaceAndTime(place, t.turn() + 1))
						.filter(this::isSafe).collect(Collectors.toMap(Function.identity(), k -> 1));
				if (t.point().equals(start) || t.point().equals(finish.move(Direction.DOWN))) {
					moves.put(new PlaceAndTime(t.point(), t.turn() + 1), 1);
				}
				return moves;
			}

			private boolean isSafe(PlaceAndTime location) {
				var spanX = maxX - minX + 1;
				var spanY = maxY - minY + 1;
				var upY = (location.point().getY() + location.turn() - minY) % spanY + minY;
				upY = upY <= 0 ? upY + spanY : upY;
				var up = new Point(location.point().getX(), upY);
				if (blizzardMap.get(up) == Direction.UP) {
					return false;
				}
				var downY = (location.point().getY() - location.turn() - minY) % spanY + minY;
				downY = downY <= 0 ? downY + spanY : downY;
				var down = new Point(location.point().getX(), downY);
				if (blizzardMap.get(down) == Direction.DOWN) {
					return false;
				}
				var leftX = (location.point().getX() + location.turn() - minX) % spanX + minX;
				leftX = leftX <= 0 ? leftX + spanX : leftX;
				var left = new Point(leftX, location.point().getY());
				if (blizzardMap.get(left) == Direction.LEFT) {
					return false;
				}
				var rightX = (location.point().getX() - location.turn() - minX) % spanX + minX;
				rightX = rightX <= 0 ? rightX + spanX : rightX;
				var right = new Point(rightX, location.point().getY());
				if (blizzardMap.get(right) == Direction.RIGHT) {
					return false;
				}
				return true;
			}

			@Override
			public boolean test(Map<PlaceAndTime, DistanceAndPrevious<PlaceAndTime>> t) {
				return targetVisited;
			}
		}
		;
		var possibleMovesCalculator = new PossibleMovesCalculator(finish);
		var part1 = PathFinding
				.pathWithWeightsAndTree(new PlaceAndTime(start, 0), possibleMovesCalculator, possibleMovesCalculator)
				.keySet().stream().filter(pat -> pat.point().equals(finish)).mapToLong(PlaceAndTime::turn).findAny()
				.getAsLong() + 1;
		System.out.println(part1);
		possibleMovesCalculator = new PossibleMovesCalculator(start.move(Direction.DOWN));
		var backForSnacks = PathFinding
				.pathWithWeightsAndTree(new PlaceAndTime(finish.move(Direction.DOWN), part1), possibleMovesCalculator,
						possibleMovesCalculator)
				.keySet().stream().filter(pat -> pat.point().equals(start.move(Direction.DOWN)))
				.mapToLong(PlaceAndTime::turn).findAny().getAsLong() + 1;
		possibleMovesCalculator = new PossibleMovesCalculator(finish);
		var backUp = PathFinding
				.pathWithWeightsAndTree(new PlaceAndTime(start, backForSnacks), possibleMovesCalculator,
						possibleMovesCalculator)
				.keySet().stream().filter(pat -> pat.point().equals(finish)).mapToLong(PlaceAndTime::turn).findAny()
				.getAsLong() + 1;
		System.out.println(backUp);
	}

	private static Direction direction(char character) {
		return switch (character) {
		case '<' -> Direction.LEFT;
		case '>' -> Direction.RIGHT;
		case 'v' -> Direction.DOWN;
		case '^' -> Direction.UP;
		case '#' -> null;
		case '.' -> null;
		default -> throw new IllegalArgumentException("Unexpected value: " + character);
		};
	}

	private static record PlaceAndTime(Point point, long turn) {
	}
//
//	private static void print(Map<Point, Direction> blizzards, PlaceAndTime location, Point end) {
//		System.out.println("===================================================");
//		System.out.println("Turn " + location.turn);
//		System.out.print("# ");
//		for (int i = 2; i <= end.getX() + 1; i++) {
//			System.out.print("#");
//		}
//		System.out.println();
//		for (long y = 1; y <= end.getY(); y++) {
//			System.out.print('#');
//			for (long x = 1; x <= end.getX(); x++) {
//				System.out.print(charAt(blizzards, new PlaceAndTime(new Point(x, y), location.turn), end));
//			}
//			System.out.println('#');
//		}
//		for (int i = 0; i < end.getX(); i++) {
//			System.out.print("#");
//		}
//		System.out.println(" #");
//	}
//
//	private static char charAt(Map<Point, Direction> blizzardMap, PlaceAndTime location, Point end) {
//		char symbol = ' ';
//		var spanX = end.getX();
//		var spanY = end.getY();
//		var upY = (location.point().getY() + location.turn() - 1) % spanY + 1;
//		upY = upY <= 0 ? upY + spanY : upY;
//		var up = new Point(location.point().getX(), upY);
//		if (blizzardMap.get(up) == Direction.UP) {
//			symbol = '^';
//		}
//		var downY = (location.point().getY() - location.turn() - 1) % spanY + 1;
//		downY = downY <= 0 ? downY + spanY : downY;
//		var down = new Point(location.point().getX(), downY);
//		if (blizzardMap.get(down) == Direction.DOWN) {
//			symbol = symbol == ' ' ? 'v' : '2';
//		}
//		var leftX = (location.point().getX() + location.turn() - 1) % spanX + 1;
//		leftX = leftX <= 0 ? leftX + spanX : leftX;
//		var left = new Point(leftX, location.point().getY());
//		if (blizzardMap.get(left) == Direction.LEFT) {
//			symbol = symbol == ' ' ? '<' : symbol == '2' ? '3' : '2';
//		}
//		var rightX = (location.point().getX() - location.turn() - 1) % spanX + 1;
//		rightX = rightX <= 0 ? rightX + spanX : rightX;
//		var right = new Point(rightX, location.point().getY());
//		if (blizzardMap.get(right) == Direction.RIGHT) {
//			symbol = symbol == ' ' ? '>' : symbol == '3' ? '4' : symbol == '2' ? '3' : '2';
//		}
//		return symbol;
//
//	}
}
