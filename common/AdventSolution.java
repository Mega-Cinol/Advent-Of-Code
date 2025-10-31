package common;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AdventSolution {

	public abstract Object part1Solution();

	public abstract Object part2Solution();

	public void solve() {
		System.out.println(part1Solution());
		System.out.println(part2Solution());
	}

	private String getInputPath() {
		return getClass().getName().toLowerCase().replace('.', '/') + ".txt";
	}

	protected Stream<String> getInput() {
		return Input.parseLines(getInputPath());
	}

	protected <T> Map<Point, T> parseGrid(Function<Character, T> converter) {
		return Input.parseGrid(getInputPath(), converter);
	}
}
