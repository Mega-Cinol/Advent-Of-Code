package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Computer<T> {
	private final Map<String, T> initialRegisters = new HashMap<>();
	private final Map<Pattern, Command<T>> knownCommands = new HashMap<>();
	private Predicate<ComputerExecution> stopCondition = e -> false;
	private Set<Predicate<ComputerExecution>> traceConditions = new HashSet<>();
	private boolean skipInvalid = false;
	private boolean debug = false;
	private Function<String, T> defaultValue;
	private final ExecutorService executor = Executors.newCachedThreadPool();

	public void setRegisterDefaultValue(T value) {
		defaultValue = k -> value;
	}

	public void setSkipInvalid() {
		skipInvalid = true;
	}

	public void setFailInvalid() {
		skipInvalid = false;
	}

	public void setDebug() {
		debug = true;
	}

	public void setDontDebug() {
		debug = false;
	}

	public void setRegisterDefaultValue(Function<String, T> defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void addRegister(String name) {
		initialRegisters.put(name, defaultValue.apply(name));
	}

	public void addRegister(String name, T value) {
		initialRegisters.put(name, value);
	}

	public void registerCommand(String pattern, Command<T> action) {
		knownCommands.put(Pattern.compile(pattern), action);
	}

	public void registerNormalCommand(String pattern, Command<T> action) {
		knownCommands.put(Pattern.compile(pattern), NormalCommand.of(action));
	}

	public void setStopCondition(Predicate<ComputerExecution> stopCnd) {
		stopCondition = stopCnd;
	}

	public void addTraceCondition(Predicate<ComputerExecution> cnd)
	{
		traceConditions.add(cnd);
	}

	public Map<String, T> execute(List<String> program) {
		ComputerExecution copmuterExec = new ComputerExecution(program);
		copmuterExec.execute();
		try {
			return copmuterExec.getResult();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ComputerExecution executeAsync(List<String> program)
	{
		return new ComputerExecution(program);
	}

	public void shutdown()
	{
		executor.shutdown();
	}

	public class ComputerExecution {
		private int ip = 0;
		private final Map<String, T> registers = new HashMap<>();
		private final List<String> program = new ArrayList<>();

		private final Map<String, BlockingQueue<T>> inputs = new HashMap<>();
		private final Map<String, BlockingQueue<T>> outputs = new HashMap<>();
		private Future<Map<String, T>> ongoingExecution = null;

		private ComputerExecution(List<String> program) {
			registers.putAll(Computer.this.initialRegisters);
			this.program.addAll(program);
		}

		public void setIp(int newIp) {
			ip = newIp;
		}

		public void moveIp(int offset) {
			ip += offset;
		}

		public void stepIp() {
			moveIp(1);
		}

		public int getIp() {
			return ip;
		}

		public Map<String, T> getRegisters() {
			return registers;
		}

		public T readInput(String key)
		{
			try {
				return inputs.get(key).poll(1, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		public void writeOutput(String key, T value)
		{
			outputs.get(key).add(value);
		}

		public void registerInput(String key, BlockingQueue<T> queue)
		{
			inputs.put(key, queue);
		}

		public void registerOutput(String key, BlockingQueue<T> queue)
		{
			outputs.put(key, queue);
		}

		@SuppressWarnings("unchecked")
		public T getValue(String desc) {
			T tmpValue;
			try {
				tmpValue = (T) Long.valueOf(desc);
				return tmpValue;
			} catch (ClassCastException | NumberFormatException e) {
				// OK
			}
			return registers.computeIfAbsent(desc, defaultValue);
		}

		public List<String> getProgram() {
			return program;
		}

		public void execute() {
			ongoingExecution = executor.submit(() -> {
				while (getIp() < getProgram().size()) {
					if (stopCondition.test(this)) {
						break;
					}
					String commandStr = getProgram().get(getIp());
					if (!executeCommand(commandStr, this)) {
						if (!skipInvalid) {
							throw new IllegalArgumentException("No command matching " + commandStr);
						}
					}
				}
				return getRegisters();
			});
		}

		public Map<String, T> getResult() throws InterruptedException, ExecutionException
		{
			if (ongoingExecution == null)
			{
				return null;
			}
			return ongoingExecution.get();
		}

		private boolean executeCommand(String command, ComputerExecution compExec) {
			if (debug || traceConditions.stream().anyMatch(p -> p.test(compExec))) {
				System.out.println(command);
			}
			List<Map.Entry<Pattern, Command<T>>> matchingCommands = knownCommands.entrySet().stream()
					.filter(e -> e.getKey().matcher(command).matches()).collect(Collectors.toList());
			if (matchingCommands.isEmpty()) {
				return false;
			}
			if (matchingCommands.size() > 1) {
				throw new IllegalArgumentException("Multiple commands matching: " + command);
			}
			Matcher argMatcher = matchingCommands.get(0).getKey().matcher(command);
			argMatcher.matches();
			List<String> arguments = new ArrayList<>();
			for (int i = 1; i <= argMatcher.groupCount(); i++) {
				arguments.add(argMatcher.group(i));
			}
			matchingCommands.get(0).getValue().execute(compExec, arguments);
			if (debug || traceConditions.stream().anyMatch(p -> p.test(compExec))) {
				System.out.println(compExec.getRegisters());
			}
			return true;
		}
	}

	@FunctionalInterface
	public interface Command<T> {
		void execute(Computer<T>.ComputerExecution computer, List<String> args);
	}

	public static class NormalCommand<T> implements Command<T> {
		private final Command<T> internalCommand;

		public NormalCommand(Command<T> internalCommand) {
			this.internalCommand = internalCommand;
		}

		public static <T> Command<T> of(Command<T> cmd) {
			return new NormalCommand<T>(cmd);
		}

		@Override
		public void execute(Computer<T>.ComputerExecution computer, List<String> args) {
			internalCommand.execute(computer, args);
			computer.stepIp();
		}
	}

	public static class JumpCommand<T> implements Command<T> {
		private enum JumpType {
			RELATIVE(Computer<?>.ComputerExecution::moveIp), ABSOLUTE(Computer<?>.ComputerExecution::setIp);

			private final BiConsumer<Computer<?>.ComputerExecution, Integer> jumpCommand;

			JumpType(BiConsumer<Computer<?>.ComputerExecution, Integer> jumpCmd) {
				jumpCommand = jumpCmd;
			}

			public void execute(Computer<?>.ComputerExecution computer, int value) {
				jumpCommand.accept(computer, value);
			}
		}

		private final BiPredicate<Computer<T>.ComputerExecution, List<String>> jumpCondition;
		private final BiFunction<Computer<T>.ComputerExecution, List<String>, Integer> jumpValue;
		private final JumpType jumpType;

		private JumpCommand(BiPredicate<Computer<T>.ComputerExecution, List<String>> jumpCondition,
				BiFunction<Computer<T>.ComputerExecution, List<String>, Integer> jumpValue, JumpType jumpType) {
			this.jumpCondition = jumpCondition;
			this.jumpValue = jumpValue;
			this.jumpType = jumpType;
		}

		public static <T> JumpCommand<T> relative(
				BiPredicate<Computer<T>.ComputerExecution, List<String>> jumpCondition,
				BiFunction<Computer<T>.ComputerExecution, List<String>, Integer> jumpValue) {
			return new JumpCommand<T>(jumpCondition, jumpValue, JumpType.RELATIVE);
		}

		public static <T> JumpCommand<T> absolute(
				BiPredicate<Computer<T>.ComputerExecution, List<String>> jumpCondition,
				BiFunction<Computer<T>.ComputerExecution, List<String>, Integer> jumpValue) {
			return new JumpCommand<T>(jumpCondition, jumpValue, JumpType.ABSOLUTE);
		}

		@Override
		public void execute(Computer<T>.ComputerExecution computer, List<String> args) {
			if (jumpCondition.test(computer, args)) {
				jumpType.execute(computer, jumpValue.apply(computer, args));
			} else {
				computer.stepIp();
			}
		}
	}
}
