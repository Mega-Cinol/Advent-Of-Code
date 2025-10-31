package tmp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7
{
    private static class Thruster implements Callable<Integer>, IntComputer.InputProvider, IntComputer.OutputConsumer
    {
        private final List<Integer> data = new ArrayList<>();
        private Thruster nextThruster = null;
        private final List<BigDecimal> program;
        private final String name;
        private int lastInput = 0;
        public Thruster(List<BigDecimal> program, String name)
        {
            this.name = name;
            this.program = program;
        }
        public void setNextThruster(Thruster thruster)
        {
            nextThruster = thruster;
        }

        @Override
        public void consumeOutput(String output)
        {
            String n = name;
            System.out.println("got" + n + " " + output);
            int intOut = Integer.valueOf(output);
            synchronized (this)
            {
                lastInput = intOut;
                data.add(intOut);
            }
        }

        @Override
        public long generateInput()
        {
            String n = name;
            while (data.isEmpty()) {
                try
                {
                    Thread.sleep(0, 1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            int result = 0;
            synchronized(this)
            {
                result = data.get(0);
                lastInput = result;
                System.out.println("read" + n + " " + result);
                data.remove(0);
            }
            return result;
        }

        @Override
        public Integer call() throws Exception
        {
            IntComputer computer = new IntComputer();
            computer.executeProgram(program, this, nextThruster);
            return lastInput;
        }
    }

    private static Set<List<Integer>> getAllCombinations(Set<Integer> values)
    {
        Set<List<Integer>> result = new HashSet<>();
        if (values.isEmpty())
        {
            result.add(new ArrayList<>());
        }
        for (Integer value : values)
        {
            Set<List<Integer>> subCombinations = getAllCombinations(values.stream()
                    .filter(v -> value != v)
                    .collect(Collectors.toSet()));
            subCombinations.stream().forEach(combination -> combination.add(0, value));
            result.addAll(subCombinations);
        }
        return result;
    }

    private static long runCombination(List<Integer> combination, List<BigDecimal> program) throws Exception
    {
        Thruster t1 = new Thruster(program, "t1");
        t1.consumeOutput(combination.get(0).toString());
        t1.consumeOutput("0");
        Thruster t2 = new Thruster(program, "t2");
        t2.consumeOutput(combination.get(1).toString());
        Thruster t3 = new Thruster(program, "t3");
        t3.consumeOutput(combination.get(2).toString());
        Thruster t4 = new Thruster(program, "t4");
        t4.consumeOutput(combination.get(3).toString());
        Thruster t5 = new Thruster(program, "t5");
        t5.consumeOutput(combination.get(4).toString());
        t1.setNextThruster(t2);
        t2.setNextThruster(t3);
        t3.setNextThruster(t4);
        t4.setNextThruster(t5);
        t5.setNextThruster(t1);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(t5);
        executor.submit(t2);
        executor.submit(t3);
        executor.submit(t4);
        t1.call();
        return t1.generateInput();
    }

    public static void main(String[] args) throws Exception
    {
        Set<Integer> input = new HashSet<Integer>();
        input.add(5);
        input.add(6);
        input.add(7);
        input.add(8);
        input.add(9);
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program = Stream.of(scanner.next().split(",")).map(Integer::parseInt).map(BigDecimal::new).collect(Collectors.toList());

        long maxResult = 0;
        List<Integer> maxCombination = null;
        for (List<Integer> combination: getAllCombinations(input))
        {
            long result = runCombination(combination, program);
            if (result > maxResult)
            {
                maxCombination = combination;
                maxResult = result;
            }
        }
        System.out.println(maxResult);
        System.out.println(maxCombination);
        scanner.close();
    }
}
