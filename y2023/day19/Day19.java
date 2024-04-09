package y2023.day19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.AdventSolution;
import common.Pair;

public class Day19 extends AdventSolution {

	public static void main(String[] args) {
		new Day19().solve();
	}

	private static record Part(long x, long m, long a, long s) {
		public static Part from(String partDesc) {
			var pattern = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}");
			var matcher = pattern.matcher(partDesc);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(partDesc);
			}
			return new Part(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)),
					Long.parseLong(matcher.group(3)), Long.parseLong(matcher.group(4)));
		}

		public long rating() {
			return x + m + a + s;
		}
	}

	private static class Condition {
		private final Predicate<Part> condition;
		private final String outcome;

		private Condition(Predicate<Part> condition, String outcome) {
			this.condition = condition;
			this.outcome = outcome;
		}

		public static Condition from(String conditionDesc) {
			if (conditionDesc.indexOf(':') == -1) {
				return new Condition(p -> true, conditionDesc);
			}
			var pattern = Pattern.compile("([xmas])([<>])(\\d+):([a-zAR]+)");
			var matcher = pattern.matcher(conditionDesc);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(conditionDesc);
			}
			ToLongFunction<Part> attributeExtractor = switch (matcher.group(1)) {
			case "x" -> Part::x;
			case "m" -> Part::m;
			case "a" -> Part::a;
			case "s" -> Part::s;
			default -> throw new IllegalArgumentException(matcher.group(1));
			};
			var gt = ">".equals(matcher.group(2));
			var value = Long.parseLong(matcher.group(3));
			Predicate<Part> predicate = p -> gt ? attributeExtractor.applyAsLong(p) > value
					: attributeExtractor.applyAsLong(p) < value;
			var outcome = matcher.group(4);
			return new Condition(predicate, outcome);
		}

		public boolean test(Part part) {
			return condition.test(part);
		}

		public String getOutcome() {
			return outcome;
		}
	}

	private static class Workflow {
		private final String name;
		private final List<Condition> conditions;

		private Workflow(String name, List<Condition> conditions) {
			this.name = name;
			this.conditions = conditions;
		}

		public static Workflow from(String workflowDesc) {
			var pattern = Pattern.compile("([a-z]+)\\{(.*)\\}");
			var matcher = pattern.matcher(workflowDesc);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(workflowDesc);
			}
			var name = matcher.group(1);
			var conditions = Arrays.stream(matcher.group(2).split(",")).map(Condition::from).toList();
			return new Workflow(name, conditions);
		}

		public String getName() {
			return name;
		}

		public String process(Part part) {
			return conditions.stream().filter(condition -> condition.test(part)).map(Condition::getOutcome).findFirst()
					.get();
		}
	}

	private static class System {
		private Map<String, Workflow> workflows = new HashMap<>();

		public void addWorkflow(Workflow workflow) {
			workflows.put(workflow.getName(), workflow);
		}

		public boolean process(Part part) {
			var outcome = "in";
			while (!outcome.equals("A") && !outcome.equals("R")) {
				outcome = workflows.get(outcome).process(part);
			}
			return outcome.equals("A");
		}
	}

	@Override
	public Object part1Solution() {
		var input = getInput().toList();
		var system = new System();
		input.subList(0, input.indexOf("")).stream().map(Workflow::from).forEach(system::addWorkflow);
		return input.subList(input.indexOf("") + 1, input.size()).stream().map(Part::from).filter(system::process)
				.mapToLong(Part::rating).sum();
	}

	private static record Part2Part(Set<Pair<Long>> x, Set<Pair<Long>> m, Set<Pair<Long>> a, Set<Pair<Long>> s) {
	}

	private static class ConditionPart2 {
		private final char attributeExtractor;
		private final boolean isGt;
		private final long value;
		private final String outcome;

		private ConditionPart2(char attributeExtractor, boolean isGt, long value, String outcome) {
			this.attributeExtractor = attributeExtractor;
			this.isGt = isGt;
			this.value = value;
			this.outcome = outcome;
		}

		public static ConditionPart2 from(String conditionDesc) {
			if (conditionDesc.indexOf(':') == -1) {
				return new ConditionPart2('0', false, 0L, conditionDesc);
			}
			var pattern = Pattern.compile("([xmas])([<>])(\\d+):([a-zAR]+)");
			var matcher = pattern.matcher(conditionDesc);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(conditionDesc);
			}
			var gt = ">".equals(matcher.group(2));
			var value = Long.parseLong(matcher.group(3));
			var outcome = matcher.group(4);
			return new ConditionPart2(matcher.group(1).charAt(0), gt, value, outcome);
		}

		private Map<Boolean, Part2Part> partitionPart(Part2Part part) {
			var currentAttribute = switch (attributeExtractor) {
			case 'x' -> part.x();
			case 'm' -> part.m();
			case 'a' -> part.a();
			case 's' -> part.s();
			default -> throw new IllegalArgumentException("Unexpected value: " + attributeExtractor);
			};
			var newAttribute = currentAttribute.stream().flatMap(range -> {
				if (isGt && range.getSecond() < value) {
					return Stream.of();
				}
				if (isGt && range.getFirst() > value) {
					return Stream.of(range);
				}
				if (!isGt && range.getSecond() < value) {
					return Stream.of(range);
				}
				if (!isGt && range.getFirst() > value) {
					return Stream.of();
				}
				return isGt ? Stream.of(Pair.of(value + 1, range.getSecond()))
						: Stream.of(Pair.of(range.getFirst(), value - 1));
			}).collect(Collectors.toSet());
			var newX = attributeExtractor == 'x' ? newAttribute : part.x;
			var newM = attributeExtractor == 'm' ? newAttribute : part.m;
			var newA = attributeExtractor == 'a' ? newAttribute : part.a;
			var newS = attributeExtractor == 's' ? newAttribute : part.s;
			var acceptedPart = new Part2Part(newX, newM, newA, newS);
			var negatedAttribute = currentAttribute.stream().flatMap(range -> {
				if (isGt && range.getSecond() < value) {
					return Stream.of(range);
				}
				if (isGt && range.getFirst() > value) {
					return Stream.of();
				}
				if (!isGt && range.getSecond() < value) {
					return Stream.of();
				}
				if (!isGt && range.getFirst() > value) {
					return Stream.of(range);
				}
				return isGt ? Stream.of(Pair.of(range.getFirst(), value))
						: Stream.of(Pair.of(value, range.getSecond()));
			}).collect(Collectors.toSet());
			var negatedX = attributeExtractor == 'x' ? negatedAttribute : part.x;
			var negatedM = attributeExtractor == 'm' ? negatedAttribute : part.m;
			var negatedA = attributeExtractor == 'a' ? negatedAttribute : part.a;
			var negatedS = attributeExtractor == 's' ? negatedAttribute : part.s;
			var rejectedPart = new Part2Part(negatedX, negatedM, negatedA, negatedS);

			return Map.of(true, acceptedPart, false, rejectedPart);
		}

		public Map<Boolean, List<Part2Part>> partition(List<Part2Part> parts) {
			if (attributeExtractor == '0') {
				return Map.of(true, parts);
			}
			var partitionedParts = new HashMap<Boolean, List<Part2Part>>();
			for (var part : parts) {
				var partitionedPart = partitionPart(part);
				partitionedParts.computeIfAbsent(true, k -> new ArrayList<>()).add(partitionedPart.get(true));
				partitionedParts.computeIfAbsent(false, k -> new ArrayList<>()).add(partitionedPart.get(false));
			}
			return partitionedParts;
		}

		public String getOutcome() {
			return outcome;
		}
	}

	private static class WorkflowPart2 {
		private final String name;
		private final List<ConditionPart2> conditions;

		private WorkflowPart2(String name, List<ConditionPart2> conditions) {
			this.name = name;
			this.conditions = conditions;
		}

		public static WorkflowPart2 from(String workflowDesc) {
			var pattern = Pattern.compile("([a-z]+)\\{(.*)\\}");
			var matcher = pattern.matcher(workflowDesc);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(workflowDesc);
			}
			var name = matcher.group(1);
			var conditions = Arrays.stream(matcher.group(2).split(",")).map(ConditionPart2::from).toList();
			return new WorkflowPart2(name, conditions);
		}

		public String getName() {
			return name;
		}

		public Map<String, List<Part2Part>> process(List<Part2Part> parts) {
			var partResults = new HashMap<String, List<Part2Part>>();
			var partToTest = parts;
			for (var condition : conditions) {
				var conditionResult = condition.partition(partToTest);
				partResults.merge(condition.getOutcome(), conditionResult.get(true), (p1, p2) -> {
					var result = new ArrayList<>(p1);
					result.addAll(p2);
					return result;
				});
				partToTest = conditionResult.get(false);
			}
			return partResults;
		}
	}

	private static class SystemPart2 {
		private Map<String, WorkflowPart2> workflows = new HashMap<>();

		public void addWorkflow(WorkflowPart2 workflow) {
			workflows.put(workflow.getName(), workflow);
		}

		public List<Part2Part> process() {
			var toProcess = new HashMap<String, List<Part2Part>>();
			toProcess.put("in", List.of(new Part2Part(Set.of(Pair.of(1L, 4000L)), Set.of(Pair.of(1L, 4000L)),
					Set.of(Pair.of(1L, 4000L)), Set.of(Pair.of(1L, 4000L)))));
			while (!toProcess.keySet().equals(Set.of("A", "R"))) {
				var nextKey = toProcess.keySet().stream().filter(workflow -> !Set.of("A", "R").contains(workflow))
						.findAny().get();
				var processedKey = workflows.get(nextKey).process(toProcess.get(nextKey));
				toProcess.remove(nextKey);
				processedKey.entrySet().stream().forEach(e -> toProcess.merge(e.getKey(), e.getValue(), (p1, p2) -> {
					var result = new ArrayList<>(p1);
					result.addAll(p2);
					return result;
				}));
			}
			return toProcess.get("A");
		}
	}

	@Override
	public Object part2Solution() {
		var input = getInput().toList();
		var system = new SystemPart2();
		input.subList(0, input.indexOf("")).stream().map(WorkflowPart2::from).forEach(system::addWorkflow);
		var allAccepted = system.process();
		return allAccepted.stream().mapToLong(accepted -> Stream.of(accepted.x(), accepted.m(), accepted.a(), accepted.s())
				.mapToLong(ranges -> ranges.stream().mapToLong(range -> range.getSecond() - range.getFirst() + 1).sum())
				.reduce((a, b) -> a * b).getAsLong()).sum();
	}
}
