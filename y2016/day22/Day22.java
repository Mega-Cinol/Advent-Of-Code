package y2016.day22;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;
import common.Point;

public class Day22 {

	public static void main(String[] args) {
		Map<Point, Data> grid = new HashMap<>();
		Input.parseLines("y2016/day22/day22.txt").forEach(s -> addData(s, grid));
		System.out.println(grid.values().stream().mapToInt(d -> d.used).filter(u -> u > 0).filter(u -> u <= 90).count());
		grid.values().stream().mapToInt(d -> d.used).sorted().forEach(System.out::println);
		List<Point> init = new ArrayList<>();
		init.add(new Point(26, 12));
		List<Point> wall = new ArrayList<>();
		for (int y = 0 ; y < 30 ; y++)
		{
			for (int x = 0 ; x < 33 ; x++)
			{
				if ((x == 0) && (y == 0))
				{
					System.out.print('S');
				}
				else if ((x == 32) && (y == 0))
				{
					System.out.print('G');
				}
				else if (grid.get(new Point(x, y)).used > 90)
				{
					wall.add(new Point(x, y));
					System.out.print('#');
				}
				else if (grid.get(new Point(x, y)).used == 0)
				{
					System.out.print('_');
				}
				else
				{
					System.out.print('.');
				}
			}
			System.out.println();
		}
		System.out.println(wall);
//		Map<Map<Point, Data>, Integer> result = PathFinding.countSteps(init, (g, depth) -> g.get(new Point(0,0)).target, Day22::getPossibleMoves);
//		result.values().stream().mapToInt(Integer::intValue).max().ifPresent(System.out::println);
	}

	private static void addData(String desc, Map<Point, Data> grid)
	{
		Pattern p = Pattern.compile("/dev/grid/node-x(\\d+)-y(\\d+)\\s*(\\d+)T\\s*(\\d+)T\\s*\\d+T\\s*\\d+%");
		Matcher m = p.matcher(desc);
		if (!m.matches())
		{
			throw new IllegalArgumentException(desc);
		}
		Data data = new Data();
//		data.available = Integer.parseInt(m.group(3));
		data.used = Integer.parseInt(m.group(4));
		Point point = new Point(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)));
//		data.target = point.equals(new Point(32, 0));
		grid.put(point, data);
	}
	private static class Data
	{
//		public int available;
		public int used;
//		public boolean target = false;
	}
//	private static Set<Map<Point, Data>> getPossibleMoves(Map<Point, Data> grid)
//	{
//		Point cursor = grid.entrySet().stream().filter(e -> e.getValue().used == 0).map(Map.Entry::getKey).findAny()
//				.get();
//		return cursor.getNeighbours().stream().filter(p -> grid.containsKey(p)).filter(p -> p.getX() == cursor.getX() || p.getY() == cursor.getY()).map(p -> swap(cursor, p, grid)).filter(m -> m != null).collect(Collectors.toSet());
//	}
//	private static Map<Point, Data> swap(Point src, Point dst, Map<Point, Data> grid)
//	{
//		if (grid.get(dst).used > 90)
//		{
//			return null;
//		}
//		Map<Point, Data> result = new HashMap<Point, Day22.Data>(grid);
//		result.put(src, grid.get(dst));
//		result.put(dst, grid.get(src));
//		return result;
//	}
//	private static boolean isDone(List<Point> path, Map<Point, Data> grid)
//	{
//		
//	}
}
