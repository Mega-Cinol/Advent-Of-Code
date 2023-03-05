package y2017.day19;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.Point;

public class Day19 {

	public static void main(String[] args) {
		List<String> rows = Input.parseLines("y2017/day19/day19.txt").collect(Collectors.toList());
		Map<Point, Character> labirynth = new HashMap<>();
		for (int i = 0 ; i < rows.size() ; i++)
		{
			parseRow(rows.get(i), i, labirynth);
		}
		Point current = new Point(107, 0);
		Direction dir = Direction.NORTH;
		String result = "";
		int stepCount = 1;
		while (labirynth.containsKey(current))
		{
			stepCount++;
			if (labirynth.get(current) == '+')
			{
				switch (dir) {
				case NORTH:
				case SOUTH:
				case UP:
				case DOWN:
					Character e = labirynth.get(current.move(Direction.EAST));
					Character w = labirynth.get(current.move(Direction.WEST));
					if (e != null && e == '-')
					{
						dir = Direction.EAST;
					}
					else if (w != null && w == '-')
					{
						dir = Direction.WEST;
					}
					else
					{
						throw new IllegalStateException(current.toString());
					}
					break;
				case EAST:
				case WEST:
				case LEFT:
				case RIGHT:
					Character n = labirynth.get(current.move(Direction.NORTH));
					Character s = labirynth.get(current.move(Direction.SOUTH));
					if (n != null && n == '|')
					{
						dir = Direction.NORTH;
					}
					else if (s != null && s == '|')
					{
						dir = Direction.SOUTH;
					}
					else
					{
						throw new IllegalStateException(current.toString());
					}
					break;
				}
			}
			if ((labirynth.get(current) != '+') &&(labirynth.get(current) != '-') && (labirynth.get(current) != '|'))
			{
				result += labirynth.get(current);
			}
			current = current.move(dir);
		}
		System.out.println(result);
		System.out.println(labirynth.size());
		System.out.println(stepCount);
	}

	private static void parseRow(String row, int rowId, Map<Point, Character> labirynth) {
		for (int i = 0 ; i < row.length() ; i++)
		{
			if (row.charAt(i) != ' ')
			{
				labirynth.put(new Point(i, rowId), row.charAt(i));
			}
		}
	}

//	private static boolean isStart(Point location, Map<Point, Character> labirynth)
//	{
//		long nCount = location.getNonDiagonalNeighbours().stream()
//				.filter(labirynth::containsKey)
//				.count();
//		return nCount == 1;
//	}
}
