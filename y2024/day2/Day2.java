package y2024.day2;

import common.AdventSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day2 extends AdventSolution {
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");

    @Override
    public Object part1Solution() {
        return getInput()
                .map(this::mapToReport)
                .filter(this::isSafe)
                .count();
    }

    private List<Integer> mapToReport(String line) {
        var matcher = DIGIT_PATTERN.matcher(line);
        var report = new ArrayList<Integer>();
        while (matcher.find()) {
            report.add(Integer.parseInt(matcher.group()));
        }
        return report;
    }

    private boolean isSafe(List<Integer> report) {
        var step = report.get(1) - report.get(0);
        var increasing = step > 0;
        for (var i = 1 ; i < report.size() ; i++) {
            if (!validateStep(report.get(i - 1), report.get(i), increasing)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateStep(int first, int second, boolean increasing) {
        if (second > first != increasing) {
            return false;
        }
        var stepSize = Math.abs(second - first);
        return stepSize >= 1 && stepSize <= 3;
    }
    @Override
    public Object part2Solution() {
        return getInput()
                .map(this::mapToReport)
                .filter(this::isSafePart2)
                .count();
    }

    private boolean isSafePart2(List<Integer> report) {
        return IntStream.range(0, report.size())
                .mapToObj(idx -> {
                    var shorterReport = new ArrayList<>(report);
                    shorterReport.remove(idx);
                    return shorterReport;
                })
                .anyMatch(this::isSafe);
    }

    public static void main(String[] args) {
        new Day2().solve();
    }
}
