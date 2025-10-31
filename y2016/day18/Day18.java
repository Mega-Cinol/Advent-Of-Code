package y2016.day18;

import java.util.ArrayList;
import java.util.List;

import common.Point;
import common.Simulation;

public class Day18 {

	public static void main(String[] args) {
		Simulation sim = new Simulation();
		List<String> initial = new ArrayList<>();
//		String step0 = ".^^.^.^^^^";
		String step0 = ".^.^..^......^^^^^...^^^...^...^....^^.^...^.^^^^....^...^^.^^^...^^^^.^^.^.^^..^.^^^..^^^^^^.^^^..^";
		initial.add(step0);
		sim.setInitialState(initial, '^', true);
		sim.setActivateCondition((point, current) -> {
			Point left = point.move(0, -1);
			Point right = point.move(0, 1);
			boolean result = (current.contains(left) && !current.contains(right))
					|| (!current.contains(left) && current.contains(right));
//			if (point.equals(new Point(0, 1)))
//			{
//				System.out.println("Left: " + left);
//				System.out.println("Right: " + right);
//				System.out.println("Left: " + current.contains(left));
//				System.out.println("Right: " + current.contains(right));
//				System.out.println("Result: " + result);
//			}
			return result;
		});
		sim.setDeactivateCondition((point, current) -> {
			Point left = point.move(0, -1);
			Point right = point.move(0, 1);
			return (current.contains(left) && current.contains(right))
					|| (!current.contains(left) && !current.contains(right));
		});
		List<Point> traps = new ArrayList<Point>();
		sim.setOnStepAction(traps::addAll);
		sim.run(400000, false);
		System.out.println(step0.length() * 400000 - traps.size());
	}

}
