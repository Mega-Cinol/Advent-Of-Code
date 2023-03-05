package y2015.day14;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day14 {
	private static final int TIME = 2503;

	public static void main(String[] args) {
		Map<Reindeer, Integer> score = new HashMap<>();
		Input.parseLines("y2015/day14/day14.txt", Reindeer::of, r -> score.put(r, 0));
		for (int i = 1 ; i <= TIME ; i++)
		{
			int time = i;
			Queue<Reindeer> sorted = score.keySet().stream().sorted((r1, r2) -> r2.travel(time) - r1.travel(time))
					.collect(Collectors.toCollection(ArrayDeque::new));
			int best = sorted.element().travel(i);
			System.out.println(best);
			sorted.stream().filter(r -> r.travel(time) == best).forEach(r -> score.merge(r, 1, (o, n) -> o + n));
		}
		score.values().stream().sorted(java.util.Comparator.reverseOrder()).findFirst().ifPresent(System.out::println);
	}

	private static class Reindeer
	{
		private final int speed;
		private final int activeTime;
		private final int restTime;

		private Reindeer(int speed, int activeTime, int restTime)
		{
			this.speed = speed;
			this.activeTime = activeTime;
			this.restTime = restTime;
		}

		public static Reindeer of(String description)
		{
			Pattern speedPattern = Pattern
					.compile("[A-Za-z]+ can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds\\.");
			Matcher speedMatcher = speedPattern.matcher(description);
			if (!speedMatcher.matches()) {
				throw new IllegalArgumentException(description);
			}
			return new Reindeer(Integer.parseInt(speedMatcher.group(1)), Integer.parseInt(speedMatcher.group(2)),
					Integer.parseInt(speedMatcher.group(3)));
		}

		public int travel(int seconds)
		{
			return (seconds / (activeTime + restTime)) * speed * activeTime
					+ speed * Math.min(seconds % (activeTime + restTime), activeTime);
		}
	}
}
