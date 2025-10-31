package y2015.day23;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import common.Input;

public class Day23 {

	public static void main(String[] args) {
		List<Consumer<Map<String, Integer>>> instructions = Input
				.parseLines("y2015/day23/day23.txt", Day23::instructionFromString).collect(Collectors.toList());
		Map<String, Integer> state = new HashMap<>();
		state.put("a", 1);
		state.put("b", 0);
		state.put("ip", 0);
		while (state.get("ip") < instructions.size())
		{
			instructions.get(state.get("ip")).accept(state);
		}
		System.out.println(state.get("b"));
	}

	private static Consumer<Map<String, Integer>> instructionFromString(String instruction) {
		if (instruction.startsWith("inc")) {
			String arg = instruction.substring(4);
			return state -> {state.compute(arg, (k, v) -> v + 1);state.compute("ip", (k, v) -> v + 1);};
		} else if (instruction.startsWith("hlf")) {
			String arg = instruction.substring(4);
			return state -> {state.compute(arg, (k, v) -> v / 2);state.compute("ip", (k, v) -> v + 1);};
		} else if (instruction.startsWith("tpl")) {
			String arg = instruction.substring(4);
			return state -> {state.compute(arg, (k, v) -> v * 3);state.compute("ip", (k, v) -> v + 1);};
		} else if (instruction.startsWith("jmp")) {
			String arg = instruction.substring(4);
			int deltva = Integer.parseInt(arg);
			return state -> state.compute("ip", (k, v) -> v + deltva);
		} else if (instruction.startsWith("jio")) {
			String arg1 = instruction.substring(4, 5);
			String arg2 = instruction.substring(7);
			int deltva = Integer.parseInt(arg2);
			return state -> {
				if (state.get(arg1) == 1) {
					state.compute("ip", (k, v) -> v + deltva);
				} else {
					state.compute("ip", (k, v) -> v + 1);
				}
			};
		} else if (instruction.startsWith("jie")) {
			String arg1 = instruction.substring(4, 5);
			String arg2 = instruction.substring(7);
			int deltva = Integer.parseInt(arg2);
			return state -> {
				if (state.get(arg1) % 2 == 0) {
					state.compute("ip", (k, v) -> v + deltva);
				} else {
					state.compute("ip", (k, v) -> v + 1);
				}
			};
		}
		throw new IllegalArgumentException(instruction);
	}
}
