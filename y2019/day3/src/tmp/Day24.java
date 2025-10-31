package tmp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day24
{
    private static class Level
    {
        Set<Integer> bugs;
        Level outerLevel;
        Level innerLevel;

        private long countBugs()
        {
            Level current = this;
            while (current.outerLevel != null)
            {
                current = current.outerLevel;
            }
            long result = 0;
            while (current.innerLevel != null)
            {
                result += current.bugs.size();
                current = current.innerLevel;
            }
            result += current.bugs.size();
            return result;
        }
    }

    private static int boolToInt(boolean value)
    {
        return value ? 1 : 0;
    }

    private static int neighbours(int index, Set<Integer> current, Set<Integer> outer, Set<Integer> inner)
    {
        // Top Edge
        if (index == 0)
        {
            return boolToInt(outer.contains(7)) + boolToInt(outer.contains(11)) + boolToInt(current.contains(1))
                    + boolToInt(current.contains(5));
        }
        if (index > 0 && index < 4)
        {
            return boolToInt(outer.contains(7)) + boolToInt(current.contains(index - 1))
                    + boolToInt(current.contains(index + 1)) + boolToInt(current.contains(index + 5));
        }
        if (index == 4)
        {
            return boolToInt(outer.contains(7)) + boolToInt(outer.contains(13)) + boolToInt(current.contains(3))
                    + boolToInt(current.contains(9));
        }
        // Bottom edge
        if (index == 20)
        {
            return boolToInt(outer.contains(17)) + boolToInt(outer.contains(11)) + boolToInt(current.contains(21))
                    + boolToInt(current.contains(15));
        }
        if (index > 20 && index < 24)
        {
            return boolToInt(outer.contains(17)) + boolToInt(current.contains(index - 1))
                    + boolToInt(current.contains(index + 1)) + boolToInt(current.contains(index - 5));
        }
        if (index == 24)
        {
            return boolToInt(outer.contains(17)) + boolToInt(outer.contains(13)) + boolToInt(current.contains(23))
                    + boolToInt(current.contains(19));
        }
        // Left edge
        if (index % 5 == 0)
        {
            return boolToInt(outer.contains(11)) + boolToInt(current.contains(index + 1))
                    + boolToInt(current.contains(index - 5)) + boolToInt(current.contains(index + 5));
        }
        // Right edge
        if (index % 5 == 4)
        {
            return boolToInt(outer.contains(13)) + boolToInt(current.contains(index - 1))
                    + boolToInt(current.contains(index - 5)) + boolToInt(current.contains(index + 5));
        }
        if (index == 7)
        {
            int neights = 0;
            for (int i = 0; i < 5; i++)
            {
                neights += boolToInt(inner.contains(i));
            }
            return neights + boolToInt(current.contains(6)) + boolToInt(current.contains(8))
                    + boolToInt(current.contains(2));
        }
        if (index == 11)
        {
            int neights = 0;
            for (int i = 0; i < 25; i += 5)
            {
                neights += boolToInt(inner.contains(i));
            }
            return neights + boolToInt(current.contains(10)) + boolToInt(current.contains(6))
                    + boolToInt(current.contains(16));
        }
        if (index == 13)
        {
            int neights = 0;
            for (int i = 4; i < 25; i += 5)
            {
                neights += boolToInt(inner.contains(i));
            }
            return neights + boolToInt(current.contains(14)) + boolToInt(current.contains(8))
                    + boolToInt(current.contains(18));
        }
        if (index == 17)
        {
            int neights = 0;
            for (int i = 20; i < 25; i++)
            {
                neights += boolToInt(inner.contains(i));
            }
            return neights + boolToInt(current.contains(16)) + boolToInt(current.contains(18))
                    + boolToInt(current.contains(22));
        }
        return boolToInt(current.contains(index + 1)) + boolToInt(current.contains(index - 1))
                + boolToInt(current.contains(index + 5)) + boolToInt(current.contains(index - 5));
    }

    private static Set<Integer> mutate(Set<Integer> current, Set<Integer> outer, Set<Integer> inner)
    {
        Set<Integer> next = new HashSet<>();
        for (int i = 0; i < 25; i++)
        {
            if (i == 12)
            {
                continue;
            }
            int neights = neighbours(i, current, outer, inner);
            if (current.contains(i) && neights == 1)
            {
                next.add(i);
            }
            if (!current.contains(i) && (neights == 1 || neights == 2))
            {
                next.add(i);
            }
        }
        return next;
    }

    private static Level nextStep(Level current)
    {
        while (current.outerLevel != null)
        {
            current = current.outerLevel;
        }
        System.out.println("Found outermost level");
        List<Set<Integer>> newLevels = new ArrayList<>();
        newLevels.add(mutate(new HashSet<>(), new HashSet<>(), current.bugs));
        if (newLevels.get(0).isEmpty())
        {
            newLevels.remove(0);
        }
        while (current.innerLevel != null)
        {
            newLevels.add(
                    mutate(current.bugs, current.outerLevel == null ? new HashSet<>() : current.outerLevel.bugs,
                            current.innerLevel != null ? current.innerLevel.bugs : new HashSet<>()));
            current = current.innerLevel;
        }
        System.out.println("Found innermost level");
        newLevels.add(
                mutate(current.bugs, current.outerLevel == null ? new HashSet<>() : current.outerLevel.bugs, new HashSet<>()));
        newLevels.add(mutate(new HashSet<>(), current.bugs, new HashSet<>()));
        if (newLevels.get(newLevels.size() - 1).isEmpty())
        {
            newLevels.remove(newLevels.size() - 1);
        }
        Level previous = null;
        Level someLevel = null;
        for (Set<Integer> levelBugs : newLevels)
        {
            someLevel = new Level();
            printBugs(levelBugs);
            someLevel.bugs = levelBugs;
            someLevel.outerLevel = previous;
            previous = someLevel;
            if (someLevel.outerLevel != null)
            {
                someLevel.outerLevel.innerLevel = someLevel;
            }
        }
        return someLevel;
    }

    private static long biodiversity(Set<Integer> input)
    {
        long result = 0;
        for (Integer bug : input)
        {
            result += Math.pow(2, bug);
        }
        return result;
    }

    private static void printBugs(Set<Integer> bugs)
    {
        System.out.println("=======");
        for (int i = 0; i < 25; i++)
        {
            System.out.print(bugs.contains(i) ? "#" : ".");
            if (i % 5 == 4)
            {
                System.out.println();
            }
        }
        System.out.println("=======");
    }

    public static void main(String[] args)
    {
        Set<Integer> bugs = new HashSet<>();
        bugs.add(1);
        bugs.add(3);
        bugs.add(6);
        bugs.add(7);
        bugs.add(11);
        bugs.add(16);
        bugs.add(17);
        bugs.add(18);
        bugs.add(20);
        bugs.add(21);
        bugs.add(24);

//        bugs.add(4);
//        bugs.add(5);
//        bugs.add(8);
//        bugs.add(10);
//        bugs.add(13);
//        bugs.add(14);
//        bugs.add(17);
//        bugs.add(20);
        printBugs(bugs);
        Level level = new Level();
        level.bugs = bugs;
        for (int i = 0; i < 200; i++)
        {
            level = nextStep(level);
        }
        System.out.println(level.countBugs());
        //        System.out.println(level.countBugs());
        //        Set<Long> layouts = new HashSet<>();
        //        boolean found = false;
        //        while (!found)
        //        {
        //            found = !layouts.add(biodiversity(bugs));
        //            if (!found)
        //            {
        //                bugs = mutate(bugs);
        //            }
        //        }
        //        printBugs(bugs);
        //        System.out.println(biodiversity(bugs));
    }

}
