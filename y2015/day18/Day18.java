package y2015.day18;

import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;
import common.Simulation;

public class Day18 {

	public static void main(String[] args) {
		Simulation sim = new Simulation();
		sim.setInitialState(Input.parseLines("y2015/day18/day18.txt", Function.identity()).collect(Collectors.toList()), '#', true);
		sim.setActivateCondition(sim.activeNeighbourCountCondition(c -> c ==3).or((point, points) -> (point.getX() == 0 || point.getX() == 99) && (point.getY() == 0 || point.getY() == 99)));
		sim.setDeactivateCondition(sim.activeNeighbourCountCondition(c -> c  < 2 || c > 3).and((point, points) -> (point.getX() > 0 && point.getX() < 99) || (point.getY() > 0 && point.getY() < 99)));
		System.out.println(sim.run(100).size());
	}

}
