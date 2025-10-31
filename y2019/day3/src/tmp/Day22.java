package tmp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day22
{
    private static void dealWithInc(int inc, List<Long> deck)
    {
        List<Long> old = new ArrayList<>();
        old.addAll(deck);
        for (int i = 0; i < old.size(); i++)
        {
            deck.set(i * inc % old.size(), old.get(i));
        }
    }

    private static void cut(int number, List<Long> deck)
    {
        number = number > 0 ? number : deck.size() + number;
        List<Long> old = new ArrayList<>();
        old.addAll(deck);
        deck.clear();
        for (int i = number; i < old.size(); i++)
        {
            deck.add(old.get(i));
        }
        for (int i = 0; i < number; i++)
        {
            deck.add(old.get(i));
        }
    }

    public static void main(String[] args)
    {
        long size = 119_315_717_514_047L;
//                long size = 10007;
//        List<Long> deck = LongStream.range(0, size).boxed().collect(Collectors.toList());
        Scanner scanner = new Scanner(System.in);
        List<String> cmds = new ArrayList<String>();
        while (scanner.hasNext())
        {
            String cmd = scanner.nextLine();
            if (cmd.equals("dupa"))
            {
                break;
            }
            cmds.add(cmd);
        }
        scanner.close();
        System.out.println("Start calc");
        Collections.reverse(cmds);
        long current = 0;
        long next = 1;
        long repeat = 27;
                        repeat = 101_741_582_076_661L;
//        for (long i = 0 ;i < repeat ; i++)
//        {
//            for (String cmd : cmds)
//            {
//                if ("deal into new stack".equals(cmd))
//                {
//                    Collections.reverse(deck);
//                }
//                if (cmd.startsWith("cut "))
//                {
//                    cut(Integer.parseInt(cmd.substring(4)), deck);
//                }
//                if (cmd.startsWith("deal with increment "))
//                {
//                    dealWithInc(Integer.parseInt(cmd.substring("deal with increment ".length())), deck);
//                }
//            }
//            System.out.println("=======");
//            deck.stream().limit(2).forEach(System.out::println);
//            System.out.println("=======");
//        }
//        for (long i = 0; i < repeat; i++)
//        {
//            for (String cmd : cmds)
//            {
//                if ("deal into new stack".equals(cmd))
//                {
//                    current = size - current - 1;
//                    next = size - next - 1;
//                }
//                if (cmd.startsWith("cut "))
//                {
//                    current += Integer.parseInt(cmd.substring(4));
//                    current %= size;
//                    next += Integer.parseInt(cmd.substring(4));
//                    next %= size;
//                }
//                if (cmd.startsWith("deal with increment "))
//                {
//                    int inc = Integer.parseInt(cmd.substring("deal with increment ".length()));
////                    System.out.println("old current: " + current + " inc " + inc);
//                    long n = 0;
//                    while ((n * size + current) % inc != 0)
//                    {
//                        n++;
//                    }
//                    current = (n * size + current) / inc;
//                    n = 0;
//                    while ((n * size + next) % inc != 0)
//                    {
//                        n++;
//                    }
//                    next = (n * size + next) / inc;
////                    System.out.println("new current: " + current);
//                    //                dealWithInc(Integer.parseInt(cmd.substring("deal with increment ".length())), deck);
//                }
//            }
//        }
        System.out.println("After loop (8), elements 0 and 1:");
        System.out.println(current);
        System.out.println(next);
        System.out.println("Calculated, elements 0 and 1:");
//        long gap2 = 94813818455880L - 90909137492982L;
//        long offset2 = 90909137492982L;
//        long offset2 = 62348456758466L;
//        long gap2 = size - 51787594638418L;
        State initial = new State();
        initial.offset = 62348456758466L;
        initial.gap = size - 51787594638418L;
        State result = calculate(initial, repeat, size);
        // reverse
//        offset -= gap;
//        offset %= size;
//        if (offset < 0)
//        {
//            offset += size;
//        }

        // after 4 steps
//        long offset4 = offset2 + (new BigDecimal(gap2).multiply(new BigDecimal(offset2)).remainder(new BigDecimal(size))).longValue();
//        offset4 %= size;
//        long gap4 = (new BigDecimal(gap2).multiply(new BigDecimal(gap2)).remainder(new BigDecimal(size))).longValue();
////        offset = (new BigDecimal(gap).multiply(new BigDecimal(offset)).remainder(new BigDecimal(size))).longValue();
////        offset -= gap;
////        offset %= size;
////        if (offset < 0)
////        {
////            offset += size;
////        }
////
////        // after 8 steps
//        long offset8 = offset4 + (new BigDecimal(gap4).multiply(new BigDecimal(offset4)).remainder(new BigDecimal(size))).longValue();
//        offset8 %= size;
//        long gap8 = (new BigDecimal(gap4).multiply(new BigDecimal(gap4)).remainder(new BigDecimal(size))).longValue();
////        offset -= gap;
////        offset %= size;
////        if (offset < 0)
////        {
////            offset += size;
////        }
////        
////        // after 8 steps
////        offset = (new BigDecimal(gap).multiply(new BigDecimal(offset)).remainder(new BigDecimal(size))).longValue();
////        gap = (new BigDecimal(gap).multiply(new BigDecimal(gap)).remainder(new BigDecimal(size))).longValue();
////        offset -= gap;
////        offset %= size;
////        if (offset < 0)
////        {
////            offset += size;
////        }
//        State state8 = new State();
//        state8.gap = gap8;
//        state8.offset = offset8;
//        State state4 = new State();
//        state4.gap = gap4;
//        state4.offset = offset4;
//        State state12 = combine(state8, state4, size);

        System.out.println(result.offset);
        System.out.println((result.offset + result.gap) % size);
        System.out.println((result.offset + 2020 * result.gap) % size);

        //        System.out.println(deck.get(3));
    }
    // 51787594638418

    private static State calculate(State initial, long iterations, long size)
    {
        List<State> subStates = new ArrayList<>();
        State currentState = initial;
        while (iterations > 0)
        {
            if (iterations % 2 == 1)
            {
                subStates.add(currentState);
            }
            currentState = combine(currentState, currentState, size);
            iterations /= 2;
        }
        currentState = subStates.get(0);
        for (int i = 1 ; i < subStates.size() ; i++)
        {
            currentState = combine(currentState, subStates.get(i), size);
        }
        return currentState;
    }
    private static State combine(State state1, State state2, long size)
    {
        State result = new State();
        result.offset = state1.offset + (new BigDecimal(state1.gap).multiply(new BigDecimal(state2.offset)).remainder(new BigDecimal(size))).longValue();
        result.offset %= size;
        result.gap = (new BigDecimal(state1.gap).multiply(new BigDecimal(state2.gap)).remainder(new BigDecimal(size))).longValue();
        return result;
    }

    private static class State
    {
        public long gap;
        public long offset;
    }
}
