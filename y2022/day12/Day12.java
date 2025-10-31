package y2022.day12;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;
import common.PathFinding;

public class Day12 {

	public static void main(String[] args) {
		var map = Input.parseGrid("y2022/day12/day12.txt", Function.identity());
		var initial = map.entrySet().stream().filter(e -> e.getValue() == 'S').map(Map.Entry::getKey).findFirst().get();
		var destination = map.entrySet().stream().filter(e -> e.getValue() == 'E').map(Map.Entry::getKey).findFirst()
				.get();

		var paths = PathFinding.pathWithWeights(initial, found -> found.containsKey(destination),
				pos -> pos.getNonDiagonalNeighbours().stream().filter(map::containsKey)
						.filter(to -> reachable(map.get(pos), map.get(to)))
						.collect(Collectors.toMap(Function.identity(), p -> 1)));

		System.out.println(paths.get(destination));

		var bestA = map.entrySet().stream().filter(e -> e.getValue() == 'a' || e.getValue() == 'S')
				.map(e -> PathFinding.pathWithWeights(e.getKey(), found -> found.containsKey(destination),
						pos -> pos.getNonDiagonalNeighbours().stream().filter(map::containsKey)
								.filter(to -> reachable(map.get(pos), map.get(to)))
								.collect(Collectors.toMap(Function.identity(), p -> 1))))
				.map(pathz -> pathz.get(destination)).filter(dist -> dist != null).min(Integer::compare).get();
		System.out.println(bestA);
	}

	private static boolean reachable(char from, char to) {
		if (to == 'S' || from == 'E') {
			return true;
		}
		if (from == 'S') {
			return to <= 'b';
		}
		if (to == 'E') {
			return from >= 'y';
		}
		return from >= to - 1;
	}
}
