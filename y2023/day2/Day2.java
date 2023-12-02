package y2023.day2;

import java.util.regex.Pattern;

import common.Input;

public class Day2 {

	public static void main(String[] args) {
		// part 1
		System.out.println(Input.parseLines("y2023/day2/day2.txt").mapToLong(Day2::gamePossible).sum());
		// part 2
		System.out.println(Input.parseLines("y2023/day2/day2.txt").mapToLong(Day2::gamePower).sum());
	}

	private static long gamePossible(String game) {
		var red = checkColor(game, "red", 12);
		var blue = checkColor(game, "blue", 14);
		var green = checkColor(game, "green", 13);
		if (red && green && blue) {
			var gameIdMatcher = Pattern.compile("Game (\\d+):").matcher(game);
			if (!gameIdMatcher.find()) {
				throw new IllegalArgumentException(game);
			}
			return Long.valueOf(gameIdMatcher.group(1));
		}
		return 0;
	}

	private static long gamePower(String game) {
		var red = minColorAmount(game, "red");
		var blue = minColorAmount(game, "blue");
		var green = minColorAmount(game, "green");
		return red * blue * green;
	}

	private static boolean checkColor(String game, String color, long limit) {
		return minColorAmount(game, color) <= limit;
	}

	private static long minColorAmount(String game, String color) {
		var colorPattern = Pattern.compile("(\\d+) " + color);
		var gameMatcher = colorPattern.matcher(game);
		var minCount = 0L;
		while(gameMatcher.find()) {
			var count = Long.parseLong(gameMatcher.group(1));
			if (count > minCount) {
				minCount = count;
			}
		}
		return minCount;
	}
}
