package y2024.day13;

import common.AdventSolution;

import java.util.regex.Pattern;

public class Day13 extends AdventSolution {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private record Machine(long adx, long ady, long bdx, long bdy, long prizeX, long prizeY) {
        public long tokensNeeded() {
            // px == a * ax + b * bx => px * ay = a * ax * ay + b * bx * ay
            // py == a * ay + b * by => py * ax = a * ax * ay + b * by * ax
            // px * ay - py * ax = b * (bx * ay -  by * ax)
            var bTop = prizeX * ady - prizeY * adx;
            var bBottom = bdx * ady - bdy * adx;
            if (bBottom == 0) {
                if (bTop != 0) {
                    return 0;
                }
                throw new UnsupportedOperationException(this.toString());
            }
            if (bTop % bBottom != 0) {
                return 0;
            }
            var bCount = bTop / bBottom;
            var aTop = prizeX - bCount * bdx;
            var aBottom = adx;
            if (aTop % aBottom != 0) {
                return 0;
            }
            var aCount = aTop / aBottom;
            return aCount * 3 + bCount;
        }
    }
    @Override
    public Object part1Solution() {
        var lines = getInput().toList();
        var tokensNeeded = 0L;
        for (var i = 0 ; i < lines.size() ; i+=4) {
            var aMatcher = NUMBER_PATTERN.matcher(lines.get(i));
            var adx = -1L;
            var ady = -1L;
            while (aMatcher.find()) {
                if (adx == -1) {
                    adx = Long.parseLong(aMatcher.group());
                } else {
                    ady = Long.parseLong(aMatcher.group());
                }
            }
            var bMatcher = NUMBER_PATTERN.matcher(lines.get(i+1));
            var bdx = -1L;
            var bdy = -1L;
            while (bMatcher.find()) {
                if (bdx == -1) {
                    bdx = Long.parseLong(bMatcher.group());
                } else {
                    bdy = Long.parseLong(bMatcher.group());
                }
            }
            var prizeMatcher = NUMBER_PATTERN.matcher(lines.get(i+2));
            var prizeX = -1L;
            var prizeY = -1L;
            while (prizeMatcher.find()) {
                if (prizeX == -1) {
                    prizeX = Long.parseLong(prizeMatcher.group());
                } else {
                    prizeY = Long.parseLong(prizeMatcher.group());
                }
            }
            var machine = new Machine(adx, ady, bdx, bdy, prizeX, prizeY);
            tokensNeeded += machine.tokensNeeded();
        }
        return tokensNeeded;
    }

    @Override
    public Object part2Solution() {
        var lines = getInput().toList();
        var tokensNeeded = 0L;
        for (var i = 0 ; i < lines.size() ; i+=4) {
            var aMatcher = NUMBER_PATTERN.matcher(lines.get(i));
            var adx = -1L;
            var ady = -1L;
            while (aMatcher.find()) {
                if (adx == -1) {
                    adx = Long.parseLong(aMatcher.group());
                } else {
                    ady = Long.parseLong(aMatcher.group());
                }
            }
            var bMatcher = NUMBER_PATTERN.matcher(lines.get(i+1));
            var bdx = -1L;
            var bdy = -1L;
            while (bMatcher.find()) {
                if (bdx == -1) {
                    bdx = Long.parseLong(bMatcher.group());
                } else {
                    bdy = Long.parseLong(bMatcher.group());
                }
            }
            var prizeMatcher = NUMBER_PATTERN.matcher(lines.get(i+2));
            var prizeX = -1L;
            var prizeY = -1L;
            while (prizeMatcher.find()) {
                if (prizeX == -1) {
                    prizeX = 10000000000000L + Long.parseLong(prizeMatcher.group());
                } else {
                    prizeY = 10000000000000L + Long.parseLong(prizeMatcher.group());
                }
            }
            var machine = new Machine(adx, ady, bdx, bdy, prizeX, prizeY);
            tokensNeeded += machine.tokensNeeded();
        }
        return tokensNeeded;
    }
    public static void main(String[] args) {
        new Day13().solve();
    }
}
