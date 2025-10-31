package y2024.day19;

import common.AdventSolution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day19 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var input = getInput().toList();
        var towels = Arrays.stream(input.get(0).split(", ")).collect(Collectors.toSet());
        return input.subList(2, input.size()).stream()
                .filter(pattern -> canBuild(pattern, towels))
                .count();
    }

    private boolean canBuild(String pattern, Set<String> towels) {
        Map<Integer, Set<String>> towelsStart = new HashMap<>();
        var towelsEnd = new HashMap<Integer, Set<String>>();
        towels.forEach(towel -> {
            var location = pattern.indexOf(towel);
            while (location != -1) {
                towelsStart.computeIfAbsent(location, k -> new HashSet<>()).add(towel);
                towelsEnd.computeIfAbsent(location + towel.length() - 1, k -> new HashSet<>()).add(towel);
                location = pattern.indexOf(towel, location + 1);
            }
        });
        for (var idx = 1; idx < pattern.length(); idx++) {
            if (!towelsStart.getOrDefault(idx, Set.of()).isEmpty()) {
                if (towelsEnd.getOrDefault(idx - 1, Set.of()).isEmpty()) {
                    int finalIdx = idx;
                    towelsStart.remove(idx)
                            .forEach(towel -> {
                                var endIdx = finalIdx + towel.length() - 1;
                                towelsEnd.get(endIdx).remove(towel);
                                if (towelsEnd.get(endIdx).isEmpty()) {
                                    towelsEnd.remove(endIdx);
                                }
                            });
                }
            }
        }
        for (var idx = 0; idx < pattern.length() - 1; idx++) {
            if (!towelsEnd.getOrDefault(idx, Set.of()).isEmpty()) {
                if (towelsStart.getOrDefault(idx + 1, Set.of()).isEmpty()) {
                    int finalIdx = idx;
                    towelsEnd.remove(idx)
                            .forEach(towel -> {
                                var startIdx = finalIdx - towel.length() + 1;
                                towelsStart.get(startIdx).remove(towel);
                                if (towelsStart.get(startIdx).isEmpty()) {
                                    towelsStart.remove(startIdx);
                                }
                            });
                }
            }
        }
        return towelsStart.containsKey(0) && towelsEnd.containsKey(pattern.length() - 1);
    }

    private long countAlternatives(String pattern, Set<String> towels) {
        Map<Integer, Set<String>> towelsStart = new HashMap<>();
        var towelsEnd = new HashMap<Integer, Set<String>>();
        towels.forEach(towel -> {
            var location = pattern.indexOf(towel);
            while (location != -1) {
                towelsStart.computeIfAbsent(location, k -> new HashSet<>()).add(towel);
                towelsEnd.computeIfAbsent(location + towel.length() - 1, k -> new HashSet<>()).add(towel);
                location = pattern.indexOf(towel, location + 1);
            }
        });
        for (var idx = 1; idx < pattern.length(); idx++) {
            if (!towelsStart.getOrDefault(idx, Set.of()).isEmpty()) {
                if (towelsEnd.getOrDefault(idx - 1, Set.of()).isEmpty()) {
                    int finalIdx = idx;
                    towelsStart.remove(idx)
                            .forEach(towel -> {
                                var endIdx = finalIdx + towel.length() - 1;
                                towelsEnd.get(endIdx).remove(towel);
                                if (towelsEnd.get(endIdx).isEmpty()) {
                                    towelsEnd.remove(endIdx);
                                }
                            });
                }
            }
        }
        for (var idx = 0; idx < pattern.length() - 1; idx++) {
            if (!towelsEnd.getOrDefault(idx, Set.of()).isEmpty()) {
                if (towelsStart.getOrDefault(idx + 1, Set.of()).isEmpty()) {
                    int finalIdx = idx;
                    towelsEnd.remove(idx)
                            .forEach(towel -> {
                                var startIdx = finalIdx - towel.length() + 1;
                                towelsStart.get(startIdx).remove(towel);
                                if (towelsStart.get(startIdx).isEmpty()) {
                                    towelsStart.remove(startIdx);
                                }
                            });
                }
            }
        }
        return countAlternatives(towelsStart, pattern.length(), 0, new HashMap<>());
    }

    private long countAlternatives(Map<Integer, Set<String>> towelStarts, int expectedLength, int fromIdx, Map<Integer, Long> resultCache) {
        if (fromIdx == expectedLength) {
            return 1;
        }
        if (!towelStarts.containsKey(fromIdx)) {
            return 0;
        }
        if (resultCache.containsKey(fromIdx)) {
            return resultCache.get(fromIdx);
        }
        var result = towelStarts.get(fromIdx)
                .stream()
                .mapToInt(String::length)
                .mapToLong(l -> countAlternatives(towelStarts, expectedLength, fromIdx + l, resultCache))
                .sum();
        resultCache.put(fromIdx, result);
        return result;
    }

    @Override
    public Object part2Solution() {
        var input = getInput().toList();
        var towels = Arrays.stream(input.get(0).split(", ")).collect(Collectors.toSet());
        return input.subList(2, input.size()).stream()
                .mapToLong(pattern -> countAlternatives(pattern, towels))
                .sum();
    }

    public static void main(String[] args) {
        new Day19().solve();
    }
}
