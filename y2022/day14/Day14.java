package y2022.day14;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day14 {

	public static void main(String[] args) {
		Map<Point, FieldType> blocked = new HashMap<>();
		Input.parseLines("y2022/day14/day14.txt", Day14::parseRocks,
				r -> r.stream().forEach(s -> blocked.put(s, FieldType.ROCK)));
		var maxY = Point.maxY(blocked.keySet());
		boolean fullOfSand = false;
		long added = 0;
		while (!fullOfSand) {
			var sand = new Point(500, 0);
			while ((!blocked.containsKey(sand.move(0, 1)) || !blocked.containsKey(sand.move(-1, 1))
					|| !blocked.containsKey(sand.move(1, 1))) && sand.getY() < maxY) {
				if (!blocked.containsKey(sand.move(0, 1))) {
					sand = sand.move(0, 1);
				} else if (!blocked.containsKey(sand.move(-1, 1))) {
					sand = sand.move(-1, 1);
				} else if (!blocked.containsKey(sand.move(1, 1))) {
					sand = sand.move(1, 1);
				}
			}
			if (sand.getY() >= maxY) {
				fullOfSand = true;
			} else {
				blocked.put(sand, FieldType.SAND);
				added++;
			}
		}
		System.out.println(added);
		blocked.clear();
		Input.parseLines("y2022/day14/day14.txt", Day14::parseRocks,
				r -> r.stream().forEach(s -> blocked.put(s, FieldType.ROCK)));
		boolean canMove = true;
		added = 0;
		while (canMove) {
			var sand = new Point(500, 0);
			while ((!blocked.containsKey(sand.move(0, 1)) || !blocked.containsKey(sand.move(-1, 1))
					|| !blocked.containsKey(sand.move(1, 1))) && sand.getY() < maxY + 1) {
				if (!blocked.containsKey(sand.move(0, 1))) {
					sand = sand.move(0, 1);
				} else if (!blocked.containsKey(sand.move(-1, 1))) {
					sand = sand.move(-1, 1);
				} else if (!blocked.containsKey(sand.move(1, 1))) {
					sand = sand.move(1, 1);
				}
			}
			blocked.put(sand, FieldType.SAND);
			added++;
			if (sand.equals(new Point(500, 0))) {
				canMove = false;
			}
		}
		print(blocked);
		System.out.println(added);
	}

	private static Set<Point> parseRocks(String rocksStr) {
		var rockPoints = new HashSet<Point>();
		var rocksPeaks = rocksStr.split(" -> ");
		for (int i = 1; i < rocksPeaks.length; i++) {
			var from = parsePoint(rocksPeaks[i - 1]);
			var to = parsePoint(rocksPeaks[i]);
			Point.range(from, to).forEach(rockPoints::add);
		}
		return rockPoints;
	}

	private static Point parsePoint(String pointStr) {
		return new Point(Stream.of(pointStr.split(",")).map(Long::parseLong).toList());
	}

	private static void print(Map<Point, FieldType> points) {
		var minX = Point.minX(points.keySet());
		var maxX = Point.maxX(points.keySet());
		var minY = Point.minY(points.keySet());
		var maxY = Point.maxY(points.keySet());

		for (long y = minY; y <= maxY; y++) {
			for (long x = minX; x <= maxX; x++) {
				System.out.print(
						points.containsKey(new Point(x, y)) ? points.get(new Point(x, y)) == FieldType.ROCK ? '#' : 'o'
								: ' ');
			}
			System.out.println();
		}
	}

	private enum FieldType {
		ROCK, SAND
	}
}
