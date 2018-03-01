
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;

public abstract class AbstractFunction implements Runnable {
  public abstract AbstractFunction clone();
  public abstract void init(String[] args, InputStream inp, OutputStream outp) throws IOException;

  public static void pushFlow(InputStream inp, OutputStream outp) throws IOException {
    int data = ' ';
    while (data != -1) {
      outp.write(data);
      outp.flush();
      if (inp != null) {
        data = inp.read();
      }
    }
  }
}


final class EchoFunction extends AbstractFunction {
  private String[] args;
  private OutputStream outp;
  private InputStream inp;

  public AbstractFunction clone() {
    return new EchoFunction();
  }
  public void init(String[] args, InputStream inp, OutputStream outp) {
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



final class CatFunction extends AbstractFunction {
  private String[] args;
  private InputStream inp;
  private OutputStream outp;

  public AbstractFunction clone() {
    return new CatFunction();
  }
  public void init(String[] args, InputStream inp, OutputStream outp) {
    this.args = args;
    this.inp = inp;
    this.outp = outp;
  }
  public void run() {
    try {
      if (args == null || args.length == 0) {
        pushFlow(inp, outp);
        outp.write("\n".getBytes());
      } else {
        for (String fileName : args) {
          try (InputStream finp = new FileInputStream(fileName)) {
            pushFlow(finp, outp);
            outp.write("\n".getBytes());
          } catch (FileNotFoundException e) {
            outp.write(("cat: " + fileName + ": file not found\n").getBytes());
          }
        }
      }
      outp.close();
    } catch (IOException e) {
    }
  }
}


final class ProcessFunction extends AbstractFunction {
  private String command;
  private InputStream inp;
  private OutputStream outp;
  private ProcessBuilder pb;

  public AbstractFunction clone() {
    return new ProcessFunction();
  }
  public void init(String[] shell, InputStream inp, OutputStream outp) throws IOException {
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
    try (OutputStream fakeOut = outp) {
      OutputStream processIn = process.getOutputStream();
      InputStream processOut = process.getInputStream();
      Thread t = new Thread(new Remapper(inp, processIn));
      t.start();
      pushFlow(processOut, outp);
      int errCode = process.waitFor();
      t.interrupt();
    } catch (Exception e) {
    }
  }

}


