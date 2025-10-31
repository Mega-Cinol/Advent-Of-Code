package y2020.day19;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day19 {
	private final static Map<Integer, String> rules = new HashMap<>();
	private final static Map<Integer, Rule> resolvedRules = new HashMap<>();
	private final static Set<String> input = new HashSet<>();
	private static boolean rulesRead = false;

	public static void main(String[] args) {
		Input.parseLines("y2020/day19/day19.txt", Function.identity(), Day19::parseLine);
		Rule rule = resolveRule(0);
		input.stream().filter(text -> rule.match(text, 0).stream().anyMatch(index -> index >= text.length()))
				.forEach(System.out::println);
		System.out.println(input.stream()
				.filter(text -> rule.match(text, 0).stream().anyMatch(index -> index >= text.length())).count());
	}

	private static Rule resolveRule(int i) {
		String strRule = rules.get(i);
		if (resolvedRules.containsKey(i)) {
			return resolvedRules.get(i);
		}
		if (i == 8) {
			Rule rule8 = new Rule8(resolveRule(42));
			resolvedRules.put(8, rule8);
			return rule8;
		}
		if (i == 11) {
			Rule rule11 = new Rule11(resolveRule(42), resolveRule(31));
			resolvedRules.put(11, rule11);
			return rule11;
		}
		Matcher valueMatcher = Pattern.compile("\"([ab])\"").matcher(strRule);
		Matcher singleNum = Pattern.compile("([0-9]+)").matcher(strRule);
		Matcher twoNums = Pattern.compile("([0-9]+) ([0-9]+)").matcher(strRule);
		Matcher twoOrTwo = Pattern.compile("([0-9]+) ([0-9]+) \\| ([0-9]+) ([0-9]+)").matcher(strRule);
		Matcher oneOrOne = Pattern.compile("([0-9]+) \\| ([0-9]+)").matcher(strRule);

		if (valueMatcher.matches()) {
			Rule valueRule = new ValueRule(valueMatcher.group(1).charAt(0));
			resolvedRules.put(i, valueRule);
			return valueRule;
		}
		if (singleNum.matches()) {
			Rule oneRule = new OneRule(resolveRule(Integer.parseInt(singleNum.group(1))));
			resolvedRules.put(i, oneRule);
			return oneRule;
		}
		if (twoNums.matches()) {
			Rule twoRules = new TwoRule(resolveRule(Integer.parseInt(twoNums.group(1))),
					resolveRule(Integer.parseInt(twoNums.group(2))));
			resolvedRules.put(i, twoRules);
			return twoRules;
		}
		if (twoOrTwo.matches()) {
			Rule alt1 = new TwoRule(resolveRule(Integer.parseInt(twoOrTwo.group(1))),
					resolveRule(Integer.parseInt(twoOrTwo.group(2))));
			Rule alt2 = new TwoRule(resolveRule(Integer.parseInt(twoOrTwo.group(3))),
					resolveRule(Integer.parseInt(twoOrTwo.group(4))));
			Rule orRule = new OrRule(alt1, alt2);
			resolvedRules.put(i, orRule);
			return orRule;
		}
		if (oneOrOne.matches()) {
			Rule alt1 = new OneRule(resolveRule(Integer.parseInt(oneOrOne.group(1))));
			Rule alt2 = new OneRule(resolveRule(Integer.parseInt(oneOrOne.group(2))));
			Rule orRule = new OrRule(alt1, alt2);
			resolvedRules.put(i, orRule);
			return orRule;
		}
		throw new IllegalArgumentException(strRule);
	}

	private static void parseLine(String line) {
		if ("".equals(line)) {
			rulesRead = true;
			return;
		}
		if (rulesRead) {
			input.add(line);
		} else {
			Pattern rulePattern = Pattern.compile("([0-9]+): (.*)");
			Matcher ruleMathcer = rulePattern.matcher(line);
			ruleMathcer.matches();
			rules.put(Integer.valueOf(ruleMathcer.group(1)), ruleMathcer.group(2));
		}
	}

	private interface Rule {
		Set<Integer> match(String input, int startIndex);
	}

	private static class ValueRule implements Rule {
		private final char character;

		public ValueRule(char character) {
			this.character = character;
		}

		@Override
		public Set<Integer> match(String input, int startIndex) {
			Set<Integer> result = new HashSet<>();
			if (input.charAt(startIndex) == character) {
				result.add(startIndex + 1);
			}
			return result;
		}
	}

	private static class OneRule implements Rule {
		private final Rule referencedRule;

		public OneRule(Rule rule) {
			referencedRule = rule;
		}

		@Override
		public Set<Integer> match(String input, int startIndex) {
			return referencedRule.match(input, startIndex);
		}
	}

	private static class TwoRule implements Rule {
		private final Rule firstRule;
		private final Rule secondRule;

		public TwoRule(Rule rule1, Rule rule2) {
			firstRule = rule1;
			secondRule = rule2;
		}

		@Override
		public Set<Integer> match(String input, int startIndex) {
			return firstRule.match(input, startIndex).stream()
					.flatMap(nextStartIndex -> secondRule.match(input, nextStartIndex).stream())
					.collect(Collectors.toSet());
		}
	}

	private static class OrRule implements Rule {
		private final Rule option1;
		private final Rule option2;

		public OrRule(Rule rule1, Rule rule2) {
			option1 = rule1;
			option2 = rule2;
		}

		@Override
		public Set<Integer> match(String input, int startIndex) {
			return Stream.concat(option1.match(input, startIndex).stream(), option2.match(input, startIndex).stream())
					.collect(Collectors.toSet());
		}
	}

	private static class Rule8 implements Rule {
		private final Rule innerRule;

		public Rule8(Rule rule) {
			innerRule = rule;
		}

		@Override
		public Set<Integer> match(String input, int startIndex) {
			Set<Integer> result = new HashSet<>();
			Set<Integer> innerResult = innerRule.match(input, startIndex);
			while (!innerResult.isEmpty()) {
				result.addAll(innerResult);
				innerResult = innerResult.stream().filter(idx -> idx < input.length()).flatMap(index -> innerRule.match(input, index).stream())
						.collect(Collectors.toSet());
			}
			return result;
		}
	}
	private static class Rule11 implements Rule {
		private final Rule innerRule1;
		private final Rule innerRule2;

		public Rule11(Rule rule1, Rule rule2) {
			innerRule1 = rule1;
			innerRule2 = rule2;
		}

		@Override
		public Set<Integer> match(String input, int startIndex) {
			Set<Integer> result = new HashSet<>();
			for (int i = 1 ; i < 10 ; i++)
			{
				result.addAll(executeStep(i, startIndex, input));
			}
			return result;
		}

		private Set<Integer> executeStep(int count, int start, String input)
		{
			Set<Integer> result = new HashSet<>();
			result.add(start);
			for (int i = 0 ; i < count ; i++)
			{
				result = result.stream().filter(idx -> idx < input.length()).flatMap(idx -> innerRule1.match(input, idx).stream()).collect(Collectors.toSet());
			}
			for (int i = 0 ; i < count ; i++)
			{
				result = result.stream().filter(idx -> idx < input.length()).flatMap(idx -> innerRule2.match(input, idx).stream()).collect(Collectors.toSet());
			}
			return result;
		}
	}
}
