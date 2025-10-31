package tmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day10
{
    private static class Ratio implements Comparable<Ratio>
    {
        private final int a;
        private final int b;

        public Ratio(int a, int b)
        {
            int factor = maxFactor(a, b);
            this.a = a / factor;
            this.b = b / factor;
        }

        private int getQuadrant()
        {
            if (a >= 0 && b >= 0)
            {
                return 1;
            }
            else if (a >= 0 && b < 0)
            {
                return 2;
            }
            else if (a < 0 && b < 0)
            {
                return 3;
            }
            else
            {
                return 4;
            }
        }

        @Override
        public int compareTo(Ratio other)
        {
            if (getQuadrant() != other.getQuadrant())
            {
                return other.getQuadrant() - getQuadrant();
            }
            switch (getQuadrant())
            {
                case 1:
                    if (a == 0)
                    {
                        return 1;
                    }
                    if (other.a == 0)
                    {
                        return -1;
                    }
                    return Double.compare((double)b/a, (double)other.b/other.a);
                case 2:
                    if (a == 0)
                    {
                        return -1;
                    }
                    if (other.a == 0)
                    {
                        return 1;
                    }
                    return Double.compare((double)b/a, (double)other.b/other.a);
                case 3:
                    return Double.compare((double)b/a, (double)other.b/other.a);
                case 4:
                    return Double.compare((double)b/a, (double)other.b/other.a);
                default:
                    return 0;
            }
        }

        private int maxFactor(int num1, int num2)
        {
            num1 = Math.abs(num1);
            num2 = Math.abs(num2);
            while (num1 * num2 > 0)
            {
                if (num1 > num2)
                {
                    num1 %= num2;
                }
                else
                {
                    num2 %= num1;
                }
            }
            return Math.max(num1, num2);
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + a;
            result = prime * result + b;
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Ratio other = (Ratio) obj;
            if (a != other.a) return false;
            if (b != other.b) return false;
            return true;
        }

        @Override
        public String toString()
        {
            return a + "/" + b; 
        }
    }

    private static class Asteroid
    {
        private final int x;
        private final int y;

        public Asteroid(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        private int distance(Asteroid other)
        {
            return Math.abs(other.x - x) + Math.abs(other.y - y);
        }

        public Ratio getRatio(Asteroid otherAsteroid)
        {
            return new Ratio(otherAsteroid.x - x, y - otherAsteroid.y);
        }

        public long countVisibleAsteroids(List<Asteroid> otherAsteroids)
        {
            long count = otherAsteroids.stream()
                    .filter(other -> other != this)
                    .map(this::getRatio)
                    .distinct()
                    .count();
            return count;
        }

        @Override
        public String toString()
        {
            return x*100 + y + ""; 
        }
    }
    private static Asteroid bestAsteroid;
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        int row = 0;
        List<Asteroid> asteroids = new ArrayList<>();
        while (scanner.hasNext())
        {
            String line = scanner.next();
            if ("dupa".equals(line))
            {
                break;
            }
            for (int col = 0 ; col < line.length() ; col++)
            {
                if ('#' == line.charAt(col))
                {
                    asteroids.add(new Asteroid(col, row));
                }
            }
            row++;
        }
        long bestCount = 0;
        for (Asteroid asteroid : asteroids)
        {
            long count = asteroid.countVisibleAsteroids(asteroids);
            if (count > bestCount)
            {
                bestCount = count;
                bestAsteroid = asteroid;
            }
        }
        System.out.println(bestAsteroid);
        System.out.println(bestCount);
        asteroids.remove(bestAsteroid);
        Map<Ratio, List<Asteroid>> rankedAsteroids = new HashMap<>();
        for (Asteroid asteroid : asteroids)
        {
            Ratio ratio = bestAsteroid.getRatio(asteroid);
            rankedAsteroids.computeIfAbsent(ratio, r -> new ArrayList<>());
            rankedAsteroids.get(ratio).add(asteroid);
        }
        rankedAsteroids.values().forEach(asteroidList -> asteroidList.sort((a1, a2) -> Integer.compare(bestAsteroid.distance(a1), bestAsteroid.distance(a2))));
        List<Ratio> sortedRatios = new ArrayList<>(rankedAsteroids.keySet());
        sortedRatios.sort((r1, r2) -> r2.compareTo(r1));
        int id = 1;
        while (!asteroids.isEmpty())
        {
            for (Ratio ratio : sortedRatios)
            {
                if (rankedAsteroids.get(ratio).isEmpty())
                {
                    continue;
                }
                Asteroid toRemove = rankedAsteroids.get(ratio).get(0);
                rankedAsteroids.get(ratio).remove(0);
                asteroids.remove(toRemove);
                System.out.println(id++ + " " + toRemove);
            }
        }
        System.out.println(sortedRatios);
        scanner.close();
    }
}
/*
.#....#####...#..
##...##.#####..##
##...#...#.#####.
..#.....#...###..
..#.#.....#....##
*/
