package tmp;

public class Day4
{
    public static void main(String[] args)
    {
        int count = 0;
        for (int a = 1 ; a <= 5 ; a++)
        {
            for (int b = a ; b <= 9 ; b++)
            {
                if (a == 1 && b < 3)
                {
                    continue;
                }
                if (a == 5 && b > 8)
                {
                    continue;
                }
                for (int c = b ; c <=9 ; c++)
                {
                    if (a == 1 && b == 3 && c <7)
                    {
                        continue;
                    }
                    for (int d = c ; d <= 9 ; d++)
                    {
                        for (int e = d ; e <= 9 ; e++)
                        {
                            for (int f = e ; f <= 9 ; f++)
                            {
                                if (a == b && b != c)
                                {
                                    count++;
                                    System.out.println("" + a + b + c + d + e + f);
                                    continue;
                                }
                                if (a != b && b == c && c != d)
                                {
                                    count++;
                                    System.out.println("" + a + b + c + d + e + f);
                                    continue;
                                }
                                if (b != c && d == c && e != d)
                                {
                                    count++;
                                    System.out.println("" + a + b + c + d + e + f);
                                    continue;
                                }
                                if (c != d && d == e && e != f)
                                {
                                    count++;
                                    System.out.println("" + a + b + c + d + e + f);
                                    continue;
                                }
                                if (d != e && e == f)
                                {
                                    count++;
                                    System.out.println("" + a + b + c + d + e + f);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(count);
    }
}
