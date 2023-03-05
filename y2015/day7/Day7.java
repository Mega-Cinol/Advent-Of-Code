package y2015.day7;

import static y2015.day7.Day7.Circuit.CachedSupplier.wrap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day7 {
	public static void main(String[] args) {
		Circuit circuit = new Circuit();
		Input.parseLines("y2015/day7/day7.txt", Function.identity(), circuit::addGate);
		int a = circuit.getOutput("a");
		System.out.println(a);
//		circuit.addGate("" + a + " -> b");
//		circuit.reset();
//		a = circuit.getOutput("a");
//		System.out.println(a);
	}

	public static class Circuit {
		private Map<String, CachedSupplier> wires = new HashMap<>();

		public void reset()
		{
			wires.values().forEach(CachedSupplier::reset);
		}
		public void addGate(String description) {
			Pattern gatePattern = Pattern.compile(
					"^((?<op1>[a-z]+|\\d+) )?((?<cmd>NOT|AND|OR|RSHIFT|LSHIFT) )?(?<op2>[a-z]+|\\d+) -> (?<result>[a-z]+)");
			Matcher gateMatcher = gatePattern.matcher(description);
			if (!gateMatcher.matches()) {
				throw new IllegalArgumentException(description);
			}
			String output = gateMatcher.group("result");
			String cmd = gateMatcher.group("cmd");
			if (cmd == null) {
				wires.put(output, fromString(gateMatcher.group("op2")));
			} else {
				CachedSupplier opSupplier;
				switch (cmd) {
				case "NOT":
					opSupplier = notSupplier(fromString(gateMatcher.group("op2")));
					break;
				case "AND":
					opSupplier = andSupplier(fromString(gateMatcher.group("op1")),
							fromString(gateMatcher.group("op2")));
					break;
				case "OR":
					opSupplier = orSupplier(fromString(gateMatcher.group("op1")), fromString(gateMatcher.group("op2")));
					break;
				case "RSHIFT":
					opSupplier = rShiftSupplier(fromString(gateMatcher.group("op1")),
							fromString(gateMatcher.group("op2")));
					break;
				case "LSHIFT":
					opSupplier = lShiftSupplier(fromString(gateMatcher.group("op1")),
							fromString(gateMatcher.group("op2")));
					break;
				default:
					throw new IllegalArgumentException(description);
				}
				wires.put(output, opSupplier);
			}
		}

		private CachedSupplier fromString(String input) {
			Predicate<String> isNumber = Pattern.compile("\\d+").asPredicate();
			return isNumber.test(input) ? constantSupplier(Integer.parseInt(input)) : wireSupplier(input);
		}

		public int getOutput(String wire) {
			return wires.get(wire).getAsInt();
		}

		private CachedSupplier constantSupplier(int value) {
			return wrap(() -> { System.out.println(value); return value;});
		}

		private CachedSupplier wireSupplier(String wire) {
			return wrap(() -> { System.out.println(wire); return wires.get(wire).getAsInt();});
		}

		private CachedSupplier notSupplier(IntSupplier input) {
			return wrap(() -> ~input.getAsInt() % 65536);
		}

		private CachedSupplier andSupplier(IntSupplier input1, IntSupplier input2) {
			return wrap(() -> (input1.getAsInt() & input2.getAsInt()) % 65536);
		}

		private CachedSupplier orSupplier(IntSupplier input1, IntSupplier input2) {
			return wrap(() -> (input1.getAsInt() | input2.getAsInt()) % 65536);
		}

		private CachedSupplier rShiftSupplier(IntSupplier input, IntSupplier shift) {
			return wrap(() -> (input.getAsInt() >> shift.getAsInt()) % 65536);
		}

		private CachedSupplier lShiftSupplier(IntSupplier input, IntSupplier shift) {
			return wrap(() -> (input.getAsInt() << shift.getAsInt()) % 65536);
		}

		public static class CachedSupplier implements IntSupplier
		{
			private Integer result = null;
			private final IntSupplier source;
			private CachedSupplier(IntSupplier source)
			{
				this.source = source;
			}

			public static CachedSupplier wrap(IntSupplier source)
			{
				return new CachedSupplier(source);
			}
			@Override
			public int getAsInt() {
				if (result == null)
				{
					result = source.getAsInt();
				}
				return result;
			}
			public void reset()
			{
				result = null;
			}
		}
	}
}
