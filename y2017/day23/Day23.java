package y2017.day23;

import common.Computer;
import common.Computer.JumpCommand;

public class Day23 {

	public static void main(String[] argz) {
		Computer<Long> cmp = new Computer<>();
		cmp.setRegisterDefaultValue(0L);
		cmp.addRegister("a", 1L);
		cmp.registerNormalCommand("set ([a-h]) (.+)", (exec, args) -> {
			exec.getRegisters().put(args.get(0), exec.getValue(args.get(1)));
		});
		cmp.registerNormalCommand("sub ([a-z]) (.+)", (exec, args) -> {
			exec.getRegisters().merge(args.get(0), -1 * exec.getValue(args.get(1)), (v1, v2) -> v1 + v2);
		});
		cmp.registerNormalCommand("mul ([a-z]) (.+)", (exec, args) -> {
			exec.getRegisters().put(args.get(0), exec.getValue(args.get(0)) * exec.getValue(args.get(1)));
		});
		cmp.registerCommand("jnz (.+) (.+)", JumpCommand.relative((exec, args) -> exec.getValue(args.get(0)) != 0,
				(exec, args) -> exec.getValue(args.get(1)).intValue()));
//		List<String> program = Input.parseLines("y2017/day23/day23.txt").collect(Collectors.toList());
		cmp.setDebug();
//		System.out.println(cmp.execute(program));
		asJava(1);
	}

	private static void asJava(int initA) {

		int h = 0;
		int a = initA;
		int b = 65;
		int c = b;
		int f = 0;
		if (a != 0) {
			b *= 100;
			b += 100000;
			c = b;
			c += 17000;
		}
		b = 10;
		c = 78;
		do {
			f = 1;
			top:
			for (int d = 2 ; d < b ; d++)
			{
				for (int e = 2 ; e * d <= b ; e++)
				{
					if (d * e == b) {
						f = 0;
						System.out.println(d + " * " + e + " = " + b);
						break top;
					}
				}
			}
			if (f == 0) {
				h++;
			} else {
				System.out.println("Prime: " + b);
			}
			b += 17;
			System.out.println(b);
		} while (b != c);
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(f);
		System.out.println(h);
	}
}
