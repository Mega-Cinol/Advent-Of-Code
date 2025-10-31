package tmp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tmp.IntComputer.AsciiOutputConsumer;
import tmp.IntComputer.AsciiSequenceInputProvider;

public class Day25
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program =
                Stream.of(scanner.next().split(",")).map(BigDecimal::new).collect(Collectors.toList());
        IntComputer computer = new IntComputer();
        computer.executeProgram(program, new AsciiSequenceInputProvider(scanner), new AsciiOutputConsumer());
        scanner.close();
    }

}
