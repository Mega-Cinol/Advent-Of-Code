package y2018.day19;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import common.Computer;
import common.Computer.Command;
import common.Input;

public class Day19 {
	public static Computer<Long> getY2018Computer() {
		Computer<Long> computer = new Computer<>();
		computer.setRegisterDefaultValue(0L);
		computer.addRegister("0");
		computer.addRegister("1");
		computer.addRegister("2");
		computer.addRegister("3");
		computer.addRegister("4");
		computer.addRegister("5");
		computer.addRegister("cmdCount");
		computer.setFailInvalid();
		computer.setStopCondition(e -> e.getIp() > e.getProgram().size());
		computer.registerCommand("addr (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) + pc.getRegisters().get(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("addi (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) + Long.parseLong(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("mulr (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) * pc.getRegisters().get(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("muli (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) * Long.parseLong(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("banr (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) & pc.getRegisters().get(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("bani (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) & Long.parseLong(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("borr (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) | pc.getRegisters().get(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("bori (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0)) | Long.parseLong(argz.get(1));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("setr (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = pc.getRegisters().get(argz.get(0));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("seti (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			long newValue = Long.parseLong(argz.get(0));
			pc.getRegisters().put(argz.get(2), newValue);
		}));
		computer.registerCommand("gtir (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			boolean comparison = Long.parseLong(argz.get(0)) > pc.getRegisters().get(argz.get(1));
			pc.getRegisters().put(argz.get(2), comparison ? 1L : 0L);
		}));
		computer.registerCommand("gtri (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			boolean comparison = pc.getRegisters().get(argz.get(0)) > Long.parseLong(argz.get(1));
			pc.getRegisters().put(argz.get(2), comparison ? 1L : 0L);
		}));
		computer.registerCommand("gtrr (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			boolean comparison = pc.getRegisters().get(argz.get(0)) > pc.getRegisters().get(argz.get(1));
			pc.getRegisters().put(argz.get(2), comparison ? 1L : 0L);
		}));
		computer.registerCommand("eqir (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			boolean comparison = Long.parseLong(argz.get(0)) == pc.getRegisters().get(argz.get(1));
			pc.getRegisters().put(argz.get(2), comparison ? 1L : 0L);
		}));
		computer.registerCommand("eqri (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			boolean comparison = pc.getRegisters().get(argz.get(0)) == Long.parseLong(argz.get(1));
			pc.getRegisters().put(argz.get(2), comparison ? 1L : 0L);
		}));
		computer.registerCommand("eqrr (\\d+) (\\d+) (\\d+)", new RegisterBoundCommand((pc, argz) -> {
			boolean comparison = pc.getRegisters().get(argz.get(0)).equals(pc.getRegisters().get(argz.get(1)));
			pc.getRegisters().put(argz.get(2), comparison ? 1L : 0L);
		}));
		return computer;
	}

	public static void main(String[] args) {
		String ipRegister = Input.parseLines("y2018/day19/day19.txt").filter(line -> line.startsWith("#")).findFirst()
				.get().substring(4);
		List<String> program = Input.parseLines("y2018/day19/day19.txt").filter(line -> !line.startsWith("#"))
				.collect(Collectors.toList());
		Computer<Long> computer = getY2018Computer();
		computer.addRegister("ipRegister", Long.parseLong(ipRegister));
		System.out.println(computer.execute(program).get("0"));
		System.out.println(LongStream.rangeClosed(1, 10551331).filter(v -> (10551331 % v) == 0).sum());
		System.exit(0);
	}

	private static class RegisterBoundCommand implements Command<Long> {
		private final Command<Long> internalCommand;

		public RegisterBoundCommand(Command<Long> internalCommand) {
			this.internalCommand = internalCommand;
		}

		@Override
		public void execute(Computer<Long>.ComputerExecution computer, List<String> args) {
			String ipRegister = computer.getRegisters().get("ipRegister").toString();
			computer.getRegisters().put(ipRegister, (long) computer.getIp());
			internalCommand.execute(computer, args);
			computer.setIp(computer.getRegisters().get(ipRegister).intValue());
			computer.getRegisters().merge("cmdCount",  1L, (o, n) -> o + n);
			computer.stepIp();
		}
	}
}
