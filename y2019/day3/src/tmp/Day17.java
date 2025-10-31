package tmp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tmp.IntComputer.SequenceInputProvider;

public class Day17
{
    private static class AsciiOutputConsumer implements tmp.IntComputer.OutputConsumer
    {
        private final char map[][] = new char[200][200];
        int x = 0;
        int y = 0;

        @Override
        public void consumeOutput(String output)
        {
            int intOut = Integer.parseInt(output);
            if (intOut == 10)
            {
                y++;
                x = 0;
            }
            else
            {
                map[x++][y] = (char)intOut;
            }
        }
        public void print()
        {
            int sum = 0;
            for (int row = 0 ; row < 40 ; row++)
            {
                for (int col =0 ; col < 68 ; col++)
                {
                    if ((row > 0) && (row < 39) && (col > 0) && (col < 67))
                    {
                        if (map[col][row] == '#')
                        {
                            if ((map[col-1][row] == '#') && (map[col+1][row] == '#') && (map[col][row-1] == '#') && (map[col][row+1] == '#'))
                            {
                                sum += row*col;
                                System.out.print('O');
                                continue;
                            }
                        }
                    }
                    System.out.print(map[col][row]);
                }
                System.out.println();
            }
            System.out.println(sum);
        }
    }
    /*
    A,A,B,C,B,C,B,C,B,A

    A:
    84,44,6,44,76,44,12,44,84,44,6
    B:
    76,44,49,50,44,82,44,54,44,76,44,56,44,76,44,49,50,10
    C:
    84,44,49,50,44,76,44,49,48,44,76,44,49,48,10
    */
    public static void main(String[] args)
    {
        SequenceInputProvider input = new SequenceInputProvider(65,44,65,44,66,44,67,44,66,44,67,44,66,44,67,44,66,44,65,10,
                82,44,54,44,76,44,49,50,44,82,44,54,10,
                76,44,49,50,44,82,44,54,44,76,44,56,44,76,44,49,50,10,
                82,44,49,50,44,76,44,49,48,44,76,44,49,48,10,
                (int)'n',10);
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program =
                Stream.of(scanner.next().split(",")).map(Integer::parseInt).map(BigDecimal::new).collect(Collectors.toList());
        scanner.close();
        IntComputer computer = new IntComputer();
        AsciiOutputConsumer outc = new AsciiOutputConsumer();
        computer.executeProgram(program, input, out -> System.out.print(Integer.parseInt(out)));
//        outc.print();
    }
}
