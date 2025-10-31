package tmp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tmp.IntComputer.InputProvider;
import tmp.IntComputer.OutputConsumer;

public class Day15
{
    private static class Address
    {
        private final int x;
        private final int y;
        public Address(int x, int y)
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
            Address other = (Address) obj;
            if (x != other.x) return false;
            if (y != other.y) return false;
            return true;
        }
        @Override
        public String toString()
        {
            return "[ " + x + ", " + y + " ]";
        }
    }
    private static class Tile
    {
        private final boolean wall;
        private int distanceFromStart;
        public Tile(boolean wall, int distanceFromStart)
        {
            this.wall = wall;
            this.distanceFromStart = distanceFromStart;
        }
        public int getDistanceFromStart()
        {
            return distanceFromStart;
        }
        public boolean isWall()
        {
            return wall;
        }
        @Override
        public String toString()
        {
            if (isWall())
            {
                return "#";
            }
            else
            {
                return "" + getDistanceFromStart();
            }
        }

    }
    private static class Drone implements InputProvider, OutputConsumer
    {
        private int input = -1;
        private int output = -1;
        public Drone(List<BigDecimal> droneProgram)
        {
            IntComputer computer = new IntComputer();
            Executors.newSingleThreadExecutor().submit(() -> computer.executeProgram(droneProgram, this, this));
        }
        @Override
        public long generateInput()
        {
            while (input < 0)
            {
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            int newInput = input;
            input = -1;
            return newInput;
        }
        @Override
        public void consumeOutput(String output)
        {
            this.output = Integer.valueOf(output);
        }
        public int go(int direction)
        {
            input = direction;
            while (output < 0)
            {
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            int newOutput = output;
            output = -1;
            return newOutput;
        }
    }
    private static Address targetAddress;
    private static int getReversedMoveOperation(int operation)
    {
        switch(operation)
        {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 4;
            case 4:
                return 3;
        }
        throw new IllegalArgumentException();
    }
    private static void checkAddress(Address address, Map<Address, Tile> knownTiles, int entryOperation, Drone drone, int depth)
    {
        Tile targetTile = knownTiles.get(address);
        if (targetTile == null || (!targetTile.isWall() && targetTile.getDistanceFromStart() > depth+1))
        {
            visit(address, knownTiles, entryOperation, drone, depth+1);
        }
    }
    private static void visit(Address address, Map<Address, Tile> knownTiles, int entryOperation, Drone drone, int depth)
    {
        int tileType = drone.go(entryOperation);
        if (tileType == 0)
        {
            knownTiles.put(address, new Tile(true, depth));
            return;
        }
        knownTiles.put(address, new Tile(false, depth));
        if (tileType == 2)
        {
            targetAddress = address;
        }
        // North
        Address newAddress = new Address(address.getX(), address.getY() -1);
        checkAddress(newAddress, knownTiles, 1, drone, depth);
        // South
        newAddress = new Address(address.getX(), address.getY() +1);
        checkAddress(newAddress, knownTiles, 2, drone, depth);
        // West
        newAddress = new Address(address.getX() -1, address.getY());
        checkAddress(newAddress, knownTiles, 3, drone, depth);
        // East
        newAddress = new Address(address.getX() + 1, address.getY());
        checkAddress(newAddress, knownTiles, 4, drone, depth);
        
        drone.go(getReversedMoveOperation(entryOperation));
    }

    private static boolean findOxygen(Address address, Map<Address, Tile> knownTiles, int entryOperation, Drone drone)
    {
        int tileType = drone.go(entryOperation);
        if (tileType == 0)
        {
            knownTiles.put(address, new Tile(true, 0));
            return false;
        }
        knownTiles.put(address, new Tile(false, 0));
        if (tileType == 2)
        {
            targetAddress = address;
            return true;
        }
        // North
        Address newAddress = new Address(address.getX(), address.getY() -1);
        if (!knownTiles.containsKey(newAddress))
        {
            if (findOxygen(newAddress, knownTiles, 1, drone))
            {
                return true;
            }
        }
        // South
        newAddress = new Address(address.getX(), address.getY() +1);
        if (!knownTiles.containsKey(newAddress))
        {
            if (findOxygen(newAddress, knownTiles, 2, drone))
            {
                return true;
            }
        }

        // West
        newAddress = new Address(address.getX() -1, address.getY());
        if (!knownTiles.containsKey(newAddress))
        {
            if (findOxygen(newAddress, knownTiles, 3, drone))
            {
                return true;
            }
        }

        // East
        newAddress = new Address(address.getX() + 1, address.getY());
        if (!knownTiles.containsKey(newAddress))
        {
            if (findOxygen(newAddress, knownTiles, 4, drone))
            {
                return true;
            }
        }
        
        drone.go(getReversedMoveOperation(entryOperation));
        return false;
    }
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program = Stream.of(scanner.next().split(",")).map(Integer::parseInt).map(BigDecimal::new).collect(Collectors.toList());
        Drone drone = new Drone(program);
        scanner.close();

        Map<Address, Tile> knownTiles = new HashMap<>();
        knownTiles.put(new Address(0, 0), new Tile(false, 0));
        boolean found = false;
        found = findOxygen(new Address(1, 0), knownTiles, 4, drone);
        if (!found)
        {
            found = findOxygen(new Address(0, 1), knownTiles, 1, drone);
        }
        if (!found)
        {
            found = findOxygen(new Address(-1, 0), knownTiles, 3, drone);
        }
        if (!found)
        {
            found = findOxygen(new Address(0, -1), knownTiles, 2, drone);
        }

        int minX = knownTiles.keySet().stream().map(Address::getX).min(Integer::compare).get();
        int maxX = knownTiles.keySet().stream().map(Address::getX).max(Integer::compare).get();
        int minY = knownTiles.keySet().stream().map(Address::getY).min(Integer::compare).get();
        int maxY = knownTiles.keySet().stream().map(Address::getY).max(Integer::compare).get();
        System.out.println(minX);
        System.out.println(maxX);
        System.out.println(minY);
        System.out.println(maxY);
//        System.out.println(knownTiles);
        for (int row = minY ; row <= maxY ; row++)
        {
            for (int col = minX ; col <= maxX ; col++)
            {
                Address address = new Address(row, col);
                if (address.equals(targetAddress))
                {
                    System.out.print("X");
                }
                else if (!knownTiles.containsKey(address) || knownTiles.get(address).isWall())
                {
                    System.out.print("#");
                }
                else if (address.equals(new Address(0, 0)))
                {
                    System.out.print("0");
                }
                else
                {
                    int dist = knownTiles.get(address).getDistanceFromStart();
//                    if (dist < 10)
//                    {
//                        System.out.print(" " + dist);
//                    }
//                    else
//                    {
//                        System.out.print(dist);
//                    }
                    System.out.print(dist % 10);
//                    System.out.print(knownTiles.get(address).getDistanceFromStart());
                }
            }
            System.out.println();
        }
        System.out.println(knownTiles.get(targetAddress));
        knownTiles.clear();
        knownTiles.put(new Address(0, 0), new Tile(false, 0));
        visit(new Address(1, 0), knownTiles, 3, drone, 1);
        visit(new Address(0, 1), knownTiles, 2, drone, 1);
        visit(new Address(-1, 0), knownTiles, 4, drone, 1);
        visit(new Address(0, -1), knownTiles, 1, drone, 1);
        minX = knownTiles.keySet().stream().map(Address::getX).min(Integer::compare).get();
        maxX = knownTiles.keySet().stream().map(Address::getX).max(Integer::compare).get();
        minY = knownTiles.keySet().stream().map(Address::getY).min(Integer::compare).get();
        maxY = knownTiles.keySet().stream().map(Address::getY).max(Integer::compare).get();
        System.out.println(minX);
        System.out.println(maxX);
        System.out.println(minY);
        System.out.println(maxY);

        for (int row = minX ; row <= maxX ; row++)
        {
            for (int col = minY ; col <= maxY ; col++)
            {
                Address address = new Address(row, col);
                if (!knownTiles.containsKey(address) || knownTiles.get(address).isWall())
                {
                    System.out.print("#");
                }
                else if (address.equals(new Address(0, 0)))
                {
                    System.out.print("S");
                }
                else
                {
                    int dist = knownTiles.get(address).getDistanceFromStart();
//                    if (dist < 10)
//                    {
//                        System.out.print(" " + dist);
//                    }
//                    else
//                    {
//                        System.out.print(dist);
//                    }
                    System.out.print(dist % 10);
//                    System.out.print(knownTiles.get(address).getDistanceFromStart());
                }
            }
            System.out.println();
        }
        System.out.println(knownTiles.values().stream().map(Tile::getDistanceFromStart).max(Integer::compare).get());
    }
}
