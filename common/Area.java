package common;

import y2023.day18.Day18;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Area {
  public Set<Pair<Point>> getSubAreas(Set<Pair<Point>> edges) {
    var subAreas = new HashSet<Pair<Point>>();
    var xIn = List.<Pair<Long>>of();
    var ySorted = edges.stream().map(e -> List.of(e.getFirst().getY(), e.getSecond().getY()))
            .flatMap(List::stream).sorted().distinct().toList();
    for (int i = 0; i < ySorted.size() - 1; i++) {
      var currentY = ySorted.get(i);
      var nextY = ySorted.get(i + 1);
      var xRanges = edges.stream()
              .filter(d -> d.getFirst().getY() == currentY && d.getSecond().getY() == currentY)
              .map(e -> Pair.of(Math.min(e.getFirst().getX(), e.getSecond().getX()),
                      Math.max(e.getFirst().getX(), e.getSecond().getX())))
              .sorted(Comparator.comparing(Pair::getFirst)).toList();
      var midXRanges = jointLength(xIn, xRanges);
      xIn = evaluateXIn(xIn, xRanges);
      if ((nextY - currentY - 1) > 0) {
        xIn.stream().map(xRange -> {
          var firstPoint = new Point(xRange.getFirst(), currentY + 1);
          var secondPoint = new Point(xRange.getSecond(), nextY - 1);
          return Pair.of(firstPoint, secondPoint);
        }).forEach(subAreas::add);
      }
      midXRanges.stream().map(xRange -> {
        var firstPoint = new Point(xRange.getFirst(), currentY);
        var secondPoint = new Point(xRange.getSecond(), currentY);
        return Pair.of(firstPoint, secondPoint);
      }).forEach(subAreas::add);
    }
    var currentY = ySorted.get(ySorted.size() - 1);
    edges.stream().filter(d -> d.getFirst().getY() == currentY && d.getSecond().getY() == currentY)
            .map(e -> {
              var first = new Point(Math.min(e.getFirst().getX(), e.getSecond().getX()), currentY);
              var second = new Point(Math.max(e.getFirst().getX(), e.getSecond().getX()), currentY);
              return Pair.of(first, second);
            })
            .forEach(subAreas::add);

    return subAreas;
  }

  private List<Pair<Long>> evaluateXIn(List<Pair<Long>> current, List<Pair<Long>> newIn) {
    if (current.isEmpty()) {
      return newIn;
    }
    if (newIn.isEmpty()) {
      return current;
    }
    var resultXIn = new ArrayList<Pair<Long>>();
    var newInIdx = 0;
    outer: for (var currentElement : current) {
      var currentToAdd = currentElement;
      while (newInIdx < newIn.size()) {
        var newElement = newIn.get(newInIdx++);
        if (newElement.getSecond().longValue() < currentToAdd.getFirst().longValue()) {
          resultXIn.add(newElement);
        } else if (newElement.getSecond().longValue() == currentToAdd.getFirst().longValue()) {
          currentToAdd = Pair.of(newElement.getFirst(), currentToAdd.getSecond());
        } else if (newElement.getFirst().longValue() < currentToAdd.getFirst().longValue()
                && newElement.getSecond().longValue() > currentToAdd.getFirst().longValue()) {
          throw new UnsupportedOperationException("?");
        } else if (newElement.getFirst().longValue() == currentToAdd.getFirst().longValue()
                && newElement.getSecond().longValue() == currentToAdd.getSecond().longValue()) {
          continue outer;
        } else if (newElement.getFirst().longValue() == currentToAdd.getFirst().longValue()) {
          if (newElement.getSecond().longValue() > currentToAdd.getSecond().longValue()) {
            throw new UnsupportedOperationException("??");
          }
          currentToAdd = Pair.of(newElement.getSecond(), currentToAdd.getSecond());
        } else if (newElement.getFirst().longValue() > currentToAdd.getFirst().longValue()
                && newElement.getSecond().longValue() < currentToAdd.getSecond().longValue()) {
          resultXIn.add(Pair.of(currentToAdd.getFirst(), newElement.getFirst()));
          currentToAdd = Pair.of(newElement.getSecond(), currentToAdd.getSecond());
        } else if (newElement.getFirst().longValue() > currentToAdd.getFirst().longValue()
                && newElement.getSecond().longValue() == currentToAdd.getSecond().longValue()) {
          currentToAdd = Pair.of(currentToAdd.getFirst(), newElement.getFirst());
        } else if (newElement.getFirst().longValue() < currentToAdd.getSecond().longValue()
                && newElement.getSecond().longValue() > currentToAdd.getSecond().longValue()) {
          throw new UnsupportedOperationException("???");
        } else if (newElement.getFirst().longValue() == currentToAdd.getSecond().longValue()) {
          currentToAdd = Pair.of(currentToAdd.getFirst(), newElement.getSecond());
        } else if (newElement.getFirst().longValue() > currentElement.getSecond().longValue()) {
          newInIdx--;
          break;
        }
      }
      resultXIn.add(currentToAdd);
    }
    while (newInIdx < newIn.size()) {
      resultXIn.add(newIn.get(newInIdx++));
    }
    Collections.sort(resultXIn, Comparator.comparing(Pair::getFirst));
    for (int i = 0; i < resultXIn.size() - 1; i++) {
      if (resultXIn.get(i).getSecond().longValue() == resultXIn.get(i + 1).getFirst().longValue()) {
        resultXIn.set(i, Pair.of(resultXIn.get(i).getFirst(), resultXIn.get(i + 1).getSecond()));
        resultXIn.remove(i + 1);
        i--;
      }
    }
    return resultXIn;
  }

  private List<Pair<Long>> jointLength(List<Pair<Long>> current, List<Pair<Long>> newIn) {
    var jointXs = new ArrayList<Pair<Long>>();
    if (current.isEmpty()) {
      jointXs.addAll(newIn);
    }
    if (newIn.isEmpty()) {
      jointXs.addAll(current);
    }
    if (!current.isEmpty() && !newIn.isEmpty()) {
      var newInIdx = 0;
      for (var currentElement : current) {
        var currentToAdd = currentElement;
        while (newInIdx < newIn.size()) {
          var newElement = newIn.get(newInIdx++);
          if (newElement.getSecond().longValue() < currentToAdd.getFirst().longValue()) {
            jointXs.add(newElement);
          } else if (newElement.getSecond().longValue() == currentToAdd.getFirst().longValue()) {
            currentToAdd = Pair.of(newElement.getFirst(), currentToAdd.getSecond());
          } else if (newElement.getFirst().longValue() == currentToAdd.getSecond().longValue()) {
            currentToAdd = Pair.of(currentToAdd.getFirst(), newElement.getSecond());
          } else if (newElement.getFirst().longValue() > currentElement.getSecond().longValue()) {
            newInIdx--;
            break;
          }
        }
        jointXs.add(currentToAdd);
      }
      while (newInIdx < newIn.size()) {
        jointXs.add(newIn.get(newInIdx++));
      }
      Collections.sort(jointXs, Comparator.comparing(Pair::getFirst));
      for (int i = 0; i < jointXs.size() - 1; i++) {
        if (jointXs.get(i).getSecond().longValue() == jointXs.get(i + 1).getFirst().longValue()) {
          jointXs.set(i, Pair.of(jointXs.get(i).getFirst(), jointXs.get(i + 1).getSecond()));
          jointXs.remove(i + 1);
          i--;
        }
      }
    }
    return jointXs;
  }
}
