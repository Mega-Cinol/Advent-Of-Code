package y2015.day5;

import java.util.function.Function;

import common.Input;

public class Day5 {

	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2015/day5/day5.txt", Function.identity()).filter(Day5::validate).count());
		System.out.println(Input.parseLines("y2015/day5/day5.txt", Function.identity()).filter(Day5::validate2).count());
	}

	private static boolean validate(String str) {
		if (str.chars().filter(c -> c == 'a' || c == 'e' || c == 'u' || c == 'i' || c == 'o').count() < 3) {
			return false;
		}
		if (str.contains("cd") || str.contains("ab") || str.contains("pq") || str.contains("xy")) {
			return false;
		}
		for (int i = 1; i < str.length(); i++) {
			if (str.charAt(i) == str.charAt(i - 1)) {
				return true;
			}
		}
		return false;
	}

	private static boolean validate2(String str) {
		boolean pairFound = false;
		for (int i = 1; i < str.length() - 2; i++) {
			if (str.substring(i + 1).contains(str.substring(i - 1, i + 1))) {
				pairFound = true;
				break;
			}
		}
		if (!pairFound) {
			return false;
		}
		for (int i = 2; i < str.length(); i++) {
			if (str.charAt(i) == str.charAt(i - 2)) {
				return true;
			}
		}
		return false;
	}
}
