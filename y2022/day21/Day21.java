package y2022.day21;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day21 {

	private final static Pattern OP_PATTERN = Pattern.compile("([a-z]+) (.) ([a-z]+)");

	public static void main(String[] args) {
		var monkeys = Input.parseLines("y2022/day21/day21.txt").map(s -> s.split(": "))
				.collect(Collectors.toMap(a -> a[0], a -> a[1]));
		var root = evaluate(monkeys, "root");
		System.out.println(root);

		var undoStack = new ArrayDeque<LongUnaryOperator>();
		var isLeft = populateUndoStackAndIsLeft(monkeys, undoStack);
		var rootMatcher = OP_PATTERN.matcher(monkeys.get("root"));
		if (!rootMatcher.matches()) {
			throw new IllegalArgumentException(monkeys.get("root"));
		}
		long expectedValue = isLeft ? evaluate(monkeys, rootMatcher.group(3)) : evaluate(monkeys, rootMatcher.group(1));
		while (!undoStack.isEmpty()) {
			expectedValue = undoStack.pop().applyAsLong(expectedValue);
		}
		System.out.println(expectedValue);
	}

	private static long evaluate(Map<String, String> monkeys, String current) {
		if (monkeys.get(current).matches("\\d+")) {
			return Long.parseLong(monkeys.get(current));
		} else {
			var m = OP_PATTERN.matcher(monkeys.get(current));
			if (!m.matches()) {
				throw new IllegalArgumentException(monkeys.get(current));
			}
			var left = evaluate(monkeys, m.group(1));
			var right = evaluate(monkeys, m.group(3));
			LongBinaryOperator op = switch (m.group(2)) {
			case "+" -> (l, r) -> l + r;
			case "-" -> (l, r) -> l - r;
			case "*" -> (l, r) -> l * r;
			case "/" -> (l, r) -> l / r;
			default -> throw new IllegalArgumentException("Unexpected value: " + m.group(2));
			};
			return op.applyAsLong(left, right);
		}
	}

	private static boolean populateUndoStackAndIsLeft(Map<String, String> monkeys, Deque<LongUnaryOperator> undoStack) {
		var previous = "humn";
		var current = findPreviousMonkey(monkeys, previous);
		while (!current.equals("root")) {
			var m = OP_PATTERN.matcher(monkeys.get(current));
			if (!m.matches()) {
				throw new IllegalArgumentException(monkeys.get(current));
			}
			boolean isLeft = m.group(1).equals(previous);
			long other = isLeft ? evaluate(monkeys, m.group(3)) : evaluate(monkeys, m.group(1));
			undoStack.push(switch (m.group(2)) {
			case "+" -> v -> v - other;
			case "-" -> v -> isLeft ? v + other : other - v;
			case "*" -> v -> v / other;
			case "/" -> v -> isLeft ? v * other : other / v;
			default -> throw new IllegalArgumentException("Unexpected value: " + m.group(2));
			});
			previous = current;
			current = findPreviousMonkey(monkeys, current);
		}
		var m = OP_PATTERN.matcher(monkeys.get(current));
		if (!m.matches()) {
			throw new IllegalArgumentException(monkeys.get(current));
		}
		return m.group(1).equals(previous);
	}

	private static String findPreviousMonkey(Map<String, String> monkeys, String current) {
		return monkeys.entrySet().stream().filter(e -> e.getValue().contains(current)).map(Map.Entry::getKey).findFirst().get();
	}
}
