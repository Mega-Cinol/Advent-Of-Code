package y2024.day17;

import common.AdventSolution;
import common.Computer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day17 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var input = getInput().toList();
        var regA = Long.parseLong(getFromList("Register A", input));
        var computer = new Computer<Long>();
        var out = initializeComputer(regA, computer, input);
        computer.execute(Arrays.asList(getFromList("Program", input).split(",")));
        computer.shutdown();
        return String.join(",", out);
    }

    private long resolveComboParam(long value, Computer<Long>.ComputerExecution execution) {
        if (value <= 3) {
            return value;
        }
        return switch ((int) value) {
            case 4 -> execution.getRegisters().get("A");
            case 5 -> execution.getRegisters().get("B");
            case 6 -> execution.getRegisters().get("C");
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    private String getFromList(String key, List<String> list) {
        return list.stream()
                .filter(entry -> entry.startsWith(key))
                .map(entry -> entry.split(": ")[1])
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(key + " not found"));
    }

    private List<String> initializeComputer(long regA, Computer<Long> computer, List<String> input) {
        computer.addRegister("A", regA);

        var regB = Long.parseLong(getFromList("Register B", input));
        computer.addRegister("B", regB);

        var regC = Long.parseLong(getFromList("Register C", input));
        computer.addRegister("C", regC);
        computer.registerCommand("0", (execution, args) -> {
            var argument = Long.parseLong(execution.getProgram().get(execution.getIp() + 1));
            var argValue = resolveComboParam(argument, execution);
            var result = execution.getRegisters().get("A") / (long) Math.pow(2, argValue);
            execution.getRegisters().put("A", result);
            execution.moveIp(2);
        });
        computer.registerCommand("6", (execution, args) -> {
            var argument = Long.parseLong(execution.getProgram().get(execution.getIp() + 1));
            var argValue = resolveComboParam(argument, execution);
            var result = execution.getRegisters().get("A") / (long) Math.pow(2, argValue);
            execution.getRegisters().put("B", result);
            execution.moveIp(2);
        });
        computer.registerCommand("7", (execution, args) -> {
            var argument = Long.parseLong(execution.getProgram().get(execution.getIp() + 1));
            var argValue = resolveComboParam(argument, execution);
            var result = execution.getRegisters().get("A") / (long) Math.pow(2, argValue);
            execution.getRegisters().put("C", result);
            execution.moveIp(2);
        });
        computer.registerCommand("1", (execution, args) -> {
            var argument = Long.parseLong(execution.getProgram().get(execution.getIp() + 1));
            var result = execution.getRegisters().get("B") ^ argument;
            execution.getRegisters().put("B", result);
            execution.moveIp(2);
        });
        computer.registerCommand("2", (execution, args) -> {
            var argument = Long.parseLong(execution.getProgram().get(execution.getIp() + 1));
            var argValue = resolveComboParam(argument, execution) % 8;
            execution.getRegisters().put("B", argValue);
            execution.moveIp(2);
        });
        computer.registerCommand("3", (execution, args) -> {
            var argument = Long.parseLong(execution.getProgram().get(execution.getIp() + 1));
            if (execution.getRegisters().get("A") == 0) {
                execution.moveIp(2);
            } else {
                execution.setIp((int) argument);
            }
        });
        computer.registerCommand("4", (execution, args) -> {
            var result = execution.getRegisters().get("B") ^ execution.getRegisters().get("C");
            execution.getRegisters().put("B", result);
            execution.moveIp(2);
        });
        var out = new ArrayList<String>();
        computer.registerCommand("5", (execution, args) -> {
            var argument = Long.parseLong(execution.getProgram().get(execution.getIp() + 1));
            var argValue = resolveComboParam(argument, execution) % 8;
            out.add(String.valueOf(argValue));
            execution.moveIp(2);
        });
        return out;
    }

    @Override
    public Object part2Solution() {
        var input = getInput().toList();

        var program = Arrays.asList(getFromList("Program", input).split(","));
        var outToA = LongStream.rangeClosed(1, 1024).mapToObj(a -> {
                    var computer = new Computer<Long>();
                    var out = initializeComputer(a, computer, input);
                    computer.execute(program);
                    computer.shutdown();
                    return Map.entry(out.getFirst(), a);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Set.of(e.getValue()),
                        (Set<Long> l1, Set<Long> l2) -> {
                            var result = new HashSet<>(l1);
                            result.addAll(l2);
                            return result;
                        }));
        var currentAs = outToA.get(program.get(program.size() - 1))
                .stream()
                .filter(a -> a < 8)
                .collect(Collectors.toSet());
        for (var i = program.size() - 2; i >= 0; i--) {
            var nextPossibleAs = outToA.get(program.get(i));
            currentAs = currentAs.stream()
                    .flatMap(a -> LongStream.rangeClosed(0, 7)
                            .map(bit -> a * 8 + bit)
                            .filter(possibleA -> nextPossibleAs.contains(possibleA % 1024 == 0 ? 1024 : possibleA % 1024))
                            .boxed())
                    .collect(Collectors.toSet());
        }
        return currentAs.stream().min(Long::compareTo).get();
    }

    public static void main(String[] args) {
        new Day17().solve();
    }
}
