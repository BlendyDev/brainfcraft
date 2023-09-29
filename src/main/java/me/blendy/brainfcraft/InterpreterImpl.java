package me.blendy.brainfcraft;

import org.bukkit.ChatColor;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class InterpreterImpl implements Interpreter {
    private int memoryPointer = 0;
    private int instructionPointer = 0;
    private int inputPointer = 0;
    private final int maxTime;
    private long startTimestamp = -1;
    private final Stack<Integer> loops = new Stack<>();
    private final char[] source,input;
    private final byte[] memory;
    private final StringBuilder output;
    private final Map<Integer, Integer> loopCache = new HashMap<>();
    private enum BFInstruction {
        ADD('+'),
        SUBTRACT('-'),
        LOOP_START('['),
        LOOP_END(']'),
        NEXT_CELL('>'),
        PREVIOUS_CELL('<'),
        INPUT(','),
        OUTPUT('.');
        private final char value;
        BFInstruction(char value) {
            this.value = value;
        }
        public char getValue () {
            return value;
        }
        public static BFInstruction fromValue(char value) {
            for (BFInstruction instruction : BFInstruction.values()) {
                if (value == instruction.getValue()) return instruction;
            }
            return null;
        }
    }
    public InterpreterImpl(int memorySize, int maxTime, char[] source, char[] input) {
        memory = new byte[memorySize];
        this.maxTime = maxTime;
        Arrays.fill(memory, (byte) 0);
        this.source = optimizeSource(source);
        this.input = input;
        output = new StringBuilder();
    }
    @Override
    public int getMemoryPointer() {
        return memoryPointer;
    }
    @Override
    public int getInstructionPointer() {
        return instructionPointer;
    }
    @Override
    public int getInputPointer() {
        return inputPointer;
    }

    @Override
    public int getMaxTime() {
        return 0;
    }

    @Override
    public Stack<Integer> getLoops() {
        return loops;
    }

    @Override
    public char[] getSource() {
        return source;
    }
    @Override
    public int getMemorySize() {
        return memory.length;
    }

    @Override
    public byte[] getMemory() {
        return memory;
    }
    @Override
    public char[] getInput() {
        return input;
    }

    @Override
    public String process() {
        if (!validateSource()) return ChatColor.RED + "Source code is invalid";
        if (startTimestamp != -1) return ChatColor.RED + "Same instance simultaneous interpretation is not supported";
        startTimestamp = System.currentTimeMillis();
        while (instructionPointer < getSource().length) {
            if (System.currentTimeMillis()-startTimestamp > maxTime) {
                return ChatColor.RED + "The program forcibly finished after " + maxTime + " ms";
            }
            try {
                executeInstruction(Objects.requireNonNull(BFInstruction.fromValue(source[instructionPointer])));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        startTimestamp = -1;
        return output.toString();
    }
    private boolean validateSource () {
        int openingBraces = 0, closingBraces = 0;
        for (char c : source) {
            if (c == BFInstruction.LOOP_START.value) openingBraces++;
            if (c == BFInstruction.LOOP_END.value) closingBraces++;
        }
        return openingBraces == closingBraces;
    }
    private char[] optimizeSource (char[] rawSource) {
        StringBuilder optimizedSource = new StringBuilder();
        for (char c : rawSource) {
            if (!Arrays.stream(BFInstruction.values()).map(BFInstruction::getValue).collect(Collectors.toSet()).contains(c)) continue;
            optimizedSource.append(c);
        }
        return optimizedSource.toString().toCharArray();
    }
    private int endLoopLookAhead(int start, int depth) {
        int localPointer = start;
        int found = 0;
        while (depth > found) {
            localPointer++;
            if (source[localPointer] == BFInstruction.LOOP_END.getValue()) found++;
        }
        loopCache.put(start, localPointer);
        return localPointer;
    }
    private void executeInstruction(BFInstruction instruction) throws UnsupportedEncodingException {
        int[] intArray = new int[memory.length];

        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = memory[i] & 0xFF;
        }
        System.out.println("memory: "+ Arrays.toString(intArray) + "\ndepth: " + loops.size() + "\nexecuting instruction " + instruction.toString());
        switch (instruction) {
            case ADD -> {
                memory[memoryPointer]++;
                instructionPointer++;
            }
            case SUBTRACT -> {
                memory[memoryPointer]--;
                instructionPointer++;
            }
            case LOOP_START -> {
                loops.push(instructionPointer);
                int endAddress = loopCache.containsKey(instructionPointer) ? loopCache.get(instructionPointer) : endLoopLookAhead(instructionPointer, loops.size());
                if (memory[memoryPointer] == 0) instructionPointer = endAddress;
                else instructionPointer++;

            }
            case LOOP_END -> {
                if (memory[memoryPointer] != 0) instructionPointer = loops.pop();
                else {
                    loops.pop();
                    instructionPointer++;
                }
            }
            case NEXT_CELL -> {
                memoryPointer++;
                if (memoryPointer == getMemorySize()) memoryPointer = 0;
                instructionPointer++;
            }
            case PREVIOUS_CELL -> {
                memoryPointer--;
                if (memoryPointer < 0) memoryPointer = getMemorySize()-1;
                instructionPointer++;
            }
            case INPUT -> {
                if (inputPointer == input.length) inputPointer = 0;
                memory[memoryPointer] = (byte) input[inputPointer];
                inputPointer++;
                instructionPointer++;
            }
            case OUTPUT -> {
                output.append(new String(new byte[]{memory[memoryPointer]}, StandardCharsets.ISO_8859_1));
                instructionPointer++;
            }
        }
    }
}
