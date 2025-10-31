package tmp;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day14
{
    private static class Recipie
    {
        private final Map<String, Long> ingredients;
        private final long productAmount;

        public Recipie(Map<String, Long> ingredients, long productAmount)
        {
            this.ingredients = ingredients;
            this.productAmount = productAmount;
        }
    }

    private static long getRequiredOre(long fuelAmount, Map<String, Recipie> recipies)
    {
        Map<String, Long> whishList = new HashMap<>();
        Map<String, Long> surplus = new HashMap<>();
        whishList.put("FUEL", fuelAmount);
        while (whishList.size() > 1 || !whishList.containsKey("ORE"))
        {
            Map.Entry<String, Long> whish = whishList.entrySet().stream().filter(e -> !"ORE".equals(e.getKey())).findAny().get();
            Recipie recipie = recipies.get(whish.getKey());
            long multiplier = whish.getValue() / recipie.productAmount + 1;
            whishList.remove(whish.getKey());
            for (Map.Entry<String, Long> ingredient : recipie.ingredients.entrySet())
            {
                long ingredientSurplus = surplus.containsKey(ingredient.getKey()) ? surplus.get(ingredient.getKey()) : 0;
                long oldValue = whishList.containsKey(ingredient.getKey()) ? whishList.get(ingredient.getKey()) : 0;
                long newValue = oldValue + ingredient.getValue() * multiplier;
                if (newValue <= ingredientSurplus)
                {
                    whishList.remove(ingredient.getKey());
                    ingredientSurplus -= newValue;
                    if (ingredientSurplus == 0)
                    {
                        surplus.remove(ingredient.getKey());
                    }
                    else
                    {
                        surplus.put(ingredient.getKey(), ingredientSurplus);
                    }
                }
                else
                {
                    whishList.put(ingredient.getKey(), newValue - ingredientSurplus);
                    surplus.remove(ingredient.getKey());
                }
            }
            if (recipie.productAmount * multiplier > whish.getValue())
            {
                if (surplus.containsKey(whish.getKey()))
                {
                    surplus.put(whish.getKey(), surplus.get(whish.getKey()) + recipie.productAmount * multiplier - whish.getValue());
                }
                else
                {
                    surplus.put(whish.getKey(), recipie.productAmount * multiplier - whish.getValue());
                }
            }
        }
        return whishList.get("ORE");
    }

    public static void main(String[] args)
    {
        Map<String, Recipie> recipies = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        String line = "";
        while (true)
        {
            line = sc.nextLine();
            if ("dupa".equals(line))
            {
                break;
            }
            String[] recipieArray = line.split(" => ");
            String[] ingredientsArray = recipieArray[0].split(", ");
            Map<String, Long> recipieIngredients = new HashMap<>();
            for (String ingredientDesc : ingredientsArray)
            {
                recipieIngredients.put(ingredientDesc.split(" ")[1], Long.valueOf(ingredientDesc.split(" ")[0]));
            }
            String recipieProduct = recipieArray[1].split(" ")[1];
            recipies.put(recipieProduct, new Recipie(recipieIngredients, Long.valueOf(recipieArray[1].split(" ")[0])));
        }
        sc.close();
        long min = 1L;
        long max = 99982925000L;
        do
        {
            long guess = (min + max) / 2;
            long lt = getRequiredOre(guess, recipies);
            long gt = getRequiredOre(guess + 1, recipies);
            if ((lt <= 1000000000000L) && (gt > 1000000000000L))
            {
                System.out.println(guess);
                break;
            }
            else if (gt <= 1000000000000L)
            {
                long oldGuess = guess;
                min = guess;
                guess = (guess + max) / 2;
                if (guess == oldGuess)
                {
                    guess++;
                }
            }
            else
            {
                long oldGuess = guess;
                max = guess;
                guess = (guess + min) / 2;
                if (guess == oldGuess)
                {
                    guess--;
                }
            }
            System.out.println(guess);
        }
        while (true);
        System.out.println(getRequiredOre(82925000L, recipies));
    }
}
