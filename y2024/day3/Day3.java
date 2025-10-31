package y2024.day3;

import common.AdventSolution;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day3 extends AdventSolution {
    private static final Pattern MUL_PATTERN = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
    @Override
    public Object part1Solution() {
        var input = getInput().collect(Collectors.joining());
        var matcher = MUL_PATTERN.matcher(input);
        var mulResult = 0L;
        while (matcher.find()) {
            mulResult += Long.parseLong(matcher.group(1)) * Long.parseLong(matcher.group(2));
        }
        return mulResult;
    }

    @Override
    public Object part2Solution() {
        var input = getInput().collect(Collectors.joining());

        var dontPosition = input.indexOf("don't()");
        while (dontPosition > -1) {
            int nextDoPosition = input.indexOf("do()", dontPosition);
            var left = input.substring(0, dontPosition);
            var right = nextDoPosition != -1 ? input.substring(nextDoPosition) : "";
            input = left + right;
            dontPosition = input.indexOf("don't()");
        }
        var matcher = MUL_PATTERN.matcher(input);
        var mulResult = 0L;
        while (matcher.find()) {
            mulResult += Long.parseLong(matcher.group(1)) * Long.parseLong(matcher.group(2));
        }
        return mulResult;
    }

    public static void main(String[] args) {
        new Day3().solve();
    }
}
