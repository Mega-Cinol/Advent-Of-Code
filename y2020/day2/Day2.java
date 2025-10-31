package y2020.day2;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day2 {

	public static void main(String[] args) {
		Set<Password> passwords = new HashSet<>();
		Input.parseLines("y2020/day2/day2.txt", Password::fromString, passwords::add);
		System.out.println(passwords.stream().filter(Password::isValid).count());
		System.out.println(passwords.stream().filter(Password::isValid2).count());
	}

	private static class Password {
		private static final Pattern PATTERN = Pattern
				.compile("(?<min>[0-9]+)-(?<max>[0-9]+) (?<char>[a-z]): (?<pass>[a-z]+)");

		private String pass;
		private char policyLetter;
		private int minAmount;
		private int maxAmount;

		public static Password fromString(String input) {
			Matcher matcher = PATTERN.matcher(input);
			if (matcher.matches()) {
				return new Password(matcher.group("pass"), matcher.group("char").charAt(0),
						Integer.parseInt(matcher.group("min")), Integer.parseInt(matcher.group("max")));
			} else {
				throw new IllegalArgumentException("Invalid string: " + input);
			}
		}

		private Password(String pass, char policyLetter, int minAmount, int maxAmount) {
			this.pass = pass;
			this.policyLetter = policyLetter;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
		}

		public boolean isValid() {
			long amount = pass.chars().filter(c -> c == policyLetter).count();
			return amount >= minAmount && amount <= maxAmount;
		}

		public boolean isValid2() {
			return pass.charAt(minAmount - 1) == policyLetter ^ pass.charAt(maxAmount - 1) == policyLetter;
		}
	}
}
