package y2018.day16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day16 {
	private static final BinaryOperator<List<Integer>> ADDR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) + regs.get(op.get(2)));
//		if (op.get(0) == 1) {
//			System.out.println("ADDR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> ADDI = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) + op.get(2));
//		if (op.get(0) == 1) {
//			System.out.println("ADDI: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> MULR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) * regs.get(op.get(2)));
//		if (op.get(0) == 1) {
//			System.out.println("MULR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> MULI = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) * op.get(2));
//		if (op.get(0) == 1) {
//			System.out.println("MULI: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> BANR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) & regs.get(op.get(2)));
//		if (op.get(0) == 1) {
//			System.out.println("BANR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> BANI = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) & op.get(2));
//		if (op.get(0) == 1) {
//			System.out.println("BANI: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> BORR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) | regs.get(op.get(2)));
//		if (op.get(0) == 1) {
//			System.out.println("BORR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> BORI = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) | op.get(2));
//		if (op.get(0) == 1) {
//			System.out.println("BORI: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> SETR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)));
//		if (op.get(0) == 1) {
//			System.out.println("SETR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> SETI = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), op.get(1));
//		if (op.get(0) == 1) {
//			System.out.println("SETI: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> GTIR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), op.get(1) > regs.get(op.get(2)) ? 1 : 0);
//		if (op.get(0) == 1) {
//			System.out.println("GTIR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> GTRI = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) > op.get(2) ? 1 : 0);
//		if (op.get(0) == 1) {
//			System.out.println("GTRI: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> GTRR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) > regs.get(op.get(2)) ? 1 : 0);
//		if (op.get(0) == 1) {
//			System.out.println("GTRR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> EQIR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), op.get(1) == regs.get(op.get(2)) ? 1 : 0);
//		if (op.get(0) == 1) {
//			System.out.println("EQIR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> EQRI = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) == op.get(2) ? 1 : 0);
//		if (op.get(0) == 1) {
//			System.out.println("EQRI: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final BinaryOperator<List<Integer>> EQRR = (regs, op) -> {
		ArrayList<Integer> result = new ArrayList<>(regs);
		result.set(op.get(3), regs.get(op.get(1)) == regs.get(op.get(2)) ? 1 : 0);
//		if (op.get(0) == 1) {
//			System.out.println("EQRR: op: " + op + " regs: " + regs + " result: " + result);
//		}
		return result;
	};
	private static final Set<BinaryOperator<List<Integer>>> OPERATIONS = new HashSet<>();
	static {
		OPERATIONS.add(ADDI);
		OPERATIONS.add(ADDR);

		OPERATIONS.add(MULI);
		OPERATIONS.add(MULR);

		OPERATIONS.add(BANI);
		OPERATIONS.add(BANR);

		OPERATIONS.add(BORI);
		OPERATIONS.add(BORR);

		OPERATIONS.add(SETI);
		OPERATIONS.add(SETR);

		OPERATIONS.add(GTRI);
		OPERATIONS.add(GTIR);
		OPERATIONS.add(GTRR);

		OPERATIONS.add(EQRI);
		OPERATIONS.add(EQIR);
		OPERATIONS.add(EQRR);
	}

	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2018/day16/day16.txt").collect(Collectors.toList());
		Sample currentSample = new Sample();
		int sampleManyMatches = 0;
		Map<Integer, Set<BinaryOperator<List<Integer>>>> opCodeMap = new HashMap<>();
		int idx = 0;
		for (; idx < input.size(); idx++) {
			String line = input.get(idx);
			if (line.startsWith("Before")) {
				currentSample.setBefore(line);
			} else if (line.startsWith("After")) {
				currentSample.setAfter(line);
			} else if (!line.isEmpty()) {
				currentSample.setOp(line);
			} else {
				if (!currentSample.isInitialized()) {
					break;
				}
				Sample smp = currentSample;
				Set<BinaryOperator<List<Integer>>> mathcingOps = OPERATIONS.stream().filter(op -> smp.matches(op))
						.collect(Collectors.toCollection(HashSet::new));
//				if (smp.getOpCode() == 1)
//				{
//					System.out.println("====================");
//				}
				if (opCodeMap.containsKey(smp.getOpCode())) {
					opCodeMap.get(smp.getOpCode()).retainAll(mathcingOps);
				} else {
					opCodeMap.put(smp.getOpCode(), mathcingOps);
				}
				if (mathcingOps.size() >= 3) {
					sampleManyMatches++;
				}
				currentSample = new Sample();
			}
		}
		System.out.println(sampleManyMatches);
		Map<Integer, BinaryOperator<List<Integer>>> opCodeMapCompiled = new HashMap<>();
		while (!opCodeMap.isEmpty())
		{
			opCodeMap.entrySet().stream().filter(e -> e.getValue().size() == 1).forEach(e -> 
				opCodeMapCompiled.put(e.getKey(), e.getValue().iterator().next())
			);
			opCodeMap.values().stream().forEach(ops -> ops.removeAll(opCodeMapCompiled.values()));
			for (int i = 0 ; i < 16 ; i++)
			{
				if (opCodeMap.get(i) != null)
				{
					if (opCodeMap.get(i).isEmpty())
					{
						opCodeMap.remove(i);
					}
				}
			}
		}
		while (input.get(idx).isEmpty()) {
			idx++;
		}
		List<Integer> registers = new ArrayList<>();
		registers.add(0);
		registers.add(0);
		registers.add(0);
		registers.add(0);
		for (; idx < input.size(); idx++) {
			List<Integer> op = Stream.of(input.get(idx).split(" ")).map(Integer::parseInt).collect(Collectors.toList());
			registers = opCodeMapCompiled.get(op.get(0)).apply(registers, op);
		}
		System.out.println(registers.get(0));
	}

	private static class Sample {
		private List<Integer> before;
		private List<Integer> after;
		private List<Integer> op;

		public void setBefore(String before) {
			this.before = Stream.of(before.substring(before.indexOf("[") + 1, before.indexOf("]")).split(", "))
					.map(Integer::parseInt).collect(Collectors.toList());
		}

		public void setAfter(String after) {
			this.after = Stream.of(after.substring(after.indexOf("[") + 1, after.indexOf("]")).split(", "))
					.map(Integer::parseInt).collect(Collectors.toList());
		}

		public void setOp(String op) {
			this.op = Stream.of(op.split(" ")).map(Integer::parseInt).collect(Collectors.toList());
		}

		public int getOpCode() {
			return op.get(0);
		}

		public boolean isInitialized() {
			return before != null && after != null && op != null;
		}

		public boolean matches(BinaryOperator<List<Integer>> operation) {
			boolean matches = after.equals(operation.apply(before, op));
//			if (op.get(0) == 1) {
//				System.out.println((matches ? "Matches " : "Doesn't match ") + after);
//			}
			return matches;
		}
	}
}