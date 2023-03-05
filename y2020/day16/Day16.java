package y2020.day16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day16 {

	public static void main(String[] args) {
		TicketsRegistry reg = new TicketsRegistry();
		Input.parseLines("y2020/day16/day16.txt", Function.identity(), reg::parseLine);
		System.out.println(reg.getScanningErrorRate());
		System.out.println(reg.getDeparture());
	}

	private static class TicketsRegistry {
		private static final Pattern RULE_PATTERN = Pattern
				.compile("([a-z\\ ]*): ([0-9]*)-([0-9]*) or ([0-9]*)-([0-9]*)");

		private final Map<String, Set<Range>> rules = new HashMap<>();
		private final List<Integer> myTicket = new ArrayList<>();
		private final Set<List<Integer>> otherTickets = new HashSet<>();

		private enum ParsingState {
			RULES, OWN_TICKET, OTHER_TICKETS;
		}

		private ParsingState state = ParsingState.RULES;

		public void addRule(String rule) {
			Matcher matcher = RULE_PATTERN.matcher(rule);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(rule);
			}
			Range range1 = new Range(Integer.valueOf(matcher.group(2)), Integer.valueOf(matcher.group(3)));
			Range range2 = new Range(Integer.valueOf(matcher.group(4)), Integer.valueOf(matcher.group(5)));
			Set<Range> ranges = new HashSet<>();
			ranges.add(range1);
			ranges.add(range2);
			rules.put(matcher.group(1), ranges);
		}

		public void setMyTicket(String ticket) {
			Stream.of(ticket.split(",")).map(Integer::valueOf).forEach(myTicket::add);
		}

		public void addOtherTicket(String ticket) {
			otherTickets.add(Stream.of(ticket.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
		}

		public void parseLine(String line) {
			if (line.equals("")) {
				return;
			}
			switch (state) {
			case RULES:
				if ("your ticket:".equals(line)) {
					state = ParsingState.OWN_TICKET;
				} else {
					addRule(line);
				}
				break;
			case OWN_TICKET:
				if ("nearby tickets:".equals(line)) {
					state = ParsingState.OTHER_TICKETS;
				} else {
					setMyTicket(line);
				}
				break;
			case OTHER_TICKETS:
				addOtherTicket(line);
			default:
				break;
			}
		}

		public long getScanningErrorRate() {
			Predicate<Integer> valueValid = num -> rules.values().stream().flatMap(Set::stream)
					.anyMatch(range -> range.inRange(num));
			return otherTickets.stream().flatMap(List::stream).filter(valueValid.negate()).mapToInt(Integer::intValue)
					.sum();
		}

		public long getDeparture() {
			Map<String, Integer> positions = determinePositions();
			System.out.println(positions);
			return positions.entrySet().stream().filter(e -> e.getKey().startsWith("departure"))
					.map(Map.Entry::getValue).peek(System.out::println).mapToLong(myTicket::get)
					.reduce(1, (a, b) -> a * b);
		}

		private Map<String, Integer> determinePositions() {
			Set<List<Integer>> validTickets = new HashSet<>();
			otherTickets.stream().filter(this::isTicketValid).forEach(validTickets::add);

			Map<String, List<Integer>> possiblePositions = new HashMap<>();
			for (int i = 0; i < myTicket.size(); i++) {
				final int idx = i;
				for (Map.Entry<String, Set<Range>> rule : rules.entrySet()) {
					if (rule.getValue().stream().anyMatch(range -> range.inRange(myTicket.get(idx)))) {
						List<Integer> positionsForRule = possiblePositions.computeIfAbsent(rule.getKey(),
								key -> new ArrayList<>());
						positionsForRule.add(i);
					}
				}
			}

			for (List<Integer> ticket : validTickets) {
				for (int i = 0; i < ticket.size(); i++) {
					final int idx = i;
					for (Map.Entry<String, Set<Range>> rule : rules.entrySet()) {
						if (rule.getValue().stream().noneMatch(range -> range.inRange(ticket.get(idx)))) {
							possiblePositions.get(rule.getKey()).remove(Integer.valueOf(i));
						}
					}
				}
			}
			Map<String, Integer> result = new HashMap<String, Integer>();
			while (!possiblePositions.isEmpty()) {
				possiblePositions.entrySet().stream().filter(e -> e.getValue().size() == 1)
						.forEach(e -> result.put(e.getKey(), e.getValue().get(0)));
				result.keySet().forEach(key -> possiblePositions.remove(key));
				result.values()
						.forEach(value -> possiblePositions.values().forEach(list -> list.remove(Integer.valueOf(value))));
			}
			return result;
		}

		private boolean isTicketValid(List<Integer> ticket) {
			Predicate<Integer> valueValid = num -> rules.values().stream().flatMap(Set::stream)
					.anyMatch(range -> range.inRange(num));
			return ticket.stream().allMatch(valueValid);
		}
	}

	private static class Range {
		private final int lower;
		private final int higher;

		public Range(int lower, int higher) {
			this.lower = lower;
			this.higher = higher;
		}

		public boolean inRange(int number) {
			return number <= higher && number >= lower;
		}
	}
}
