package tmp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public class Day18
{
    private static class Graph
    {
        private final Map<Edge, Integer> edges = new HashMap<>();
        public void addEdge(Address node1, Address node2, Set<Character> doors, int distance)
        {
            edges.put(new Edge(node1, node2, doors), distance);
        }
        private boolean isStartEdge(Edge edge, Map<Address, Tile> maze)
        {
            return maze.get(edge.node1).getWithKey() == null || maze.get(edge.node2).getWithKey() == null;
        }
        public Edge mergeEdges(Edge e1, Edge e2, Map<Address, Tile> maze)
        {
            Address key1 = maze.get(e1.node1).getWithKey() != null ? e1.node1 : e1.node2;
            Address key2 = maze.get(e2.node1).getWithKey() != null ? e2.node1 : e2.node2;
            Set<Character> doors = new HashSet<>();
            doors.addAll(e1.doors);
            doors.addAll(e2.doors);
            return new Edge(key1, key2, doors);
        }
        public void addGraph(Graph otherGraph, Map<Address, Tile> maze, int centerDistance)
        {
            Set<Edge> myStartEdges = edges.keySet().stream()
                    .filter(e -> isStartEdge(e, maze))
                    .collect(Collectors.toSet());
            Set<Edge> otherStartEdges = otherGraph.edges.keySet().stream()
                    .filter(e -> isStartEdge(e, maze))
                    .collect(Collectors.toSet());
            for (Edge myEdge : myStartEdges)
            {
                for (Edge otherEdge : otherStartEdges)
                {
                    edges.put(mergeEdges(myEdge, otherEdge, maze), edges.get(myEdge) + otherGraph.edges.get(otherEdge) + centerDistance);
                }
            }
            otherGraph.edges.entrySet().stream()
                    .forEach(e -> addEdge(e.getKey().node1, e.getKey().node2, e.getKey().doors, e.getValue()));
        }
        private static class Edge
        {
            private final Set<Character> doors = new HashSet<>();
            private final Address node1;
            public Set<Character> getDoors()
            {
                return doors;
            }
            public Address getAddressWithKey(Map<Address, Tile> maze)
            {
                if (maze.get(node1).getWithKey() != null)
                {
                    return node1;
                }
                return node2;
            }
            private final Address node2;
            public Edge(Address node1, Address node2, Set<Character> doors)
            {
                this.node1 = node1;
                this.node2 = node2;
                doors.stream()
                        .map(door -> "" + door)
                        .map(String::toLowerCase)
                        .map(doorStr -> doorStr.charAt(0))
                        .forEach(this.doors::add);
            }
            @Override
            public int hashCode()
            {
                return node1.hashCode() + node2.hashCode();
            }
            @Override
            public boolean equals(Object obj)
            {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                Edge other = (Edge) obj;
                if (!node1.equals(other.node1) && !node1.equals(other.node2)) return false;
                if (!node2.equals(other.node1) && !node2.equals(other.node2)) return false;
                return true;
            }
        }
        private String nodeToStr(Address address, Map<Address, Tile> maze)
        {
            if (maze.get(address).getWithKey() != null)
            {
                return "" + maze.get(address).getWithKey();
            }
            return "@";
        }
        public void print(Map<Address, Tile> maze)
        {
            for (Map.Entry<Edge, Integer> edge : edges.entrySet())
            {
                System.out.println("From " + nodeToStr(edge.getKey().node1, maze) + " to " + nodeToStr(edge.getKey().node2, maze) + " " + edge.getValue() + " " + edge.getKey().doors);
            }
        }
    }
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

    private static int keysCount = 0;
    private static class Tile
    {
        private final Character requiredKey;
        private final Character withKey;

        public Tile(Character requiredKey, Character withKey)
        {
            this.requiredKey = requiredKey;
            this.withKey = withKey;
            if (withKey != null)
            {
                keysCount++;
            }
        }

        public Character getRequiredKey()
        {
            return requiredKey;
        }

        public Character getWithKey()
        {
            return withKey;
        }

        @Override
        public String toString()
        {
            return "Tile" + (requiredKey == null ? "" : " requiredKey: " + requiredKey) + (withKey == null ? "" : " withKey: " + withKey);
        }
    }


    private static boolean hasCycles(Address parentAddress,
                                     Address address,
                                     Map<Address, Tile> maze,
                                     IntPredicate xRange,
                                     IntPredicate yRange,
                                     Address lastKey,
                                     int depth,
                                     Set<Character> doors,
                                     Graph result)
    {
        if (maze.get(address).getWithKey() != null)
        {
            result.addEdge(lastKey, address, doors, depth);
            lastKey = address;
            doors = new HashSet<>();
            depth = 0;
        }
        if (maze.get(address).getRequiredKey() != null)
        {
            doors.add(maze.get(address).getRequiredKey());
        }

        Address newAddress = new Address(address.getX(), address.getY() - 1);
        if (maze.containsKey(newAddress) && !newAddress.equals(parentAddress) && xRange.test(newAddress.getX()) && yRange.test(newAddress.getY()))
        {
            hasCycles(address, newAddress, maze, xRange, yRange, lastKey, depth + 1, doors, result);
        }
        // South
        newAddress = new Address(address.getX(), address.getY() + 1);
        if (maze.containsKey(newAddress) && !newAddress.equals(parentAddress) && xRange.test(newAddress.getX()) && yRange.test(newAddress.getY()))
        {
            hasCycles(address, newAddress, maze, xRange, yRange, lastKey, depth + 1, doors, result);
        }
        // West
        newAddress = new Address(address.getX() - 1, address.getY());
        if (maze.containsKey(newAddress) && !newAddress.equals(parentAddress) && xRange.test(newAddress.getX()) && yRange.test(newAddress.getY()))
        {
            hasCycles(address, newAddress, maze, xRange, yRange, lastKey, depth + 1, doors, result);
        }
        // East
        newAddress = new Address(address.getX() + 1, address.getY());
        if (maze.containsKey(newAddress) && !newAddress.equals(parentAddress) && xRange.test(newAddress.getX()) && yRange.test(newAddress.getY()))
        {
            hasCycles(address, newAddress, maze, xRange, yRange, lastKey, depth + 1, doors, result);
        }
        if (maze.get(address).getRequiredKey() != null)
        {
            doors.remove(maze.get(address).getRequiredKey());
        }

        return true;
    }

    private static void findKeysToVisit(Address current, Graph graph, Map<Address, Tile> maze, Set<Character> ownedKeys, Map<Address, Integer> result, Set<Address> checkedAddresses, int distance)
    {
        checkedAddresses.add(current);
        Set<Graph.Edge> neighbours = graph.edges.keySet()
                .stream()
                .filter(e -> e.node1.equals(current) || e.node2.equals(current))
                .collect(Collectors.toSet());
        for (Graph.Edge neighbour : neighbours)
        {
            Address otherAddress = neighbour.node1.equals(current) ? neighbour.node2 : neighbour.node1;
            if (maze.get(otherAddress).getWithKey() == null)
            {
                continue;
            }
            if (ownedKeys.contains(maze.get(otherAddress).getWithKey()) && !checkedAddresses.contains(otherAddress))
            {
                findKeysToVisit(otherAddress, graph, maze, ownedKeys, result, checkedAddresses, distance + graph.edges.get(neighbour));
            }
            else if (ownedKeys.containsAll(neighbour.getDoors()) && !checkedAddresses.contains(otherAddress))
            {
                result.put(otherAddress, graph.edges.get(neighbour) + distance);
            }
        }

    }

    private static int findShortestPath(Address startAddress, Graph graph, Set<Character> ownedKeys, Map<Address, Tile> maze)
    {
        if (maze.get(startAddress).getWithKey() != null)
        {
            System.out.println("On address: " + startAddress + " , key: " + maze.get(startAddress).getWithKey());
            ownedKeys.add(maze.get(startAddress).getWithKey());
        }
        else
        {
            System.out.println("On initial position");
        }
        if (ownedKeys.size() == 26)
        {
            ownedKeys.remove(maze.get(startAddress).getWithKey());
            return 0;
        }
        Map<Address, Integer> toVisit = new HashMap<>();
        findKeysToVisit(startAddress, graph, maze, ownedKeys, toVisit, new HashSet<>(), 0);
        for (Map.Entry<Address, Integer> itemToVisit : toVisit.entrySet())
        {
            System.out.println("Can visit " + itemToVisit.getKey() + " with key " + maze.get(itemToVisit.getKey()).getWithKey());
        }
        int result = Integer.MAX_VALUE / 2;
        for (Map.Entry<Address, Integer> itemToVisit : toVisit.entrySet())
        {
            int dist = findShortestPath(itemToVisit.getKey(), graph, ownedKeys, maze) + itemToVisit.getValue();
            if (dist < result)
            {
                result = dist;
            }
        }
        if (maze.get(startAddress).getWithKey() != null)
        {
            ownedKeys.remove(maze.get(startAddress).getWithKey());
        }
        return result;
    }

    private static Graph mergeGraphs(Graph topGraph, Graph bottomGraphs, Map<Address, Tile> maze)
    {
        Graph resultGraph = new Graph();
        topGraph.edges.entrySet().stream()
                .filter(e -> !topGraph.isStartEdge(e.getKey(), maze))
                .forEach(e -> resultGraph.addEdge(e.getKey().node1, e.getKey().node2, e.getKey().doors, e.getValue()));
        bottomGraphs.edges.entrySet().stream()
                .filter(e -> !bottomGraphs.isStartEdge(e.getKey(), maze))
                .forEach(e -> resultGraph.addEdge(e.getKey().node1, e.getKey().node2, e.getKey().doors, e.getValue()));

        Set<Graph.Edge> topStartEdges = topGraph.edges.keySet().stream()
                .filter(e -> topGraph.isStartEdge(e, maze))
                .collect(Collectors.toSet());
        Set<Graph.Edge> bottomStartEdges = bottomGraphs.edges.keySet().stream()
                .filter(e -> bottomGraphs.isStartEdge(e, maze))
                .collect(Collectors.toSet());
        for (Graph.Edge topEdge : topStartEdges)
        {
            for (Graph.Edge bottomEdge : bottomStartEdges)
            {
                Graph.Edge commonEdge = resultGraph.mergeEdges(topEdge, bottomEdge, maze);
                int centerDistance = 4;
                if ((topEdge.node1.x > 40 && bottomEdge.node1.x > 40) || (topEdge.node1.x < 40 && bottomEdge.node1.x < 40))
                {
                    centerDistance = 2;
                }
                resultGraph.edges.put(commonEdge, topGraph.edges.get(topEdge) + bottomGraphs.edges.get(bottomEdge) + centerDistance);
                Address topKey = topEdge.getAddressWithKey(maze);
                resultGraph.addEdge(topKey, new Address(40, 40), topEdge.doors, topGraph.edges.get(topEdge) + 2);
                Address bottomKey = bottomEdge.getAddressWithKey(maze);
                resultGraph.addEdge(bottomKey, new Address(40, 40), bottomEdge.doors, bottomGraphs.edges.get(bottomEdge) + 2);
            }
        }
        return resultGraph;
    }

    private static int pathLength(Address current, Address to, Map<Address, Tile> maze, int depth, Set<Address> visited)
    {
        if (current.equals(to))
        {
            return depth;
        }
        visited.add(current);
        int result = Integer.MAX_VALUE;
        // North
        Address newAddress = new Address(current.getX(), current.getY() - 1);
        if (maze.containsKey(newAddress) && !visited.contains(newAddress))
        {
            int tmp = pathLength(newAddress, to, maze, depth + 1, visited);
            if (tmp < result)
            {
                result = tmp;
            }
        }
        // South
        newAddress = new Address(current.getX(), current.getY() + 1);
        if (maze.containsKey(newAddress) && !visited.contains(newAddress))
        {
            int tmp = pathLength(newAddress, to, maze, depth + 1, visited);
            if (tmp < result)
            {
                result = tmp;
            }
        }
        // West
        newAddress = new Address(current.getX() - 1, current.getY());
        if (maze.containsKey(newAddress) && !visited.contains(newAddress))
        {
            int tmp = pathLength(newAddress, to, maze, depth + 1, visited);
            if (tmp < result)
            {
                result = tmp;
            }
        }
        // East
        newAddress = new Address(current.getX() + 1, current.getY());
        if (maze.containsKey(newAddress) && !visited.contains(newAddress))
        {
            int tmp = pathLength(newAddress, to, maze, depth + 1, visited);
            if (tmp < result)
            {
                result = tmp;
            }
        }
        visited.remove(current);
        return result;
    }
    private static Address findKeyAddress(char key, Map<Address, Tile> maze)
    {
        return maze.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getWithKey() != null && entry.getValue().getWithKey().equals(key))
                .findAny()
                .get()
                .getKey();
    }
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Map<Address, Tile> maze = new HashMap<>();
        int y = 0;
        Address startAdress = null;
        while(scanner.hasNext())
        {
            String line = scanner.nextLine();
            if ("dupa".equals(line))
            {
                break;
            }
            for (int x = 0 ; x < line.length() ; x++)
            {
                char tileChar = line.charAt(x);
                if ('#' == tileChar)
                {
                    continue;
                }
                Character withKey = tileChar >= 'a' && tileChar <= 'z' ? tileChar : null;
                    
                Character requiredKey = tileChar >= 'A' && tileChar <= 'Z' ? tileChar : null;
                Tile tile = new Tile(requiredKey, withKey);
                maze.put(new Address(x, y), tile);
                if (tileChar == '@')
                {
                    startAdress = new Address(x, y);
                }
            }
            y++;
        }
        scanner.close();
        System.out.println("KeysCount " + keysCount);
        System.out.println("Start from: " + startAdress);
        System.out.println(maze);
        Graph graph = new Graph();
        hasCycles(null, new Address(39, 41), maze, x -> x < 40, yy -> yy > 40, new Address(39, 41), 0, new HashSet<>(), graph);
        graph.print(maze);
        System.out.println("==============");
        Graph tempGraph = new Graph();
        Graph tempGraph2 = new Graph();
        hasCycles(null, new Address(41, 41), maze, x -> x > 40, yy -> yy > 40, new Address(41, 41), 0, new HashSet<>(), tempGraph);
        tempGraph.print(maze);
        System.out.println("==============");
        graph.addGraph(tempGraph, maze, 2);
        tempGraph = new Graph();
        hasCycles(null, new Address(39, 39), maze, x -> x < 40, yy -> yy < 40, new Address(39, 39), 0, new HashSet<>(), tempGraph);
        tempGraph.print(maze);
        System.out.println("==============");
        hasCycles(null, new Address(41, 39), maze, x -> x > 40, yy -> yy < 40, new Address(41, 39), 0, new HashSet<>(), tempGraph2);
        tempGraph2.print(maze);
        System.out.println("==============");
        tempGraph.addGraph(tempGraph2, maze, 2);
        graph = mergeGraphs(graph, tempGraph, maze);
        graph.print(maze);
