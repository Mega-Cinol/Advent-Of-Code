package y2021.day16;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import common.Input;

public class Day16 {

	public static void main(String[] args) {
		String input = Input.parseLines("y2021/day16/day16.txt").findFirst().get();
		BitStream bs = new BitStream(input);
		Packet top = PacketParser.parsePacket(bs);
		// part 1
		System.out.println(top.getTotalVersion());
		// part 2
		System.out.println(top.getValue());
	}

	private static class BitStream {
		private final String source;
		private int bitIndex = 0;

		public BitStream(String src) {
			source = src;
		}

		public int readInt(int bitsCount) {
			int result = 0;
			while (bitsCount > 0) {
				bitsCount--;
				result += getNextBit() * Math.pow(2, bitsCount);
			}
			return result;
		}

		public boolean readBoolean() {
			return getNextBit() == 1;
		}

		public int getBitIndex() {
			return bitIndex;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(source);
			sb.append('\n');
			for (int i = 0; i < source.length(); i++) {
				int hexValue = hexToInt(source.charAt(i));
				sb.append((hexValue & 8) / 8);
				sb.append((hexValue & 4) / 4);
				sb.append((hexValue & 2) / 2);
				sb.append((hexValue & 1));
				sb.append(' ');
			}
			sb.append('\n');
			for (int i = 0; i < bitIndex; i++) {
				sb.append(' ');
				if ((i % 4) == 3) {
					sb.append(' ');
				}
			}
			sb.append('^');
			return sb.toString();
		}

		private int getNextBit() {
			int currentCharIdx = bitIndex / 4;
			if (currentCharIdx >= source.length()) {
				return 0;
			}
			int offset = 3 - (bitIndex % 4);

			int charAsInt = hexToInt(source.charAt(currentCharIdx));

			int offsetedValue = charAsInt & (1 << offset);
			bitIndex++;
			return offsetedValue > 0 ? 1 : 0;
		}

		private int hexToInt(char character) {
			if (character >= '0' && character <= '9') {
				return character - '0';
			} else {
				return character - 'A' + 10;
			}
		}
	}

	private static interface Packet {
		int getVersion();

		int getTotalVersion();

		long getValue();
	}

	private static abstract class AbstractPacket implements Packet {
		private final int version;

		public AbstractPacket(int version) {
			this.version = version;
		}

		@Override
		public int getVersion() {
			return version;
		}
	}

	private static class LiteralPacket extends AbstractPacket {
		private final long value;

		public LiteralPacket(int version, BitStream bs) {
			super(version);
			List<Integer> valueBuilder = new ArrayList<>();
			while (bs.readBoolean()) {
				valueBuilder.add(bs.readInt(4));
			}
			valueBuilder.add(bs.readInt(4));
			long multiplier = 1;
			long finalValue = 0;
			for (int i = valueBuilder.size() - 1; i >= 0; i--) {
				finalValue += multiplier * valueBuilder.get(i);
				multiplier *= 16;
			}
			value = finalValue;
		}

		@Override
		public int getTotalVersion() {
			return getVersion();
		}

		@Override
		public long getValue() {
			return value;
		}
	}

	private static class OperatorPacket extends AbstractPacket {
		private final Function<List<Packet>, Long> operator;
		private final List<Packet> children = new ArrayList<>();

		public OperatorPacket(int version, BitStream bs, int type) {
			super(version);
			if (bs.readBoolean()) {
				int childrenCount = bs.readInt(11);
				for (int i = 0; i < childrenCount; i++) {
					children.add(PacketParser.parsePacket(bs));
				}
			} else {
				int childrenSize = bs.readInt(15);
				int stop = bs.getBitIndex() + childrenSize;
				while (bs.getBitIndex() < stop) {
					children.add(PacketParser.parsePacket(bs));
				}
			}
			operator = getOperator(type);
		}

		@Override
		public int getTotalVersion() {
			return getVersion() + children.stream().mapToInt(Packet::getTotalVersion).sum();
		}

		@Override
		public long getValue() {
			return operator.apply(children);
		}

		private Function<List<Packet>, Long> getOperator(int type) {
			switch (type) {
			case 0: // sum
				return c -> c.stream().mapToLong(Packet::getValue).sum();
			case 1: // product
				return c -> c.stream().mapToLong(Packet::getValue).reduce(1, (a, b) -> a * b);
			case 2: // min
				return c -> c.stream().mapToLong(Packet::getValue).min().getAsLong();
			case 3: // max
				return c -> c.stream().mapToLong(Packet::getValue).max().getAsLong();
			case 5: // gt
				return c -> c.get(0).getValue() > c.get(1).getValue() ? 1L : 0L;
			case 6: // lt
				return c -> c.get(0).getValue() < c.get(1).getValue() ? 1L : 0L;
			case 7: // eq
				return c -> c.get(0).getValue() == c.get(1).getValue() ? 1L : 0L;
			default:
				throw new IllegalArgumentException("Invalid type " + type);
			}
		}
	}

	private static class PacketParser {
		public static Packet parsePacket(BitStream bs) {
			int version = bs.readInt(3);
			int type = bs.readInt(3);
			if (type == 4) {
				return new LiteralPacket(version, bs);
			} else {
				return new OperatorPacket(version, bs, type);
			}
		}
	}
}
