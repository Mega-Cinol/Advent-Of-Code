package common.equations;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record Nominal(Fraction multiplier, List<Variable> variables) {
	public static Nominal parse(String description) {
		var numberPattern = Pattern.compile("-?\\d+(/\\d+)?");
		var multiplier = new Fraction(1,1);
		var variables = new ArrayList<Variable>();
		for (String variable : description.replaceAll("\\s*", "").split("\\*")) {
			if (numberPattern.matcher(variable).matches()) {
				multiplier = multiplier.multiply(Fraction.parse(variable));
			} else {
				variables.add(new Variable(variable));
			}
		}
		return new Nominal(multiplier, List.copyOf(variables));
	}

	public Nominal multiply(long value) {
		return new Nominal(multiplier.multiply(value), variables);
	}

	public Nominal multiply(Fraction value) {
		return new Nominal(multiplier.multiply(value), variables);
	}

	public Nominal multiply(Nominal value) {
		var newVariables = value.variables.stream().collect(Collectors.groupingBy(Variable::name, Collectors
				.collectingAndThen(Collectors.summarizingLong(Variable::exp), LongSummaryStatistics::getSum)));
		variables.forEach(var -> newVariables.merge(var.name(), var.exp(), Long::sum));
		return new Nominal(value.multiplier.multiply(multiplier), newVariables.entrySet().stream()
				.map(e -> new Variable(e.getKey(), e.getValue())).toList());
	}

	public Nominal add(Nominal other) {
		if (!variables.equals(other.variables)) {
			throw new IllegalArgumentException("The given nominal's variables %s and %s do not match".formatted(variables, other.variables));
		}
		return new Nominal(multiplier.add(other.multiplier), variables);
	}

	public boolean hasVariable(String name) {
		return variables.stream().map(Variable::name).anyMatch(v -> v.equals(name));
	}

	public Variable getVariable(String name) {
		return variables.stream().filter(v -> v.name().equals(name)).findAny().orElse(null);
	}

	public Nominal setVarValue(String varName, long value) {
		if (variables.stream().map(Variable::name).noneMatch(name -> name.equals(varName))) {
			return this;
		}
		var newMultiplier = multiplier;
		var newVariables = new ArrayList<Variable>();
		for (var variable : variables) {
			if (variable.name().equals(varName)) {
				newMultiplier.multiply((long) Math.pow(value, variable.exp()));
			} else {
				newVariables.add(variable);
			}
		}
		return new Nominal(newMultiplier, newVariables);
	}

	public Nominal setVarValue(String varName, Fraction value) {
		if (variables.stream().map(Variable::name).noneMatch(name -> name.equals(varName))) {
			return this;
		}
		var newMultiplier = multiplier;
		var newVariables = new ArrayList<Variable>();
		for (var variable : variables) {
			if (variable.name().equals(varName)) {
				newMultiplier.multiply(value.power(variable.exp()));
			} else {
				newVariables.add(variable);
			}
		}
		return new Nominal(newMultiplier, newVariables);
	}

	public boolean hasNumericValue() {
		return variables == null || variables.isEmpty();
	}

	public Fraction getNumericValue() {
		if (variables != null && !variables.isEmpty()) {
			throw new IllegalStateException("Unresolved variables: " + variables);
		}
		return multiplier;
	}

	@Override
	public String toString() {
		if (variables.isEmpty()) {
			return "" + multiplier;
		}
		var varString = variables.stream().map(Variable::toString).collect(Collectors.joining("*"));
		return switch ((int) multiplier.toDouble()) {
		case 1 -> varString;
		case -1 -> "-" + varString;
		default -> multiplier + "*" + varString;
		};
	}
}