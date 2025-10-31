package y2023.day6;

import java.util.HashSet;
import java.util.regex.Pattern;

import common.Input;

public class Day6 {

	public static void main(String[] args) {
		var input = Input.parseLines("y2023/day6/day6.txt").toList();
		System.out.println(allWaysToWin(input.get(0), input.get(1)));
		System.out.println(allWaysToWin(input.get(0).replaceAll("\\s", ""), input.get(1).replaceAll("\\s", "")));
	}

	private static long allWaysToWin(String raceTimes, String records) {
		var numberPattern = Pattern.compile("\\d+");
		var raceTimeMatcher = numberPattern.matcher(raceTimes);
		var recordMatcher = numberPattern.matcher(records);

		var races = new HashSet<Race>();
		while (raceTimeMatcher.find() && recordMatcher.find()) {
			races.add(new Race(Long.parseLong(raceTimeMatcher.group()), Long.parseLong(recordMatcher.group())));
		}
		return races.stream().mapToLong(Day6::waysToWin).reduce(1, (a, b) -> a* b);
	}

	private static long waysToWin(Race race) {
		// -x^2 + raceTime * x - record > 0
		var delta = race.raceTime * race.raceTime - 4 * race.record;
		var lower = (long) Math.ceil((race.raceTime - Math.sqrt(delta)) / 2);
		var higher = (long) Math.floor((race.raceTime + Math.sqrt(delta)) / 2);
		return higher - lower + 1;
	}

	private static record Race(long raceTime, long record) {
	}
}
