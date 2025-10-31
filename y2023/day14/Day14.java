package y2023.day14;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.Direction;
import common.Point;

public class Day14 extends AdventSolution {

	public static void main(String[] args) {
		new Day14().solve();
	}

	@Override
	public Object part1Solution() {
		var rockGrid = parseGrid(symbol -> {
			return switch (symbol) {
			case '#' -> RockType.SOLID;
			case 'O' -> RockType.MOVABLE;
			default -> null;
			};
		});
		var maxX = Point.maxX(rockGrid.keySet());
		var maxY = Point.maxY(rockGrid.keySet());
		rockGrid = rockGrid.entrySet().stream().filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
//		printGrid(rockGrid);
		rotate(rockGrid, Direction.UP, maxX, maxY);
//		printGrid(rockGrid);
		return rockGrid.entrySet().stream().filter(e -> e.getValue() == RockType.MOVABLE).map(Map.Entry::getKey)
				.map(Point::getY).mapToLong(y -> maxY + 1 - y).sum();
	}

//	private void printGrid(Map<Point, RockType> grid) {
//		var maxX = Point.maxX(grid.keySet());
//		var maxY = Point.maxY(grid.keySet());
//		for (var y = 0L; y <= maxY; y++) {
//			for (var x = 0L; x <= maxX; x++) {
//				var typeAtPos = grid.get(new Point(x, y));
//				if (typeAtPos == null) {
//					typeAtPos = RockType.EMPTY;
//				}
//				System.out.print(switch (typeAtPos) {
//				case SOLID -> '#';
//				case MOVABLE -> 'O';
//				default -> '.';
//				});
//			}
//			System.out.println();
//		}
//	}

	@Override
	public Object part2Solution() {
		var rockGrid = parseGrid(symbol -> {
			return switch (symbol) {
			case '#' -> RockType.SOLID;
			case 'O' -> RockType.MOVABLE;
			default -> null;
			};
		});
		var maxX = Point.maxX(rockGrid.keySet());
		var maxY = Point.maxY(rockGrid.keySet());
		rockGrid = rockGrid.entrySet().stream().filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
		Map<Map<Point, RockType>, Long> history = new HashMap<>();
//		printGrid(rockGrid);
		long cycleId = 0;
		while (!history.containsKey(Map.copyOf(rockGrid))) {
			history.put(Map.copyOf(rockGrid), cycleId);
			cycle(rockGrid, maxX, maxY);
			cycleId++;
		}
		var cycleStart = history.get(rockGrid);
		var cycleSize = cycleId - history.get(rockGrid);
		var offset = (1_000_000_000 - cycleStart) % cycleSize;
		var resultGrid = history.entrySet().stream().filter(e -> e.getValue() == cycleStart + offset).findAny().get().getKey();
		
//		printGrid(rockGrid);
		return resultGrid.entrySet().stream().filter(e -> e.getValue() == RockType.MOVABLE).map(Map.Entry::getKey)
				.map(Point::getY).mapToLong(y -> maxY + 1 - y).sum();
	}

	private void cycle(Map<Point, RockType> grid, long maxX, long maxY) {
		rotate(grid, Direction.UP, maxX, maxY);
		rotate(grid, Direction.LEFT, maxX, maxY);
		rotate(grid, Direction.DOWN, maxX, maxY);
		rotate(grid, Direction.RIGHT, maxX, maxY);
	}

	private void rotate(Map<Point, RockType> grid, Direction direction, long maxX, long maxY) {
		var comparator = switch (direction) {
		case UP -> Comparator.comparing(Point::getY);
		case DOWN -> Comparator.comparing(Point::getY).reversed();
		case LEFT -> Comparator.comparing(Point::getX);
		case RIGHT -> Comparator.comparing(Point::getX).reversed();
		default -> throw new UnsupportedOperationException(direction.name());
		};
		Predicate<Point> borderCondition = switch (direction) {
		case UP -> p -> p.getY() > 0;
		case DOWN -> p -> p.getY() < maxY;
		case LEFT -> p -> p.getX() > 0;
		case RIGHT -> p -> p.getX() < maxX;
		default -> throw new UnsupportedOperationException(direction.name());
		};
		var rocksToMove = grid.entrySet().stream().filter(e -> e.getValue() == RockType.MOVABLE).map(Map.Entry::getKey)
				.sorted(comparator).toList();
		for (var rockPosition : rocksToMove) {
			var newPosition = rockPosition;
			while (borderCondition.test(newPosition) && !grid.containsKey(newPosition.move(direction))) {
				grid.remove(newPosition);
				newPosition = newPosition.move(direction);
				grid.put(newPosition, RockType.MOVABLE);
			}
		}

	}

	private enum RockType {
		SOLID, MOVABLE, EMPTY
	}
}
