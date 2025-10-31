package tmp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tmp.IntComputer.OutputConsumer;

public class Day11
{
    private static enum Direction
    {
        LEFT(-1, 0, "UP", "DOWN"),
        UP(0, 1, "RIGHT", "LEFT"),
        RIGHT(1, 0, "DOWN", "UP"),
        DOWN(0, -1, "LEFT", "RIGHT");

        private final int dx;
        private final int dy;
        private final String next;
        private final String previous;

        private Direction(int dx, int dy, String next, String previous)
        {
            this.dx = dx;
            this.dy = dy;
            this.next = next;
            this.previous = previous;
        }
        public int getDx()
        {
            return dx;
        }
        public int getDy()
        {
            return dy;
        }
        public Direction rotate(int direction)
        {
            if (direction == 1)
            {
                return Direction.valueOf(next);
            }
            else
            {
                return Direction.valueOf(previous);
            }
        }
    }
    private static class Tile
    {
        private final int x;
        private final int y;

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public Tile(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public Tile offset(int dx, int dy)
        {
            return new Tile(x + dx, y + dy);
        }
        public Tile offset(Direction d)
        {
            return offset(d.getDx(), d.getDy());
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
            Tile other = (Tile) obj;
            if (x != other.x) return false;
            if (y != other.y) return false;
            return true;
        }
        
    }
    private static class Robot
    {
        private final Map<Tile, Integer> visitedTiles = new HashMap<>();
        private Tile currentTile = new Tile(0,0);
        private Direction direction = Direction.UP;
        public Robot()
        {
            visitedTiles.put(currentTile, 1);
        }
        public int getCurrentTileColor()
        {
            return visitedTiles.get(currentTile);
        }
        public void paintCurrentTile(int color)
        {
            visitedTiles.put(currentTile, color);
        }
        public void turnAndMove(int newDirection)
        {
            direction = direction.rotate(newDirection);
            currentTile = currentTile.offset(direction);
            if (!visitedTiles.containsKey(currentTile))
            {
                visitedTiles.put(currentTile, 0);
            }
        }
        public int getVisitedTilesCount()
        {
            return visitedTiles.size();
        }
        public void print()
        {
            int minX = visitedTiles.keySet().stream().map(Tile::getX).min(Integer::compare).get();
            int maxX = visitedTiles.keySet().stream().map(Tile::getX).max(Integer::compare).get();
            int minY = visitedTiles.keySet().stream().map(Tile::getY).min(Integer::compare).get();
            int maxY = visitedTiles.keySet().stream().map(Tile::getY).max(Integer::compare).get();
            for (int col = minY ; col <= maxY ; col++)
            {
                for (int row = minX ; row <= maxX ; row++)
                {
                    Tile tile = new Tile(row, col);
                    Integer color = visitedTiles.get(tile);
                    if (color == null)
                    {
                        System.out.print(' ');
                    }
                    else if (color == 1)
                    {
                        System.out.print('#');
                    }
                    else
                    {
                        System.out.print(' ');
                    }
                }
                System.out.println();
            }
        }
    }
    public static void main(String[] args)
    {
        Robot robot = new Robot();

        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program = Stream.of(scanner.next().split(",")).map(BigDecimal::new).collect(Collectors.toList());

        IntComputer computer = new IntComputer();
//        computer.executeProgram(program, ()-> Integer.valueOf(scanner.next()), System.out::println); 
        computer.executeProgram(program, () -> {
            return robot.getCurrentTileColor();
        }, new OutputConsumer()
        {
            private boolean state = false;
            @Override
            public void consumeOutput(String output)
            {
                int intOutput = Integer.valueOf(output);
                if (state)
                {
                    robot.turnAndMove(intOutput);
                }
                else
                {
                    robot.paintCurrentTile(intOutput);
                }
                state = !state;
            }
        });

        scanner.close();
        System.out.println(robot.getVisitedTilesCount());
        robot.print();
    }

}
