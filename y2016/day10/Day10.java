package y2016.day10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day10 {
	private final static Map<Integer, Bot> BOTS = new HashMap<>();

	public static void main(String[] args) {
		Input.parseLines("y2016/day10/day10.txt", Function.identity()).forEach(Day10::parse);
		System.out.println(BOTS.get(-1).getValue() * BOTS.get(-2).getValue() * BOTS.get(-3).getValue());
	}

	private static void parse(String command)
	{
		if (command.startsWith("value"))
		{
			Pattern rectPattern = Pattern.compile("(\\d+) goes to bot (\\d+)");
			Matcher rectMatcher = rectPattern.matcher(command);
			rectMatcher.find();
			int value = Integer.parseInt(rectMatcher.group(1));
			int botId = Integer.parseInt(rectMatcher.group(2));
			Bot bot = BOTS.computeIfAbsent(botId, k -> new Bot(BOTS, false, k));
			bot.give(value);
		}
		else {
			Pattern rectPattern = Pattern.compile("bot (\\d+) gives low to ([a-z]+) (\\d+) and high to ([a-z]+) (\\d+)");
			Matcher rectMatcher = rectPattern.matcher(command);
			rectMatcher.find();
			int lowTargetId = addTargetBot(rectMatcher.group(2), rectMatcher.group(3));
			int highTargetId = addTargetBot(rectMatcher.group(4), rectMatcher.group(5));
			int botId = Integer.parseInt(rectMatcher.group(1));
			Bot bot = BOTS.computeIfAbsent(botId, k -> new Bot(BOTS, false, k));
			bot.configure(lowTargetId, highTargetId);
		}
	}
	private static int addTargetBot(String type, String id)
	{
		int targetId = Integer.parseInt(id);
		if (type.equals("output")) {
			targetId++;
			targetId *= -1;
		}
		BOTS.computeIfAbsent(targetId, k -> new Bot(BOTS, k < 0, k));
		return targetId;
	}
	private static class Bot
	{
		private final int myId;
		private final Map<Integer, Bot> otherBots;
		private Integer lowerDestination;
		private Integer higherDestination;

		private final List<Integer> items = new ArrayList<>();
		private final boolean isOutput;

		public Bot(Map<Integer, Bot> bots, boolean isOutput, int id)
		{
			otherBots = bots;
			this.isOutput = isOutput;
			myId = id;
		}

		public void configure(int lowerDest, int higherDest)
		{
			lowerDestination = lowerDest;
			higherDestination = higherDest;
			processItems();
		}

		public void give(int value)
		{
			items.add(value);
			if (isOutput)
			{
				return;
			}
			processItems();
			
		}
		private void processItems()
		{
			if ((items.size() > 1) && (lowerDestination != null) && (higherDestination != null))
			{
				int lower = Math.min(items.get(0), items.get(1));
				int higher = Math.max(items.get(0), items.get(1));
				System.out.println(myId + " lower: " + lower + " higher: " + higher);
				items.clear();
				otherBots.get(lowerDestination).give(lower);
				otherBots.get(higherDestination).give(higher);
			}
		}
		public int getValue()
		{
			return items.get(0);
		}
	}
}
