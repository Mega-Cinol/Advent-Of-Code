package y2020.day8;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day8 {

	private static final Pattern CMD_PATTERN = Pattern.compile("(?<cmd>[a-z]{3}) (?<arg>[\\+-][0-9]+)");

	public static void main(String[] args) {
		Computer computer = new Computer();
		Input.parseLines("y2020/day8/day8.txt", Function.identity(), computer::addCommand);
		while (!computer.execute())
		{
			computer.mutate();
		}
		System.out.println(computer.getAcc());
	}

	private static class Computer {
		private final List<Command> commands = new ArrayList<>();
		private int accumulator;
		private int instructionPointer;
		private int currentMutated = -1;

		public void jump(int relative) {
			instructionPointer += relative;
		}

		public void updateAcc(int diff) {
			accumulator += diff;
		}

		public int getAcc()
		{
			return accumulator;
		}

		public boolean execute() {
			instructionPointer = 0;
			accumulator = 0;
			commands.stream().forEach(Command::reset);
			while (instructionPointer < commands.size()) {
				Command current = commands.get(instructionPointer++);
				if (current.isExecuted()) {
					return false;
				}
				current.execute();
			}
			return true;
		}

		public void addCommand(String command) {
			Matcher cmdMatcher = CMD_PATTERN.matcher(command);
			if (!cmdMatcher.find()) {
				throw new IllegalArgumentException(command + " is not a valid command");
			}
			Command newCmd;
			int argument = Integer.parseInt(cmdMatcher.group("arg"));
			switch (cmdMatcher.group("cmd")) {
			case "nop":
				newCmd = new NopCommand(this, argument);
				break;
			case "acc":
				newCmd = new AccCommand(this, argument);
				break;
			case "jmp":
				newCmd = new JmpCommand(this, argument);
				break;
			default:
				throw new IllegalArgumentException(command + " is not a valid command");
			}
			commands.add(newCmd);
		}

		public void mutate()
		{
			if (currentMutated >= 0)
			{
				commands.set(currentMutated, commands.get(currentMutated).invert());
			}
			currentMutated++;
			commands.set(currentMutated, commands.get(currentMutated).invert());
		}
	}

	private static abstract class Command {
		protected final Computer computer;
		private boolean executed;

		public Command(Computer computer) {
			this.computer = computer;
		}

		public final void execute() {
			executed = true;
			runCommand();
		}

		public boolean isExecuted() {
			return executed;
		}
		public void reset()
		{
			executed = false;
		}

		public abstract Command invert();

		protected abstract void runCommand();
	}

	private static class NopCommand extends Command {
		private final int arg;
		public NopCommand(Computer computer, int arg) {
			super(computer);
			this.arg = arg;
		}

		@Override
		protected void runCommand() {
		}

		@Override
		public Command invert() {
			return new JmpCommand(computer, arg);
		}
		@Override
		public String toString()
		{
			return "NopCommand(" + arg + ")";
		}
	}

	private static class AccCommand extends Command {
		private final int accChange;

		public AccCommand(Computer computer, int accChange) {
			super(computer);
			this.accChange = accChange;
		}

		@Override
		public void runCommand() {
			computer.updateAcc(accChange);
		}

		@Override
		public Command invert() {
			return this;
		}
		@Override
		public String toString()
		{
			return "AccCommand(" + accChange + ")";
		}

	}

	private static class JmpCommand extends Command {
		private final int jump;

		public JmpCommand(Computer computer, int jump) {
			super(computer);
			this.jump = jump;
		}

		@Override
		public void runCommand() {
			computer.jump(jump - 1);
		}

		@Override
		public Command invert() {
			return new NopCommand(computer, jump);
		}
		@Override
		public String toString()
		{
			return "JmpCommand(" + jump + ")";
		}

	}
}
