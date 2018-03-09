package ru.spbau.mit.util;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.*;

public class ProcessFunctionTest {

    @Test
    public void run() throws IOException, InterruptedException {
        String[] shell = {"echo", "one", "two   with   addition", "three"};
        PipedOutputStream outp = new PipedOutputStream();
        InputStream inp = new PipedInputStream(outp);
        String path = Paths.get(".").toAbsolutePath().normalize().toString() + '/';
        Scanner sc = new Scanner(inp);

        AbstractFunction processFunction = new ProcessFunction();
        processFunction.init(shell, null, outp, path);
        Thread threadProcess = new Thread(processFunction);
        threadProcess.start();

        assertTrue(sc.hasNextLine());
        String line = sc.nextLine();
        assertEquals(line, "one two   with   addition three");

        threadProcess.join();
    }
}
