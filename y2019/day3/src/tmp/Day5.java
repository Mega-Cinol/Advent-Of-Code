package tmp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program = Stream.of(scanner.next().split(",")).map(Integer::parseInt).map(BigDecimal::new).collect(Collectors.toList());
        IntComputer computer = new IntComputer();
        List<BigDecimal> newProgram = computer.executeProgram(program, () -> 5, System.out::println);
        System.out.println("Result: " + newProgram.get(0));
        scanner.close();
    }
}
