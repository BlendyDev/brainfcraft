package me.blendy.brainfcraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterImplTest {

    Interpreter interpreter = new InterpreterImpl(10, 100, "+>+[,+[-.[-]>]<-]".toCharArray(), "eyyyÿ".toCharArray());
    @Test
    void process() {
        assertEquals(interpreter.process(), "eyyy");
    }
}