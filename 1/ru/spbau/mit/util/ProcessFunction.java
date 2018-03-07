
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;


final class ProcessFunction extends AbstractFunction {
  private String command;
  private InputStream inp;
  private OutputStream outp;
  private ProcessBuilder pb;

  public AbstractFunction clone() {
    return new ProcessFunction();
  }
  public void init(String[] shell, InputStream inp, OutputStream outp, String path) throws IOException {
    this.inp = inp;
    this.outp = outp;
    this.command = shell[0];

    StringBuilder sb = new StringBuilder();
    String args = null;
    if (1 < shell.length) {
      for (int i = 1; i < shell.length; i++) {
        String s = shell[i];
        if (s.matches("\\s")) {
          s = "\'" + s + '\'';
        }
        sb.append(s).append(" ");
      }
      args = sb.toString();
    }

    if (args != null) {
      System.out.println("cmd = " + command + " " + args);
      this.pb = new ProcessBuilder("bash", "-c", command + " " + args);
    } else {
      this.pb = new ProcessBuilder(command);
    }
    pb.redirectErrorStream(true);
    pb.directory(new File(path));
  }

  public void run() {
    Process process = null;
    try {
      process = pb.start();
    } catch (Throwable e) {
      System.out.printf("shell: %s: command not find...\n", command);
      try {outp.close();} catch (IOException ee) {}
      return;
    }
    Thread threadRemapper = null;
    try (OutputStream fakeOut = outp) {
      OutputStream processIn = process.getOutputStream();
      InputStream processOut = process.getInputStream();
      threadRemapper = new Thread(new Remapper(inp, processIn));
      threadRemapper.start();
      Remapper.pushFlow(processOut, outp);
      int errCode = process.waitFor();
    } catch (Exception e) {
    } finally {
      if (threadRemapper != null) {
        threadRemapper.interrupt();
      }
    }
  }

}


