package y2022.day22;

import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.Point;

public class Day22 {

	public static void main(String[] args) {
		var map = Input.parseGrid("y2022/day22/day22.txt", c -> switch (c) {
		case '#' -> Boolean.FALSE;
		case '.' -> Boolean.TRUE;
		default -> null;
		});
		map.entrySet().removeIf(e -> e.getValue() == null);

		var lowestPerColumn = map.keySet().stream().collect(
				Collectors.groupingBy(Point::getX, Collectors.mapping(Point::getY, Collectors.minBy(Long::compare))));
		var highestPerColumn = map.keySet().stream().collect(
				Collectors.groupingBy(Point::getX, Collectors.mapping(Point::getY, Collectors.maxBy(Long::compare))));
		var lowestPerRow = map.keySet().stream().collect(
				Collectors.groupingBy(Point::getY, Collectors.mapping(Point::getX, Collectors.minBy(Long::compare))));
		var highestPerRow = map.keySet().stream().collect(
				Collectors.groupingBy(Point::getY, Collectors.mapping(Point::getX, Collectors.maxBy(Long::compare))));
		Map<Direction, UnaryOperator<Point>> posWrapMap = Map.of(Direction.UP,
				p -> new Point(p.getX(), highestPerColumn.get(p.getX()).get()), Direction.DOWN,
				p -> new Point(p.getX(), lowestPerColumn.get(p.getX()).get()), Direction.LEFT,
				p -> new Point(highestPerRow.get(p.getY()).get(), p.getY()), Direction.RIGHT,
				p -> new Point(lowestPerRow.get(p.getY()).get(), p.getY()));

		Map<Direction, Function<Point, Direction>> dirWrapMap = Map.of(Direction.UP, p -> Direction.UP, Direction.DOWN,
				p -> Direction.DOWN, Direction.LEFT, p -> Direction.LEFT, Direction.RIGHT, p -> Direction.RIGHT);

		System.out.println(getPassword(map, posWrapMap, dirWrapMap, new Point(lowestPerRow.get(0L).get(), 0)));

		dirWrapMap = Map.of(Direction.UP, p -> {
			if (p.getX() < 100) {
				return Direction.RIGHT;
			} else {
				return Direction.UP;
			}
		}, Direction.DOWN, p -> {
			if (p.getX() < 50) {
				return Direction.DOWN;
			} else {
				return Direction.LEFT;
			}
		}, Direction.LEFT, p -> {
			if (p.getY() < 50) {
				return Direction.RIGHT;
			} else if (p.getY() < 100) {
				return Direction.DOWN;
			} else if (p.getY() < 150) {
				return Direction.RIGHT;
			} else {
				return Direction.DOWN;
			}
		}, Direction.RIGHT, p -> {
			if (p.getY() < 50) {
				return Direction.LEFT;
			} else if (p.getY() < 100) {
				return Direction.UP;
			} else if (p.getY() < 150) {
				return Direction.LEFT;
			} else {
				return Direction.UP;
			}
		});
		posWrapMap = Map.of(Direction.UP, p -> {
			if (p.getX() < 50) {
				return new Point(50, 50 + p.getX());
			} else if (p.getX() < 100) {
				return new Point(0, 100 + p.getX());
			} else {
				return new Point(p.getX() - 100, 199);
			}
		}, Direction.DOWN, p -> {
			if (p.getX() < 50) {
				return new Point(p.getX() + 100, 0);
			} else if (p.getX() < 100) {
				return new Point(49, 100 + p.getX());
			} else {
				return new Point(99, p.getX() - 50);
			}
		}, Direction.LEFT, p -> {
			if (p.getY() < 50) {
				return new Point(0, 149 - p.getY());
			} else if (p.getY() < 100) {
				return new Point(p.getY() - 50, 100);
			} else if (p.getY() < 150) {
				return new Point(50, 149 - p.getY());
			} else {
				return new Point(p.getY() - 100, 0);
			}
		}, Direction.RIGHT, p -> {
			if (p.getY() < 50) {
				return new Point(99, 149 - p.getY());
			} else if (p.getY() < 100) {
				return new Point(p.getY() + 50, 49);
			} else if (p.getY() < 150) {
				return new Point(149, 149 - p.getY());
			} else {
				return new Point(p.getY() - 100, 149);
			}
		});
		System.out.println(getPassword(map, posWrapMap, dirWrapMap, new Point(lowestPerRow.get(0L).get(), 0)));
	}

	private static long getPassword(Map<Point, Boolean> map, Map<Direction, UnaryOperator<Point>> wrapMap,
			Map<Direction, Function<Point, Direction>> dirWrapMap, Point start) {
		var position = start;
		var direction = Direction.RIGHT;
		var path = Input.parseLines("y2022/day22/day22.txt").filter(s -> s.contains("R")).findAny().get();
		long stepLength = 0;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) != 'R' && path.charAt(i) != 'L') {
				stepLength *= 10;
				stepLength += path.charAt(i) - '0';
			}
			if (path.charAt(i) == 'L' || path.charAt(i) == 'R' || i == path.length() - 1) {
				while (stepLength > 0) {
					while (map.get(position.move(direction)) == Boolean.TRUE && stepLength > 0) {
						position = position.move(direction);
						stepLength--;
					}
					if (map.get(position.move(direction)) == Boolean.FALSE) {
						stepLength = 0;
					}
					if (stepLength > 0) {
						var newPosition = wrapMap.get(direction).apply(position);
						if (map.get(newPosition) == Boolean.TRUE) {
							stepLength--;
							direction = dirWrapMap.get(direction).apply(position);
							position = newPosition;
						} else {
							stepLength = 0;
						}
					}
				}
				direction = switch (path.charAt(i)) {
				case 'L' -> direction.left();
				case 'R' -> direction.right();
				default -> direction;
				};
			}
		}
		return 1000 * (position.getY() + 1) + 4 * (position.getX() + 1) + switch (direction) {
		case RIGHT -> 0L;
		case DOWN -> 1L;
		case LEFT -> 2L;
		case UP -> 3L;
		default -> throw new IllegalArgumentException("Unexpected value: " + direction);
		};
	}
}
