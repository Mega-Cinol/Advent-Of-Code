package y2022.day1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import common.Input;

public class Day1 {
	public static void main(String[] args) {
		var inputLines = Input.parseLines("y2022/day1/day1.txt").toList();

		var elvesCalories = new ArrayList<List<Integer>>();
		var currentElf = new ArrayList<Integer>();
		for (var calory : inputLines) {
			if (calory.isEmpty()) {
				elvesCalories.add(currentElf);
				currentElf = new ArrayList<Integer>();
			} else {
				currentElf.add(Integer.parseInt(calory));
			}
		}

		// Part 1
		System.out.println(
				elvesCalories.stream().map(list -> list.stream().collect(Collectors.summingInt(Integer::intValue)))
						.max(Integer::compare).get());

		// Part 2
		System.out.println(
				elvesCalories.stream().map(list -> list.stream().collect(Collectors.summingInt(Integer::intValue)))
						.sorted(Comparator.reverseOrder()).limit(3).collect(Collectors.summingInt(Integer::intValue)));
	}
}
