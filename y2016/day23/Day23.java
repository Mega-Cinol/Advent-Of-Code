package y2016.day23;

import java.util.List;
import java.util.stream.Collectors;

import common.Computer;
import common.Input;
import common.Computer.JumpCommand;
import common.Computer.NormalCommand;

public class Day23 {

	public static void main(String[] argz) {
		Computer<Long> computer = new Computer<>();
		computer.setRegisterDefaultValue(0L);
		computer.addRegister("a", 12L);
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
		computer.registerCommand("add ([abcd]) ([abcd])", NormalCommand.of((exec, args) -> {
			long rv = exec.getRegisters().get(args.get(1));
			exec.getRegisters().merge(args.get(0), rv, (o, n) -> o + n);
		}));
		computer.registerCommand("mul ([abcd]) ([abcd])", NormalCommand.of((exec, args) -> {
			long rv = exec.getRegisters().get(args.get(1));
			exec.getRegisters().merge(args.get(0), rv, (o, n) -> o * n);
		}));
		computer.registerCommand("nop", NormalCommand.of((exec, args) -> {}));

//		computer.setDebug();
		List<String> program = Input.parseLines("y2016/day23/day23.txt").collect(Collectors.toList());
		System.out.println(computer.execute(program));
	}

}
