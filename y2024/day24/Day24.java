package y2024.day24;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Day24 extends AdventSolution {
    private static final Pattern GATE_PATTERN = Pattern.compile("([a-z0-9]{3}) ([AXO][NOR][DR]?) ([a-z0-9]{3}) -> ([a-z0-9]{3})");

    @Override
    public Object part1Solution() {
        var wireRegistry = new HashMap<String, Wire>();
        var input = getInput().toList();
        var separatorIdx = input.indexOf("");
        input.subList(0, separatorIdx)
                .forEach(fixedDesc -> {
                    var parsed = fixedDesc.split(": ");
                    wireRegistry.put(parsed[0], new FixedWire("1".equals(parsed[1])));
                });
        input.subList(separatorIdx + 1, input.size()).forEach(gateDesc -> {
            var gateMatcher = GATE_PATTERN.matcher(gateDesc);
            if (!gateMatcher.matches()) {
                throw new IllegalArgumentException(gateDesc);
            }
            var input1 = gateMatcher.group(1);
            var input2 = gateMatcher.group(3);
            var operator = gateMatcher.group(2);
            var output = gateMatcher.group(4);
            wireRegistry.put(output, new Gate(input1, input2, operator, wireRegistry, output));
        });
        var numberBin = wireRegistry.entrySet().stream()
                .filter(e -> e.getKey().startsWith("z"))
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .map(Map.Entry::getValue)
                .map(Wire::evaluate)
                .toList();
        return toDec(numberBin);
    }

    private long toDec(List<Boolean> numberBin) {
        var numberDec = numberBin.get(0) ? 1L : 0L;
        for (var i = 1; i < numberBin.size(); i++) {
            if (numberBin.get(i)) {
                numberDec += 2L << (i - 1);
            }
        }
        return numberDec;
    }

    private long longToDec(List<Long> numberBin) {
        return toDec(numberBin.stream().map(d -> d == 1).toList());
    }

    private interface Wire {
        boolean evaluate();
    }

    private record FixedWire(boolean value) implements Wire {
        @Override
        public boolean evaluate() {
            return value;
        }
    }

    private record Gate(String input1, String input2, String operatorName,
                        Map<String, Wire> wireRegistry, String output) implements Wire {
        private static final Map<String, BinaryOperator<Boolean>> LOGICAL_OPERATORS = Map.of("AND", (a, b) -> a && b,
                "OR", (a, b) -> a || b,
                "XOR", (a, b) -> a ^ b);

        @Override
        public boolean evaluate() {
            return LOGICAL_OPERATORS.get(operatorName).apply(wireRegistry.get(input1).evaluate(), wireRegistry.get(input2).evaluate());
        }
        @Override
        public String toString() {
            return input1 + " " + operatorName + " " + input2 + " -> " + output;
        }
    }

    @Override
    public Object part2Solution() {
        var wireRegistry = new HashMap<String, Wire>();
        var input = getInput().toList();
        var separatorIdx = input.indexOf("");
        input.subList(0, separatorIdx)
                .forEach(fixedDesc -> {
                    var parsed = fixedDesc.split(": ");
                    wireRegistry.put(parsed[0], new FixedWire("1".equals(parsed[1])));
                });
        input.subList(separatorIdx + 1, input.size()).forEach(gateDesc -> {
            var gateMatcher = GATE_PATTERN.matcher(gateDesc);
            if (!gateMatcher.matches()) {
                throw new IllegalArgumentException(gateDesc);
            }
            var input1 = gateMatcher.group(1);
            var input2 = gateMatcher.group(3);
            var operator = gateMatcher.group(2);
            var output = gateMatcher.group(4);
            wireRegistry.put(output, new Gate(input1, input2, operator, wireRegistry, output));
        });
        for (var digit = 2; digit < 45; digit++) {
            int finalDigit = digit;
            var out = findGate(wireRegistry, g -> g.output().equals("z" + idxToString(finalDigit)));
            if (!"XOR".equals(out.operatorName)) {
                System.out.println(out);
            }
            var in1 = findGate(wireRegistry, g -> g.output().equals(out.input1) &&
                    "XOR".equals(g.operatorName) &&
                    (g.input1.equals("x" + idxToString(finalDigit)) || g.input1.equals("y" + idxToString(finalDigit))) &&
                    (g.input2.equals("x" + idxToString(finalDigit)) || g.input2.equals("y" + idxToString(finalDigit))) &&
                    !g.input1.equals(g.input2));
            var in2 = findGate(wireRegistry, g -> g.output().equals(out.input2) &&
                    "XOR".equals(g.operatorName) &&
                    (g.input1.equals("x" + idxToString(finalDigit)) || g.input1.equals("y" + idxToString(finalDigit))) &&
                    (g.input2.equals("x" + idxToString(finalDigit)) || g.input2.equals("y" + idxToString(finalDigit))) &&
                    !g.input1.equals(g.input2));
            if (in1 == null && in2 == null) {
                System.out.println(out);
            }
        }
        var numberBin = wireRegistry.entrySet().stream()
                .filter(e -> e.getKey().startsWith("z"))
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .map(Map.Entry::getValue)
                .map(Wire::evaluate)
                .map(b -> b ? 1L : 0L)
                .toList()
                .reversed();
        List<Long> x = new ArrayList<>();
        List<Long> y = new ArrayList<>();
        var idx = 0;
        while (wireRegistry.containsKey("x" + idxToString(idx))) {
            x.add(wireRegistry.get("x" + idxToString(idx)).evaluate() ? 1L : 0L);
            y.add(wireRegistry.get("y" + idxToString(idx)).evaluate() ? 1L : 0L);
            idx++;
        }
        x = x.reversed();
        y = y.reversed();
        System.out.println(x.toString() + "\n" + y.toString() + "\n" + numberBin.toString());
        x = x.reversed();
        y = y.reversed();
        numberBin = numberBin.reversed();
        System.out.println("x = " + longToDec(x) + " y = " + longToDec(y) + " result = " + longToDec(numberBin));
        var overflow = false;
        for (var i = 0; i < numberBin.size() - 1; i++) {
            var expected = x.get(i).equals(y.get(i)) ? 0L : 1L;
            if (overflow) {
                expected = 1 - expected;
            }
            if (!numberBin.get(i).equals(expected)) {
                System.out.printf("Error at index %d, expected %d but found %d. x = %d, y = %d, overflow = %b\n", i, expected, numberBin.get(i), x.get(i), y.get(i), overflow);
            }
            overflow = x.get(i) == 1 && y.get(i) == 1;
            overflow |= numberBin.get(i) == 0 && !x.get(i).equals(y.get(i));
        }
        return "This just prints some checks to speed up manual search for problems";
    }

    private Gate findGate(Map<String, Wire> wireRegistry, Predicate<Gate> criteria) {
        return wireRegistry.values().stream()
                .filter(w -> w instanceof Gate)
                .map(Gate.class::cast)
                .filter(criteria)
                .findFirst()
                .orElse(null);
    }

    private String idxToString(int idx) {
        if (idx < 10) {
            return "0" + idx;
        }
        return "" + idx;
    }

    public static void main(String[] args) {
        new Day24().solve();
    }
}
