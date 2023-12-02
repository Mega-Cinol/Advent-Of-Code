package y2023.day1;

import java.util.Map;

import common.Input;

public class Day1 {

	private static final Map<String, String> NUMBER_MAP = Map.ofEntries(
			Map.entry("one", "1"),
			Map.entry("two", "2"),
			Map.entry("three", "3"),
			Map.entry("four", "4"),
			Map.entry("five", "5"),
			Map.entry("six", "6"),
			Map.entry("seven", "7"),
			Map.entry("eight", "8"),
			Map.entry("nine", "9")
			);
	public static void main(String[] args) {
		// part1
		System.out.println(Input.parseLines("y2023/day1/day1.txt", Day1::rowValue).mapToLong(Integer::longValue).sum());
		// part2
		System.out.println(Input.parseLines("y2023/day1/day1.txt", Day1::rowValue2).mapToLong(Integer::longValue).sum());
	}

	private static Integer rowValue2(String row) {
		var firstDigit = "";
		for (int i = 0 ; i < row.length() ; i++) {
			if (Character.isDigit(row.charAt(i))) {
				firstDigit = "" + row.charAt(i);
				break;
			} else {
				var textDigit = isTextDigit(row, i);
				if (textDigit != null) {
					firstDigit = textDigit;
					break;
				}
			}
		}
		var lastDigit = "";
		for (int i = row.length() - 1 ; i >= 0 ; i--) {
			if (Character.isDigit(row.charAt(i))) {
				lastDigit = "" + row.charAt(i);
				break;
			} else {
				var textDigit = isTextDigitLast(row, i);
				if (textDigit != null) {
					lastDigit = textDigit;
					break;
				}
			}
		}
		return Integer.parseInt(firstDigit + lastDigit);
	}

	private static String isTextDigit(String row, int offset) {
		return NUMBER_MAP.entrySet().stream().filter(e -> row.startsWith(e.getKey(), offset)).findAny().map(Map.Entry::getValue).orElse(null);
	}

	private static String isTextDigitLast(String row, int offset) {
		return NUMBER_MAP.entrySet().stream().filter(e -> row.substring(0, offset + 1).endsWith(e.getKey())).findAny().map(Map.Entry::getValue).orElse(null);
	}

	private static Integer rowValue(String row) {
		char firstDigit = '0';
		for (int i = 0 ; i < row.length() ; i++) {
			if (Character.isDigit(row.charAt(i))) {
				firstDigit = row.charAt(i);
				break;
			}
		}
		char lastDigit = '0';
		for (int i = row.length() - 1 ; i >= 0 ; i--) {
			if (Character.isDigit(row.charAt(i))) {
				lastDigit = row.charAt(i);
				break;
			}
		}
		return Integer.parseInt("" + firstDigit + lastDigit);
	}
}
