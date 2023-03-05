package y2016.day25;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import common.Computer;
import common.Computer.JumpCommand;
import common.Computer.NormalCommand;
import common.Input;

public class Day25 {
	private final static List<Long> output = new ArrayList<>();
	private final static Set<String> visited = new HashSet<>();
	public static void main(String[] argz) {
		Computer<Long> computer = new Computer<>();
		computer.setRegisterDefaultValue(0L);
		computer.addRegister("a");
		computer.addRegister("b");
		computer.addRegister("c");
		computer.addRegister("d");
		computer.setSkipInvalid();
		computer.registerCommand("cpy (-?\\d+) ([abcd])",
				NormalCommand.of((exec, args) -> exec.getRegisters().put(args.get(1), Long.parseLong(args.get(0)))));
		computer.registerCommand("cpy ([abcd]) ([abcd])", NormalCommand
				.of((exec, args) -> exec.getRegisters().put(args.get(1), exec.getRegisters().get(args.get(0)))));
		computer.registerCommand("inc ([abcd])",
				NormalCommand.of((exec, args) -> exec.getRegisters().merge(args.get(0), 1L, (o, n) -> o + n)));
		computer.registerCommand("dec ([abcd])",
				NormalCommand.of((exec, args) -> exec.getRegisters().merge(args.get(0), -1L, (o, n) -> o + n)));
		computer.registerCommand("jnz ([abcd]) (-?\\d+)",
				JumpCommand.relative((exec, args) -> exec.getRegisters().get(args.get(0)) != 0,
						(exec, args) -> Integer.parseInt(args.get(1))));
		computer.registerCommand("jnz (-?\\d+) (-?\\d+)", JumpCommand.relative((exec, args) -> !args.get(0).equals("0"),
				(exec, args) -> Integer.parseInt(args.get(1))));
		computer.registerCommand("jnz (-?\\d+) ([abcd])", JumpCommand.relative((exec, args) -> !args.get(0).equals("0"),
				(exec, args) -> exec.getRegisters().get(args.get(1)).intValue()));
		computer.registerNormalCommand("tgl ([abcd])", (exec, args) -> {
			int offset = exec.getRegisters().get(args.get(0)).intValue();
			int ip = exec.getIp();
			List<String> prog = exec.getProgram();
			if (offset + ip >= prog.size()) {
				return;
			}
			String toUpdate = prog.get(ip + offset);
			if (toUpdate.startsWith("inc")) {
				toUpdate = toUpdate.replaceFirst("inc", "dec");
			} else if (toUpdate.startsWith("dec")) {
				toUpdate = toUpdate.replaceFirst("dec", "inc");
			} else if (toUpdate.startsWith("tgl")) {
				toUpdate = toUpdate.replaceFirst("tgl", "inc");
			} else if (toUpdate.startsWith("jnz")) {
				toUpdate = toUpdate.replaceFirst("jnz", "cpy");
			} else if (toUpdate.startsWith("cpy")) {
				toUpdate = toUpdate.replaceFirst("cpy", "jnz");
			}
			prog.set(ip + offset, toUpdate);
		});
		computer.registerCommand("out ([abcd])", NormalCommand
				.of((exec, args) -> output.add(exec.getRegisters().get(args.get(0)))));
		computer.setStopCondition(exec -> !visited.add(execHash(exec)));


//		computer.setDebug();
		List<String> program = Input.parseLines("y2016/day25/day25.txt").collect(Collectors.toList());
		long value = 0L;
		while (!testValue(value++, computer, program));
		System.out.println(value);
	}

	private static boolean testValue(long value, Computer<Long> computer, List<String> program)
	{
		visited.clear();
		computer.addRegister("a", value);
		output.clear();
		computer.execute(program);
		System.out.println(value + " " + output);
		long expected = 0;
		for (Long printed : output)
		{
			if (printed != expected)
			{
				return false;
			}
			expected = 1 - expected;
		}
		return true;
	}
	private static String execHash(Computer<Long>.ComputerExecution exec)
	{
		StringBuilder hashBuilder = new StringBuilder();
		hashBuilder.append(exec.getIp());
		hashBuilder.append(';');
		hashBuilder.append(exec.getRegisters().get("a"));
		hashBuilder.append(';');
		hashBuilder.append(exec.getRegisters().get("b"));
		hashBuilder.append(';');
		hashBuilder.append(exec.getRegisters().get("c"));
		hashBuilder.append(';');
		hashBuilder.append(exec.getRegisters().get("d"));
		hashBuilder.append(';');
		return hashBuilder.toString();
	}
}
