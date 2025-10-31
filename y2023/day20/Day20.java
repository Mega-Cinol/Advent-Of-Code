package y2023.day20;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.AdventSolution;

public class Day20 extends AdventSolution {

	public static void main(String[] args) {
		new Day20().solve();
	}

	@Override
	public Object part1Solution() {
		var system = new System();
		getInput().map(Module::from).forEach(system::addModule);
		system.connectCables();
		for (int i = 0; i < 1000; i++) {
			system.pressButton();
		}
		return system.getResult();
	}

	@Override
	public Object part2Solution() {
		var system = new System();
		getInput().map(Module::from).forEach(system::addModule);
		system.addModule(new Module("rx", "BlackHole", Set.of()));
		system.connectCables();
		return system.getResultPart2();
	}

	private static class System {
		private final Map<String, Module> modules = new HashMap<>();

		public void addModule(Module module) {
			modules.put(module.getName(), module);
		}

		public void connectCables() {
			modules.values().forEach(module -> module.getOutputs().stream().map(modules::get).filter(m -> m != null)
					.forEach(outModule -> outModule.addInput(module.getName())));
		}

		public void pressButton() {
			record Signal(String from, String to, boolean value) {
			}
			var toProcess = new ArrayDeque<Signal>();
			toProcess.add(new Signal("button", "broadcaster", false));
			while (!toProcess.isEmpty()) {
				var nextSignal = toProcess.remove();
				var module = modules.get(nextSignal.to);
				if (module != null) {
					module.processPulse(nextSignal.from, nextSignal.value).ifPresent(outputValue -> {
						module.getOutputs().stream().map(target -> new Signal(module.getName(), target, outputValue))
								.forEach(toProcess::add);
					});
				}
			}
		}

		public long getResultPart2() {
			record Cycle(long start, long size) {
				public boolean complete() {
					return start > 0 && size > 0;
				}

				public Cycle merge(Cycle other) {
					var newStart = start * other.start / highestCommonDivisor(start, other.start);
					return new Cycle(newStart, size * other.size / highestCommonDivisor(size, other.size));
				}

				private long highestCommonDivisor(long a, long b) {
					long pom;
					while (b != 0) {
						pom = b;
						b = a % b;
						a = pom;
					}
					return a;
				}

			}
			var cycleData = new HashMap<String, Cycle>();
			// Input data specific - input connected to "rx" assumed to be conjunction
			modules.get("vr").inputs.forEach(input -> cycleData.put(input, new Cycle(0, 0)));
			var step = 0L;
			while (!cycleData.values().stream().allMatch(Cycle::complete)) {
				var finalStep = step;
				var newCycles = cycleData.entrySet().stream().filter(e -> modules.get(e.getKey()).getHighCount() == 1)
						.filter(e -> !e.getValue().complete()).map(e -> {
							var newCycle = e.getValue();
							if (newCycle.start() == 0) {
								newCycle = new Cycle(finalStep, 0L);
							} else {
								newCycle = new Cycle(newCycle.start, finalStep - newCycle.start);
							}
							return Map.entry(e.getKey(), newCycle);
						}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				cycleData.putAll(newCycles);
				modules.values().forEach(Module::resetCounters);
				pressButton();
				step++;
			}
			return cycleData.values().stream().reduce(Cycle::merge).get().start();
		}

		public long getResult() {
			return modules.values().stream().mapToLong(Module::getHighCount).sum()
					* (modules.values().stream().mapToLong(Module::getLowCount).sum() + 1000);
		}
	}

	private static class Module {
		private final Set<String> outputs;
		private final ModuleBehavior behavior;
		private final String name;
		private final Set<String> inputs = new HashSet<>();
		private long highCount = 0;
		private long lowCount = 0;

		public Module(String name, String behavior, Set<String> outputs) {
			this.name = name;
			this.behavior = switch (behavior) {
			case "FlipFlop" -> new FlipFlop();
			case "Conjunction" -> new Conjunction();
			case "Broadcaster" -> new Broadcaster();
			case "BlackHole" -> new BlackHole();
			default -> throw new IllegalArgumentException(behavior);
			};
			this.outputs = outputs;
		}

		public String getName() {
			return name;
		}

		public static Module from(String moduleDescription) {
			var modulePattern = Pattern.compile("([&%]?[a-z]+) -> ([a-z ,]+)");
			var moduleMatcher = modulePattern.matcher(moduleDescription);
			if (!moduleMatcher.matches()) {
				throw new IllegalArgumentException(moduleDescription);
			}
			var name = moduleMatcher.group(1);
			String behavior = null;
			if (name.startsWith("%")) {
				name = name.substring(1);
				behavior = "FlipFlop";
			} else if (name.startsWith("&")) {
				name = name.substring(1);
				behavior = "Conjunction";
			} else if (name.equals("broadcaster")) {
				behavior = "Broadcaster";
			} else {
				throw new IllegalArgumentException(name);
			}
			var outputs = Arrays.stream(moduleMatcher.group(2).split(", ")).collect(Collectors.toSet());
			return new Module(name, behavior, outputs);
		}

		public Set<String> getOutputs() {
			return outputs;
		}

		public Optional<Boolean> processPulse(String source, boolean pulse) {
			var pulseOutcome = behavior.processPulse(source, pulse);
			pulseOutcome.ifPresent(value -> {
				if (value) {
					highCount += outputs.size();
				} else {
					lowCount += outputs.size();
				}
			});
			return pulseOutcome;
		}

		public void resetCounters() {
			highCount = 0;
			lowCount = 0;
		}

		public void addInput(String input) {
			inputs.add(input);
		}

		public long getHighCount() {
			return highCount;
		}

		public long getLowCount() {
			return lowCount;
		}

		private interface ModuleBehavior {
			Optional<Boolean> processPulse(String source, boolean pulse);
		}

		private class FlipFlop implements ModuleBehavior {
			private boolean state = false;

			@Override
			public Optional<Boolean> processPulse(String source, boolean pulse) {
				if (pulse) {
					return Optional.empty();
				}
				state = !state;
				return Optional.of(state);
			}
		}

		private class Conjunction implements ModuleBehavior {
			private Set<String> knownLastHigh = new HashSet<>();

			@Override
			public Optional<Boolean> processPulse(String source, boolean pulse) {
				if (pulse) {
					knownLastHigh.add(source);
				} else {
					knownLastHigh.remove(source);
				}
				return Optional.of(!knownLastHigh.equals(inputs));
			}
		}

		private class Broadcaster implements ModuleBehavior {

			@Override
			public Optional<Boolean> processPulse(String source, boolean pulse) {
				return Optional.of(pulse);
			}
		}

		private class BlackHole implements ModuleBehavior {

			@Override
			public Optional<Boolean> processPulse(String source, boolean pulse) {
				return Optional.empty();
			}

		}
	}
}
