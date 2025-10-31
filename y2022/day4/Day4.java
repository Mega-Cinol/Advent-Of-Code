package y2022.day4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day4 {

	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2022/day4/day4.txt", Day4::fullyOverlap).filter(p -> p == true).count());
		System.out.println(Input.parseLines("y2022/day4/day4.txt", Day4::partiallyOverlap).filter(p -> p == true).count());
	}

	private static boolean fullyOverlap(String ranges) {
		Pattern rangesPattern = Pattern.compile("(\\d+)-(\\d+),(\\d+)-(\\d+)");
		Matcher rangesMatcher = rangesPattern.matcher(ranges);
		if (!rangesMatcher.matches()) {
			throw new IllegalArgumentException(ranges);
		}
		int firstLow = Integer.parseInt(rangesMatcher.group(1));
		int firstHigh = Integer.parseInt(rangesMatcher.group(2));
		int secondLow = Integer.parseInt(rangesMatcher.group(3));
		int secondHigh = Integer.parseInt(rangesMatcher.group(4));

		return (firstLow <= secondLow && firstHigh >= secondHigh) || (firstLow >= secondLow && firstHigh <= secondHigh);
	}

	private static boolean partiallyOverlap(String ranges) {
		Pattern rangesPattern = Pattern.compile("(\\d+)-(\\d+),(\\d+)-(\\d+)");
		Matcher rangesMatcher = rangesPattern.matcher(ranges);
		if (!rangesMatcher.matches()) {
			throw new IllegalArgumentException(ranges);
		}
		int firstLow = Integer.parseInt(rangesMatcher.group(1));
		int firstHigh = Integer.parseInt(rangesMatcher.group(2));
		int secondLow = Integer.parseInt(rangesMatcher.group(3));
		int secondHigh = Integer.parseInt(rangesMatcher.group(4));

		return (firstHigh >= secondLow) && (secondHigh >= firstLow);
	}
}
