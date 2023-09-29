package me.blendy.brainfcraft;

import java.util.Stack;

public interface Interpreter {
    /**
     * @return The current pointer to the instruction about to be executed in the source code
     */
    public int getInstructionPointer();
    /**
     * @return The current pointer to a memory cell
     */
    public int getMemoryPointer();
    /**
     * @return The current pointer to the input data
     */
    public int getInputPointer();

    /**
     * @return The maximum time the interpreter can run before forcibly stopping
     */
    public int getMaxTime();
    /**
     * @return The current stack of loops mapping [ source addresses to ] addresses
     */
    public Stack<Integer> getLoops();
    /**
     * @return The source code being interpreted (truncated to valid chars only)
     */
    public char[] getSource();
    /**
     * @return The input char[] the program will use for input
     */
    public char[] getInput();
    /**
     * Starts the interpretation of the source code
     * @return The output string, fabricated by concatenating outputs
     */
    public String process();
    /**
     * @return The amount of cells created for the code interpretation
     */
    public int getMemorySize();
    /**
     * @return The current state of memory, each byte of the array being a cell
     */
    public byte[] getMemory();
}
