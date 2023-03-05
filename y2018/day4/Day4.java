package y2018.day4;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day4 {

	public static void main(String[] args) {
		Map<Integer, Map<Integer, Integer>> guardsAsleepAt = new HashMap<>();
		int guard = -1;
		int fallAsleepTime = -1;
		for (String entry : Input.parseLines("y2018/day4/day4.txt").sorted(new EventComparator()).collect(Collectors.toList()))
		{
			int minute = Integer.parseInt(entry.substring(15, 17));
			if (entry.endsWith("falls asleep"))
			{
				fallAsleepTime = minute;
			}
			if (entry.endsWith("wakes up"))
			{
				Map<Integer, Integer> minutesAsleep = guardsAsleepAt.computeIfAbsent(guard, k -> new HashMap<>());
				for (int i = fallAsleepTime ; i < minute ; i++)
				{
					minutesAsleep.merge(i, 1, (o,n)->o+n);
				}
				fallAsleepTime = -1;
			}
			if (entry.endsWith("shift"))
			{
				Pattern p = Pattern.compile(".*#(\\d+).*");
				Matcher m = p.matcher(entry);
				m.matches();
				int guardId = Integer.parseInt(m.group(1));
				guard = guardId;
			}
		}
		int maxSlept = 0;
		int sleepyGuard = 0;
		int favouriteMinute = -1;

		int mostSleepyMinute = -1;
		int mostSleepyMinuteSlept = 0;
		int guardWithMostSleepyMinute = -1;
		for (Map.Entry<Integer, Map<Integer, Integer>> guardAsleep : guardsAsleepAt.entrySet())
		{
			int slept = guardAsleep.getValue().values().stream().mapToInt(Integer::intValue).sum();
			if (slept > maxSlept)
			{
				maxSlept = slept;
				sleepyGuard = guardAsleep.getKey();
				int maxMinute = 0;
				for (int min = 0 ; min < 60 ; min++)
				{
					if (guardAsleep.getValue().getOrDefault(min, 0) > maxMinute)
					{
						maxMinute = guardAsleep.getValue().getOrDefault(min, 0);
						favouriteMinute = min;
					}
				}
			}
			for (int min = 0 ; min < 60 ; min++)
			{
				if (guardAsleep.getValue().getOrDefault(min, 0) > mostSleepyMinuteSlept)
				{
					mostSleepyMinuteSlept = guardAsleep.getValue().getOrDefault(min, 0);
					mostSleepyMinute = min;
					guardWithMostSleepyMinute = guardAsleep.getKey();
				}
			}
		}
		System.out.println(sleepyGuard);
		System.out.println(favouriteMinute);
		System.out.println(sleepyGuard * favouriteMinute);
		System.out.println("============");
		System.out.println(guardWithMostSleepyMinute);
		System.out.println(mostSleepyMinute);
		System.out.println(guardWithMostSleepyMinute * mostSleepyMinute);
	}

	private static class EventComparator implements Comparator<String>
	{
		@Override
		public int compare(String e1, String e2) {
			if (getMonth(e1) != getMonth(e2))
			{
				return getMonth(e1) - getMonth(e2);
			}
			if (getDay(e1) != getDay(e2))
			{
				return getDay(e1) - getDay(e2);
			}
			if (getHour(e1) != getHour(e2))
			{
				return getHour(e1) - getHour(e2);
			}
			if (getMinute(e1) != getMinute(e2))
			{
				return getMinute(e1) - getMinute(e2);
			}
			return 0;
		}

		private int getMonth(String event)
		{
			return Integer.parseInt(event.substring(6, 8));
		}
		private int getDay(String event)
		{
			return Integer.parseInt(event.substring(9, 11));
		}
		private int getHour(String event)
		{
			return Integer.parseInt(event.substring(12, 14));
		}
		private int getMinute(String event)
		{
			return Integer.parseInt(event.substring(15, 17));
		}
	}
}
