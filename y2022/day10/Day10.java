package y2022.day10;

import java.util.HashSet;
import java.util.Set;

import common.Computer;
import common.Input;
import common.Point;

public class Day10 {

	public static void main(String[] args) {
		Set<Point> crt = new HashSet<>();
		var computer = new Computer<Long>();
		computer.addRegister("x", 1L);
		computer.addRegister("cycles", 0L);
		computer.addRegister("result", 0L);
		computer.registerNormalCommand("noop", (exec, argz) -> {
			tick(exec, crt);
		});
		computer.registerNormalCommand("addx (-?\\d+)", (exec, argz) -> {
			tick(exec, crt);
			tick(exec, crt);
			var x = exec.getRegisters().get("x");
			x += Long.parseLong(argz.get(0));
			exec.getRegisters().put("x", x);
		});
		System.out.println(computer.execute(Input.parseLines("y2022/day10/day10.txt").toList()).get("result"));
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 40; x++) {
				if (crt.contains(new Point(x, y))) {
					System.out.print('#');
				} else {
					System.out.print(' ');
				}
			}
			System.out.println();
		}
	}

	private static final void tick(Computer<Long>.ComputerExecution exec, Set<Point> crt) {
		var cycles = exec.getRegisters().get("cycles");
		var x = exec.getRegisters().get("x");
		if (x - 1 <= cycles % 40 && x + 1 >= cycles % 40) {
			crt.add(new Point(cycles % 40, cycles / 40));
		}
		if (((cycles + 20) % 40) == 0 && cycles <= 220) {
			var result = exec.getRegisters().get("result");
			result += cycles * exec.getRegisters().get("x");
			exec.getRegisters().put("result", result);
		}
		cycles++;
		exec.getRegisters().put("cycles", cycles);
	}

}
