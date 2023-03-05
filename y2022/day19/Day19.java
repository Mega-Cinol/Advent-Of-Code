package y2022.day19;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import common.Input;

public class Day19 {

	private static final Map<Integer, Integer> PRICE_TO_TIME_TO_COLLECT = new HashMap<>();
	static {
		for (int i = 5; i <= 6; i++) {
			PRICE_TO_TIME_TO_COLLECT.put(i, 4);
		}
		for (int i = 7; i <= 10; i++) {
			PRICE_TO_TIME_TO_COLLECT.put(i, 5);
		}
		for (int i = 11; i <= 15; i++) {
			PRICE_TO_TIME_TO_COLLECT.put(i, 6);
		}
		for (int i = 16; i <= 20; i++) {
			PRICE_TO_TIME_TO_COLLECT.put(i, 7);
		}
	}

	public static void main(String[] args) {
		var part1 = Input.parseLines("y2022/day19/day19.txt").map(Blueprint::from).map(Factory::new)
				// .peek(f -> System.out.println(f.blueprint)).peek(f -> {
				// System.out.println(f);
				// })
				.mapToLong(f -> {
					var result = evaluateBlueprint(f, 24);
//					System.out.println(result);
					return result * f.getBlueprint().id();
				}).sum();
		System.out.println(part1);
		var part2 = Input.parseLines("y2022/day19/day19.txt").limit(3).map(Blueprint::from).map(Factory::new)
				.mapToLong(f -> evaluateBlueprint(f, 32)).reduce((a, b) -> a * b).getAsLong();
		System.out.println(part2);
	}

	private static long evaluateBlueprint(Factory factory, int timeLimit) {
//		System.out.println(factory.getBlueprint().id() + " - " + factory.getTime());
		if (factory.getTime() >= timeLimit || factory.isHopeless(timeLimit)) {
			return factory.getGeodeCrashed();
		}
		return Stream.of(Command.values()).filter(c -> c.getCondition().test(factory)).mapToLong(c -> {
//			System.out.println("Before " + factory);
//			System.out.println("Executing " + c);
			c.getAction().accept(factory);
//			System.out.println("After " + factory);
			var cmdValue = evaluateBlueprint(factory, timeLimit);
//			System.out.println("Before " + factory);
//			System.out.println("Undoing " + c);
			c.getUndo().accept(factory);
//			System.out.println("After " + factory);
			return cmdValue;
		}).max().getAsLong();
	}

	private static class Factory {
		private int ore = 0;
		private int clay = 0;
		private int obsidian = 0;
		private int geodeCrashed = 0;

		private int oreBots = 1;
		private int clayBots = 0;
		private int obsidianBots = 0;
		private int geodeBots = 0;

		private final Blueprint blueprint;

		private int time = 0;

		public int getTime() {
			return time;
		}

		public Factory(Blueprint blueprint) {
			this.blueprint = blueprint;
		}

		public int getGeodeCrashed() {
			return geodeCrashed;
		}

		@Override
		public String toString() {
			return "Factory [ore=" + ore + ", clay=" + clay + ", obsidian=" + obsidian + ", geodeCrashed="
					+ geodeCrashed + ", oreBots=" + oreBots + ", clayBots=" + clayBots + ", obsidianBots="
					+ obsidianBots + ", geodeBots=" + geodeBots + ", time=" + time + "]";
		}

		public Blueprint getBlueprint() {
			return blueprint;
		}

		public void waitTurn() {
			time++;
			ore += oreBots;
			clay += clayBots;
			obsidian += obsidianBots;
			geodeCrashed += geodeBots;
		}

		public void unwaitTurn() {
			time--;
			ore -= oreBots;
			clay -= clayBots;
			obsidian -= obsidianBots;
			geodeCrashed -= geodeBots;
		}

		public void buildOreBot() {
			ore -= blueprint.oreOreCost;
			waitTurn();
			oreBots++;
		}

		public void unbuildOreBot() {
			ore += blueprint.oreOreCost;
			oreBots--;
			unwaitTurn();
		}

		public void buildClayBot() {
			ore -= blueprint.clayOreCost;
			waitTurn();
			clayBots++;
		}

		public void unbuildClayBot() {
			ore += blueprint.clayOreCost;
			clayBots--;
			unwaitTurn();
		}

		public void buildObsidianBot() {
			ore -= blueprint.obsidianOreCost;
			clay -= blueprint.obsidianClayCost;
			waitTurn();
			obsidianBots++;
		}

		public void unbuildObsidianBot() {
			ore += blueprint.obsidianOreCost;
			clay += blueprint.obsidianClayCost;
			obsidianBots--;
			unwaitTurn();
		}

