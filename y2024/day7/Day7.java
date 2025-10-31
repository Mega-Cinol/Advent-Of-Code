package y2024.day7;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public class Day7 extends AdventSolution {
    @Override
    public Object part1Solution() {
        return getInput()
                .filter(line -> isValid(line, this::isValid))
                .mapToLong(this::getResult)
                .sum();
    }

    private boolean isValid(String equation, BiPredicate<Long, List<Long>> validator) {
        var equationParts = equation.split(":");
        var result = Long.parseLong(equationParts[0]);

        var equationInputPattern = Pattern.compile("\\d+");
        var equationInputMatcher = equationInputPattern.matcher(equationParts[1]);
        var equationInput = new ArrayList<Long>();
        while (equationInputMatcher.find()) {
            equationInput.add(Long.parseLong(equationInputMatcher.group()));
        }
        return validator.test(result, equationInput);
    }

    private boolean isValid(long result, List<Long> equationInput) {
        if (equationInput.isEmpty()) {
            return result == 0;
        }
        if (equationInput.size() == 1) {
            return result == equationInput.get(0);
        }
        var subList = equationInput.subList(0, equationInput.size() - 1);
        var last = equationInput.getLast();
        if (result % last == 0) {
            return isValid(result / last, subList) || isValid(result - last, subList);
        } else {
            return isValid(result - last, subList);
        }
    }

    private long getResult(String equation) {
        return Long.parseLong(equation.split(":")[0]);
    }

    @Override
    public Object part2Solution() {
        return getInput()
                .filter(line -> isValid(line, this::isValid2))
                .mapToLong(this::getResult)
                .sum();
    }

    private boolean isValid2(long result, List<Long> equationInput) {
        if (equationInput.isEmpty()) {
            return result == 0;
        }
        if (equationInput.size() == 1) {
            return result == equationInput.get(0);
        }
        var subList = equationInput.subList(0, equationInput.size() - 1);
        var last = equationInput.getLast();
        boolean couldMultiply = result % last == 0;
        boolean couldConcatenate = couldConcatenate(result, last);

        var isValid = isValid2(result - last, subList);
        if (isValid) {
            return true;
        }
        if (couldMultiply) {
            isValid = isValid2(result / last, subList);
            if (isValid) {
                return true;
            }
        }
        if (couldConcatenate) {
            var lastDecimals = getDecimals(last);
            isValid = isValid2(result / lastDecimals, subList);
            if (isValid) {
                return true;
            }
        }
        return false;
    }
    private boolean couldConcatenate(long result, long input) {
        var decimals = getDecimals(input);
        return result % decimals == input;
    }
    public long getDecimals(long number) {
        var decimals = 10;
        while (decimals <= number) {
            decimals *= 10;
        }
        return decimals;
    }
    public static void main(String[] args) {
        new Day7().solve();
    }
}
