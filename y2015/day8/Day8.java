package y2015.day8;

import common.Input;

public class Day8 {
	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2015/day8/day8.txt", String::length).mapToInt(Integer::intValue).sum());
		System.out.println(Input.parseLines("y2015/day8/day8.txt", Day8::realLength).mapToInt(Integer::intValue).sum());
		System.out.println(Input.parseLines("y2015/day8/day8.txt", Day8::encodedLength).mapToInt(Integer::intValue).sum());
	}

	private static int realLength(String input) {
		int escaped = 2;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '\\') {
				if (input.charAt(i + 1) != 'x') {
					escaped++;
					i++;
				} else {
					escaped += 3;
					i += 3;
				}
			}
		}
		return input.length() - escaped;
	}

	private static int encodedLength(String input) {
		int escaped = 2;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '\\' || input.charAt(i) == '"') {
				escaped++;
			}
		}
		return input.length() + escaped;
	}
}
