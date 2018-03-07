
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;


final class EchoFunction extends AbstractFunction {
  private String[] args;
  private OutputStream outp;
  private InputStream inp;

  public AbstractFunction clone() {
    return new EchoFunction();
  }
  public void init(String[] args, InputStream inp, OutputStream outp, String path) {
    this.args = args;
    this.inp = inp;
    this.outp = outp;
  }
  public void run() {
    try (OutputStream fakeOutp = outp) {
      for (String str : args) {
        str += " ";
        outp.write(str.getBytes());
      }
      outp.write("\n".getBytes());
      outp.flush();
    } catch (Throwable e) {
    }
  }
}

