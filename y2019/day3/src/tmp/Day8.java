package tmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day8
{
    public static void main(String[] args) throws Exception
    {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        List<String> layers = new ArrayList<>();
        while (input.length() > 0)
        {
            layers.add(input.substring(0, 150));
            input = input.substring(150, input.length());
        }
        String layer = layers.stream()
        .sorted((l1, l2) -> {
            long zCount1 = l1.chars().filter(c -> c == '0').count();
            long zCount2 = l2.chars().filter(c -> c == '0').count();
            return (int) (zCount1 - zCount2);
        }).findFirst().get();
        System.out.println(layer.chars().filter(c -> c == '1').count() * layer.chars().filter(c -> c == '2').count());
        List<Character> finalImage = new ArrayList<Character>();
        for (int i = 0 ; i < 150 ; i++)
        {
            finalImage.add('2');
        }
        for (String l : layers)
        {
            for (int i = 0 ; i < l.length() ; i++)
            {
                if (l.charAt(i) != '2' && finalImage.get(i) == '2')
                {
                    finalImage.set(i, l.charAt(i));
                }
            }
        }
        for (int i = 0 ; i < 6 ; i++)
        {
            for (int j = 0 ; j < 25 ; j++)
            {
                char c = finalImage.get(i*25 + j);
                System.out.print(c == '1' ? 'x' : ' ');
            }
            System.out.println("");
        }
        scanner.close();
    }
}
