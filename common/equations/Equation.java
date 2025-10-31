package common.equations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Equation(List<Expression> left, List<Expression> right) {
	public static Equation parse(String description) {
		var splitDescription = description.replaceAll("\\s*", "").split("=");
		var left = Expression.parse(splitDescription[0]);
		var right = Expression.parse(splitDescription[1]);
		return new Equation(List.of(left), List.of(right));
	}
	public Equation normalize() {
		var toMultiply = Stream.concat(left.stream(), right.stream()).filter(e -> e.divisor() != null)
				.map(Expression::divisor).toList();
		var newLeft = new ArrayList<Expression>();
		for (var currentLeft : left) {
			var newElement = new Expression(currentLeft.divided(), null);
			for (var elementToMultiply : toMultiply) {
				if (elementToMultiply.equals(currentLeft.divisor())) {
					continue;
				}
				newElement = newElement.multiply(new Expression(elementToMultiply, null));
			}
			newLeft.add(newElement);
		}
		var newRight = new ArrayList<Expression>();
		for (var currentRight : right) {
			var newElement = new Expression(currentRight.divided(), null);
			for (var elementToMultiply : toMultiply) {
				if (elementToMultiply.equals(currentRight.divisor())) {
					continue;
				}
				newElement = newElement.multiply(new Expression(elementToMultiply, null));
			}
			newRight.add(newElement);
		}
		var finalLeftExpression = newLeft.stream().reduce(Expression::plus).get()
				.minus(newRight.stream().reduce(Expression::plus).get());
		return new Equation(List.of(finalLeftExpression),
				List.of(new Expression(List.of(new Nomynal(0, List.of())), null)));
	}

	public Equation setVarValue(String varName, long value) {
		var newLeft = left.stream().map(e -> e.setVarValue(varName, value)).toList();
		var newRight = right.stream().map(e -> e.setVarValue(varName, value)).toList();
		return new Equation(newLeft, newRight);
	}

	public Equation setVarValue(String varName, Expression value) {
		var normalized = normalize();
		var newLeft = normalized.left.stream().map(e -> e.setVarValue(varName, value)).toList();
		var newRight = normalized.right.stream().map(e -> e.setVarValue(varName, value)).toList();
		return new Equation(newLeft, newRight).normalize();
	}

	public Expression extractLinearVariableValue(String variableName) {
		var normalizedEquation = this.normalize();
		if (normalizedEquation.left().size() > 1) {
			throw new RuntimeException("Something wrong with normalization: " + normalizedEquation);
		}
		var leftExpression = normalizedEquation.left().stream().findAny().get();
		if (leftExpression.divisor() != null) {
			throw new RuntimeException("Something wrong with normalization: " + normalizedEquation);
		}
		var leftNomynals = leftExpression.divided();
		var newDivided = new ArrayList<Nomynal>();
		var newDivisor = new ArrayList<Nomynal>();
		for (var nomynal : leftNomynals) {
			if (nomynal.hasVariable(variableName)) {
				var variable = nomynal.getVariable(variableName);
				if (variable.exp() != 1) {
					throw new UnsupportedOperationException("Only linear equations supported");
				}
				newDivisor.add(nomynal.setVarValue(variableName, 1));
			} else {
				newDivided.add(nomynal.multiply(-1));
			}
		}
		return new Expression(newDivided, newDivisor);
	}

	public Map<String, Expression> extractSqaureEquation(String variableName) {
		var normalizedEquation = this.normalize();
		if (normalizedEquation.left().size() > 1) {
			throw new RuntimeException("Something wrong with normalization: " + normalizedEquation);
		}
		var leftExpression = normalizedEquation.left().stream().findAny().get();
		if (leftExpression.divisor() != null) {
			throw new RuntimeException("Something wrong with normalization: " + normalizedEquation);
		}
		var leftNomynals = leftExpression.divided();
		var aNomynals = new ArrayList<Nomynal>();
		var bNomynals = new ArrayList<Nomynal>();
		var cNomynals = new ArrayList<Nomynal>();
		for (var nomynal : leftNomynals) {
			if (nomynal.hasVariable(variableName)) {
				var variable = nomynal.getVariable(variableName);
				if (variable.exp() == 2) {
					aNomynals.add(nomynal.setVarValue(variableName, 1));
				} else if (variable.exp() == 1) {
					bNomynals.add(nomynal.setVarValue(variableName, 1));
				} else {
					throw new UnsupportedOperationException("Only square equations supported");
				}
			} else {
				cNomynals.add(nomynal);
			}
		}
		return Map.of("a", new Expression(aNomynals, null), "b", new Expression(bNomynals, null), "c",
				new Expression(cNomynals, null));
	}

	public boolean hasInfiniteOrNoSolutions() {
		var normalized = normalize();
		return Stream.concat(normalized.left.stream(), normalized.right.stream()).allMatch(Expression::hasNumericValue);
	}

	public static Equation buildSquareEquation(Map<String, Expression> squareConstants, Expression squareVariable) {
		var variableSquared = squareVariable.square().multiply(squareConstants.get("a"));
		var variableLined = squareVariable.multiply(squareConstants.get("b"));
		return new Equation(List.of(variableSquared, variableLined, squareConstants.get("c")), List.of(new Expression(List.of(new Nomynal(0, List.of())), null))).normalize();
	}

	@Override
	public String toString() {
		var leftStr = left.isEmpty() ? "0" : left.stream().map(Expression::toString).collect(Collectors.joining("+"));
		var rightStr = right.isEmpty() ? "0" : right.stream().map(Expression::toString).collect(Collectors.joining("+"));
		return leftStr + " = " + rightStr;
	}
}