package y2020.day13;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import common.Input;

public class Day13 {
	public static void main(String[] args) {
		NavigableSet<Integer> buses = new TreeSet<>(/*Comparator.comparingInt(Day13::getWaitingTime)*/);
		List<String> input = new ArrayList<>();
		Input.parseLines("y2020/day13/day13.txt", Function.identity(), input::add);
		Stream.of(input.get(1).split(",")).filter(id -> !id.equals("x")).map(Integer::parseInt).forEach(buses::add);
		System.out.println(buses);
		buses.stream()
				.forEach(busId -> System.out.println("BusId: " + busId + " waiting time; " + getWaitingTime(busId)));

		List<String> busTable = Arrays.asList(input.get(1).split(","));
		NavigableSet<Integer> ids = new TreeSet<Integer>();
		ids.addAll(buses);
		int current = ids.last();
		long step = current;
		long result = 0;
		while (ids.lower(current) != null) {
			int lower = ids.lower(current);
			long gap = busTable.indexOf(String.valueOf(ids.last())) - busTable.indexOf(String.valueOf(lower));
			gap %= lower;
			if (gap < 0) {
				gap = lower - gap;
			}
			result = gapTimestamp(lower, step, gap, result);
			step *= lower;
			current = lower;
			System.out.println(current + " and " + result);
		}
		long endTimeStamp = result - busTable.indexOf(ids.last().toString());
		System.out.println(endTimeStamp);
		for (int i = 0; i < busTable.size(); i++) {
			if (busTable.get(i).equals("x")) {
				continue;
			}
			int busIntId = Integer.parseInt(busTable.get(i));
			System.out.println("Bus with id: " + busIntId + " at " + i + " expected timestamp:" + (endTimeStamp + i)
					+ " difference: " + (endTimeStamp + i) % busIntId);
		}
	}

	private static int getWaitingTime(int busId) {
		return busId - (1007153 % busId);
	}

	private static long gapTimestamp(long lower, long higher, long gap, long initial) {
		long timestamp = initial;
		System.out.println("Get gap: " + lower + ", " + higher + ", " + gap + ", " + initial);
		while (timestamp % lower != gap) {
			timestamp += higher;
		}
		System.out.println("Return " + timestamp);
		return timestamp;
	}
}
