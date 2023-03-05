package y2022.day6;

import common.Input;

public class Day6 {

	public static void main(String[] args) {
		String msg = Input.parseLines("y2022/day6/day6.txt").findAny().get();

		System.out.println(findStart(msg, 4));
		System.out.println(findStart(msg, 14));
	}

	private static int findStart(String msg, int size) {
		for (int i = 0; i < msg.length() - size + 1; i++) {
			if (msg.chars().skip(i).limit(size).distinct().count() == size) {
				return i + size;
			}
		}
		return -1;
	}
}
