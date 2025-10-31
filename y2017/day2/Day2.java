package y2017.day2;

import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day2 {

	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2017/day2/day2.txt", Day2::rowValue).mapToInt(Integer::intValue).sum());
		System.out.println(Input.parseLines("y2017/day2/day2.txt", Day2::rowValue2).mapToInt(Integer::intValue).sum());
	}

	private static int rowValue(String row) {
		IntSummaryStatistics stat = Stream.of(row.split(",")).mapToInt(Integer::parseInt).summaryStatistics();
		return stat.getMax() - stat.getMin();
	}
	private static int rowValue2(String row) {
		Set<Integer> nums = Stream.of(row.split(",")).map(Integer::valueOf).collect(Collectors.toSet());
		int v1 = nums.stream().filter(v -> nums.stream().filter(v2 -> v2 != v).anyMatch(v2 -> v % v2 == 0)).findAny()
				.get();
		int v3 = nums.stream().filter(v -> nums.stream().filter(v2 -> v2 != v).anyMatch(v2 -> v2 % v == 0)).findAny()
				.get();
		return v1 / v3;
	}
}
