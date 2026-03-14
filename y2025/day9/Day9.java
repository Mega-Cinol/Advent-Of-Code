package y2025.day9;

import common.AdventSolution;
import common.Area;
import common.Pair;
import common.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Day9 extends AdventSolution {
  private Point strToPoint(String str) {
    var coords = str.split(",");
    var x = Long.parseLong(coords[0]);
    var y = Long.parseLong(coords[1]);
    return new Point(x, y);
  }

  @Override
  public Object part1Solution() {
    var redTiles = getInput().map(this::strToPoint).toList();
    var maxArea = 0L;
    for (var i = 0; i < redTiles.size() - 1; i++) {
      for (var j = i + 1; j < redTiles.size(); j++) {
        var area = area(redTiles.get(i), redTiles.get(j));
        if (area > maxArea) {
          maxArea = area;
        }
      }
    }
    return maxArea;
  }

  @Override
  public Object part2Solution() {
    var redTiles = getInput().map(this::strToPoint).toList();
    var redTilePairs = new ArrayList<Pair<Point>>();
    for (var i = 0; i < redTiles.size() - 1; i++) {
      for (var j = i + 1; j < redTiles.size(); j++) {
        redTilePairs.add(Pair.of(redTiles.get(i), redTiles.get(j)));
      }
    }
    redTilePairs.sort(Comparator.comparingLong(this::pairArea).reversed());
    var edges = new HashSet<Pair<Point>>();
    for (var i = 0; i < redTiles.size() - 1; i++) {
      edges.add(Pair.of(redTiles.get(i), redTiles.get(i + 1)));
    }
    edges.add(Pair.of(redTiles.getFirst(), redTiles.getLast()));
    var coloredTiles = new Area().getSubAreas(edges);
    return redTilePairs.stream()
            .filter(tilePair -> withinColoredTiles(coloredTiles, tilePair.getFirst(), tilePair.getSecond()))
            .findFirst()
            .map(this::pairArea)
            .orElseThrow(IllegalStateException::new);
  }

  private boolean withinColoredTiles(Set<Pair<Point>> coloredTiles, Point from, Point to) {
    Set<Pair<Point>> leftToMatch = new HashSet<>();
    leftToMatch.add(Pair.of(from, to));
    for (var coloredTile : coloredTiles) {
      var newLeftToMatch = new HashSet<Pair<Point>>();
      for (var tilesLeft : leftToMatch) {
        newLeftToMatch.addAll(remove(tilesLeft, coloredTile));
      }
      leftToMatch = newLeftToMatch;
    }
    return leftToMatch.isEmpty();
  }

  private Set<Pair<Point>> remove(Pair<Point> from, Pair<Point> toRemove) {
    var fromMaxX = Math.max(from.getFirst().getX(), from.getSecond().getX());
    var fromMaxY = Math.max(from.getFirst().getY(), from.getSecond().getY());
    var fromMinX = Math.min(from.getFirst().getX(), from.getSecond().getX());
    var fromMinY = Math.min(from.getFirst().getY(), from.getSecond().getY());
    var toRemoveMaxX = Math.max(toRemove.getFirst().getX(), toRemove.getSecond().getX());
    var toRemoveMaxY = Math.max(toRemove.getFirst().getY(), toRemove.getSecond().getY());
    var toRemoveMinX = Math.min(toRemove.getFirst().getX(), toRemove.getSecond().getX());
    var toRemoveMinY = Math.min(toRemove.getFirst().getY(), toRemove.getSecond().getY());
    if (fromMinX > toRemoveMaxX || fromMaxX < toRemoveMinX ||  fromMinY > toRemoveMaxY || fromMaxY < toRemoveMinY) {
      return Set.of(from);
    }
    if (toRemoveMinX <= fromMinX && toRemoveMaxX >= fromMaxX) {
      if (toRemoveMinY <= fromMinY) {
        if (toRemoveMaxY >= fromMaxY) {
          return Set.of();
        } else {
          return Set.of(rect(fromMinX, toRemoveMaxY + 1, fromMaxX, fromMaxY));
        }
      } else {
        if (toRemoveMaxY >= fromMaxY) {
          return Set.of(rect(fromMinX, fromMinY, fromMaxX, toRemoveMinY - 1));
        } else {
          return Set.of(rect(fromMinX, fromMinY, fromMaxX, toRemoveMinY - 1),
                  rect(fromMinX, toRemoveMaxY + 1, fromMaxX, fromMaxY));
        }
      }
    }
    if (toRemoveMinY <= fromMinY && toRemoveMaxY >= fromMaxY) {
      if (toRemoveMinX <= fromMinX) {
        if (toRemoveMaxX >= fromMaxX) {
          return Set.of();
        } else {
          return Set.of(rect(toRemoveMaxX + 1, fromMinY, fromMaxX, fromMaxY));
        }
      } else {
        if (toRemoveMaxX >= fromMaxX) {
          return Set.of(rect(fromMinX, fromMinY, toRemoveMinX - 1, fromMaxY));
        } else {
          return Set.of(rect(fromMinX, fromMinY, toRemoveMinX - 1, fromMaxY),
                  rect(toRemoveMaxX + 1, fromMinY, fromMaxX, fromMaxY));
        }
      }
    }
    if (toRemoveMinX <= fromMinX) {
      if (toRemoveMinY <= fromMinY) {
        return Set.of(rect(toRemoveMaxX + 1, fromMinY, fromMaxX, fromMaxY),
                rect(fromMinX, toRemoveMaxY + 1, toRemoveMaxX, fromMaxY));
      }
      else {
        if (toRemoveMaxY < fromMaxY) {
          return Set.of(rect(fromMinX, fromMinY, fromMaxX, toRemoveMinY - 1),
                  rect(toRemoveMaxX + 1, toRemoveMinY, fromMaxX, toRemoveMaxY),
                  rect(fromMinX, toRemoveMaxY + 1, fromMaxX, fromMaxY));
        } else {
          return Set.of(rect(fromMinX, fromMinY, fromMaxX, toRemoveMinY - 1),
                  rect(toRemoveMaxX + 1, toRemoveMinY, fromMaxX, fromMaxY));
        }
      }
    }
    if (toRemoveMaxX >= fromMaxX) {
      if (toRemoveMinY <= fromMinY) {
        return Set.of(rect(fromMinX, fromMinY, toRemoveMinX - 1, toRemoveMaxY),
                rect(fromMinX, toRemoveMaxY + 1, fromMaxX, fromMaxY));
      }
      else {
        if (toRemoveMaxY < fromMaxY) {
          return Set.of(rect(fromMinX, fromMinY, fromMaxX, toRemoveMinY - 1),
                  rect(fromMinX, toRemoveMinY, toRemoveMinX - 1, toRemoveMaxY),
                  rect(fromMinX, toRemoveMaxY + 1, fromMaxX, fromMaxY));
        } else {
          return Set.of(rect(fromMinX, fromMinY, fromMaxX, toRemoveMinY - 1),
                  rect(fromMinX, toRemoveMinY, toRemoveMinX - 1, fromMaxY));
        }
      }
    }
    if (toRemoveMinY  <= fromMinY) {
      return Set.of(rect(fromMinX, fromMinY, toRemoveMinX - 1, fromMaxY),
              rect(toRemoveMinX, toRemoveMaxY + 1, toRemoveMaxX, fromMaxY),
              rect(toRemoveMaxX + 1, fromMinY, fromMaxX, fromMaxY));
    } else {
      if (toRemoveMaxY >= fromMaxY) {
        return Set.of(rect(fromMinX, fromMinY, toRemoveMinX - 1, fromMaxY),
                rect(toRemoveMinX, fromMinY, toRemoveMaxX, toRemoveMinY - 1),
                rect(toRemoveMaxX + 1, fromMinY, fromMaxX, fromMaxY));
      } else {
        return Set.of(rect(fromMinX, fromMinY, fromMaxX, toRemoveMinY - 1),
                rect(fromMinX, toRemoveMinY, toRemoveMinX - 1, toRemoveMaxY),
                rect(toRemoveMaxX + 1, toRemoveMinY, fromMaxX, toRemoveMaxY),
                rect(fromMinX, toRemoveMaxY + 1, fromMaxX, fromMaxY));
      }
    }
  }

  private Pair<Point> rect(long topLeftX, long topLeftY, long bottomRightX, long bottomRightY) {
    return Pair.of(new Point(topLeftX, topLeftY), new Point(bottomRightX, bottomRightY));
  }

  private long pairArea(Pair<Point> verts) {
    return area(verts.getFirst(), verts.getSecond());
  }

  private long area(Point v1, Point v2) {
    return (Math.abs(v1.getX() - v2.getX()) + 1) * (Math.abs(v1.getY() - v2.getY()) + 1);
  }
  public static void main(String[] args) {
    new Day9().solve();
  }
}
