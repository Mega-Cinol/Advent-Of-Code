package tmp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

public class Day16
{
    private static BitSet getPositivePattern(int row, int size)
    {
        BitSet pattern = new BitSet(size);
        int position = row;
        while (position < size)
        {
            for (int i = 0; i <= row; i++)
            {
                pattern.set(position + i);
                if (position + i >= size)
                {
                    return pattern;
                }
            }
            position += 4 * row + 4;
        }
        return pattern;
    }

    private static BitSet getNegativePattern(int row, int size)
    {
        BitSet pattern = new BitSet(size);
        int position = 3 * row + 2;
        while (position < size)
        {
            for (int i = 0; i <= row; i++)
            {
                pattern.set(position + i);
                if (position + i >= size)
                {
                    return pattern;
                }
            }
            position += 4 * row + 4;
        }
        return pattern;
    }

    private static void calculateRow(int row,
                                     Map<Integer, BitSet> vector,
                                     int size,
                                     Consumer<Integer> outputConsumer,
                                     List<BitSet> positivePatterns,
                                     List<BitSet> negativePatterns)
    {
        //        System.out.println("Row " + row + " started");
        int result = 0;
        BitSet positivePattern = /*positivePatterns.get(row);*/getPositivePattern(row, size);
        BitSet negativePattern = /*negativePatterns.get(row);*/getNegativePattern(row, size);
        for (int i = 1; i < 10; i++)
        {
            BitSet patternCopy = (BitSet) positivePattern.clone();
            patternCopy.and(vector.get(i));
            result += i * patternCopy.cardinality();
            patternCopy = (BitSet) negativePattern.clone();
            patternCopy.and(vector.get(i));
            result -= i * patternCopy.cardinality();
        }
        outputConsumer.accept(Math.abs(result) % 10);
        //        System.out.println("Row " + row + " ready, result: " + Math.abs(result) %10);
    }

    private static void addToOutputMap(int row, int value, Map<Integer, BitSet> outputMap)
    {
        BitSet outputSet = outputMap.get(value);
        synchronized (outputSet)
        {
            outputSet.set(row);
        }
    }

    public static void oldMain(String[] args) throws InterruptedException
    {
        String input =
                "59782619540402316074783022180346847593683757122943307667976220344797950034514416918778776585040527955353805734321825495534399127207245390950629733658814914072657145711801385002282630494752854444244301169223921275844497892361271504096167480707096198155369207586705067956112600088460634830206233130995298022405587358756907593027694240400890003211841796487770173357003673931768403098808243977129249867076581200289745279553289300165042557391962340424462139799923966162395369050372874851854914571896058891964384077773019120993386024960845623120768409036628948085303152029722788889436708810209513982988162590896085150414396795104755977641352501522955134675";
        //        String input = "12345678";
        int inputSize = input.length();
        int multiplier = 1_0000;
        Map<Integer, BitSet> inputMap = new HashMap<>();
        Map<Integer, BitSet> outputMap = new HashMap<>();
        List<BitSet> positivePatterns = new ArrayList<>();
        List<BitSet> negativePatterns = new ArrayList<>();
        /*        for (int row = 0 ; row < multiplier * inputSize ; row++)
        {
            positivePatterns.add(getPositivePattern(row, multiplier * inputSize));
            negativePatterns.add(getNegativePattern(row, multiplier * inputSize));
        }*/
        for (int i = 1; i < 10; i++)
        {
            inputMap.put(i, new BitSet(inputSize * multiplier));
            outputMap.put(i, new BitSet(inputSize * multiplier));
        }
        for (int position = 0; position < input.length(); position++)
        {
            int charAsNum = input.charAt(position) - 48;
            if (charAsNum == 0)
            {
                continue;
            }
            BitSet charPositions = inputMap.get(charAsNum);
            for (int i = 0; i < multiplier; i++)
            {
                charPositions.set(position + i * inputSize);
            }
        }
        System.out.println("Start");
        LocalDateTime startTime = LocalDateTime.now();
        ExecutorService executor = Executors.newFixedThreadPool(64);
        for (int i = 0; i < /*inputSize * multiplier*/650_0; i++)
        {
            int row = i;
            executor.submit(() -> calculateRow(row, inputMap, inputSize * multiplier, result -> addToOutputMap(row, result, outputMap),
                    positivePatterns, negativePatterns));
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.println(Duration.between(startTime, LocalDateTime.now()));
        /*        for (int i = 0 ; i < inputSize * multiplier ; i++)
        {
            boolean found = false;
            for (int v = 1 ; v < 10 ; v++)
            {
                if (outputMap.get(v).get(i))
                {
                    System.out.print(v);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                System.out.print(0);
            }
        }
        System.out.println();*/
    }

    private static List<Integer> transform(List<Integer> input)
    {
        List<Integer> output = new ArrayList<>();
        int sum = input.stream().reduce(0, (a,b) -> a+b);
        for (int i = 0 ; i < input.size() ; i++)
        {
            output.add(Math.abs(sum) % 10);
            sum -= input.get(i);
        }
        return output;
    }

    public static void main(String[] args) throws InterruptedException
    {
        String input =
                "59782619540402316074783022180346847593683757122943307667976220344797950034514416918778776585040527955353805734321825495534399127207245390950629733658814914072657145711801385002282630494752854444244301169223921275844497892361271504096167480707096198155369207586705067956112600088460634830206233130995298022405587358756907593027694240400890003211841796487770173357003673931768403098808243977129249867076581200289745279553289300165042557391962340424462139799923966162395369050372874851854914571896058891964384077773019120993386024960845623120768409036628948085303152029722788889436708810209513982988162590896085150414396795104755977641352501522955134675";
        List<Integer> inputAsIntList = input.chars().boxed().collect(Collectors.toList());
        int offset = 5_978_261;
        int noCharsLeft = 6_500_000 - offset;
        int repeatInputTimes = noCharsLeft / 650;
        int inputOffset = 650 - noCharsLeft % 650;
        List<Integer> inputEnd = new ArrayList<>();
        for (int i = inputOffset; i < 650; i++)
        {
            inputEnd.add(input.charAt(i) - 48);
        }
        for (int i = 0; i < repeatInputTimes; i++)
        {
            inputEnd.addAll(inputAsIntList);
        }
        for (int i = 0 ; i < 100 ; i++)
        {
            inputEnd = transform(inputEnd);
        }
        inputEnd.stream().limit(8).forEach(System.out::print);
        System.out.println();
    }
}
