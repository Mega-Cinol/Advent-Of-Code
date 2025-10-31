package tmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class Day20
{
    private static class Address
    {
        private final int x;
        private final int y;
        private final int z;

        public Address(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + z;
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
            if (z != other.z) return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "[ " + x + ", " + y + ", " + z + " ]";
        }
    }

    private static class Node
    {
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((address == null) ? 0 : address.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Node other = (Node) obj;
            if (address == null)
            {
                if (other.address != null) return false;
            }
            else if (!address.equals(other.address)) return false;
            return true;
        }

        private final Address address;
        private final Set<Node> neighbours = new HashSet<>();
        private String innerPortal = "";
        private String outerPortal = "";

        public Node(Address address)
        {
            this.address = address;
        }
    }

    private static class Portal
    {
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + (inner ? 1231 : 1237);
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Portal other = (Portal) obj;
            if (inner != other.inner) return false;
            if (name == null)
            {
                if (other.name != null) return false;
            }
            else if (!name.equals(other.name)) return false;
            return true;
        }
        private final String name;
        private final boolean inner;
        public Portal(String name, boolean inner)
        {
            this.name = name;
            this.inner = inner;
        }
        @Override
        public String toString()
        {
            return name + " " + (inner ? "inner" : "outer");
        }
    }
    private static class PortalNeighbour
    {
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + distance;
            result = prime * result + ((portal == null) ? 0 : portal.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PortalNeighbour other = (PortalNeighbour) obj;
            if (distance != other.distance) return false;
            if (portal == null)
            {
                if (other.portal != null) return false;
            }
            else if (!portal.equals(other.portal)) return false;
            return true;
        }
        private final Portal portal;
        private final int distance;
        public PortalNeighbour(Portal portal, int distance)
        {
            this.portal = portal;
            this.distance = distance;
        }
        @Override
        public String toString()
        {
            return portal.toString() + " " + distance + " steps";
        }
    }
    private static class PortalWithLevel
    {
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + level;
            result = prime * result + ((portal == null) ? 0 : portal.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PortalWithLevel other = (PortalWithLevel) obj;
            if (level != other.level) return false;
            if (portal == null)
            {
                if (other.portal != null) return false;
            }
            else if (!portal.equals(other.portal)) return false;
            return true;
        }
        private final Portal portal;
        private final int level;
        public PortalWithLevel(Portal portal, int level)
        {
            this.portal = portal;
            this.level = level;
        }
        @Override
        public String toString()
        {
            return portal.toString() + " on level " + level;
        }
    }
    private static void addMazeLevel(Set<Node> maze, int level)
    {
        if (maze.stream().anyMatch(n -> n.address.z == level))
        {
            return;
        }
        Set<Node> newLevel = new HashSet<>();
        maze.stream().filter(n -> n.address.z == 0).forEach(n -> {
            Address newAddress = new Address(n.address.x, n.address.y, level);
            Node newNode = new Node(newAddress);
            newNode.innerPortal = n.innerPortal;
            if (!"AA".equals(n.outerPortal) && !"ZZ".equals(n.outerPortal))
            {
                newNode.outerPortal = n.outerPortal;
            }
            for (Node oldNeighbour : n.neighbours)
            {
                Optional<Node> newNeighbour = newLevel.stream()
                        .filter(nn -> nn.address.x == oldNeighbour.address.x && nn.address.y == oldNeighbour.address.y).findAny();
                newNeighbour.ifPresent(newNeigh -> {
                    newNeigh.neighbours.add(newNode);
                    newNode.neighbours.add(newNeigh);
                });
            }
            newLevel.add(newNode);
        });
        maze.addAll(newLevel);
    }
    private static boolean canEnterPortal(String portal, int level, Map<String, List<Integer>> usedInnerPortals)
    {
        if (level > 30)
        {
            return false;
        }
        List<Integer> usedLevels = usedInnerPortals.get(portal);
        if (usedLevels == null || usedLevels.isEmpty())
        {
            return true;
        }
        if (usedLevels.size() > 2)
        {
            return false;
        }
        return usedLevels.stream().noneMatch(ul -> level % ul == 0);
    }
    private static void findPathLength(Address current, Set<Node> maze, Map<Address, Integer> visited, int depth, Map<String, List<Integer>> usedInnerPortals)
    {
        visited.merge(current, depth, (d1, d2) -> d1 > d2 ? d2 : d1);
        Node currentNode = maze.stream().filter(n -> n.address.equals(current)).findFirst().get();
        for (Node neighbour : currentNode.neighbours)
        {
            if (visited.get(neighbour.address) != null && visited.get(neighbour.address) < depth + 1)
            {
                continue;
            }
            findPathLength(neighbour.address, maze, visited, depth + 1, usedInnerPortals);
        }
        if (!currentNode.outerPortal.isEmpty() && current.z > 0)
        {
            Node nextNode = maze.stream().filter(n -> n.address.z == current.z - 1)
                    .filter(n -> n.innerPortal.equals(currentNode.outerPortal)).findAny().get();
            if (visited.get(nextNode.address) == null || visited.get(nextNode.address) >= depth + 1)
            {
                findPathLength(nextNode.address, maze, visited, depth + 1, usedInnerPortals);
            }
        }
        if (!currentNode.innerPortal.isEmpty())
        {
            addMazeLevel(maze, current.z + 1);
            Node nextNode = maze.stream().filter(n -> n.address.z == current.z + 1)
                    .filter(n -> n.outerPortal.equals(currentNode.innerPortal)).findAny().get();
            if (canEnterPortal(currentNode.innerPortal, current.z + 1, usedInnerPortals))
            {
                if (visited.get(nextNode.address) == null || visited.get(nextNode.address) >= depth + 1)
                {
                    usedInnerPortals.computeIfAbsent(currentNode.innerPortal, k -> new ArrayList<>()).add(current.z + 1);
                    findPathLength(nextNode.address, maze, visited, depth + 1, usedInnerPortals);
                    usedInnerPortals.get(currentNode.innerPortal).remove(new Integer(current.z + 1));
                }
            }
        }
    }

    private static void findPathLength(Portal current, Map<Portal, Set<PortalNeighbour>> portalConnections, int level, Map<String, List<Integer>> usedInnerPortals, Map<PortalWithLevel, Integer> distances, int distance)
    {
        distances.merge(new PortalWithLevel(current, level), distance, (d1, d2) -> d1 > d2 ? d2 : d1);
        if (!portalConnections.containsKey(current))
        {
            System.out.println(current);
        }
        for (PortalNeighbour neighbour : portalConnections.get(current))
        {
            if (neighbour.portal.equals(current))
            {
                continue;
            }
            if (!neighbour.portal.inner && level == 0)
            {
                if (neighbour.portal.name.equals("ZZ"))
                {
                    distances.merge(new PortalWithLevel(neighbour.portal, level), distance + neighbour.distance, (d1, d2) -> d1 > d2 ? d2 : d1);
                    return;
                }
                continue;
            }
            if (neighbour.portal.name.equals("AA") || neighbour.portal.name.equals("ZZ"))
            {
                continue;
            }
            int nextLevel = level + (neighbour.portal.inner ? 1 : -1);
            if (distances.get(new PortalWithLevel(neighbour.portal, nextLevel)) != null && distances.get(new PortalWithLevel(neighbour.portal, nextLevel)) < distance + neighbour.distance)
            {
                continue;
            }
            if (neighbour.portal.inner)
            {
                if (canEnterPortal(neighbour.portal.name, nextLevel, usedInnerPortals))
                {
                    usedInnerPortals.computeIfAbsent(neighbour.portal.name, k -> new ArrayList<>()).add(nextLevel);
                    findPathLength(new Portal(neighbour.portal.name, false), portalConnections, nextLevel, usedInnerPortals, distances, distance + neighbour.distance + 1);
                    usedInnerPortals.get(neighbour.portal.name).remove(new Integer(nextLevel));
                }
            }
            else
            {
                findPathLength(new Portal(neighbour.portal.name, true), portalConnections, nextLevel, usedInnerPortals, distances, distance + neighbour.distance + 1);
            }
        }
    }

    private static void findPortalDistances(Address current, Set<Node> maze, Map<Address, Integer> visited, int depth)
    {
        visited.merge(current, depth, (d1, d2) -> d1 > d2 ? d2 : d1);
        Node currentNode = maze.stream().filter(n -> n.address.equals(current)).findFirst().get();
        for (Node neighbour : currentNode.neighbours)
        {
            if (visited.get(neighbour.address) != null && visited.get(neighbour.address) < depth + 1)
            {
                continue;
            }
            findPortalDistances(neighbour.address, maze, visited, depth + 1);
        }
    }
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        while (scanner.hasNext())
        {
            String line = scanner.nextLine();
            if ("dupa".equals(line))
            {
                break;
            }
            lines.add(line);
        }
        scanner.close();
        Set<Node> nodes = new HashSet<>();
        for (int y = 2 ; y < lines.size() ; y++)
        {
            String line = lines.get(y);
            for (int x = 2 ; x < line.length() ; x++)
            {
                if (line.charAt(x) == '.')
                {
                    Node newNode = new Node(new Address(x, y, 0));
                    if (lines.get(y - 1).charAt(x) == '.')
                    {
                        final int tempX = x;
                        final int tempY = y - 1;
                        Node newNeighbour = nodes.stream().filter(n -> n.address.equals(new Address(tempX, tempY, 0))).findAny().get();
                        newNeighbour.neighbours.add(newNode);
                        newNode.neighbours.add(newNeighbour);
                    }
                    if (lines.get(y).charAt(x - 1) == '.')
                    {
                        final int tempX = x - 1;
                        final int tempY = y;
                        Node newNeighbour = nodes.stream().filter(n -> n.address.equals(new Address(tempX, tempY, 0))).findAny().get();
                        newNeighbour.neighbours.add(newNode);
                        newNode.neighbours.add(newNeighbour);
                    }
                    if (lines.get(y - 1).charAt(x) >= 'A' && lines.get(y - 1).charAt(x) <= 'Z')
                    {
                        String portalName = "" + lines.get(y - 2).charAt(x) + lines.get(y - 1).charAt(x);
                        if (y == 2)
                        {
                            newNode.outerPortal = portalName;
                        }
                        else
                        {
                            newNode.innerPortal = portalName;
                        }
                    }
                    if (lines.get(y + 1).charAt(x) >= 'A' && lines.get(y + 1).charAt(x) <= 'Z')
                    {
                        String portalName = "" + lines.get(y + 1).charAt(x) + lines.get(y + 2).charAt(x);
                        if (y == lines.size() - 3)
                        {
                            newNode.outerPortal = portalName;
                        }
                        else
                        {
                            newNode.innerPortal = portalName;
                        }
                    }
                    if (lines.get(y).charAt(x - 1) >= 'A' && lines.get(y).charAt(x - 1) <= 'Z')
                    {
                        String portalName = "" + lines.get(y).charAt(x - 2) + lines.get(y).charAt(x - 1);
                        if (x == 2)
                        {
                            newNode.outerPortal = portalName;
                        }
                        else
                        {
                            newNode.innerPortal = portalName;
                        }
                    }
                    if (lines.get(y).charAt(x + 1) >= 'A' && lines.get(y).charAt(x + 1) <= 'Z')
                    {
                        String portalName = "" + lines.get(y).charAt(x + 1) + lines.get(y).charAt(x + 2);
                        if (x == lines.get(y).length() - 3)
                        {
                            newNode.outerPortal = portalName;
                        }
                        else
                        {
                            newNode.innerPortal = portalName;
                        }
                    }
                    nodes.add(newNode);
                }
            }
        }
        Node startNode = nodes.stream().filter(n -> "AA".equals(n.outerPortal)).findAny().get();
        Node endNode = nodes.stream().filter(n -> "ZZ".equals(n.outerPortal)).findAny().get();
        System.out.println(startNode.address);
        System.out.println(endNode.address);
        Map<Portal, Set<PortalNeighbour>> portalConnections = new HashMap<>();
        nodes.stream().filter(n -> !n.outerPortal.isEmpty()).forEach(n -> {
            Portal portal = new Portal(n.outerPortal, false);
            Map<Address, Integer> distances = new HashMap<>();
            findPortalDistances(n.address, nodes, distances, 0);
            distances.entrySet().stream().filter(e -> {
                Node node = nodes.stream().filter(nn -> nn.address.equals(e.getKey())).findAny().get();
                return !node.innerPortal.isEmpty() || !node.outerPortal.isEmpty();
            })
            .forEach(e -> {
                Node node = nodes.stream().filter(nn -> nn.address.equals(e.getKey())).findAny().get();
                boolean inner = !node.innerPortal.isEmpty();
                Portal p = new Portal(inner ? node.innerPortal : node.outerPortal, inner);
                PortalNeighbour pn = new PortalNeighbour(p, e.getValue());
                portalConnections.computeIfAbsent(portal, k -> new HashSet<>()).add(pn);
            });
        });
        nodes.stream().filter(n -> !n.innerPortal.isEmpty()).forEach(n -> {
            Portal portal = new Portal(n.innerPortal, true);
            Map<Address, Integer> distances = new HashMap<>();
            findPortalDistances(n.address, nodes, distances, 0);
            distances.entrySet().stream().filter(e -> {
                Node node = nodes.stream().filter(nn -> nn.address.equals(e.getKey())).findAny().get();
                return !node.innerPortal.isEmpty() || !node.outerPortal.isEmpty();
            })
            .forEach(e -> {
                Node node = nodes.stream().filter(nn -> nn.address.equals(e.getKey())).findAny().get();
                boolean inner = !node.innerPortal.isEmpty();
                Portal p = new Portal(inner ? node.innerPortal : node.outerPortal, inner);
                PortalNeighbour pn = new PortalNeighbour(p, e.getValue());
                portalConnections.computeIfAbsent(portal, k -> new HashSet<>()).add(pn);
            });
        });
        System.out.println(portalConnections);
        Portal startPortal = new Portal("AA", false);
        Portal endPortal = new Portal("ZZ", false);
        Map<PortalWithLevel, Integer> distances = new HashMap<>();
        findPathLength(startPortal, portalConnections, 0, new HashMap<>(), distances, 0);
//        Map<Address, Integer> distances = new HashMap<>();
//        findPathLength(startNode.address, nodes, distances, 0, new HashMap<>());
//        distances.keySet().stream().filter(k -> k.z == 0).forEach(System.out::println);
        System.out.println(distances.get(new PortalWithLevel(endPortal, 0)));
    }
}
