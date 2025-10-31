package tmp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3
{
    private static class Point
    {
        private final int x;
        private final int y;

        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public Point add(Point other)
        {
            return new Point(x + other.x, y + other.y);
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Point other = (Point) obj;
            if (x != other.x) return false;
            if (y != other.y) return false;
            return true;
        }

        @Override
        public String toString()
        {
            return getX() + " " + getY();
        }
    }

    private static enum Direction
    {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, 1), DOWN(0, -1);

        private final Point delta;

        public static Direction fromChar(char descriptionChar)
        {
            switch (descriptionChar)
            {
                case 'U':
                    return UP;
                case 'D':
                    return DOWN;
                case 'L':
                    return LEFT;
                case 'R':
                    return RIGHT;
            }
            return null;
        }

        private Direction(int x, int y)
        {
            this.delta = new Point(x, y);
        }

        public Point getDelta()
        {
            return delta;
        }
    }

    private static class Vector
    {
        private static final Pattern VECTOR_DESC_PATTERN = Pattern.compile("([UDLR])(\\d+)");
        private final Direction direction;
        private final int length;

        public Vector(String description)
        {
            Matcher vectorMatcher = VECTOR_DESC_PATTERN.matcher(description);
            vectorMatcher.find();
            direction = Direction.fromChar(vectorMatcher.group(1).charAt(0));
            length = Integer.parseInt(vectorMatcher.group(2));
        }

        public Point move(Point current, List<Point> usedPoints)
        {
            for (int i = 0; i < length; i++)
            {
                current = current.add(direction.getDelta());
                usedPoints.add(current);
            }
            return current;
        }
    }

    private static List<Point> getUsedPoints(List<Vector> vectors)
    {
        Point current = new Point(0, 0);
        List<Point> usedPoints = new ArrayList<>();
        System.out.println("Get used points");
        for (Vector vector : vectors)
        {
            current = vector.move(current, usedPoints);
        }
        return usedPoints;
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<Vector> firstCable = Stream.of(scanner.next().split(",")).map(Vector::new).collect(Collectors.toList());
        List<Point> firstCablePoints = getUsedPoints(firstCable);
        List<Vector> secondCable = Stream.of(scanner.next().split(",")).map(Vector::new).collect(Collectors.toList());
        List<Point> secondCablePoints = getUsedPoints(secondCable);

        Set<Point> intersections = new HashSet<>(firstCablePoints);
        intersections.retainAll(secondCablePoints);
        System.out.println(intersections);
        intersections.remove(new Point(0,0));
        int minDistance = Integer.MAX_VALUE;
        for (Point intersection : intersections)
        {
            int distance = firstCablePoints.indexOf(intersection) + secondCablePoints.indexOf(intersection);
            minDistance = distance < minDistance ? distance : minDistance;
            System.out.println(intersection);
            System.out.println(distance);
        }
        System.out.println(minDistance);
        scanner.close();
    }
}
