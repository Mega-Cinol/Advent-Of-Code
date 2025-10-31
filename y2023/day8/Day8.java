package y2023.day8;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import common.Input;
import common.Pair;

public class Day8 {

	public static void main(String[] args) {
		var input = Input.parseLines("y2023/day8/day8.txt").toList();

		var map = new HashMap<String, Pair<String>>();
		input.subList(2, input.size()).forEach(line -> addToMap(line, map));

		var instructions = input.get(0);
		// part 1
		var current = "AAA";
		var steps = 0L;
		while (!"ZZZ".equals(current)) {
			for (int i = 0; i < instructions.length(); i++) {
				if (instructions.charAt(i) == 'L') {
					current = map.get(current).getFirst();
				} else {
					current = map.get(current).getSecond();
				}
				steps++;
				if ("ZZZ".equals(current)) {
					break;
				}
			}
		}
		System.out.println(steps);

		// part 2
		System.out.println(map.keySet().stream().filter(node -> node.endsWith("A")).map(Ghost::new)
				.map(ghost -> ghost.findCycle(map, instructions)).reduce(Cycle::merge).get().start());
	}

	private static record Position(String node, int instructionCounter) {
	}

	private static class Ghost {
		private Position currentPosition;
		private Map<Position, Long> previousSteps = new HashMap<>();

		public Ghost(String initialPosition) {
			currentPosition = new Position(initialPosition, 0);
		}

		public Cycle findCycle(Map<String, Pair<String>> map, String instructions) {
			long step = 0;
			int instructionCount = 0;
			while (!previousSteps.containsKey(currentPosition)) {
				previousSteps.put(currentPosition, step++);
				var nextNode = instructions.charAt(instructionCount) == 'L' ? map.get(currentPosition.node()).getFirst()
						: map.get(currentPosition.node()).getSecond();
				instructionCount++;
				instructionCount %= instructions.length();
				currentPosition = new Position(nextNode, instructionCount);
			}
//			System.out.println("Cycle found between " + previousSteps.get(currentPosition) + " and " + step);
//			previousSteps.entrySet().stream().filter(e -> e.getKey().node().endsWith("Z"))
//					.map(e -> "End %s reached at step %d".formatted(e.getKey().node(), e.getValue()))
//					.forEach(System.out::println);
			var start = previousSteps.entrySet().stream().filter(e -> e.getKey().node().endsWith("Z"))
					.map(Map.Entry::getValue).findAny().get();
			return new Cycle(start, step - previousSteps.get(currentPosition));
		}
	}

	private static record Cycle(long start, long offset) {
		public Cycle merge(Cycle other) {
			long current1 = start;
			long current2 = other.start;
			while (current1 != current2) {
				if (current1 < current2) {
					current1 += offset;
				} else {
					current2 += other.offset;
				}
			}
			return new Cycle(current1, offset * other.offset / highestCommonDivisor(offset, other.offset));
		}

		private long highestCommonDivisor(long a, long b) {
			long pom;
			while (b != 0) {
				pom = b;
				b = a % b;
				a = pom;
			}
			return a;
		}
	}

	private static void addToMap(String line, Map<String, Pair<String>> map) {
		var pattern = Pattern.compile("([A-Z]{3}) = \\(([A-Z]{3}), ([A-Z]{3})\\)");
		var matcher = pattern.matcher(line);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(line);
		}
		map.put(matcher.group(1), Pair.of(matcher.group(2), matcher.group(3)));
	}
}
