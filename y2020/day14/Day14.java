package y2020.day14;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day14 {
	public static void main(String[] args) {
		Memory mem = new Memory();
		Input.parseLines("y2020/day14/day14.txt", CommandFactory::fromString, cmd -> cmd.accept(mem));
		System.out.println(mem.getSum());
	}

	private enum CommandFactory implements BiFunction<Object, Long, Consumer<Memory>> {
		MEM((address, value) -> mem -> mem.setMemory((Long) address, value)),
		MASK((mask, ignore) -> mem -> mem.setMask((String) mask));

		private final BiFunction<Object, Long, Consumer<Memory>> function;

		CommandFactory(BiFunction<Object, Long, Consumer<Memory>> function) {
			this.function = function;
		}

		@Override
		public Consumer<Memory> apply(Object param1, Long param2) {
			return function.apply(param1, param2);
		}

		public static Consumer<Memory> fromString(String strCommand) {
			Pattern memPattern = Pattern.compile("mem\\[([0-9]*)\\] = ([0-9]*)");
			Matcher memMatcher = memPattern.matcher(strCommand);
			if (memMatcher.matches()) {
				return MEM.apply(Long.valueOf(memMatcher.group(1)), Long.valueOf(memMatcher.group(2)));
			}
			Pattern maskPattern = Pattern.compile("mask = ([01X]*)");
			Matcher maskMatcher = maskPattern.matcher(strCommand);
			if (maskMatcher.matches()) {
				return MASK.apply(maskMatcher.group(1), Long.valueOf(0));
			}
			throw new IllegalArgumentException(strCommand);
		}
	}

	private static class Memory {
//		private static final long MAX_MASK = 68719476735L;
		private Map<Long, Long> memory = new HashMap<>();
		private String mask;

		public void setMask(String strMask) {
			mask = strMask;
		}

		public void setMemory(long address, long value) {
			Set<Long> addressesToSet = new HashSet<>();
			addressesToSet.add(0L);
			for (int i = 0; i < mask.length(); i++) {
				final int idx = i;
				switch (mask.charAt(mask.length() - i - 1)) {
				case '0':
					addressesToSet = addressesToSet.stream()
							.map(val -> val + (long) ((long) Math.pow(2, idx) & address)).collect(Collectors.toSet());
					break;
				case '1':
					addressesToSet = addressesToSet.stream().map(val -> val + (long) Math.pow(2, idx))
							.collect(Collectors.toSet());
					break;
				case 'X':
					Set<Long> newAddr = addressesToSet.stream().map(val -> val + (long) Math.pow(2, idx)).collect(Collectors.toSet());
					addressesToSet.addAll(newAddr);
					break;
				}
			}
			addressesToSet.forEach(addr -> memory.put(addr, value));
		}

		public long getSum() {
			return memory.values().stream().mapToLong(Long::longValue).sum();
		}
	}
}
