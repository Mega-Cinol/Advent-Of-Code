package y2020.day4;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day4 {

	public static void main(String[] args) {
		PassportRegistry registry = new PassportRegistry();
		Input.parseLines("y2020/day4/day4.txt", Function.identity(), registry);
		System.out.println(registry.countValid());
	}

	private static class PassportRegistry implements Consumer<String> {
		private final Deque<Passport> passports = new ArrayDeque<>();

		public PassportRegistry() {
			passports.add(new Passport());
		}

		@Override
		public void accept(String line) {
			if (line.isEmpty()) {
				passports.add(new Passport());
			} else {
				passports.getLast().addProperties(line);
			}
		}

		public long countValid() {
			return passports.stream().filter(Passport::isValid).count();
		}
	}

	private static class Passport {
		private static final Pattern PATTERN = Pattern.compile("(?<key>[^\\ ]+):(?<value>[^\\ ]+)");
		private static final Set<String> EYE_COLORS = new HashSet<>();
		static {
			EYE_COLORS.add("amb");
			EYE_COLORS.add("blu");
			EYE_COLORS.add("brn");
			EYE_COLORS.add("gry");
			EYE_COLORS.add("grn");
			EYE_COLORS.add("hzl");
			EYE_COLORS.add("oth");
		}
		private static final Map<String, Predicate<String>> REQUIRED_KEYS = new HashMap<>();
		static {
			REQUIRED_KEYS.put("byr", Pattern.compile("[0-9]{4}").asPredicate().and(new YearPredicate(1920, 2002)));
			REQUIRED_KEYS.put("iyr", Pattern.compile("[0-9]{4}").asPredicate().and(new YearPredicate(2010, 2020)));
			REQUIRED_KEYS.put("eyr", Pattern.compile("[0-9]{4}").asPredicate().and(new YearPredicate(2020, 2030)));
			REQUIRED_KEYS.put("hgt", input -> {
				try {
					int value = Integer.parseInt(input.substring(0, input.length() -2));
					if (input.endsWith("cm")) {
						return value >= 150 && value <=193;
					} else if (input.endsWith("in")) {
						return value >= 59 && value <=76;
					} else
						return false;
				} catch (NumberFormatException e) {
					return false;
				}
			});
			REQUIRED_KEYS.put("hcl", Pattern.compile("^#[0-9a-f]{6}$").asPredicate());
			REQUIRED_KEYS.put("ecl", EYE_COLORS::contains);
			REQUIRED_KEYS.put("pid", Pattern.compile("^[0-9]{9}$").asPredicate());
		}
		private final Map<String, String> properties = new TreeMap<>();

		public void addProperties(String passProperties) {
			Matcher propertiesMatcher = PATTERN.matcher(passProperties);
			while (propertiesMatcher.find()) {
				properties.put(propertiesMatcher.group("key"), propertiesMatcher.group("value"));
			}
		}

		public boolean isValid() {
			NotNullPredicate notNull = new NotNullPredicate();
			boolean valid = REQUIRED_KEYS.entrySet().stream()
					.allMatch(rule -> notNull.and(rule.getValue()).test(properties.get(rule.getKey())));
			return valid;
		}
		@Override
		public String toString()
		{
			return properties.toString();
		}
	}

	private static class NotNullPredicate implements Predicate<String> {
		@Override
		public boolean test(String input) {
			return input != null;
		}
	}

	private static class YearPredicate implements Predicate<String> {
		private int min;
		private int max;

		public YearPredicate(int min, int max) {
			this.min = min;
			this.max = max;
		}

		@Override
		public boolean test(String input) {
			int value = Integer.parseInt(input);
			return value >= min && value <= max;
		}
	}
}