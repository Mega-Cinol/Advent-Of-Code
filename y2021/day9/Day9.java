package y2021.day9;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import common.Input;
import common.PathFinding;
import common.Point;

public class Day9 {

	public static void main(String[] args) {
		Map<Point, Integer> heightMap = parseInput(
				Input.parseLines("y2021/day9/day9.txt").collect(Collectors.toList()));
		// part1
		System.out.println(heightMap.entrySet().stream()
				.filter(e -> e.getKey().getNonDiagonalNeighbours().stream().filter(heightMap::containsKey)
						.allMatch(n -> heightMap.get(n) > e.getValue()))
				.map(Map.Entry::getValue).mapToInt(v -> v + 1).sum());
		// part2
		System.out.println(heightMap.entrySet().stream()
				.filter(e -> e.getKey().getNonDiagonalNeighbours().stream().filter(heightMap::containsKey)
						.allMatch(n -> heightMap.get(n) > e.getValue()))
				.map(e -> PathFinding.countSteps(e.getKey(), (p, pSet) -> false,
						p -> p.getNonDiagonalNeighbours().stream().filter(heightMap::containsKey)
								.filter(pt -> heightMap.get(pt) < 9).collect(Collectors.toSet())))
				.map(Map::size).sorted(Collections.reverseOrder()).limit(3).reduce(1, (a, b) -> a * b));
	}

	private static Map<Point, Integer> parseInput(List<String> input) {
		Map<Point, Integer> heightMap = new HashMap<>();
		for (int y = 0; y < input.size(); y++) {
			for (int x = 0; x < input.get(y).length(); x++) {
				heightMap.put(new Point(x, y), input.get(y).charAt(x) - '0');
			}
		}
		return heightMap;
	}

}
