package y2018.day21;

import java.util.HashSet;
import java.util.Set;

public class Day21 {

	public static void main(String[] args) {
		Set<Long> values = new HashSet<>();
		long last = 0;
		long zero, two, four, five;
		four = 0;
		zero = 0;
		do {
			five = four | 65536;
			four = 10704114;
			do {
				two = five & 255;
				four += two;
				four &= 16777215;
				four *= 65899;
				four &= 16777215;
				two = (five / 256);
				five = two;
			} while (two >= 256);
			four += two;
			four &= 16777215;
			four *= 65899;
			four &= 16777215;
			if (!values.add(four))
			{
				break;
			}
			last = four;
		} while (four != zero);
		System.out.println(last);

//		System.out.println("=============");
//		String ipRegister = Input.parseLines("y2018/day21/day21.txt").filter(line -> line.startsWith("#")).findFirst()
//				.get().substring(4);
//		List<String> program = Input.parseLines("y2018/day21/day21.txt").filter(line -> !line.startsWith("#"))
//				.collect(Collectors.toList());
//		Computer<Long> computer = Day19.getY2018Computer();
//		computer.addRegister("ipRegister", Long.parseLong(ipRegister));
//		
//		computer.addTraceCondition(exec -> exec.getIp() == 28);
//		computer.setStopCondition(exec -> {
//			if (exec.getIp() == 28 && !values.add(exec.getRegisters().get("4"))) {
//				System.out.println(exec.getRegisters().get("last"));
//				return true;
//			}
////			if (exec.getRegisters().get("4") == 12420065)
////			{
////				return true;
////			}
//			exec.getRegisters().put("last", exec.getRegisters().get("4"));
//			return false;
//		});
//		computer.execute(program);
//		System.exit(0);
	}

}
