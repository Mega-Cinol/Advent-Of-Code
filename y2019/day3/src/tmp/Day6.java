package tmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day6
{
    public static void main(String[] args)
    {
        Map<String, String> map = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        String line = "";
        while (sc.hasNext())
        {
            line = sc.next();
            if ("dupa".equals(line))
            {
                break;
            }
            String[] entries = line.split("\\)");
            map.put(entries[1], entries[0]);
        }
        sc.close();
        int result = 0;
        for (String planet : map.keySet())
        {
            int count = 0;
            while (!"COM".equals(planet))
            {
                planet = map.get(planet);
                count++;
            }
            result += count;
        }
        List<String> planetsToCom = new ArrayList<>();
        String planet = map.get("YOU");
        while (!"COM".equals(planet))
        {
            planetsToCom.add(planet);
            planet = map.get(planet);
        }
        planet = map.get("SAN");
        int count = 0;
        while (!planetsToCom.contains(planet))
        {
            count++;
            planet = map.get(planet);
        }
        count += planetsToCom.indexOf(planet);
        System.out.println(count);
    }
}
