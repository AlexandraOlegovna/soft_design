package ru.spbau.mit.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;
import java.util.Scanner;

import static org.junit.Assert.*;

public class EchoFunctionTest {

    @Test
    public void run() throws IOException, InterruptedException {
        String[] args = {"one", "two", "three"};
        PipedOutputStream outp = new PipedOutputStream();
        PipedInputStream inp = new PipedInputStream(outp);
        Scanner sc = new Scanner(inp);

        EchoFunction echoFunction = new EchoFunction();
        echoFunction.init(args, null, outp, null);
        Thread threadEcho = new Thread(echoFunction);
        threadEcho.start();

        assertTrue(sc.hasNextLine());
        String line = sc.nextLine();
        assertEquals(line, "one two three ");
        threadEcho.join();
    }
}
