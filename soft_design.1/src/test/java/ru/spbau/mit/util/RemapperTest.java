package ru.spbau.mit.util;

import org.junit.Test;

import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;

public class RemapperTest {

    @Test
    public void run() throws IOException, InterruptedException {
        final int sourceSize = 8192;
        Random random = new Random();
        byte[] source = new byte[sourceSize];
        random.nextBytes(source);

        ByteArrayInputStream bais = new ByteArrayInputStream(source);
        PipedOutputStream outp = new PipedOutputStream();
        PipedInputStream inp = new PipedInputStream(outp);
        Thread threadRemapper = new Thread(new Remapper(bais, outp));
        threadRemapper.start();
        for (int i = 0; i < sourceSize; i++) {
            byte data = (byte)inp.read();
            assertEquals(source[i], data);
        }
        threadRemapper.join();
    }
}
