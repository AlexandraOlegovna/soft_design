package ru.spbau.mit.util;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    public void cdDirNotExists() throws IOException {
        Executor ex = new Executor();
        String path = ex.getPath();
        ex.execute("cd aaa", null, null);
        assertEquals("no such file or directory..." + System.lineSeparator(), outContent.toString());
        assertEquals(path, ex.getPath());
    }

    @Test
    public void cdManyArguments() throws IOException {
        Executor ex = new Executor();
        String path = ex.getPath();
        ex.execute("cd src tests", null, null);
        assertEquals("cd: too many arguments" + System.lineSeparator(), outContent.toString());
        assertEquals(path, ex.getPath());
    }

    @Test
    public void cdSimpleFolder() throws IOException {
        Executor ex = new Executor();
        String path = ex.getPath();
        ex.execute("cd src", null, null);
        assertEquals(path + "src" + File.separator, ex.getPath());
    }

    @Test
    public void cdComplexFolder() throws IOException {
        Executor ex = new Executor();
        String path = ex.getPath();
        ex.execute("cd src" + File.separator + "test", null, null);
        assertEquals(path + "src" + File.separator + "test" + File.separator, ex.getPath());
    }

}
