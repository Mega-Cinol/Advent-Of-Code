package y2018.day18;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day18 {
	private static final int SIDE = 50;
	enum Field {
		OPEN, TREE, MILL;
	}
	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2018/day18/day18.txt").collect(Collectors.toList());
		Map<Point, Field> map = parseInput(input);
		Map<String, Integer> knownPatterns = new HashMap<>();
		boolean fastForward = true;
		for (int i = 0 ; i < 1000000000 ; i++)
		{
			if (fastForward) {
				String currentPattern = mapToString(map);
				Integer oldI = knownPatterns.get(currentPattern);
				if (oldI != null) {
					System.out.println(oldI + " repeated in " + i);
					int step = i - oldI;
					int repeats = (1000000000 - i) / step;
					i = repeats * step + oldI;
					fastForward = false;
					System.out.println("New i: " + i);
				}
				knownPatterns.put(currentPattern, i);
			}
			Map<Point, Field> next = new HashMap<>();
			for (int y = 0 ; y < SIDE ; y++)
			{
				for (int x = 0 ; x < SIDE ; x++)
				{
					Point current = new Point(x,y);
					switch (map.get(current)) {
					case OPEN:
						if (current.getNeighbours().stream().map(map::get).filter(f -> f == Field.TREE).count() >= 3)
						{
							next.put(current, Field.TREE);
						} else {
							next.put(current, Field.OPEN);
						}
						break;
					case TREE:
						if (current.getNeighbours().stream().map(map::get).filter(f -> f == Field.MILL).count() >= 3)
						{
							next.put(current, Field.MILL);
						} else {
							next.put(current, Field.TREE);
						}
						
						break;
					case MILL:
						Set<Field> neights = current.getNeighbours().stream().map(map::get).collect(Collectors.toSet());
						if (neights.contains(Field.TREE) && neights.contains(Field.MILL))
						{
							next.put(current, Field.MILL);
						} else {
							next.put(current, Field.OPEN);
						}
						
						break;
					}
				}
			}
			if (i == 10)
			{
				System.out.println("Part 1:");
				long trees = map.values().stream().filter(v -> v == Field.TREE).count();
				long mills = map.values().stream().filter(v -> v == Field.MILL).count();
				System.out.println(trees);
				System.out.println(mills);
				System.out.println(trees * mills);
			}
			map = next;
		}
		System.out.println("Part 2:");
		long trees = map.values().stream().filter(v -> v == Field.TREE).count();
		long mills = map.values().stream().filter(v -> v == Field.MILL).count();
		System.out.println(trees);
		System.out.println(mills);
		System.out.println(trees * mills);

	}
	private static Map<Point, Field> parseInput(List<String> input) {
		Map<Point, Field> map = new HashMap<>();
		for (int y = 0 ; y < input.size() ; y++)
		{
			for (int x = 0 ; x < input.get(y).length() ; x++)
			{
				Field field = Field.OPEN;
				switch (input.get(y).charAt(x)) {
				case '#':
					field = Field.MILL;
					break;
				case '|':
					field = Field.TREE;
					break;
				default:
					break;
				}
				map.put(new Point(x, y), field);
			}
		}
		return map;
	}
	private static String mapToString(Map<Point, Field> map)
	{
		StringBuilder sb = new StringBuilder();
		for (int y = 0 ; y < SIDE ; y++)
		{
			for (int x = 0 ; x < SIDE ; x++)
			{
				switch (map.get(new Point(x, y))) {
				case TREE:
					sb.append('|');
					break;
				case MILL:
					sb.append('#');
					break;
				case OPEN:
					sb.append('.');
					break;

				default:
					break;
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
