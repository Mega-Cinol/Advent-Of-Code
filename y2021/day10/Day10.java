package y2021.day10;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import common.Input;

public class Day10 {

	public static void main(String[] args) {
		// part1
		System.out.println(Input.parseLines("y2021/day10/day10.txt").mapToInt(Day10::getSyntaxErrorScore).sum());
		// part2
		List<Long> incompleteScores = Input.parseLines("y2021/day10/day10.txt").map(Day10::getIncompleteScore)
				.sorted()
				.filter(i -> i != 0).collect(Collectors.toList());
		System.out.println(incompleteScores.get(incompleteScores.size() / 2));

	}

	private static int getSyntaxErrorScore(String line) {
		Deque<Character> expectedClosing = new ArrayDeque<>();
		for (int i = 0; i < line.length(); i++) {
			switch (line.charAt(i)) {
			case '(':
			case '[':
			case '{':
			case '<':
				expectedClosing.push(closing(line.charAt(i)));
				break;
			case ')':
			case ']':
			case '}':
			case '>':
				if (line.charAt(i) != expectedClosing.pop()) {
					return score(line.charAt(i));
				}
				break;
			default:
				throw new IllegalArgumentException(line);
			}
		}
		return 0;
	}

	private static long getIncompleteScore(String line) {
		Deque<Character> expectedClosing = new ArrayDeque<>();
		for (int i = 0; i < line.length(); i++) {
			switch (line.charAt(i)) {
			case '(':
			case '[':
			case '{':
			case '<':
				expectedClosing.push(closing(line.charAt(i)));
				break;
			case ')':
			case ']':
			case '}':
			case '>':
				if (line.charAt(i) != expectedClosing.pop()) {
					return 0;
				}
				break;
			default:
				throw new IllegalArgumentException(line);
			}
		}
		return score(expectedClosing);
	}

	private static char closing(char opening) {
		switch (opening) {
		case '(':
			return ')';
		case '[':
			return ']';
		case '{':
			return '}';
		case '<':
			return '>';
		default:
			throw new IllegalArgumentException("" + opening);
		}
	}

	private static int score(char invlid) {
		switch (invlid) {
		case ')':
			return 3;
		case ']':
			return 57;
		case '}':
			return 1197;
		case '>':
			return 25137;
		default:
			throw new IllegalArgumentException("" + invlid);
		}
	}

	private static long score(Deque<Character> missing) {
		long score = 0;
		while (!missing.isEmpty()) {
			score *= 5;
			switch (missing.pop()) {
			case ')':
				score += 1;
				break;
			case ']':
				score += 2;
				break;
			case '}':
				score += 3;
				break;
			case '>':
				score += 4;
				break;
			default:
				throw new IllegalArgumentException("" + missing);
			}
		}
		return score;
	}
}
