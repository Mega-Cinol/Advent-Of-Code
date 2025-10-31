package y2015.day6;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day6 {
	private static final String TURN_ON = "turn on";
	private static final String TURN_OFF = "turn off";
	private static final String TOGGLE = "toggle";

	public static void main(String[] args) {
		Map<Point, Integer> lightsOn = new HashMap<>();
		Input.parseLines("y2015/day6/day6.txt", Day6::toCommand, cmd -> {/* System.out.println(lightsOn.size());*/ cmd.accept(lightsOn);});
		System.out.println(lightsOn.values().stream().mapToInt(Integer::intValue).sum());
	}

	private static Consumer<Map<Point, Integer>> toCommand(String input)
	{
//		System.out.println("Parsing: " + input);
		if (input.startsWith(TURN_ON))
		{
			return lights -> parseRange(input).forEach(light -> on(lights, light));
		}
		else if (input.startsWith(TURN_OFF))
		{
			return lights -> parseRange(input).forEach(light -> off(lights, light));
		}
		else if (input.startsWith(TOGGLE))
		{
			return lights -> parseRange(input).forEach(light -> toggle(lights, light));
		}
		throw new IllegalArgumentException(input);
	}

	private static void toggle(Map<Point, Integer> lights, Point light)
	{
		lights.merge(light, 2, (oldBrightness, newBrightness) -> oldBrightness + newBrightness);
	}

	private static void on(Map<Point, Integer> lights, Point light)
	{
		lights.merge(light, 1, (oldBrightness, newBrightness) -> oldBrightness + newBrightness);
	}

	private static void off(Map<Point, Integer> lights, Point light)
	{
		lights.computeIfPresent(light, (key, current) -> Math.max(0, current - 1));
	}

	private static Stream<Point> parseRange(String range)
	{
		Pattern rangePattern = Pattern.compile("([0-9]*),([0-9]*) through ([0-9]*),([0-9]*)");
		Matcher rangeMatcher = rangePattern.matcher(range);

		if (!rangeMatcher.find())
		{
			throw new IllegalArgumentException(range);
		}

		Point first = new Point(Integer.valueOf(rangeMatcher.group(1)), Integer.valueOf(rangeMatcher.group(2)));
		Point last = new Point(Integer.valueOf(rangeMatcher.group(3)), Integer.valueOf(rangeMatcher.group(4)));

		return Point.range(first, last);
	}
}
