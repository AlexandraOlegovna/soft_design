
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;

public class Remapper implements Runnable {
  private InputStream inp;
  private OutputStream outp;

  public Remapper(InputStream inp, OutputStream outp) {
    this.inp = inp;
    this.outp = outp;
  }
  public void run() {
    try (OutputStream fakeOut = outp) {
      pushFlow(inp, outp);
    } catch (IOException e) {
    }
  }

  public static void pushFlow(InputStream inp, OutputStream outp) throws IOException {
    if (inp == null) {
      return;
    }
    int data = inp.read();
    while (data != -1) {
      outp.write(data);
      outp.flush();
      if (Thread.interrupted()) {
        long available = inp.available();
        inp.skip(available);
        break;
      }
      data = inp.read();
    }
  }

}

