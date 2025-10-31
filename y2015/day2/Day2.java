package y2015.day2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day2 {

	public static void main(String[] args) {
		System.out.println(
				Input.parseLines("y2015/day2/day2.txt", Present::fromString).mapToInt(Present::requiredPaper).sum());
		System.out.println(
				Input.parseLines("y2015/day2/day2.txt", Present::fromString).mapToInt(Present::requiredRibbon).sum());
	}

	private static class Present {
		private final int w;
		private final int l;
		private final int h;

		private Present(int w, int l, int h) {
			this.w = w;
			this.l = l;
			this.h = h;
		}

		public int requiredPaper() {
			int max = Math.max(Math.max(w, l), h);
			return w * l * h / max + 2 * w * l + 2 * w * h + 2 * h * l;
		}

		public int requiredRibbon() {
			int max = Math.max(Math.max(w, l), h);
			return 2 * (w + l + h - max) + w*l*h;
		}

		public static Present fromString(String str) {
			Pattern presentPattern = Pattern.compile("([0-9]*)x([0-9]*)x([0-9]*)");
			Matcher presentMatcher = presentPattern.matcher(str);
			presentMatcher.matches();
			return new Present(Integer.valueOf(presentMatcher.group(1)), Integer.valueOf(presentMatcher.group(2)),
					Integer.valueOf(presentMatcher.group(3)));
		}
	}
}
