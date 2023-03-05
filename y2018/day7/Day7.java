package y2018.day7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;

public class Day7 {

	public static void main(String[] args) {
		Map<Character, Set<Character>> steps = new HashMap<>();
		List<Character> ordered = new ArrayList<>();
		Input.parseLines("y2018/day7/day7.txt").forEach(l -> parse(l, steps));
		while (ordered.size() < steps.size())
		{
			ordered.add(getNext(steps, ordered, new HashSet<>()).get());
		}
		ordered.stream().forEach(System.out::print);
		System.out.println();
		int time = 0;
		Set<Worker> workers = new HashSet<>();
		ordered.clear();
		for (int i = 0 ; i < 5 ; i++)
		{
			workers.add(new Worker());
		}
		while (ordered.size() < steps.size())
		{
			Set<Character> completed = workers.stream().map(Worker::getCompleted).filter(w -> w != null).collect(Collectors.toSet());
			completed.stream().filter(t -> !ordered.contains(t)).forEach(ordered::add);
			Set<Character> started = workers.stream().map(Worker::getOngoing).filter(w -> w != null).collect(Collectors.toSet());
			workers.stream().filter(w -> w.getDoneIn() <= 0).forEach(w -> {
				Optional<Character> toStart = getNext(steps, ordered, started);
				toStart.ifPresent(started::add);
				w.work(toStart);	
			});
			int timeStep = workers.stream().mapToInt(Worker::getDoneIn).filter(d -> d > 0).min().orElse(0);
			workers.stream().forEach(w -> w.passTime(timeStep));
			time += timeStep;
		}
		System.out.println(time);
	}

	private static void parse(String line, Map<Character, Set<Character>> steps) {
		Character dependent = line.charAt(1);
		Character required = line.charAt(0);
		steps.computeIfAbsent(required, k -> new HashSet<>());
		steps.computeIfAbsent(dependent, k -> new HashSet<>()).add(required);
	}

	private static Optional<Character> getNext(Map<Character, Set<Character>> steps, List<Character> executed, Set<Character> started) {
		return steps.entrySet().stream().filter(e -> e.getValue().stream().allMatch(executed::contains))
				.map(Map.Entry::getKey).filter(c -> !executed.contains(c)).filter(c -> !started.contains(c)).sorted().findFirst();
	}

	private static class Worker
	{
		private int doneIn = -1;
		private Character stepInProgress = null;

		public void work(Optional<Character> step)
		{
			if (step.isPresent())
			{
				stepInProgress = step.get();
				doneIn = 61 + (int)(step.get() - 'A');
			}
		}

		public Character getOngoing()
		{
			if (doneIn > 0)
			{
				return stepInProgress;
			}
			return null;
		}

		public Character getCompleted()
		{
			if (doneIn <= 0)
			{
				return stepInProgress;
			}
			return null;
		}

		public void passTime(int time)
		{
			doneIn -= time;
		}

		public int getDoneIn()
		{
			return doneIn;
		}
	}
}
