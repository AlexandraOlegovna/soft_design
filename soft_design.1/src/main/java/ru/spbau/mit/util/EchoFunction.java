
package ru.spbau.mit.util;

import java.io.*;


final class EchoFunction extends AbstractFunction {
  private String[] args;
  private OutputStream outputStream;

  @Override
  public AbstractFunction clone() {
    return new EchoFunction();
  }

  @Override
  public void init(String[] args, InputStream inp, OutputStream outp, String path) {
    this.args = args;
    this.outputStream = outp;
  }

  public void run() {
    try (OutputStream fakeOutput = outputStream) {
      for (String str : args) {
        str += " ";
        outputStream.write(str.getBytes());
      }
      outputStream.write("\n".getBytes());
      outputStream.flush();
    } catch (Throwable e) {
    }
  }
}

