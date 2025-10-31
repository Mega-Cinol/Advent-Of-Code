package tmp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tmp.IntComputer.AsciiOutputConsumer;
import tmp.IntComputer.AsciiSequenceInputProvider;

public class Day21
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program =
                Stream.of(scanner.next().split(",")).map(Integer::parseInt).map(BigDecimal::new).collect(Collectors.toList());
        IntComputer computer = new IntComputer();
        computer.executeProgram(program, new AsciiSequenceInputProvider(scanner,
                "NOT A J",
                "NOT B T",
                "OR T J",
                "NOT C T",
                "OR T J",
                "AND D J",
                "AND D T",
                "AND I T",
                "OR F T",
                "AND E T",
                "OR H T",
                "AND T J",
                "RUN"
                ), new AsciiOutputConsumer());
        scanner.close();
    }
}