//        System.out.println(findShortestPath(new Address(40, 40), graph, new HashSet<>(), maze));
        int result = 0;
        result += pathLength(new Address(41, 39), findKeyAddress('v', maze), maze, 0, new HashSet<>());
        result += pathLength(findKeyAddress('v', maze), findKeyAddress('r', maze), maze, 0, new HashSet<>());
        result += pathLength(findKeyAddress('r', maze), findKeyAddress('g', maze), maze, 0, new HashSet<>());

        result += pathLength(new Address(41, 41), findKeyAddress('b', maze), maze, 0, new HashSet<>());
        result += pathLength(findKeyAddress('b', maze), findKeyAddress('o', maze), maze, 0, new HashSet<>());

        result += pathLength(new Address(39, 41), findKeyAddress('s', maze), maze, 0, new HashSet<>());
        result += pathLength(findKeyAddress('s', maze), findKeyAddress('w', maze), maze, 0, new HashSet<>());

        result += pathLength(new Address(39, 39), findKeyAddress('n', maze), maze, 0, new HashSet<>());
        result += pathLength(findKeyAddress('n', maze), findKeyAddress('a', maze), maze, 0, new HashSet<>());
        result += pathLength(findKeyAddress('a', maze), findKeyAddress('u', maze), maze, 0, new HashSet<>());

        System.out.println(result);
    }
}
