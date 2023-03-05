package y2022.day8;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import common.Direction;
import common.Input;
import common.Point;

public class Day8 {

	public static void main(String[] args) {
		var trees = Input.parseGrid("y2022/day8/day8.txt", c -> Integer.valueOf("" + c));
		var visibleTrees = new HashSet<Point>();

		long maxX = Point.maxX(trees.keySet());
		long maxY = Point.maxY(trees.keySet());
		// Up
		for (int x = 0; x <= maxX; x++) {
			int currentHeight = -1;
			for (int y = 0; y <= maxY; y++) {
				if (trees.get(new Point(x, y)) > currentHeight) {
					visibleTrees.add(new Point(x, y));
					currentHeight = trees.get(new Point(x, y));
				}
			}
		}
		// Down
		for (int x = 0; x <= maxX; x++) {
			int currentHeight = -1;
			for (long y = maxY; y >= 0; y--) {
				if (trees.get(new Point(x, y)) > currentHeight) {
					visibleTrees.add(new Point(x, y));
					currentHeight = trees.get(new Point(x, y));
				}
			}
		}
		// Left
		for (int y = 0; y <= maxY; y++) {
			int currentHeight = -1;
			for (int x = 0; x <= maxX; x++) {
				if (trees.get(new Point(x, y)) > currentHeight) {
					visibleTrees.add(new Point(x, y));
					currentHeight = trees.get(new Point(x, y));
				}
			}
		}
		// Right
		for (int y = 0; y <= maxY; y++) {
			int currentHeight = -1;
			for (long x = maxX; x >= 0; x--) {
				if (trees.get(new Point(x, y)) > currentHeight) {
					visibleTrees.add(new Point(x, y));
					currentHeight = trees.get(new Point(x, y));
				}
			}
		}
		System.out.println(visibleTrees.size());

		System.out.println(trees.entrySet().stream().mapToLong(e -> scenicScore(e.getKey(), trees)).max().getAsLong());
	}

	private static long scenicScore(Point tree, Map<Point, Integer> trees) {
		return Stream.of(Direction.values()).map(d -> scenicScore(tree, trees, d)).reduce(1L, (a, b) -> a * b);
	}

	private static long scenicScore(Point tree, Map<Point, Integer> trees, Direction direction) {
		long count = 0;
		Point currentTree = tree.move(direction);
		while (trees.containsKey(currentTree) && trees.get(currentTree) < trees.get(tree)) {
			count++;
			currentTree = currentTree.move(direction);
		}
		if (trees.getOrDefault(currentTree, 11) == trees.get(tree)) {
			count++;
		}
		return count;
	}
}
