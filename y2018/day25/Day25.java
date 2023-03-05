package y2018.day25;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day25 {
	public static void main(String[] args) {
		List<Point> points = Input.parseLines("y2018/day25/day25.txt", Day25::fromString).collect(Collectors.toList());
		Map<Point, Integer> pointConstelations = new HashMap<>();
		for (int i = 0 ; i < points.size() ; i++)
		{
			int finalI = i;
			int currentConstellation = pointConstelations.computeIfAbsent(points.get(i), p -> finalI);
			for (int j = i + 1 ; j < points.size() ; j++)
			{
				if (points.get(i).getManhattanDistance(points.get(j)) <= 3)
				{
					if (!pointConstelations.containsKey(points.get(j)))
					{
						pointConstelations.put(points.get(j), currentConstellation);
					} else if (pointConstelations.get(points.get(j)) != currentConstellation) {
						int old = pointConstelations.get(points.get(j));
						Set<Point> toReplace = pointConstelations.entrySet().stream().filter(e -> e.getValue() == old).map(Map.Entry::getKey).collect(Collectors.toSet());
						toReplace.stream().forEach(p -> pointConstelations.put(p, currentConstellation));
					}
				}
			}
		}
		System.out.println(pointConstelations.values().stream().distinct().count());
	}

	private static Point fromString(String pointStr) {
		return new Point(Stream.of(pointStr.split(",")).map(Long::valueOf).collect(Collectors.toList()));
	}
}
