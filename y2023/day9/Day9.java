package y2023.day9;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import common.AdventSolution;

public class Day9 extends AdventSolution {

	public static void main(String[] args) {
		new Day9().solve();
	}

	@Override
	public Object part1Solution() {
		return getInput().mapToLong(this::nextValue).sum();
	}

	private long nextValue(String historyDescription) {
		var extrapolation = getExtrapolation(historyDescription);
		extrapolation.get(extrapolation.size() - 1).add(0L);
		for (var level = extrapolation.size() - 2; level >= 0; level--) {
			extrapolation.get(level).add(extrapolation.get(level).get(extrapolation.get(level).size() - 1)
					+ extrapolation.get(level + 1).get(extrapolation.get(level + 1).size() - 1));
		}
		return extrapolation.get(0).get(extrapolation.get(0).size() - 1);
	}

	@Override
	public Object part2Solution() {
		return getInput().mapToLong(this::previousValue).sum();
	}

	private long previousValue(String historyDescription) {
		var extrapolation = getExtrapolation(historyDescription);
		var previousValues = new ArrayList<Long>();
		previousValues.add(0L);
		for (var level = extrapolation.size() - 2; level >= 0; level--) {
			previousValues.add(extrapolation.get(level).get(0) - previousValues.get(previousValues.size() - 1));
		}
		return previousValues.get(previousValues.size() - 1);
	}

	private List<List<Long>> getExtrapolation(String historyDescription) {
		var numberPattern = Pattern.compile("-?\\d+");
		var numberMatcher = numberPattern.matcher(historyDescription);
		var extrapolation = new ArrayList<List<Long>>();
		extrapolation.add(new ArrayList<>());
		while (numberMatcher.find()) {
			extrapolation.get(0).add(Long.parseLong(numberMatcher.group()));
		}
		while (!extrapolation.get(extrapolation.size() - 1).stream().allMatch(i -> i == 0)) {
			var nextRow = new ArrayList<Long>();
			for (var i = 1; i < extrapolation.get(extrapolation.size() - 1).size(); i++) {
				nextRow.add(extrapolation.get(extrapolation.size() - 1).get(i)
						- extrapolation.get(extrapolation.size() - 1).get(i - 1));
			}
			extrapolation.add(nextRow);
		}
		return extrapolation;
	}
}
