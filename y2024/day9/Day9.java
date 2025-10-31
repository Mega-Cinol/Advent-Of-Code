package y2024.day9;

import common.AdventSolution;

import java.util.*;

public class Day9 extends AdventSolution {
    private record File(long id, long position, long size) {
        public long checksum() {
            return id * ((position + position + size - 1) * size / 2);
        }
    }

    @Override
    public Object part1Solution() {
        var files = new TreeMap<Long, File>();
        var diskMap = getInput().findFirst().get();
        var position = 0L;
        for (var i = 0 ; i < diskMap.length() ; i+=2) {
            files.put(position, new File(i/2, position, diskMap.charAt(i) - '0'));
            if (i + 1 < diskMap.length()) {
                position += (diskMap.charAt(i + 1) - '0') + (diskMap.charAt(i) - '0');
            }
        }
        var gapPositon = nextGapPosition(0L, files);
        while (gapPositon > 0) {
            var nextFile = nextFile(gapPositon, files);
            var gapSize = nextFile.position() - gapPositon;
            while (gapSize > 0) {
                var lastFile = files.get(files.lastKey());
                if (lastFile.position() <= gapPositon) {
                    gapSize = 0;
                    break;
                }
                if (lastFile.size() <= gapSize) {
                    files.put(gapPositon, new File(lastFile.id(), gapPositon, lastFile.size()));
                    gapSize -= lastFile.size();
                    gapPositon += lastFile.size();
                    files.remove(lastFile.position());
                } else {
                    files.remove(lastFile.position());
                    files.put(lastFile.position(), new File(lastFile.id(), lastFile.position(), lastFile.size() - gapSize));
                    files.put(gapPositon, new File(lastFile.id(), gapPositon, gapSize));
                    gapPositon += gapSize;
                    gapSize = 0;
                }
            }
            gapPositon = nextGapPosition(gapPositon + gapSize, files);
        }
        return files.values()
                .stream()
                .mapToLong(File::checksum)
                .sum();
    }

    private long nextGapPosition(long startFrom, SortedMap<Long, File> files) {
        var lastFilePosition = files.lastKey();
        while (files.containsKey(startFrom)) {
            startFrom += files.get(startFrom).size();
        }
        return startFrom < lastFilePosition ? startFrom : -1;
    }
    private File nextFile(long startFrom, SortedMap<Long, File> files) {
        var lastFilePosition = files.lastKey();
        if (startFrom > lastFilePosition) {
            return null;
        }
        while (!files.containsKey(startFrom)) {
            startFrom++;
        }
        return files.get(startFrom);
    }
    @Override
    public Object part2Solution() {
        var filesByPosition = new TreeMap<Long, File>();
        var filesById = new TreeMap<Long, File>();
        var diskMap = getInput().findFirst().get();
        var position = 0L;
        for (var i = 0 ; i < diskMap.length() ; i+=2) {
            filesByPosition.put(position, new File(i/2, position, diskMap.charAt(i) - '0'));
            filesById.put(i/2L, new File(i/2, position, diskMap.charAt(i) - '0'));
            if (i + 1 < diskMap.length()) {
                position += (diskMap.charAt(i + 1) - '0') + (diskMap.charAt(i) - '0');
            }
        }
        for (var id = filesById.lastKey() ; id > 0 ; id--) {
            var lastFile = filesById.get(id);
            var gapPositon = nextGapPosition(0L, filesByPosition);
            while (gapPositon > 0 && gapPositon < lastFile.position) {
                var nextFile = nextFile(gapPositon, filesByPosition);
                var gapSize = nextFile.position() - gapPositon;
                if (gapSize >= lastFile.size()) {
                    filesByPosition.remove(lastFile.position());
                    filesByPosition.put(gapPositon, new File(lastFile.id(), gapPositon, lastFile.size()));
                    System.out.println("Moving %s to %d".formatted(lastFile, gapPositon));
                    break;
                }
                gapPositon = nextGapPosition(gapPositon + gapSize, filesByPosition);
            }
        }
        return filesByPosition.values()
                .stream()
                .mapToLong(File::checksum)
                .sum();
    }

    public static void main(String[] args) {
        new Day9().solve();
    }
}
