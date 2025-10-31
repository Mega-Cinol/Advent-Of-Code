package y2016.day21;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import common.Computer;
import common.Input;

public class Day21 {

	public static void main(String[] argz) {
		Computer<String> computer = new Computer<>();
		computer.setRegisterDefaultValue("abcdefgh");
		computer.addRegister("pass");
		computer.registerCommand("swap position (\\d+) with position (\\d+)", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> swap(v, Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1))));
			exec.stepIp();
		});
		computer.registerCommand("swap letter ([a-h]) with letter ([a-h])", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> swap(v, args.get(0).charAt(0), args.get(1).charAt(0)));
			exec.stepIp();
		});
		computer.registerCommand("rotate left (\\d+) steps?", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> rotateLeft(v, Integer.parseInt(args.get(0))));
			exec.stepIp();
		});
		computer.registerCommand("rotate right (\\d+) steps?", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> rotateRight(v, Integer.parseInt(args.get(0))));
			exec.stepIp();
		});
		computer.registerCommand("rotate based on position of letter ([a-h])", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> rotatePosition(v, args.get(0).charAt(0)));
			exec.stepIp();
		});
		computer.registerCommand("reverse positions (\\d+) through (\\d+)", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> reverse(v, Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1))));
			exec.stepIp();
		});
		computer.registerCommand("move position (\\d+) to position (\\d+)", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> move(v, Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1))));
			exec.stepIp();
		});
		List<String> program = Input.parseLines("y2016/day21/day21.txt").collect(Collectors.toList());
		System.out.println(computer.execute(program));
		Computer<String> reversedComputer = new Computer<>();
		reversedComputer.setRegisterDefaultValue("fbgdceah");
		reversedComputer.addRegister("pass");
		reversedComputer.registerCommand("swap position (\\d+) with position (\\d+)", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> swap(v, Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1))));
			exec.stepIp();
		});
		reversedComputer.registerCommand("swap letter ([a-h]) with letter ([a-h])", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> swap(v, args.get(0).charAt(0), args.get(1).charAt(0)));
			exec.stepIp();
		});
		reversedComputer.registerCommand("rotate left (\\d+) steps?", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> rotateRight(v, Integer.parseInt(args.get(0))));
			exec.stepIp();
		});
		reversedComputer.registerCommand("rotate right (\\d+) steps?", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> rotateLeft(v, Integer.parseInt(args.get(0))));
			exec.stepIp();
		});
		reversedComputer.registerCommand("rotate based on position of letter ([a-h])", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> unrotatePosition(v, args.get(0).charAt(0)));
			exec.stepIp();
		});
		reversedComputer.registerCommand("reverse positions (\\d+) through (\\d+)", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> reverse(v, Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1))));
			exec.stepIp();
		});
		reversedComputer.registerCommand("move position (\\d+) to position (\\d+)", (exec, args) -> {
			exec.getRegisters().computeIfPresent("pass", (k, v) -> move(v, Integer.parseInt(args.get(1)), Integer.parseInt(args.get(0))));
			exec.stepIp();
		});
		List<String> reversedProgram = new ArrayList<>();
		for (int i = program.size() - 1 ; i >= 0 ; i--)
		{
			reversedProgram.add(program.get(i));
		}
		System.out.println(reversedComputer.execute(reversedProgram));
	}
/* ebcda
 * edcba
 * abcde
 * bcdea
 * bdeac
 * abdec
 * ecabd
 * decab
 */
	private static String swap(String in, int pos1, int pos2)
	{
		return in.substring(0, Math.min(pos1, pos2)) + in.charAt(Math.max(pos1, pos2))
				+ in.substring(Math.min(pos1, pos2) + 1, Math.max(pos1, pos2)) + in.charAt(Math.min(pos1, pos2))
				+ in.substring(Math.max(pos1, pos2) + 1);
	}

	private static String swap(String in, char c1, char c2)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < in.length() ; i++)
		{
			if (in.charAt(i) == c1)
			{
				sb.append(c2);
			} else
			if (in.charAt(i) == c2)
			{
				sb.append(c1);
			} else {
				sb.append(in.charAt(i));
			}
			
		}
		return sb.toString();
	}

	private static String rotateLeft(String in, int steps)
	{
		steps %= in.length();
		return in.substring(steps) + in.substring(0, steps);
	}
	private static String rotateRight(String in, int steps)
	{
		steps %= in.length();
		return in.substring(in.length() - steps) + in.substring(0, in.length() - steps);
	}
	private static String rotatePosition(String in, char ref)
	{
		return rotateRight(in, 1 + in.indexOf(ref) + (in.indexOf(ref) >= 4 ? 1 : 0));
	}
	private static String unrotatePosition(String in, char ref)
	{
//		int[] locAfterR = { 1, 3, 5, 7, 2, 4, 6, 0};
//		int[] returnTo = { 7, 0, 4, 1, 5, 2, 6, 3};
		int[] steps = { 1, 1, 6, 2, 7, 3, 0, 4};
		return rotateLeft(in, steps[in.indexOf(ref)]);
	}
	private static String reverse(String in, int pos1, int pos2)
	{
		StringBuilder sb = new StringBuilder(in.substring(0, Math.min(pos1, pos2)));
		for (int i = Math.max(pos1, pos2) ; i >= Math.min(pos1, pos2) ; i--)
		{
			sb.append(in.charAt(i));
		}
		sb.append(in.substring(Math.max(pos1, pos2) + 1));
		return sb.toString();
	}
	private static String move(String in, int from, int to)
	{
		StringBuilder sb = new StringBuilder(in);
		if (from < to)
		{
			sb.insert(to + 1, in.charAt(from));
			sb.deleteCharAt(from);
		} else {
			sb.deleteCharAt(from);
			sb.insert(to, in.charAt(from));
		}
		return sb.toString();
	}
}
