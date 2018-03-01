
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
      AbstractFunction.pushFlow(inp, outp);
    } catch (IOException e) {
    }
  }
}


