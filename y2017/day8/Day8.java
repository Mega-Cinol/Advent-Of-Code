package y2017.day8;

import java.util.stream.Collectors;

import common.Computer;
import common.Computer.NormalCommand;
import common.Input;

public class Day8 {

	public static void main(String[] argz) {
		Computer<Long> c = new Computer<>();
		c.setRegisterDefaultValue(r -> 0L);
		c.setDebug();
		c.registerCommand("([a-z]+) inc (-?[0-9]+) if ([a-z]+) == (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue == Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) dec (-?[0-9]+) if ([a-z]+) == (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue == Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), -1*Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) inc (-?[0-9]+) if ([a-z]+) > (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue > Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) dec (-?[0-9]+) if ([a-z]+) > (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue > Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), -1*Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) inc (-?[0-9]+) if ([a-z]+) < (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue < Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) dec (-?[0-9]+) if ([a-z]+) < (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue < Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), -1*Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) inc (-?[0-9]+) if ([a-z]+) <= (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue <= Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) dec (-?[0-9]+) if ([a-z]+) <= (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue <= Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), -1*Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) inc (-?[0-9]+) if ([a-z]+) >= (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue >= Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) dec (-?[0-9]+) if ([a-z]+) >= (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue >= Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), -1*Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) inc (-?[0-9]+) if ([a-z]+) != (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue != Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		c.registerCommand("([a-z]+) dec (-?[0-9]+) if ([a-z]+) != (-?[0-9]+)", NormalCommand.of(
				(exec, args) -> {
					long regValue = exec.getRegisters().computeIfAbsent(args.get(2), k -> 0L);
					if (regValue != Long.parseLong(args.get(3)))
					{
						exec.getRegisters().merge(args.get(0), -1*Long.parseLong(args.get(1)), (o,n) -> o + n);
						long maxV = exec.getRegisters().values().stream().mapToLong(Long::longValue).max().orElse(0L);
						exec.getRegisters().merge("max", maxV, (o, n) -> Math.max(o, n));
					}
				}
				));
		System.out.println(c.execute(Input.parseLines("y2017/day8/day8.txt").collect(Collectors.toList())).values().stream().mapToLong(Long::longValue).max());
	}

}
