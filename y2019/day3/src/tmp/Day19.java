package tmp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tmp.IntComputer.OutputConsumer;
import tmp.IntComputer.SequenceInputProvider;

public class Day19
{
    private static class TractorBeamOutputConsumer implements OutputConsumer
    {
        private boolean result = false;

        @Override
        public void consumeOutput(String output)
        {
            result = "1".equals(output);
        }
        public boolean isPulled()
        {
            return result;
        }
    }
    private static boolean checkField(int x, int y, List<BigDecimal> program)
    {
        IntComputer computer = new IntComputer();
        TractorBeamOutputConsumer outConsumer = new TractorBeamOutputConsumer();
        computer.executeProgram(program, new SequenceInputProvider(x, y), outConsumer);
        return outConsumer.isPulled();
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program =
                Stream.of(scanner.next().split(",")).map(Integer::parseInt).map(BigDecimal::new).collect(Collectors.toList());
        scanner.close();
        int distance = 0;
        boolean found = false;
        int x = 0;
        int y = 0;
        while (!found)
        {
            for (int i = 0 ; i <= distance ; i++)
            {
                x = i;
                y = distance - i;
                if (checkField(x, y, program) && checkField(x + 99, y, program) && checkField(x, y + 99, program) && checkField(x + 99, y + 99, program))
                {
                    found = true;
                    break;
                }
            }
            distance++;
        }
        System.out.println(x);
        System.out.println(y);
    }
}
