package y2018.day20;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.Direction;
import common.Input;
import common.PathFinding;
import common.Point;

public class Day20 {
	private static final boolean PARSE_REGEX = true;
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		if (PARSE_REGEX) {
			String regexMap = Input.parseLines("y2018/day20/day20.txt").findAny().get();
			Map<Point, Set<Point>> map = new HashMap<>();
			readRegex(regexMap, map);
			ObjectOutputStream os = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream("y2018/day20/day20.dat")));
			os.writeObject(map);
			os.close();
		}
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("y2018/day20/day20.dat")));
		@SuppressWarnings("unchecked")
		Map<Point, Set<Point>> map = (Map<Point, Set<Point>>) ois.readObject();
		ois.close();
		PathFinding.countSteps(new Point(0, 0), visited -> false, map::get).values().stream()
				.max(Integer::compareTo).ifPresent(System.out::println);
		System.out.println(PathFinding.countSteps(new Point(0, 0), visited -> false, map::get).values().stream().filter(d -> d >= 1000)
				.count());
	}

	private static void readRegex(String regexMap, Map<Point, Set<Point>> map)
	{
		Set<Point> currentPositions = new HashSet<>();
		Deque<Set<Point>> branchPoints = new ArrayDeque<>();
		currentPositions.add(new Point(0, 0));
		Deque<Set<Point>> positionsToMerge = new ArrayDeque<>();
		for (int i = 0 ; i < regexMap.length() ; i++)
		{
			if (i % (regexMap.length() / 100) == 0)
			{
				System.out.println(i / (regexMap.length() / 100) + "% - " + currentPositions.size());
			}
			switch (regexMap.charAt(i)) {
			case 'N':
				currentPositions = updateCurrentPositions(currentPositions, Direction.NORTH, map);
				break;
			case 'S':
				currentPositions = updateCurrentPositions(currentPositions, Direction.SOUTH, map);
				break;
			case 'W':
				currentPositions = updateCurrentPositions(currentPositions, Direction.WEST, map);
				break;
			case 'E':
				currentPositions = updateCurrentPositions(currentPositions, Direction.EAST, map);
				break;
			case '(':
				branchPoints.push(currentPositions);
				positionsToMerge.push(new HashSet<>());
				break;
			case '|':
				positionsToMerge.peek().addAll(currentPositions);
				currentPositions = branchPoints.peek();
				break;
			case ')':
				currentPositions.addAll(positionsToMerge.pop());
				branchPoints.pop();
			}
		}
	}

	private static Point addPointToMap(Point point, Direction directon, Map<Point, Set<Point>> map)
	{
		map.computeIfAbsent(point, k -> new HashSet<>()).add(point.move(directon));
		point = point.move(directon);
		map.computeIfAbsent(point, k -> new HashSet<>()).add(point.move(directon.opposite()));
		return point;
	}

	private static Set<Point> updateCurrentPositions(Set<Point> points, Direction directon, Map<Point, Set<Point>> map)
	{
		Set<Point> newPositions = new HashSet<>();
		for (Point p : points)
		{
			newPositions.add(addPointToMap(p, directon, map));
		}
		return newPositions;
	}
}
