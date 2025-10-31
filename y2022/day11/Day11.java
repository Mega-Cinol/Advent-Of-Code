package y2022.day11;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day11 {

	private static final Pattern ITEMS_PATTERN = Pattern.compile("Starting items: ((\\d+, )*\\d+)");
	private static final Pattern OPERATOR_PATTERN = Pattern.compile("Operation: new = old ([\\*'\\+]) (\\d+|old)");
	private static final Pattern DIVISIBLE_PATTERN = Pattern.compile("Test: divisible by (\\d+)");
	private static final Pattern IF_TRUE_PATTERN = Pattern.compile("If true: throw to monkey (\\d+)");
	private static final Pattern IF_FALSE_PATTERN = Pattern.compile("If false: throw to monkey (\\d+)");

	public static void main(String[] args) {
		part1();
		part2();
	}

	// broken...
	private static final void part1() {
		var items = new ArrayList<Item>();
		var monkeys = loadMonkeys(items, WorryLevelPart1::new);
		items.forEach(i -> i.initWorryLevel(monkeys.stream().map(Monkey::getDivisibleBy).collect(Collectors.toSet())));
		for (long i = 0; i < 20; i++) {
			monkeys.forEach(Monkey::turn);
		}
		System.out.println(monkeys.stream().map(Monkey::getInspectedCount).sorted(Comparator.reverseOrder()).limit(2)
				.reduce((a, b) -> a * b).get());
	}

	private static final void part2() {
		var items = new ArrayList<Item>();
		var monkeys = loadMonkeys(items, WorryLevelPart2::new);
		items.forEach(i -> i.initWorryLevel(monkeys.stream().map(Monkey::getDivisibleBy).collect(Collectors.toSet())));
		for (long i = 0; i < 10000; i++) {
			monkeys.forEach(Monkey::turnNoRelax);
		}
		System.out.println(monkeys.stream().map(Monkey::getInspectedCount).sorted(Comparator.reverseOrder()).limit(2)
				.reduce((a, b) -> a * b).get());
	}

	private static List<Monkey> loadMonkeys(List<Item> itemz, LongFunction<WorryLevel> worryLevelCreator) {
		itemz.clear();
		var input = Input.parseLines("y2022/day11/day11.txt").toList();
		Map<Long, String> targetMonkeys = new HashMap<>();
		var monkeys = new ArrayList<Monkey>();
		Deque<Item> items = null;
		Consumer<WorryLevel> monkeyOperator = null;
		long divisibleBy = 0;
		for (var line : input) {
			var itemsMatcher = ITEMS_PATTERN.matcher(line);
			if (itemsMatcher.find()) {
				items = Stream.of(itemsMatcher.group(1).split(", ")).map(Long::valueOf).map(wl -> new Item(wl, worryLevelCreator))
						.collect(Collectors.toCollection(ArrayDeque::new));
				itemz.addAll(items);
			}
			var operatorMatcher = OPERATOR_PATTERN.matcher(line);
			if (operatorMatcher.find()) {
				monkeyOperator = switch (operatorMatcher.group(1)) {
				case "+" -> operatorMatcher.group(2).equals("old") ? (o -> {
				}) : (o -> o.add(Long.parseLong(operatorMatcher.group(2))));
				case "*" -> operatorMatcher.group(2).equals("old") ? (o -> o.square())
						: (o -> o.mul(Long.parseLong(operatorMatcher.group(2))));
				default -> throw new IllegalArgumentException("Unexpected value: " + operatorMatcher.group(0));
				};
			}
			var divisibleMatcher = DIVISIBLE_PATTERN.matcher(line);
			if (divisibleMatcher.find()) {
				divisibleBy = Long.parseLong(divisibleMatcher.group(1));
			}
			if (IF_FALSE_PATTERN.matcher(line).find() || IF_TRUE_PATTERN.matcher(line).find()) {
				targetMonkeys.merge((long) monkeys.size(), line, (old, newLine) -> old + newLine);
			}
			if (line.isBlank()) {
				Monkey monkey = new Monkey(monkeyOperator, divisibleBy, items);
				monkeys.add(monkey);
			}
		}
		Monkey monkey = new Monkey(monkeyOperator, divisibleBy, items);
		monkeys.add(monkey);
		targetMonkeys.entrySet().stream().forEach(e -> {
			var trueMatcher = IF_TRUE_PATTERN.matcher(e.getValue());
			trueMatcher.find();
			var trueTarget = monkeys.get((int) Long.parseLong(trueMatcher.group(1)));
			var falseMatcher = IF_FALSE_PATTERN.matcher(e.getValue());
			falseMatcher.find();
			var falseTarget = monkeys.get((int) Long.parseLong(falseMatcher.group(1)));
			monkeys.get(e.getKey().intValue()).setIfTrue(trueTarget);
			monkeys.get(e.getKey().intValue()).setIfFalse(falseTarget);
		});
		return monkeys;
	}

	private static class Item {
		private WorryLevel worryLevel;

		public Item(long worryLevel, LongFunction<WorryLevel> worryLevelCreator) {
			this.worryLevel = worryLevelCreator.apply(worryLevel);
		}

		public void inspectByMonkey(Consumer<WorryLevel> worryLevelOperator) {
			worryLevelOperator.accept(worryLevel);
		}

		public void relax() {
			worryLevel.relax();
		}

		public long getWorryLevelReminder(long div) {
			return worryLevel.getRemainder(div);
		}

		public void initWorryLevel(Set<Long> divs) {
			worryLevel.initRemainders(divs);
		}
	}

	private interface WorryLevel {
		void initRemainders(Set<Long> divideBy);

		void mul(long val);

		void add(long val);

		void square();

		long getRemainder(long div);

		void relax();
	}

	private static class WorryLevelPart2 implements WorryLevel {
		private final Map<Long, Long> remainders = new HashMap<>();
		private final long initialValue;

		public WorryLevelPart2(long initialValue) {
			this.initialValue = initialValue;
		}

		@Override
		public void initRemainders(Set<Long> divideBy) {
			divideBy.forEach(div -> remainders.put(div, initialValue % div));
		}

		@Override
		public void mul(long val) {
			remainders.replaceAll((div, remainder) -> (remainder * val) % div);
		}

		@Override
		public void add(long val) {
			remainders.replaceAll((div, remainder) -> (remainder + val) % div);
		}

		@Override
		public void square() {
			remainders.replaceAll((div, remainder) -> (remainder * remainder) % div);
		}

		@Override
		public long getRemainder(long div) {
			return remainders.get(div);
		}

		@Override
		public void relax() {
			throw new UnsupportedOperationException();
		}
	}

	private static class WorryLevelPart1 implements WorryLevel {
		private long initialValue;

		public WorryLevelPart1(long initialValue) {
			this.initialValue = initialValue;
		}

		@Override
		public void initRemainders(Set<Long> divideBy) {
		}

		@Override
		public void mul(long val) {
			initialValue *= val;
		}

		@Override
		public void add(long val) {
			initialValue *= val;
		}

		@Override
		public void square() {
			initialValue *= initialValue;
		}

		@Override
		public long getRemainder(long div) {
			return initialValue % div;
		}

		@Override
		public void relax() {
			initialValue /= 3;
		}
	}

	private static class Monkey {
		private final Consumer<WorryLevel> inspectOperator;
		private final long divisibleBy;

		public long getDivisibleBy() {
			return divisibleBy;
		}

		private Monkey ifTrue;
		private Monkey ifFalse;
		private final Deque<Item> items = new ArrayDeque<>();
		private long inspectedCount;

		public Monkey(Consumer<WorryLevel> inspectOperator, long divisibleBy, Deque<Item> items) {
			this.inspectOperator = inspectOperator;
			this.divisibleBy = divisibleBy;
			this.items.addAll(items);
		}

		public void turn() {
			items.stream().forEach(item -> {
				item.inspectByMonkey(inspectOperator);
				inspectedCount++;
				item.relax();
				var targetMonkey = item.getWorryLevelReminder(divisibleBy) == 0 ? ifTrue : ifFalse;
				targetMonkey.getItems().addLast(item);
			});
			items.clear();
		}

		public void turnNoRelax() {
			items.stream().forEach(item -> {
				item.inspectByMonkey(inspectOperator);
				inspectedCount++;
				var targetMonkey = item.getWorryLevelReminder(divisibleBy) == 0 ? ifTrue : ifFalse;
				targetMonkey.getItems().addLast(item);
			});
			items.clear();
		}

		public void setIfTrue(Monkey ifTrue) {
			this.ifTrue = ifTrue;
		}

		public void setIfFalse(Monkey ifFalse) {
			this.ifFalse = ifFalse;
		}

		public Deque<Item> getItems() {
			return items;
		}

		public long getInspectedCount() {
			return inspectedCount;
		}
	}
}
