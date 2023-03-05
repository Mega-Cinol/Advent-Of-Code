package y2018.day3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day3 {

	public static void main(String[] args) {
		Set<Point> used = new HashSet<Point>();
		Set<Point> overbooked = new HashSet<Point>();
		Input.parseLines("y2018/day3/day3.txt", Day3::parseRange, ps -> ps.forEach(p -> {
			if (!used.add(p)) {
				overbooked.add(p);
			}
		}));
		System.out.println(overbooked.size());
		List<Set<Point>> claims = Input.parseLines("y2018/day3/day3.txt", Day3::parseRange)
				.map(s -> s.collect(Collectors.toSet())).collect(Collectors.toList());
		out:
		for (int i = 0 ; i < claims.size() ; i++)
		{
			for (int j = 0 ; j < claims.size() ; j++)
			{
				if (j == i)
				{
					continue;
				}
				Set<Point> tmp = new HashSet<>();
				tmp.addAll(claims.get(i));
				tmp.retainAll(claims.get(j));
				if (!tmp.isEmpty())
				{
					continue out;
				}
			}
			System.out.println(i);
		}
		
	}

	private static Stream<Point> parseRange(String desc) {
		Pattern p = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");
		Matcher m = p.matcher(desc);
		m.matches();
		int top = Integer.parseInt(m.group(3));
		int left = Integer.parseInt(m.group(2));
		int width = Integer.parseInt(m.group(4));
		int height = Integer.parseInt(m.group(5));
		return Point.range(new Point(left, top), new Point(left + width - 1, top + height - 1));
	}
}
