package ru.spbau.mit.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    public void lsDirNotExists() throws IOException {
        Executor ex = new Executor();
        ex.execute("ls aaa", null, null);
        assertEquals("no such file or directory..." + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void lsNoArguments() throws IOException {
        Executor ex = new Executor();
        ex.execute("cd src" + File.separator + "main" + File.separator + "java", null, null);
        ex.execute("ls", null, null);
        assertEquals("ru" + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void lsSimpleFolder() throws IOException {
        Executor ex = new Executor();
        ex.execute("ls src", null, null);
        assertEquals("main" + System.lineSeparator()+ "test" + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void lsComplexFolder() throws IOException {
        Executor ex = new Executor();
        ex.execute("ls src"+ File.separator + "main" + File.separator + "java", null, null);
        assertEquals("ru" + System.lineSeparator(), outContent.toString());
    }

}
