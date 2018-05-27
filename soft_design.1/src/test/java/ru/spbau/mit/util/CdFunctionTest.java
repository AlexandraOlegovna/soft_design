package ru.spbau.mit.util;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class CdFunctionTest {

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
        String path = ex.getPath();
        ex.execute("cd aaa", null, null);
        assertEquals("no such file or directory...\n", outContent.toString());
        assertEquals(path, ex.getPath());
    }

    @Test
    public void test2() throws IOException {
        Executor ex = new Executor();
        String path = ex.getPath();
        ex.execute("cd src tests", null, null);
        assertEquals("cd: too many arguments\n", outContent.toString());
        assertEquals(path, ex.getPath());
    }

    @Test
    public void test3() throws IOException {
        Executor ex = new Executor();
        String path = ex.getPath();
        ex.execute("cd src", null, null);
        assertEquals(path + "src/", ex.getPath());
    }

    @Test
    public void test4() throws IOException {
        Executor ex = new Executor();
        String path = ex.getPath();
        ex.execute("cd src/test", null, null);
        assertEquals(path + "src/test/", ex.getPath());
    }

}
