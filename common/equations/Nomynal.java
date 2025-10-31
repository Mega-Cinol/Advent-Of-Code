package common.equations;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record Nomynal(long multiplier, List<Variable> variables) {
	public static Nomynal parse(String description) {
		var numberPattern = Pattern.compile("-?\\d+");
		var multiplier = 1L;
		var variables = new ArrayList<Variable>();
		for (String variable : description.replaceAll("\\s*", "").split("\\*")) {
			if (numberPattern.matcher(variable).matches()) {
				multiplier *= Long.valueOf(variable);
			} else {
				variables.add(new Variable(variable));
			}
		}
		return new Nomynal(multiplier, List.copyOf(variables));
	}

	public Nomynal multiply(long value) {
		return new Nomynal(value * multiplier, variables);
	}

	public Nomynal multiply(Nomynal value) {
		var newVariables = value.variables.stream().collect(Collectors.groupingBy(Variable::name, Collectors
				.collectingAndThen(Collectors.summarizingLong(Variable::exp), LongSummaryStatistics::getSum)));
		variables.forEach(var -> newVariables.merge(var.name(), var.exp(), (a, b) -> a + b));
		return new Nomynal(value.multiplier * multiplier, newVariables.entrySet().stream()
				.map(e -> new Variable(e.getKey(), e.getValue())).toList());
	}

	public boolean hasVariable(String name) {
		return variables.stream().map(Variable::name).anyMatch(v -> v.equals(name));
	}

	public Variable getVariable(String name) {
		return variables.stream().filter(v -> v.name().equals(name)).findAny().orElse(null);
	}

	public Nomynal setVarValue(String varName, long value) {
		if (variables.stream().map(Variable::name).noneMatch(name -> name.equals(varName))) {
			return this;
		}
		var newMultiplier = multiplier;
		var newVariables = new ArrayList<Variable>();
		for (var variable : variables) {
			if (variable.name().equals(varName)) {
				newMultiplier *= Math.pow(value, variable.exp());
			} else {
				newVariables.add(variable);
			}
		}
		return new Nomynal(newMultiplier, newVariables);
	}

	public boolean hasNumericValue() {
		return variables == null || variables.isEmpty();
	}

	public long getNumericValue() {
		if (variables != null && !variables.isEmpty()) {
			throw new IllegalStateException("Unresolved varibales: " + variables);
		}
		return multiplier;
	}

	@Override
	public String toString() {
		if (variables.isEmpty()) {
			return "" + multiplier;
		}
		var varString = variables.stream().map(Variable::toString).collect(Collectors.joining("*"));
		return switch ((int) multiplier) {
		case 1 -> varString;
		case -1 -> "-" + varString;
		default -> multiplier + "*" + varString;
		};
	}
}