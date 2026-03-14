package y2025.day10;

import common.AdventSolution;
import common.equations.Fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day10 extends AdventSolution {
  private record Machine(Set<Integer> goal, List<Set<Integer>> buttons, List<Integer> power) {
    private static final Pattern INPUT_PATTERN = Pattern.compile("(?<goal>\\[[.#]+])(?<buttons>( \\([0-9](,[0-9])*\\))+) (?<power>\\{[0-9]+(,[0-9]+)*})");

    public static Machine fromString(String input) {
      var matcher = INPUT_PATTERN.matcher(input);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid input " + input);
      }
      var goalStr = matcher.group("goal");
      var goal = new HashSet<Integer>();
      for (var i = 1; i < goalStr.length(); i++) {
        if (goalStr.charAt(i) == '#') {
          goal.add(i - 1);
        }
      }
      var buttonsStr = matcher.group("buttons").trim().split(" ");
      var buttons = new ArrayList<Set<Integer>>();
      for (var buttonStr : buttonsStr) {
        var button = Arrays.stream(buttonStr.replaceAll("\\(", "").replaceAll("\\)", "").split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        buttons.add(button);
      }
      var power = Arrays.stream(matcher.group("power").trim().replaceAll("\\{", "").replaceAll("}", "").split(","))
              .map(Integer::parseInt)
              .toList();
      return new Machine(goal, buttons, power);
    }

    public Set<Set<Integer>> getShortestCombination() {
      var combinations = IntStream.range(0, buttons.size()).mapToObj(Set::of).toList();
      var matchingCombination = combinations.stream().filter(this::checkCombination).findFirst();
      while (matchingCombination.isEmpty()) {
        combinations = getLargerCombinations(combinations);
        matchingCombination = combinations.stream().filter(this::checkCombination).findFirst();
      }
      return matchingCombination.map(buttonIds -> buttonIds.stream().map(buttons::get).collect(Collectors.toSet())).get();
    }

    private List<Set<Integer>> getLargerCombinations(List<Set<Integer>> combinations) {
      var newCombinations = new ArrayList<Set<Integer>>();
      for (Set<Integer> combination : combinations) {
        for (int i = 0; i < buttons.size(); i++) {
          if (combination.contains(i)) {
            continue;
          }
          var newCombination = new HashSet<Integer>();
          newCombination.addAll(combination);
          newCombination.add(i);
          newCombinations.add(newCombination);
        }
      }
      return newCombinations;
    }

    private boolean checkCombination(Set<Integer> combination) {
      var pressedButtons = combination.stream()
              .map(buttons::get)
              .collect(Collectors.toSet());
      var maxGoal = goal.stream().mapToInt(Integer::intValue).max().getAsInt();
      var maxBtn = pressedButtons.stream().flatMap(Set::stream).mapToInt(Integer::intValue).max().getAsInt();
      for (int i = 0; i <= Math.max(maxGoal, maxBtn); i++) {
        var finalI = i;
        var lit = pressedButtons.stream()
                .filter(btn -> btn.contains(finalI))
                .count() % 2 == 1;
        if (lit != goal.contains(i)) {
          return false;
        }
      }
      return true;
    }

    private List<List<Fraction>> getRowEchelonForm(List<List<Fraction>> matrix) {
      var newMatrix = new ArrayList<List<Fraction>>();
      matrix.stream().map(ArrayList::new).forEach(newMatrix::add);
      var rows = newMatrix.size() - 1;
      var cols = newMatrix.getFirst().size() - 1;
      var row = 0;
      var col = 0;
      while (row <= rows && col <= cols) {
        var maxRowIdxForCol = argMax(row, col, rows, newMatrix);
        if (newMatrix.get(maxRowIdxForCol).get(col).isZero()) {
          col++;
        } else {
          var maxRow = newMatrix.get(maxRowIdxForCol);
          newMatrix.set(maxRowIdxForCol, newMatrix.get(row));
          newMatrix.set(row, maxRow);
          for (var i = row + 1 ; i <= rows; i++) {
            var factor = newMatrix.get(i).get(col).divide(newMatrix.get(row).get(col));
            newMatrix.get(i).set(col, new Fraction(0));
            for (var j = col + 1; j <= cols; j++) {
              var newValue =  newMatrix.get(i).get(j).subtract(newMatrix.get(row).get(j).multiply(factor));
              newMatrix.get(i).set(j, newValue);
            }
          }
          row++;
          col++;
        }
      }
      return newMatrix;
    }

    private int argMax(int fromRow, int col, int rows, List<List<Fraction>> matrix) {
      var maxValue = matrix.get(fromRow).get(col).abs();
      var maxIdx = fromRow;
      for (var i = fromRow + 1; i <= rows; i++) {
        if (matrix.get(i).get(col).abs().isGreaterThan(maxValue)) {
          maxIdx = i;
          maxValue = matrix.get(i).get(col).abs();
        }
      }
      return maxIdx;
    }

    public Fraction findShortestPowerCombinationSum() {
      var matrix = buildPowerCombinationsMatrix();
//      matrix.forEach(System.out::println);
//      System.out.println("================================");
      matrix = getRowEchelonForm(matrix);
//      matrix.forEach(System.out::println);
//      System.out.println("================================");

      var solution = getMatrixSolution(matrix);
      if (solution.freeFactors().isEmpty()) {
        return solution.getSum(Map.of());
      }
//      System.out.println(solution);
      var freeFactorsMax = solution.freeFactors().stream()
              .collect(Collectors.toMap(Function.identity(), factorId ->
                buttons().get(factorId).stream()
                        .mapToLong(powerLevelId -> power().get(powerLevelId))
                        .min()
                        .stream().mapToObj(Fraction::new)
                        .findFirst()
                        .orElseThrow(IllegalStateException::new)
              ));
      var freeFactorsCurrent = new HashMap<>(freeFactorsMax);
      var freeFactorsList = List.copyOf(solution.freeFactors());
      var minSum = new Fraction(Integer.MAX_VALUE);
      while (!freeFactorsCurrent.isEmpty()) {
        if (solution.isValid(freeFactorsCurrent)) {
          var currentSum = solution.getSum(freeFactorsCurrent);
          if (currentSum.isLessThan(minSum)) {
            minSum = currentSum;
          }
//          System.out.println(freeFactorsCurrent + " " + currentSum);
        }
        var i = 0;
        while (freeFactorsList.size() > i && freeFactorsCurrent.get(freeFactorsList.get(i)).isZero()) {
          i++;
        }
        if (i == freeFactorsList.size()) {
          freeFactorsCurrent.clear();
          break;
        }
        freeFactorsCurrent.put(freeFactorsList.get(i), freeFactorsCurrent.get(freeFactorsList.get(i)).subtract(1));
        i--;
        while (i >= 0) {
          freeFactorsCurrent.put(freeFactorsList.get(i), freeFactorsMax.get(freeFactorsList.get(i)));
          i--;
        }
      }
//      System.out.println(minSum);
      return minSum;
    }

    private MatrixSolution getMatrixSolution(List<List<Fraction>> matrix) {
      var solution = new MatrixSolution(new HashSet<>(), new HashMap<>());
      for (var rowId = matrix.size() - 1; rowId >= 0; rowId--) {
        var row = matrix.get(rowId);

        var pivotId = IntStream.range(0, row.size()).filter(col -> !row.get(col).isZero()).findFirst().orElse(-1);
        if (pivotId == row.size() - 1) {
          throw new IllegalStateException("Contradiction in matrix");
        }
        if (pivotId == -1) {
          continue;
        }
        var freeFactors = new HashMap<Integer, Fraction>();
        var value = row.getLast().divide(row.get(pivotId));
        for (var colId = pivotId + 1; colId < row.size() - 1; colId++) {
          if (!row.get(colId).isZero()) {
            var colVal =  row.get(colId).divide(row.get(pivotId));
            if (solution.pivotFactors.containsKey(colId)) {
              value = value.subtract(solution.pivotFactors.get(colId).value().multiply(colVal));
              solution.pivotFactors().get(colId).freeFactors.forEach((ffId, ffValue) ->
                freeFactors.merge(ffId, colVal.multiply(-1).multiply(ffValue), Fraction::add)
              );
            } else {
              solution.freeFactors.add(colId);
              freeFactors.merge(colId, colVal.multiply(-1), Fraction::add);
            }
          }
        }
        solution.pivotFactors.put(pivotId, new MatrixEquationSolution(freeFactors, value));
      }
      return solution;
    }

    private List<List<Fraction>> buildPowerCombinationsMatrix() {
      List<List<Fraction>> matrix = new ArrayList<>();
      for (var powerLevelIdx = 0; powerLevelIdx < power.size(); powerLevelIdx++) {
        var matrixRow = new ArrayList<Fraction>();
        int finalPowerLevelIdx = powerLevelIdx;
        buttons.forEach(button -> matrixRow.add(button.contains(finalPowerLevelIdx) ? new Fraction(1) : new Fraction(0)));
        matrixRow.add(new Fraction(power().get(powerLevelIdx)));
        matrix.add(matrixRow);
      }
      return matrix;
    }

    private record MatrixSolution(Set<Integer> freeFactors, Map<Integer, MatrixEquationSolution> pivotFactors) {
      public boolean isValid(Map<Integer, Fraction> freeFactorValues) {
        if (freeFactorValues.values().stream().anyMatch(v -> v.isLessThan(0))) {
          return false;
        }
        return pivotFactors.values()
                .stream()
                .map(pivot -> pivot.getValue(freeFactorValues))
                .allMatch(v -> v.isGreaterThanOrEqual(0) && v.isInt());
      }

      public Fraction getSum(Map<Integer, Fraction> freeFactorValues) {
        Fraction freeFactorsSum = freeFactorValues.values()
                .stream()
                .reduce(new Fraction(0), Fraction::add);
        return pivotFactors.values()
                .stream()
                .map(pivot -> pivot.getValue(freeFactorValues))
                .reduce(new Fraction(0), Fraction::add).add(freeFactorsSum);
      }
      @Override
      public String toString() {
        return pivotFactors.entrySet().stream()
                .map(e -> "x" + e.getKey() + " = " + e.getValue())
                .collect(Collectors.joining("\n"));
      }
    }

    private record MatrixEquationSolution(Map<Integer, Fraction> freeFactors, Fraction value) {
      public Fraction getValue(Map<Integer, Fraction> freeFactorValues) {
        return freeFactors.entrySet().stream()
                .map(e -> freeFactorValues.getOrDefault(e.getKey(), new Fraction(0)).multiply(e.getValue()))
                .reduce(new Fraction(0), Fraction::add).add(value);
      }
      @Override
      public String toString() {
        var freeFactorsString = freeFactors.entrySet().stream()
                .map((entry) -> {
                  if (entry.getValue().isZero()) {
                    return " ";
                  } else if (entry.getValue().isLessThan(0)) {
                    return " %s * x%d".formatted(entry.getValue(), entry.getKey());
                  } else {
                    return " + %s * x%d".formatted(entry.getValue(), entry.getKey());
                  }
                }).collect(Collectors.joining(""));
        if (value.isGreaterThan(0)) {
          freeFactorsString += " + %s".formatted(value);
        } else if (value.isLessThan(0)) {
          freeFactorsString += " %s".formatted(value);
        }
        return freeFactorsString;
      }
    }
  }

  @Override
  public Object part1Solution() {
    return getInput().map(Machine::fromString)
            .map(Machine::getShortestCombination)
            .mapToInt(Set::size)
            .sum();
  }

  @Override
  public Object part2Solution() {
    return getInput().map(Machine::fromString)
            .map(Machine::findShortestPowerCombinationSum)
            .reduce(new Fraction(0), Fraction::add);
  }

  public static void main(String[] args) {
    new Day10().solve();
  }
}
