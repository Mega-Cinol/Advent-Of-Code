package y2020.day11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day11 {

	public static void main(String[] args) {
		Function<String, List<State>> f = line -> line.chars().mapToObj(charCode -> State.fromSymbol((char) charCode))
				.collect(Collectors.toList());
		List<List<State>> plan = new ArrayList<>();
		Input.parseLines("y2020/day11/day11.txt", f, plan::add);
		List<List<State>> oldPlan = new ArrayList<>();
		boolean changed = false;
		do {
			changed = false;
			copyPlan(plan, oldPlan);
			for (int y = 0; y < plan.size(); y++) {
				for (int x = 0; x < plan.get(y).size(); x++) {
					if (oldPlan.get(y).get(x) == State.EMPTY && countNeights(oldPlan, x, y) == 0) {
						plan.get(y).set(x, State.TAKEN);
						changed = true;
					}
					if (oldPlan.get(y).get(x) == State.TAKEN && countNeights(oldPlan, x, y) >= 5) {
						plan.get(y).set(x, State.EMPTY);
						changed = true;
					}
				}
			}
		} while (changed);
		System.out.println(plan.stream().flatMap(List::stream).filter(s -> s == State.TAKEN).count());
	}

	private static void copyPlan(List<List<State>> src, List<List<State>> dst) {
		dst.clear();
		src.forEach(row -> {
			List<State> dstRow = new ArrayList<>();
			row.forEach(dstRow::add);
			dst.add(dstRow);
		});
	}

	private static int countNeights(List<List<State>> plan, int x, int y) {
		int sum = 0;
		sum += neightTaken(plan, x, y, -1, -1);
		sum += neightTaken(plan, x, y, 0, -1);
		sum += neightTaken(plan, x, y, 1, -1);

		sum += neightTaken(plan, x, y, -1, 0);
		sum += neightTaken(plan, x, y, 1, 0);

		sum += neightTaken(plan, x, y, -1, 1);
		sum += neightTaken(plan, x, y, 0, 1);
		sum += neightTaken(plan, x, y, 1, 1);
		return sum;
	}

	private static int neightTaken(List<List<State>> plan, int x, int y, int dx, int dy)
	{
		do
		{
			x += dx;
			y += dy;
			if (x < 0 || x >= plan.get(0).size() || y < 0 || y >= plan.size())
			{
				return 0;
			}
			if (plan.get(y).get(x) == State.TAKEN)
			{
				return 1;
			}
			if (plan.get(y).get(x) == State.EMPTY)
			{
				return 0;
			}
		} while (true);
	}

	private enum State {
		FLOOR('.'), EMPTY('L'), TAKEN('#');

		private final char symbol;

		State(char symbol) {
			this.symbol = symbol;
		}

		public static State fromSymbol(char symbol) {
			return Stream.of(State.values()).filter(state -> state.symbol == symbol).findAny().get();
		}
	}
}
