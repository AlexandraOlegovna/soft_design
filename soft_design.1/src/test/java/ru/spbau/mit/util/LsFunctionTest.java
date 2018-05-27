package ru.spbau.mit.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class LsFunctionTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
    }


    @Test
    public void test1() throws IOException {
        Executor ex = new Executor();
        ex.execute("ls aaa", null, null);
        assertEquals("no such file or directory...\n", outContent.toString());
    }

    @Test
    public void test2() throws IOException {
        Executor ex = new Executor();
        ex.execute("cd src/main/java", null, null);
        ex.execute("ls", null, null);
        assertEquals("ru\n", outContent.toString());
    }

    @Test
    public void test3() throws IOException {
        Executor ex = new Executor();
        ex.execute("ls src", null, null);
        assertEquals("main\ntest\n", outContent.toString());
    }

    @Test
    public void test4() throws IOException {
        Executor ex = new Executor();
        ex.execute("ls src/main/java", null, null);
        assertEquals("ru\n", outContent.toString());
    }

}
