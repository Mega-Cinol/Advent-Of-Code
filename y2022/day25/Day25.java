package y2022.day25;

import common.Input;

public class Day25 {

	public static void main(String[] args) {
		System.out.println(decToSnafu(Input.parseLines("y2022/day25/day25.txt").mapToLong(Day25::snafuToDec).sum()));
	}

	private static long snafuToDec(String snafu) {
		long dec = 0;
		for (int i = 0; i < snafu.length(); i++) {
			int digit = switch (snafu.charAt(snafu.length() - 1 - i)) {
			case '2' -> 2;
			case '1' -> 1;
			case '0' -> 0;
			case '-' -> -1;
			case '=' -> -2;
			default -> throw new IllegalArgumentException("Unexpected value: " + snafu.charAt(snafu.length() - 1 - i));
			};
			dec += Math.pow(5, i) * digit;
		}
		return dec;
	}

	private static String decToSnafu(long dec) {
		String snafu = "";
		long overflow = 0;
		while (dec > 0) {
			long digit = (dec + overflow) % 5;
			overflow = digit > 2 || (overflow == 1 && digit == 0) ? 1 : 0;
			snafu = switch ((int) digit) {
			case 0 -> "0";
			case 1 -> "1";
			case 2 -> "2";
			case 3 -> "=";
			case 4 -> "-";
			default -> throw new IllegalArgumentException("Unexpected value: " + digit);
			} + snafu;
			dec /= 5;
		}
		if (overflow > 0) {
			snafu = "1" + snafu;
		}
		return snafu;
	}
}
