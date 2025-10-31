package y2024.day22;

import common.AdventSolution;

import java.util.HashMap;

public class Day22 extends AdventSolution {
    @Override
    public Object part1Solution() {
        return getInput()
                .mapToLong(Long::parseLong)
                .map(this::get2000thNextSecret)
                .sum();
    }

    private record Sequence(long first, long second, long third, long fourth) {}

    @Override
    public Object part2Solution() {
        var sellers = getInput().map(Long::valueOf).toList();
        var sequenceValues = new HashMap<Sequence, Long>();
        for (var seller : sellers) {
            var sellerSequenceValues = new HashMap<Sequence, Long>();
            long fourAgo = seller;
            var threeAgo = getNextSecret(fourAgo);
            var twoAgo = getNextSecret(threeAgo);
            var oneAgo = getNextSecret(twoAgo);
            var current = getNextSecret(oneAgo);
            sellerSequenceValues.put(getSequence(fourAgo, threeAgo, twoAgo, oneAgo, current), current % 10);
            for (var i = 3 ; i < 2000 ; i++) {
                fourAgo = threeAgo;
                threeAgo = twoAgo;
                twoAgo = oneAgo;
                oneAgo = current;
                current = getNextSecret(current);
                long finalCurrent = current;
                sellerSequenceValues.computeIfAbsent(getSequence(fourAgo, threeAgo, twoAgo, oneAgo, current), s -> finalCurrent % 10);
            }
            sellerSequenceValues.forEach((sequence, value) -> sequenceValues.merge(sequence, value, Long::sum));
        }
        return sequenceValues.values().stream()
                .max(Long::compareTo);
    }

    private Sequence getSequence(long first, long second, long third, long fourth, long fifth) {
        return new Sequence(second % 10 - first % 10, third % 10 - second % 10, fourth % 10 - third % 10, fifth % 10 - fourth % 10);
    }

    private long get2000thNextSecret(long secret) {
        var next = secret;
        for (var i = 0 ; i < 2000; i++) {
            next = getNextSecret(next);
        }
        return next;
    }

    private long getNextSecret(long secret) {
        secret ^= secret << 6;
        secret %= 16777216;
        secret ^= secret >> 5;
        secret %= 16777216;
        secret ^= secret << 11;
        secret %= 16777216;
        return secret;
    }

    public static void main(String[] args) {
        new Day22().solve();
    }
}
