package y2017.day18;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import common.Computer;
import common.Computer.JumpCommand;
import common.Input;

public class Day18 {

	public static void main(String[] argz) throws InterruptedException, ExecutionException {
		Computer<Long> cmp = new Computer<>();
		cmp.setRegisterDefaultValue(0L);
		cmp.registerNormalCommand("snd (.+)", (exec, args) -> {
			exec.writeOutput("io", exec.getValue(args.get(0)));
			if (exec.getValue("execId") == 1L)
			{
				exec.getRegisters().merge("sendCount", 1L, (o, n) -> o+n);
				System.out.println(exec.getRegisters().get("sendCount"));
			}
		});
		cmp.registerNormalCommand("set ([a-z]) (.+)", (exec, args) -> {
			exec.getRegisters().put(args.get(0), exec.getValue(args.get(1)));
		});
		cmp.registerNormalCommand("add ([a-z]) (.+)", (exec, args) -> {
			exec.getRegisters().merge(args.get(0), exec.getValue(args.get(1)), (v1, v2) -> v1 + v2);
		});
		cmp.registerNormalCommand("mul ([a-z]) (.+)", (exec, args) -> {
			exec.getRegisters().put(args.get(0), exec.getValue(args.get(0)) * exec.getValue(args.get(1)));
		});
		cmp.registerNormalCommand("mod ([a-z]) (.+)", (exec, args) -> {
			exec.getRegisters().merge(args.get(0), exec.getValue(args.get(1)), (v1, v2) -> v1 % v2);
		});
		cmp.registerNormalCommand("rcv (.+)", (exec, args) -> {
			Long value = exec.readInput("io");
			exec.getRegisters().put(args.get(0), value);
		});
		cmp.registerCommand("jgz (.+) (.+)", JumpCommand.relative((exec, args) -> exec.getValue(args.get(0)) > 0,
				(exec, args) -> exec.getValue(args.get(1)).intValue()));
		List<String> program = Input.parseLines("y2017/day18/day18.txt").collect(Collectors.toList());
		cmp.setDebug();
		cmp.addRegister("execId", 0L);
		Computer<Long>.ComputerExecution exec1 = cmp.executeAsync(program);
		cmp.addRegister("execId", 1L);
		cmp.addRegister("p", 1L);
		Computer<Long>.ComputerExecution exec2 = cmp.executeAsync(program);
		BlockingQueue<Long> o1i0 = new ArrayBlockingQueue<>(1000);
		BlockingQueue<Long> o0i1 = new ArrayBlockingQueue<>(1000);
		exec1.registerInput("io", o1i0);
		exec1.registerOutput("io", o0i1);
		exec2.registerInput("io", o0i1);
		exec2.registerOutput("io", o1i0);
		exec1.execute();
		exec2.execute();
		System.out.println(exec1.getResult());
		System.out.println(exec2.getResult());
	}
}
