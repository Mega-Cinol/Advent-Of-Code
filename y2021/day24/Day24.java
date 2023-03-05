package y2021.day24;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import common.Computer;
import common.Computer.NormalCommand;
import common.Input;

public class Day24 {

	public static void main(String[] argz) throws InterruptedException, ExecutionException {
		List<String> program = Input.parseLines("y2021/day24/day24.txt").collect(Collectors.toList());

		Computer<Long> pc = new Computer<>();
		pc.setRegisterDefaultValue(0L);
		pc.addRegister("w");
		pc.addRegister("x");
		pc.addRegister("y");
		pc.addRegister("z");
		pc.registerCommand("inp ([wxyz])", NormalCommand.of((exec, args) -> {
			long value = exec.readInput("");
			exec.getRegisters().put(args.get(0), value);
		}));
		pc.registerCommand("add ([wxyz]) (-?[0-9]+)", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = Long.parseLong(args.get(1));
			exec.getRegisters().put(args.get(0), regValue + other);
		}));
		pc.registerCommand("add ([wxyz]) ([wxyz])", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = exec.getRegisters().get(args.get(1));
			exec.getRegisters().put(args.get(0), regValue + other);
		}));
		pc.registerCommand("mul ([wxyz]) (-?[0-9]+)", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = Long.parseLong(args.get(1));
			exec.getRegisters().put(args.get(0), regValue * other);
		}));
		pc.registerCommand("mul ([wxyz]) ([wxyz])", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = exec.getRegisters().get(args.get(1));
			exec.getRegisters().put(args.get(0), regValue * other);
		}));
		pc.registerCommand("div ([wxyz]) (-?[0-9]+)", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = Long.parseLong(args.get(1));
			exec.getRegisters().put(args.get(0), regValue / other);
		}));
		pc.registerCommand("div ([wxyz]) ([wxyz])", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = exec.getRegisters().get(args.get(1));
			exec.getRegisters().put(args.get(0), regValue / other);
		}));
		pc.registerCommand("mod ([wxyz]) (-?[0-9]+)", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = Long.parseLong(args.get(1));
			exec.getRegisters().put(args.get(0), regValue % other);
		}));
		pc.registerCommand("mod ([wxyz]) ([wxyz])", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = exec.getRegisters().get(args.get(1));
			exec.getRegisters().put(args.get(0), regValue % other);
		}));
		pc.registerCommand("eql ([wxyz]) (-?[0-9]+)", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = Long.parseLong(args.get(1));
			exec.getRegisters().put(args.get(0), regValue == other ? 1L : 0L);
		}));
		pc.registerCommand("eql ([wxyz]) ([wxyz])", NormalCommand.of((exec, args) -> {
			long regValue = exec.getRegisters().get(args.get(0));
			long other = exec.getRegisters().get(args.get(1));
			exec.getRegisters().put(args.get(0), regValue == other ? 1L : 0L);
		}));
		pc.setDebug();
		Computer<Long>.ComputerExecution exec = null;
		long[] modelNumber = { 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L };
		do {
			exec = pc.executeAsync(program);
			BlockingQueue<Long> input = new ArrayBlockingQueue<>(14);
			for (long n : modelNumber)
			{
				input.add(n);
			}
			exec.registerInput("", input);
			// step model number
			int digit = 13;
			while (modelNumber[digit] == 0)
			{
				modelNumber[digit] = 9L;
				digit--;
			}
			modelNumber[digit]--;
			// start
			exec.execute();
		} while (exec.getResult().get("z") != 0);
		int digit = 13;
		while (modelNumber[digit] == 9)
		{
			modelNumber[digit] = 0L;
			digit--;
		}
		modelNumber[digit]++;
		for (long n : modelNumber)
		{
			System.out.print(n);
		}
		System.out.println();
		pc.shutdown();
	}

}
