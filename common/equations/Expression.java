package common.equations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Expression(List<Nomynal> divided, List<Nomynal> divisor) {
	public static Expression parse(String description) {
		var splitDescription = description.replaceAll("\\s*", "").split("/");
		var divided = Arrays.stream(splitDescription[0].split("\\+"))
				.filter(s -> s != null && !s.isBlank())
				.map(Nomynal::parse)
				.toList();
		var divisors = splitDescription.length > 1 ? Arrays.stream(splitDescription[0].replaceAll("-", "+-").split("\\+"))
				.map(Nomynal::parse)
				.toList() : null;
		return new Expression(divided, divisors);
	}
	public Expression minus(Expression other) {
		if (divisor != null || other.divisor != null) {
			throw new UnsupportedOperationException();
		}
		var newNomynals = new ArrayList<>(divided);
		other.divided.stream().map(nomynal -> nomynal.multiply(-1)).forEach(newNomynals::add);
		return new Expression(normalizeNomynals(newNomynals), null);
	}

	public Expression plus(Expression other) {
		if (divisor != null || other.divisor != null) {
			throw new UnsupportedOperationException();
		}
		var newNomynals = new ArrayList<>(divided);
		newNomynals.addAll(other.divided);
		return new Expression(normalizeNomynals(newNomynals), null);
	}

	public Expression multiply(Expression other) {
		var newDivided = multiply(divided, other.divided);
		var newDivisor = divisor == null ? other.divisor : other.divisor == null ? divisor : multiply(divisor, other.divisor);
		return new Expression(newDivided, newDivisor);
	}

	private List<Nomynal> multiply(List<Nomynal> a, List<Nomynal> b) {
		var newNomynals = new ArrayList<Nomynal>();
		for (var aNomynal : a) {
			for (var bNomynal : b) {
				newNomynals.add(aNomynal.multiply(bNomynal));
			}
		}
		return normalizeNomynals(newNomynals);
	}

	public Expression square() {
		var dividedExpression = new Expression(divided, null);
		dividedExpression = dividedExpression.multiply(dividedExpression);

		var divisorExpression = new Expression(divisor, null);
		divisorExpression = divisorExpression.multiply(divisorExpression);
		return new Expression(dividedExpression.divided(), divisorExpression.divided());
	}

	public Expression setVarValue(String varName, long value) {
		var newDivided = normalizeNomynals(new ArrayList<Nomynal>(
				divided.stream().map(n -> n.setVarValue(varName, value)).toList()));
		var newDivisor = divisor == null ? null
				: normalizeNomynals(new ArrayList<Nomynal>(
						divisor.stream().map(n -> n.setVarValue(varName, value)).toList()));
		return new Expression(newDivided, newDivisor);
	}

	public Expression setVarValue(String varName, Expression value) {
		if (divisor != null && !divisor.isEmpty() && ((divisor.size() > 1) || !divisor.get(0).hasNumericValue())) {
			throw new UnsupportedOperationException();
		}
		var withVariable = new ArrayList<Nomynal>();
		var withoutVariable = new ArrayList<Nomynal>();
		for (var nomynal : divided) {
			if (nomynal.hasVariable(varName)) {
				var variable = nomynal.getVariable(varName);
				if (variable.exp() != 1) {
					throw new UnsupportedOperationException();
				}
				withVariable.add(nomynal.setVarValue(varName, 1));
			} else {
				withoutVariable.add(nomynal);
			}
		}
		var withVariableExpression = new Expression(withVariable, null);
		var withoutVariableExpression = new Expression(withoutVariable, null);
		var newValueDividedExpression = new Expression(value.divided, null);
		var newValueDivisorExpression = new Expression(value.divisor, null);
		var newDivided = withVariableExpression.multiply(newValueDividedExpression)
				.plus(withoutVariableExpression.multiply(newValueDivisorExpression));
		var newDivisor = divisor == null || divisor.isEmpty() ? value.divisor() : value.divisor().stream()
				.map(nomynal -> nomynal.multiply(divisor.get(0).getNumericValue()))
				.toList();
		return new Expression(newDivided.divided(), newDivisor);
	}

	private List<Nomynal> normalizeNomynals(List<Nomynal> nomynals) {
		var groupedNomynals = nomynals.stream().collect(
				Collectors.groupingBy(Nomynal::variables, Collectors.summarizingLong(Nomynal::multiplier)));
		return groupedNomynals.entrySet().stream().filter(e -> e.getValue().getSum() != 0)
				.map(e -> new Nomynal(e.getValue().getSum(), e.getKey())).toList();
	}

	public boolean hasNumericValue() {
		var allNomynals = divisor == null ? divided.stream() : Stream.concat(divided.stream(), divisor.stream());
		return divided.isEmpty() || allNomynals.allMatch(Nomynal::hasNumericValue);
	}

	public long getDividedNumericValue() {
		return divided.stream().mapToLong(Nomynal::getNumericValue).sum();
	}

	public long getDivisorNumericValue() {
		return divisor == null || divisor.isEmpty() ? 1 : divisor.stream().mapToLong(Nomynal::getNumericValue).sum();
	}

	public double getNumericValue() {
		var dividedVal = getDividedNumericValue();
		var divisorVal = getDivisorNumericValue();
		return (double)dividedVal/divisorVal;
	}

	@Override
	public String toString() {
		if (divided.isEmpty()) {
			return "0";
		}
		var topPart = divided.stream().map(Nomynal::toString).collect(Collectors.joining("+")).replace("+-", "-");
		if (divisor == null) {
			return topPart;
		}
		var bottomPart = divisor.stream().map(Nomynal::toString).collect(Collectors.joining("+")).replace("+-",
				"-");
		if (divided.size() > 1) {
			topPart = "(" + topPart + ")";
		}
		if (divisor.size() > 1) {
			bottomPart = "(" + bottomPart + ")";
		}
		return topPart + "/" + bottomPart;
	}
}
