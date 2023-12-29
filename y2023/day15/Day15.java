package y2023.day15;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import common.AdventSolution;

public class Day15 extends AdventSolution {

	public static void main(String[] args) {
		new Day15().solve();
	}

	@Override
	public Object part1Solution() {
		var line = getInput().findAny().get();
		return Arrays.stream(line.split(",")).mapToLong(this::hash).sum();
	}

	private int hash(String str) {
		var current = 0L;
		for (var i = 0 ; i < str.length() ; i++) {
			current += str.charAt(i);
			current *= 17;
			current %= 256;
		}
		return (int) current;
	}

	@Override
	public Object part2Solution() {
		var boxes = new Box[256];
		Arrays.setAll(boxes, i -> new Box());
		var line = getInput().findAny().get();
		Arrays.stream(line.split(",")).forEach(lens -> boxes[lensToBox(lens)].processLens(lens));
		var focusPower = 0L;
		for (int i = 0 ; i < 256 ; i++) {
			focusPower += (i + 1) * boxes[i].getFocusPower();
		}
		return focusPower;
	}

	private int lensToBox(String lens) {
		var lensPattern = Pattern.compile("[a-z]*");
		var lensMatcher = lensPattern.matcher(lens);
		if (!lensMatcher.find()) {
			throw new IllegalArgumentException(lens);
		}
		return hash(lensMatcher.group());
	}

	private static class Box {
		private final Map<String, Integer> lenses = new LinkedHashMap<>();

		public void processLens(String lens) {
			if (lens.contains("=")) {
				addLens(lens);
			} else {
				removeLens(lens);
			}
		}

		private void addLens(String lens) {
			var lensParsed = lens.split("=");
			var lensName = lensParsed[0];
			var lensValue = Integer.parseInt(lensParsed[1]);
			lenses.put(lensName, lensValue);
		}

		private void removeLens(String lens) {
			lenses.remove(lens.replace("-", ""));
		}

		public long getFocusPower() {
			var result = 0L;
			var lensIdx = 1;
			for (var lens : lenses.entrySet()) {
				result += lensIdx * lens.getValue();
				lensIdx++;
			}
			return result;
		}
	}
}