		public void buildGeodeBot() {
			ore -= blueprint.geodeOreCost;
			obsidian -= blueprint.geodeObsidianCost;
			waitTurn();
			geodeBots++;
		}

		public void unbuildGeodeBot() {
			ore += blueprint.geodeOreCost;
			obsidian += blueprint.geodeObsidianCost;
			geodeBots--;
			unwaitTurn();
		}

		public boolean makesSenseToWait() {
			return ore < Math.max(Math.max(blueprint.oreOreCost, blueprint.clayOreCost),
					Math.max(blueprint.obsidianOreCost, blueprint.geodeOreCost));
		}

		public boolean canAffordOreBot() {
			return ore >= blueprint.oreOreCost
					&& oreBots < Math.max(Math.max(blueprint.oreOreCost, blueprint.clayOreCost),
							Math.max(blueprint.obsidianOreCost, blueprint.geodeOreCost));
		}

		public boolean canAffordClayBot() {
			return ore >= blueprint.clayOreCost && clayBots < blueprint.obsidianClayCost;
		}

		public boolean canAffordObsidianBot() {
			return ore >= blueprint.obsidianOreCost && clay >= blueprint.obsidianClayCost
					&& obsidianBots < blueprint.geodeObsidianCost;
		}

		public boolean canAffordGeodeBot() {
			return ore >= blueprint.geodeOreCost && obsidian >= blueprint.geodeObsidianCost;
		}

		private boolean canAffordObsidianBotEachTurn() {
			return canAffordObsidianBot() && clayBots >= blueprint.obsidianClayCost
					&& oreBots >= blueprint.obsidianOreCost;
		}

		private boolean canAffordClayBotEachTurn() {
			return canAffordClayBot() && oreBots >= blueprint.clayOreCost;
		}

		public boolean isHopeless(int timeLimit) {
			if (time >= timeLimit - 1 && geodeBots == 0) {
				return true;
			}
			if (time >= timeLimit - 2 && geodeBots == 0 && !canAffordGeodeBot()) {
				return true;
			}
			if (obsidianBots == 0 && geodeBots == 0
					&& time >= timeLimit - 1 - PRICE_TO_TIME_TO_COLLECT.get(blueprint.geodeObsidianCost)
					&& !canAffordObsidianBotEachTurn()) {
				return true;
			}
			if (clayBots == 0 && obsidianBots == 0 && geodeBots == 0
					&& time >= timeLimit - 1 - PRICE_TO_TIME_TO_COLLECT.get(blueprint.geodeObsidianCost)
							- PRICE_TO_TIME_TO_COLLECT.get(blueprint.obsidianClayCost)
					&& !canAffordClayBotEachTurn()) {
				return true;
			}
			return false;
		}
	}

	private enum Command {
		WAIT(Factory::makesSenseToWait, Factory::waitTurn, Factory::unwaitTurn),
		ORE(Factory::canAffordOreBot, Factory::buildOreBot, Factory::unbuildOreBot),
		CLAY(Factory::canAffordClayBot, Factory::buildClayBot, Factory::unbuildClayBot),
		OBSIDIAN(Factory::canAffordObsidianBot, Factory::buildObsidianBot, Factory::unbuildObsidianBot),
		GEODE(Factory::canAffordGeodeBot, Factory::buildGeodeBot, Factory::unbuildGeodeBot);

		private final Predicate<Factory> condition;
		private final Consumer<Factory> action;
		private final Consumer<Factory> undo;

		Command(Predicate<Factory> condition, Consumer<Factory> action, Consumer<Factory> undo) {
			this.condition = condition;
			this.action = action;
			this.undo = undo;
		}

		public Predicate<Factory> getCondition() {
			return condition;
		}

		public Consumer<Factory> getAction() {
			return action;
		}

		public Consumer<Factory> getUndo() {
			return undo;
		}
	}

	private record Blueprint(int id, int oreOreCost, int clayOreCost, int obsidianOreCost, int obsidianClayCost,
			int geodeOreCost, int geodeObsidianCost) {
		public static Blueprint from(String description) {
			var p = Pattern.compile(
					"Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
			var m = p.matcher(description);
			if (!m.matches()) {
				throw new IllegalArgumentException(description);
			}
			int id = Integer.parseInt(m.group(1));
			int oreOreCost = Integer.parseInt(m.group(2));
			int clayOreCost = Integer.parseInt(m.group(3));
			int obsidianOreCost = Integer.parseInt(m.group(4));
			int obsidianClayCost = Integer.parseInt(m.group(5));
			int geodeOreCost = Integer.parseInt(m.group(6));
			int geodeObsidianCost = Integer.parseInt(m.group(7));

			return new Blueprint(id, oreOreCost, clayOreCost, obsidianOreCost, obsidianClayCost, geodeOreCost,
					geodeObsidianCost);
		}
	}
}
