package y2016.day11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import common.Combinations;

public class Day11 {

	public static void main(String[] args) {
		List<Set<String>> state = new ArrayList<>();
		Set<String> floor1 = new HashSet<>();
		Set<String> floor2 = new HashSet<>();
		Set<String> floor3 = new HashSet<>();
		Set<String> floor4 = new HashSet<>();
		state.add(floor1);
		state.add(floor2);
		state.add(floor3);
		state.add(floor4);
		floor1.add("EG");
		floor1.add("EM");
		floor1.add("DG");
		floor1.add("DM");
		floor1.add("PG");
		floor1.add("PM");
		floor2.add("CoG");
		floor2.add("CuG");
		floor2.add("RG");
		floor2.add("PlG");
		floor3.add("CoM");
		floor3.add("CuM");
		floor3.add("RM");
		floor3.add("PlM");
		System.out.println(countSteps(state, 0));
	}

	private static int countSteps(List<Set<String>> state, int currentFloor) {
		State initialState = new State(state, currentFloor);
		Set<State> visited = new HashSet<>();
		int depth = 0;
		Set<State> nextStep = new HashSet<>();
		nextStep.add(initialState);
		do {
			Set<State> toVisit = new HashSet<>();
			for (State current : nextStep) {
				if (current.isDone()) {
					return depth;
				}
				toVisit.addAll(current.getPossibleMoves());
				visited.add(current);
			}
			depth++;
			nextStep = toVisit;
			nextStep.removeAll(visited);
			System.out.println(depth);
		} while (!nextStep.isEmpty());
		return -1;
	}

	private static class State {
		private final List<Set<String>> floors = new ArrayList<>();
		private final int currentFloor;

		public State(List<Set<String>> floors, int currentFloor) {
			floors.stream().map(HashSet::new).forEach(this.floors::add);
			this.currentFloor = currentFloor;
		}

		public boolean isValid() {
			return floors.stream().allMatch(this::isValid);
		}

		public boolean isDone() {
			return floors.get(0).isEmpty() && floors.get(1).isEmpty() && floors.get(2).isEmpty() && currentFloor == 3;
		}

		public Set<State> getPossibleMoves() {
			Set<Set<String>> possibleMoves = Combinations.subSets(floors.get(currentFloor), 2)
					.collect(Collectors.toSet());
			Combinations.subSets(floors.get(currentFloor), 1).forEach(possibleMoves::add);
			possibleMoves = possibleMoves.stream().filter(move -> !applyPossibleMove(move).isEmpty()).collect(Collectors.toSet());
			possibleMoves = filterPossibleMoves(possibleMoves);
			Set<State> possibleMoveResults = new HashSet<>();
			for (Set<String> possibleMove : possibleMoves) {
				possibleMoveResults.addAll(applyPossibleMove(possibleMove));
			}
			return possibleMoveResults;
		}

