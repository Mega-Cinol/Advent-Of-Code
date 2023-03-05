package y2016.day8;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day8 {
	private static final Set<Point> POINTS = new HashSet<>();
	private static final int WIDTH = 50;
	private static final int HEIGHT = 6;
	public static void main(String[] args) {
		Input.parseLines("y2016/day8/day8.txt", Function.identity()).forEach(Day8::command);
		System.out.println(POINTS.size());
	}

	private static final void command(String cmd)
	{
		if (cmd.startsWith("rect"))
		{
			Pattern rectPattern = Pattern.compile("(\\d+)x(\\d+)");
			Matcher rectMatcher = rectPattern.matcher(cmd);
			rectMatcher.find();
			for (int x = 0 ; x < Integer.parseInt(rectMatcher.group(1)) ; x++)
			{
				for (int y = 0 ; y < Integer.parseInt(rectMatcher.group(2)) ; y++)
				{
					POINTS.add(new Point(x, y));
				}
				
			}
		} else if (cmd.startsWith("rotate row")) {
			Pattern rotPattern = Pattern.compile("(\\d+) by (\\d+)");
			Matcher rotMatcher = rotPattern.matcher(cmd);
			rotMatcher.find();
			Set<Point> toRot = POINTS.stream().filter(p -> p.getY() == Integer.parseInt(rotMatcher.group(1))).collect(Collectors.toSet());
			POINTS.removeAll(toRot);
			toRot.stream().map(p -> p.move(Integer.parseInt(rotMatcher.group(2)), 0)).forEach(POINTS::add);
		} else if (cmd.startsWith("rotate col")) {
			Pattern rotPattern = Pattern.compile("(\\d+) by (\\d+)");
			Matcher rotMatcher = rotPattern.matcher(cmd);
			rotMatcher.find();
			Set<Point> toRot = POINTS.stream().filter(p -> p.getX() == Integer.parseInt(rotMatcher.group(1)))
					.collect(Collectors.toSet());
			POINTS.removeAll(toRot);
			toRot.stream().map(p -> p.move(0, Integer.parseInt(rotMatcher.group(2)))).forEach(POINTS::add);
		}
		filterPoints();
		System.out.println(cmd);
		printPoints();
		System.out.println();
	}
	private static void filterPoints()
	{
		Set<Point> toWrap = POINTS.stream().filter(p -> p.getX() >= WIDTH || p.getY() >= HEIGHT).collect(Collectors.toSet());
		POINTS.removeIf(p -> p.getX() >= WIDTH || p.getY() >= HEIGHT);
		toWrap.stream().map(p -> new Point(p.getX() % WIDTH, p.getY() % HEIGHT)).forEach(POINTS::add);
	}
	private static void printPoints()
	{
		for (int x = 0 ; x < HEIGHT ; x++)
		{
			for (int y = 0 ; y < WIDTH ; y++)
			{
				if (POINTS.contains(new Point(y, x)))
				{
					System.out.print('#');
				} else {
					System.out.print('.');
				}
			}
			System.out.println();
		}
	}
}
