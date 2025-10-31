package y2020.day1;

import java.io.IOException;
import java.util.NavigableSet;
import java.util.TreeSet;

import common.Input;

public class Day1 {
	public static void main(String[] args) throws IOException {
		NavigableSet<Integer> numbers = new TreeSet<Integer>();
		Input.parseLines("y2020/day1/day1.txt", Integer::parseInt, numbers::add);
		for (int x : numbers) {
			int y = numbers.higher(x);
			while (x + y < 2020) {
				if (numbers.contains(2020 - x - y)) {
					System.out.println((2020 - x - y) * x * y);
					return;
				}
				y = numbers.higher(y);
			}
		}
	}
}