		private Set<State> applyPossibleMove(Set<String> move)
		{
			Set<State> possibleMoveResult = new HashSet<>();
			if (currentFloor > 0) {
				List<Set<String>> stateDown = new ArrayList<>();
				for (int i = 0; i < currentFloor - 1; i++) {
					stateDown.add(new HashSet<>(floors.get(i)));
				}
				Set<String> next = new HashSet<>(floors.get(currentFloor - 1));
				next.addAll(move);
				stateDown.add(next);
				next = new HashSet<>(floors.get(currentFloor));
				next.removeAll(move);
				stateDown.add(next);
				for (int i = currentFloor + 1; i < 4; i++) {
					stateDown.add(new HashSet<>(floors.get(i)));
				}
				possibleMoveResult.add(new State(stateDown, currentFloor - 1));
			}
			if (currentFloor < 3) {
				List<Set<String>> stateUp = new ArrayList<>();
				for (int i = 0; i < currentFloor; i++) {
					stateUp.add(new HashSet<>(floors.get(i)));
				}
				Set<String> next = new HashSet<>(floors.get(currentFloor));
				next.removeAll(move);
				stateUp.add(next);
				next = new HashSet<>(floors.get(currentFloor + 1));
				next.addAll(move);
				stateUp.add(next);
				for (int i = currentFloor + 2; i < 4; i++) {
					stateUp.add(new HashSet<>(floors.get(i)));
				}
				possibleMoveResult.add(new State(stateUp, currentFloor + 1));
			}
			return possibleMoveResult.stream().filter(s -> s.isValid()).collect(Collectors.toSet());
		}
		private Set<Set<String>> filterPossibleMoves(Set<Set<String>> possibleMoves)
		{
			Set<Set<String>> newPossibleMoves = new HashSet<>();
			Set<String> justM = possibleMoves.stream().filter(s -> s.size() == 1)
					.filter(set -> set.stream().allMatch(s -> s.endsWith("M"))).map(s -> s.stream().findAny().get()).collect(Collectors.toSet());
			Set<Integer> counterPartFloors = new HashSet<>();
			for (String mic : justM)
			{
				int targetFloor = findFloor(mic.replace('M', 'G'));
				if (counterPartFloors.add(targetFloor))
				{
					Set<String> newMove = new HashSet<>();
					newMove.add(mic);
					newPossibleMoves.add(newMove);
				}
			}
			Set<String> justG = possibleMoves.stream().filter(s -> s.size() == 1)
					.filter(set -> set.stream().allMatch(s -> s.endsWith("G"))).map(s -> s.stream().findAny().get()).collect(Collectors.toSet());
			counterPartFloors.clear();
			for (String gen : justG)
			{
				int targetFloor = findFloor(gen.replace('G', 'M'));
				if (counterPartFloors.add(targetFloor))
				{
					Set<String> newMove = new HashSet<>();
					newMove.add(gen);
					newPossibleMoves.add(newMove);
				}
			}
			possibleMoves.stream().filter(set -> set.size() == 2)
					.filter(set -> set.stream().anyMatch(item -> item.endsWith("G")))
					.filter(set -> set.stream().anyMatch(item -> item.endsWith("M"))).findAny()
					.ifPresent(newPossibleMoves::add);

			Set<Set<String>> twoM = possibleMoves.stream().filter(s -> s.size() == 2)
					.filter(set -> set.stream().allMatch(s -> s.endsWith("M"))).collect(Collectors.toSet());
			Set<Set<Integer>> counterPartFloors2 = new HashSet<>();
			for (Set<String> mics : twoM)
			{
				Set<Integer> targetFloors = mics.stream().map(m -> m.replace('M', 'G')).map(this::findFloor).collect(Collectors.toSet());
				if (counterPartFloors2.add(targetFloors))
				{
					newPossibleMoves.add(mics);
				}
			}
			Set<Set<String>> twoG = possibleMoves.stream().filter(s -> s.size() == 2)
					.filter(set -> set.stream().allMatch(s -> s.endsWith("G"))).collect(Collectors.toSet());
			counterPartFloors2.clear();
			for (Set<String> gens : twoG)
			{
				Set<Integer> targetFloors = gens.stream().map(m -> m.replace('G', 'M')).map(this::findFloor).collect(Collectors.toSet());
				if (counterPartFloors2.add(targetFloors))
				{
					newPossibleMoves.add(gens);
				}
			}

			return newPossibleMoves;
		}
		private int findFloor(String element)
		{
			for (int i = 0 ; i < floors.size() ; i++)
			{
				if (floors.get(i).contains(element))
				{
					return i;
				}
			}
			return -1;
		}
		private boolean isValid(Set<String> floor) {
			if (floor.stream().noneMatch(item -> item.endsWith("G"))) {
				return true;
			}
			return floor.stream().filter(item -> item.endsWith("M")).map(item -> item.replace("M", "G"))
					.allMatch(floor::contains);
		}

		@Override
		public int hashCode() {
			return hash().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			return hash().equals(other.hash());
		}

		private String counterpart(String item)
		{
			if (item.endsWith("M"))
			{
				return item.replace('M', 'G');
			} else {
				return item.replace('G', 'M');
			}
		}
		private String hash()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(currentFloor);
			floors.forEach(floor -> floor.stream()
					.map(item -> item.substring(item.length() - 1, item.length()) + findFloor(counterpart(item))).sorted().forEach(sb::append));
			return sb.toString();
		}
	}
}
