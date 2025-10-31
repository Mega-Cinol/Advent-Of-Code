package common.equations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Expression(List<Nominal> divided, List<Nominal> divisor) {
	public static Expression parse(String description) {
		var splitDescription = description.replaceAll("\\s*", "").split("/");
		var divided = Arrays.stream(splitDescription[0].split("\\+"))
				.filter(s -> !s.isBlank())
				.map(Nominal::parse)
				.toList();
		var divisors = splitDescription.length > 1 ? Arrays.stream(splitDescription[1].replaceAll("-", "+-").split("\\+"))
				.map(Nominal::parse)
				.toList() : null;
		return new Expression(divided, divisors);
	}
	public Expression minus(Expression other) {
		if (divisor != null || other.divisor != null) {
			if (divisor == null || !divisor.equals(other.divisor)) {
				throw new UnsupportedOperationException();
			}
		}
		var newNominals = new ArrayList<>(divided);
		other.divided.stream().map(nominal -> nominal.multiply(-1)).forEach(newNominals::add);
		return new Expression(normalizeNominals(newNominals), divisor);
	}

	public Expression plus(Expression other) {
		if (divisor != null || other.divisor != null) {
			if (divisor == null || !divisor.equals(other.divisor)) {
				throw new UnsupportedOperationException();
			}
		}
		var newNominals = new ArrayList<>(divided);
        newNominals.addAll(other.divided);
		return new Expression(normalizeNominals(newNominals), divisor);
	}

	public Expression multiply(Expression other) {
		var newDivided = multiply(divided, other.divided);
		var newDivisor = divisor == null ? other.divisor : other.divisor == null ? divisor : multiply(divisor, other.divisor);
		return new Expression(newDivided, newDivisor);
	}

	public Expression divide(Expression other) {
		var newDivided = other.divisor == null ? divided : multiply(divided, other.divisor);
		var newDivisor = divisor == null ? other.divided : multiply(divisor, other.divided);
		return new Expression(newDivided, newDivisor);
	}

	private List<Nominal> multiply(List<Nominal> a, List<Nominal> b) {
		var newNominals = new ArrayList<Nominal>();
		for (var aNominal : a) {
			for (var bNominal : b) {
				newNominals.add(aNominal.multiply(bNominal));
			}
		}
		return normalizeNominals(newNominals);
	}

	public Expression square() {
		var dividedExpression = new Expression(divided, null);
		dividedExpression = dividedExpression.multiply(dividedExpression);

		var divisorExpression = new Expression(divisor, null);
		divisorExpression = divisorExpression.multiply(divisorExpression);
		return new Expression(dividedExpression.divided(), divisorExpression.divided());
	}

	public Expression setVarValue(String varName, long value) {
		var newDivided = normalizeNominals(new ArrayList<Nominal>(
				divided.stream().map(n -> n.setVarValue(varName, value)).toList()));
		var newDivisor = divisor == null ? null
				: normalizeNominals(new ArrayList<Nominal>(
						divisor.stream().map(n -> n.setVarValue(varName, value)).toList()));
		return new Expression(newDivided, newDivisor);
	}

	public Expression setVarValue(String varName, Expression value) {
		if (divisor != null && !divisor.isEmpty() && ((divisor.size() > 1) || !divisor.get(0).hasNumericValue())) {
			throw new UnsupportedOperationException();
		}
		var withVariable = new ArrayList<Nominal>();
		var withoutVariable = new ArrayList<Nominal>();
		for (var nominal : divided) {
			if (nominal.hasVariable(varName)) {
				var variable = nominal.getVariable(varName);
				if (variable.exp() != 1) {
					throw new UnsupportedOperationException();
				}
				withVariable.add(nominal.setVarValue(varName, 1));
			} else {
				withoutVariable.add(nominal);
			}
		}
		var withVariableExpression = new Expression(withVariable, null);
		var withoutVariableExpression = new Expression(withoutVariable, null);
		var newValueDividedExpression = new Expression(value.divided, null);
		var newValueDivisorExpression = new Expression(value.divisor, null);
		var newDivided = withVariableExpression.multiply(newValueDividedExpression)
				.plus(withoutVariableExpression.multiply(newValueDivisorExpression));
		var newDivisor = divisor == null || divisor.isEmpty() ? value.divisor() : value.divisor().stream()
				.map(nominal -> nominal.multiply(divisor.get(0).getNumericValue()))
				.toList();
		return new Expression(newDivided.divided(), newDivisor);
	}

	private List<Nominal> normalizeNominals(List<Nominal> nominals) {
		var groupedNominals = nominals.stream().collect(
                Collectors.toMap(Nominal::variables, Function.identity(), Nominal::add));
		return groupedNominals.entrySet().stream().filter(e -> e.getValue().multiplier().top() != 0)
				.map(e -> new Nominal(e.getValue().multiplier(), e.getKey())).toList();
	}

	public boolean hasNumericValue() {
		var allNominals = divisor == null ? divided.stream() : Stream.concat(divided.stream(), divisor.stream());
		return divided.isEmpty() || allNominals.allMatch(Nominal::hasNumericValue);
	}

	public Fraction getDividedNumericValue() {
		return divided.stream().map(Nominal::getNumericValue).reduce(Fraction::add).orElse(Fraction.parse("0"));
	}

	public Fraction getDivisorNumericValue() {
		return divisor == null || divisor.isEmpty() ? Fraction.parse("1") : divisor.stream().map(Nominal::getNumericValue).reduce(Fraction::add).get();
	}

	public Fraction getNumericValueFraction() {
		var dividedVal = getDividedNumericValue();
		var divisorVal = getDivisorNumericValue();
		return dividedVal.divide(divisorVal);
	}

	public double getNumericValue() {
		var dividedVal = getDividedNumericValue();
		var divisorVal = getDivisorNumericValue();
		return dividedVal.divide(divisorVal).toDouble();
	}

	@Override
	public String toString() {
		if (divided.isEmpty()) {
			return "0";
		}
		var topPart = divided.stream().map(Nominal::toString).collect(Collectors.joining("+")).replace("+-", "-");
		if (divisor == null) {
			return topPart;
		}
		var bottomPart = divisor.stream().map(Nominal::toString).collect(Collectors.joining("+")).replace("+-",
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
