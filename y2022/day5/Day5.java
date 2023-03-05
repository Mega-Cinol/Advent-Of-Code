package y2022.day5;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day5 {

	public static void main(String[] args) {
		var input = Input.parseLines("y2022/day5/day5.txt").toList();

		// part 1
		var crates = createCrates(input);
		for (String line : input) {
			Pattern movePattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
			Matcher moveMatcher = movePattern.matcher(line);
			if (moveMatcher.matches()) {
				printCrates(crates);
				System.out.println(line);
				for (int i = 0; i < Integer.parseInt(moveMatcher.group(1)); i++) {
					int from = Integer.parseInt(moveMatcher.group(2)) - 1;
					int to = Integer.parseInt(moveMatcher.group(3)) - 1;
					char crate = crates.get(from).pop();
					crates.get(to).push(crate);
				}
			}
		}
		System.out.println(crates.stream().map(Deque::peek).map(c -> "" + c).collect(Collectors.joining()));

		crates = createCrates(input);
		for (String line : input) {
			Pattern movePattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
			Matcher moveMatcher = movePattern.matcher(line);
			if (moveMatcher.matches()) {
				printCrates(crates);
				System.out.println(line);
				int from = Integer.parseInt(moveMatcher.group(2)) - 1;
				int to = Integer.parseInt(moveMatcher.group(3)) - 1;
				Deque<Character> lift = new ArrayDeque<>();
				for (int i = 0; i < Integer.parseInt(moveMatcher.group(1)); i++) {
					lift.push(crates.get(from).pop());
				}
				while (!lift.isEmpty()) {
					crates.get(to).push(lift.pop());
				}
			}
		}
		System.out.println(crates.stream().map(Deque::peek).map(c -> "" + c).collect(Collectors.joining()));
	}

	private static List<Deque<Character>> createCrates(List<String> input) {
		var crates = new ArrayList<Deque<Character>>();
		for (int i = 0; i < 9; i++) {
			crates.add(new ArrayDeque<Character>());
		}

		for (String line : input) {
			if (" 1   2   3   4   5   6   7   8   9 ".equals(line)) {
				break;
			}
			int col = 0;
			while (col * 4 + 1 < line.length()) {
				if (line.charAt(1 + 4 * col) != ' ') {
					crates.get(col).addLast(line.charAt(1 + 4 * col));
				}
				col++;
			}
		}
		return crates;
	}

	private static void printCrates(List<Deque<Character>> crates) {
		List<Deque<Character>> copy = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			copy.add(new ArrayDeque<Character>(crates.get(i)));
		}
		List<String> out = new ArrayList<>();
		out.add(" 1   2   3   4   5   6   7   8   9 ");
		while (!copy.stream().allMatch(Deque::isEmpty)) {
			out.add(copy.stream().map(q -> {
				var c = q.pollLast();
				return c == null ? "   " : "[" + c + "]";
			}).collect(Collectors.joining(" ")));
		}
		for (int i = out.size() - 1; i >= 0; i--) {
			System.out.println(out.get(i));
		}
	}
}
