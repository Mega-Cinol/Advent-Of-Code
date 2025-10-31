package y2020.day18;

import java.util.ArrayDeque;
import java.util.Deque;

import common.Input;

public class Day18 {

	public static void main(String[] args) {
		long sum = Input.parseLines("y2020/day18/day18.txt", Day18::addParenthesis).mapToLong(exp -> evaluate(exp)).sum();
		System.out.println(sum);
		evaluate("0", new LoopPointer(0));
	}

	private static String addParenthesis(String expresion)
	{
		return expresion;
	}

	private static long evaluate(String expression)
	{
		Deque<RpnNode> rpnExpression = toRpn(expression);
		RpnResult result = new RpnResult();
		rpnExpression.stream().forEach(node -> node.execute(result));
		return result.getResult();
	}

	private static Deque<RpnNode> toRpn(String expression)
	{
		Deque<RpnOperator> operatorStack = new ArrayDeque<>();
		Deque<RpnNode> result = new ArrayDeque<>();
		expression.chars().forEach(token -> {
			char charToken = (char)token;
			switch (charToken) {
			case ' ':
				return;
			case '+':
			case '*':
				RpnOperator operator = RpnOperator.fromChar(charToken);
				RpnOperator last = operatorStack.peek();
				while (last != null && last.hasHigherPrio(operator))
				{
					result.add(operatorStack.pop());
					last = operatorStack.peek();
				}
				operatorStack.push(operator);
				break;
			case '(':
				operatorStack.push(new RpnParenthesis());
				break;
			case ')':
				last = operatorStack.peek();
				while (!(last instanceof RpnParenthesis))
				{
					result.add(operatorStack.pop());
					last = operatorStack.peek();
				}
				operatorStack.pop();
				break;
			default:
				result.add(new RpnNumber(charToken));
				break;
			}
		});
		while (!operatorStack.isEmpty())
		{
			result.add(operatorStack.pop());
		}
		return result;
	}

	private static class RpnResult
	{
		private final Deque<Long> values = new ArrayDeque<>();
		public void store(long value)
		{
			values.push(value);
		}
		public void reduce(RpnOperator operator)
		{
			while (!operator.hasOperands())
			{
				operator.addOperand(values.pop());
			}
			values.push(operator.evaluate());
		}
		public long getResult()
		{
			return values.pop();
		}
	}
	private interface RpnNode
	{
		void execute(RpnResult result);
	}
	public static abstract class RpnOperator implements RpnNode
	{
		private final Deque<Long> operands = new ArrayDeque<>();
		private final int noOfOperands;
		private final int prio;
		public RpnOperator(int noOfOperands, int prio)
		{
			this.noOfOperands = noOfOperands;
			this.prio = prio;
		}
		public void addOperand(long operand)
		{
			operands.add(operand);
		}
		public boolean hasOperands()
		{
			return operands.size() == noOfOperands;
		}

		@Override
		public void execute(RpnResult result) {
			result.reduce(this);
		}
		public long evaluate()
		{
			return evaluate(operands);
		}
		protected abstract long evaluate(Deque<Long> operands);

		public boolean hasHigherPrio(RpnOperator other)
		{
			return prio > other.prio;
		}
		public static RpnOperator fromChar(char symbol)
		{
			switch (symbol) {
			case '+':
				return new RpnAdd();
			case '*':
				return new RpnMul();
			}
			return null;
		}
	}
	private static class RpnNumber implements RpnNode
	{
		private final long number;
		public RpnNumber(char number)
		{
			this.number = number - '0';
		}
		@Override
		public void execute(RpnResult result) {
			result.store(number);
		}
		@Override public String toString() { return "" + number;}
	}

	private static class RpnParenthesis extends RpnOperator
	{
		public RpnParenthesis() {
			super(0, -1);
		}

		@Override
		protected long evaluate(Deque<Long> operands) {
			throw new UnsupportedOperationException();
		}
		@Override public String toString() { return "(";}
	}
	private static class RpnAdd extends RpnOperator
	{
		public RpnAdd() {
			super(2, 1);
		}

		@Override
		protected long evaluate(Deque<Long> operands) {
			return operands.pop() + operands.pop();
		}
		@Override public String toString() { return "+";}
	}

	private static class RpnMul extends RpnOperator
	{
		public RpnMul() {
			super(2, 0);
		}

		@Override
		protected long evaluate(Deque<Long> operands) {
			return operands.pop() * operands.pop();
		}
		@Override public String toString() { return "*";}
	}

	private static long evaluate(String expresion, LoopPointer loopPointer)
	{
		long result = 0;
		boolean addOperator = true;
		LoopPointer i = new LoopPointer(0);
		i.setMax(expresion.length() - 1);
		for (; i.shouldContinue() ; i.step())
		{
			switch (expresion.charAt(i.getPos())) {
			case ' ':
				continue;
			case '+':
				addOperator = true;
				break;
			case '*':
				addOperator = false;
				break;
			case '(':
				if (addOperator)
				{
					result += evaluate(expresion.substring(i.getPos() + 1), i);
				}
				else
				{
					result *= evaluate(expresion.substring(i.getPos() + 1), i);
				}
				break;
			case ')':
				loopPointer.jump(i.getPos() + 1);
				return result;
			default:
				if (addOperator)
				{
					result += Integer.parseInt(expresion.substring(i.getPos(), i.getPos() + 1));
				}
				else
				{
					result *= Integer.parseInt(expresion.substring(i.getPos(), i.getPos() + 1));
				}
				break;
			}
		}
		return result;
	}

	private static class LoopPointer
	{
		private int pos;
		private int max;
		public LoopPointer(int start)
		{
			pos = start;
		}
		public void step()
		{
			pos++;
		}
		public boolean shouldContinue()
		{
			return pos <= max;
		}
		public void setMax(int max)
		{
			this.max = max;
		}
		public int getPos()
		{
			return pos;
		}
		public void jump(int step)
		{
			pos += step;
		}
	}
}
