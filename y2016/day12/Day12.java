package y2016.day12;

import java.util.List;
import java.util.stream.Collectors;

import common.Computer;
import common.Computer.JumpCommand;
import common.Computer.NormalCommand;
import common.Input;

public class Day12 {

	public static void main(String[] argz) {
		Computer<Integer> computer = new Computer<>();
		computer.setRegisterDefaultValue(0);
		computer.addRegister("a");
		computer.addRegister("b");
		computer.addRegister("c");
		computer.addRegister("d");
		computer.registerCommand("cpy (-?\\d+) ([abcd])",
				NormalCommand.of((exec, args) -> exec.getRegisters().put(args.get(1), Integer.parseInt(args.get(0)))));
		computer.registerCommand("cpy ([abcd]) ([abcd])", NormalCommand
				.of((exec, args) -> exec.getRegisters().put(args.get(1), exec.getRegisters().get(args.get(0)))));
		computer.registerCommand("inc ([abcd])", NormalCommand
				.of((exec, args) -> exec.getRegisters().merge(args.get(0), 1, (o, n) -> o + n)));
		computer.registerCommand("dec ([abcd])", NormalCommand
				.of((exec, args) -> exec.getRegisters().merge(args.get(0), -1, (o, n) -> o + n)));
		computer.registerCommand("jnz ([abcd]) (-?\\d+)",
				JumpCommand.relative((exec, args) -> exec.getRegisters().get(args.get(0)) != 0,
						(exec, args) -> Integer.parseInt(args.get(1))));
		computer.registerCommand("jnz (-?\\d+) (-?\\d+)",
				JumpCommand.relative((exec, args) -> !args.get(0).equals("0"),
						(exec, args) -> Integer.parseInt(args.get(1))));
		List<String> program = Input.parseLines("y2016/day12/day12.txt").collect(Collectors.toList());
		System.out.println(computer.execute(program));
	}

}
