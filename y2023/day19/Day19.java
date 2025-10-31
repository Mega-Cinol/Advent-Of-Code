package y2023.day19;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;

import common.AdventSolution;

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
		return input.subList(input.indexOf("") + 1, input.size()).stream().map(Part::from).filter(system::process).mapToLong(Part::rating).sum();
	}

	@Override
	public Object part2Solution() {
		// TODO Auto-generated method stub
		return null;
	}

}
