package tmp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13
{
    private static int readState = 0;
    private static int count = 0;

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program = Stream.of(scanner.next().split(",")).map(Integer::parseInt).map(BigDecimal::new).collect(Collectors.toList());
        IntComputer computer = new IntComputer();
        computer.executeProgram(program, () -> 5, out -> {
            switch (readState)
            {
                case 2:
                    if (Integer.valueOf(out) == 2)
                    {
                        count++;
                    }
                    break;
                default:
                    break;
            }
            readState++;
            readState %= 3;
        });
        System.out.println(count);
        scanner.close();
    }
}
