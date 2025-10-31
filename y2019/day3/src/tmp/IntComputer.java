package tmp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IntComputer
{
    private int memOffset = 0;
    public interface InputProvider
    {
        long generateInput();
    }

    public static class AsciiSequenceInputProvider implements InputProvider
    {
        private final List<String> input = new ArrayList<>();
        private int linePointer = 0;
        private int charPointer = 0;
        private final Scanner scanner;

        public AsciiSequenceInputProvider(Scanner scanner, String... inputValues)
        {
            this.scanner = scanner;
            Stream.of(inputValues).forEach(input::add);
        }

        @Override
        public long generateInput()
        {
            if (linePointer >= input.size())
            {
                input.add(scanner.nextLine());
            }
            if (charPointer == input.get(linePointer).length())
            {
                charPointer = 0;
                linePointer++;
                return 10;
            }
            char nextChar = input.get(linePointer).charAt(charPointer++);
            return (int)nextChar;
        }
    }
    public static class AsciiOutputConsumer implements OutputConsumer
    {

        @Override
        public void consumeOutput(String output)
        {
            int code = Integer.valueOf(output);
            if (code > 255)
            {
                System.out.println(code);
            }
            else
            {
                System.out.print((char)code);
            }
        }
    }
    public static class SequenceInputProvider implements InputProvider
    {
        private final List<Integer> input = new ArrayList<>();
        private int pointer = 0;

        public SequenceInputProvider(int... inputValues)
        {
            IntStream.of(inputValues).forEach(input::add);
        }

        @Override
        public long generateInput()
        {
            return input.get(pointer++);
        }
    }

    public interface OutputConsumer
    {
        void consumeOutput(String output);
    }

    enum VariableMode
    {
        STATIC, ADDRESS, RELATIVE
    }

    private static interface Variable
    {
        void setValue(BigDecimal newValue);
        BigDecimal getValue();
    }

    abstract class AbstractVariable implements Variable
    {
        protected final List<BigDecimal> program;
        protected final BigDecimal value;

        protected AbstractVariable(List<BigDecimal> program, BigDecimal value)
        {
            this.program = program;
            this.value = value;
        }
    }

    class StaticVariable extends AbstractVariable
    {
        protected StaticVariable(List<BigDecimal> program, BigDecimal position)
        {
            super(program, position);
        }

        @Override
        public void setValue(BigDecimal newValue)
        {
            throw new UnsupportedOperationException("Can't set a value for static variable");
        }

        @Override
        public BigDecimal getValue()
        {
            return value;
        }
    }

    class MemoryAddress extends AbstractVariable
    {

        protected MemoryAddress(List<BigDecimal> program, BigDecimal position)
        {
            super(program, position);
            for (int memPointer = program.size() ; memPointer <= position.intValue() ; memPointer++)
            {
                program.add(new BigDecimal(0));
            }
        }

        @Override
        public void setValue(BigDecimal newValue)
        {
            program.set(value.intValue(), newValue);
        }

        @Override
        public BigDecimal getValue()
        {
            return program.get(value.intValue());
        }
    }

    class RelativeAddress extends MemoryAddress
    {
        protected RelativeAddress(List<BigDecimal> program, BigDecimal position, BigDecimal offset)
        {
            super(program, position.add(offset));
        }
    }

    interface Operation
    {
        int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out);
        int getNumberOfParams();
        List<VariableMode> decodeParamModes(int opCode);
    }

    abstract class AbstractOperation implements Operation
    {
        private final int numberOfParameters;
        protected AbstractOperation(int numberOfParameters)
        {
            this.numberOfParameters = numberOfParameters;
        }
        @Override
        public int getNumberOfParams()
        {
            return numberOfParameters;
        }

        @Override
        public List<VariableMode> decodeParamModes(int opCode)
        {
            opCode /= 100;
            List<VariableMode> modes = new ArrayList<>();
            for (int paramIndex = 0 ; paramIndex < getNumberOfParams() ; paramIndex++)
            {
                int mode = opCode % 10;
                switch (mode)
                {
                    case 0:
                        modes.add(VariableMode.ADDRESS);
                        break;
                    case 1:
                        modes.add(VariableMode.STATIC);
                        break;
                    case 2:
                        modes.add(VariableMode.RELATIVE);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown variable mode: " + mode);
                }
                opCode /= 10;
            }
            return modes;
        }

        protected int defaultNewInstructionPointer(int instructionPointer)
        {
            return instructionPointer + getNumberOfParams() + 1;
        }
    }

    class HaltOperation extends AbstractOperation
    {
        protected HaltOperation()
        {
            super(0);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            return Integer.MIN_VALUE;
        }
    }

    class AddOperation extends AbstractOperation
    {
        protected AddOperation()
        {
            super(3);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            parameters.get(2).setValue(parameters.get(0).getValue().add(parameters.get(1).getValue()));
            return defaultNewInstructionPointer(instructionPointer);
        }
    }

    class MulOperation extends AbstractOperation
    {
        protected MulOperation()
        {
            super(3);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            parameters.get(2).setValue(parameters.get(0).getValue().multiply(parameters.get(1).getValue()));
            return defaultNewInstructionPointer(instructionPointer);
        }
    }

    class InOperation extends AbstractOperation
    {
        public InOperation()
        {
            super(1);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            parameters.get(0).setValue(new BigDecimal(in.generateInput()));
            return defaultNewInstructionPointer(instructionPointer);
        }
    }

    class OutOperation extends AbstractOperation
    {
        public OutOperation()
        {
            super(1);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            out.consumeOutput(parameters.get(0).getValue().toString());
            return defaultNewInstructionPointer(instructionPointer);
        }
    }

    class JumpTrueOperation extends AbstractOperation
    {
        public JumpTrueOperation()
        {
            super(2);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            return parameters.get(0).getValue().intValue() != 0 ? parameters.get(1).getValue().intValue() : defaultNewInstructionPointer(instructionPointer);
        }
    }

    class JumpFalseOperation extends AbstractOperation
    {
        public JumpFalseOperation()
        {
            super(2);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            return parameters.get(0).getValue().intValue() == 0 ? parameters.get(1).getValue().intValue() : defaultNewInstructionPointer(instructionPointer);
        }
    }

    class LessThanOperation extends AbstractOperation
    {
        public LessThanOperation()
        {
            super(3);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            parameters.get(2).setValue(parameters.get(0).getValue().compareTo(parameters.get(1).getValue()) < 0 ? new BigDecimal(1) : new BigDecimal(0));
            return defaultNewInstructionPointer(instructionPointer);
        }
    }

    class EqualsOperation extends AbstractOperation
    {
        public EqualsOperation()
        {
            super(3);
        }

        @Override
        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            parameters.get(2).setValue(parameters.get(0).getValue().equals(parameters.get(1).getValue()) ? new BigDecimal(1) : new BigDecimal(0));
            return defaultNewInstructionPointer(instructionPointer);
        }
    }

    class SetOffsetOperation extends AbstractOperation
    {
        public SetOffsetOperation()
        {
            super(1);
        }

        public int execute(List<Variable> parameters, int instructionPointer, InputProvider in, OutputConsumer out)
        {
            memOffset += parameters.get(0).getValue().intValue();
            return defaultNewInstructionPointer(instructionPointer);
        }
    }

    private Operation createOperation(int opCode)
    {
        int operationId = opCode % 100;
        switch (operationId)
        {
            case 1:
                return new AddOperation();
            case 2:
                return new MulOperation();
            case 3:
                return new InOperation();
            case 4:
                return new OutOperation();
            case 5:
                return new JumpTrueOperation();
            case 6:
                return new JumpFalseOperation();
            case 7:
                return new LessThanOperation();
            case 8:
                return new EqualsOperation();
            case 9:
                return new SetOffsetOperation();
            case 99:
                return new HaltOperation();
            default:
                throw new IllegalArgumentException("Unknown operation id: " + operationId);
        }
    }

    private List<Variable> createParameters(List<BigDecimal> program, int firstParamPosition, List<VariableMode> paramModes)
    {
        List<Variable> parameters = new ArrayList<>();
        for (VariableMode paramMode : paramModes)
        {
            Variable newParameter;
            switch (paramMode)
            {
                case STATIC:
                    newParameter = new StaticVariable(program, program.get(firstParamPosition++));
                    break;
                case ADDRESS:
                    newParameter = new MemoryAddress(program, program.get(firstParamPosition++));
                    break;
                case RELATIVE:
                    newParameter = new RelativeAddress(program, program.get(firstParamPosition++), new BigDecimal(memOffset));
                    break;
                default:
                    throw new UnsupportedOperationException("Can't create parameter for mode " + paramMode);
            }
            parameters.add(newParameter);
        }
        return parameters;
    }

    public List<BigDecimal> executeProgram(List<BigDecimal> program, InputProvider in, OutputConsumer out)
    {
        memOffset = 0;
        int operationPointer = 0;
        List<BigDecimal> workingCopy = new ArrayList<>(program);
        while (operationPointer >= 0)
        {
            int opCode = workingCopy.get(operationPointer).intValue();
            Operation operation = createOperation(opCode);
            List<Variable> parameters = createParameters(workingCopy, operationPointer + 1, operation.decodeParamModes(opCode));
            operationPointer = operation.execute(parameters, operationPointer, in, out);
        }
        return workingCopy;
    }
}