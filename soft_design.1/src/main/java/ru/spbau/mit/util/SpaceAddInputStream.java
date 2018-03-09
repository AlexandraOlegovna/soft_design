
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;


class SpaceAddInputStream extends InputStream {
  private volatile Scanner sc;
  private volatile StringReader sr;

  public SpaceAddInputStream(InputStream inp) {
    sc = new Scanner(inp);
    sr = new StringReader("");
  }

  @Override
  public int available() throws IOException {
    synchronized (this) {
      int realSkip = 0;
      if (sr.ready()) {
        sr.mark(8192);
        realSkip = (int)sr.skip(8190);
        sr.reset();
      }
      return realSkip;
    }
  }

  @Override
  public void close() {
  }

  @Override
  public void mark(int readLimit) {
    synchronized (this) {
      try {
        sr.mark(readLimit);
      } catch (IOException e) {}
    }
  }

  @Override
  public boolean markSupported() {
    synchronized (this) {
      return sr.markSupported();
    }
  }

  @Override
  public void reset() throws IOException {
    synchronized (this) {
      sr.reset();
    }
  }

  @Override
  public long skip(long ns) throws IOException {
    synchronized (this) {
      return sr.skip(ns);
    }
  }

  @Override
  public int read() throws IOException {
    synchronized (this) {
      int ret = sr.read();
      if (ret == -1 && sc.hasNextLine()) {
        String line = " " + sc.nextLine() + "\n";
        sr = new StringReader(line);
        ret = sr.read();
      }
      if (ret == -1) {
        sc = new Scanner(System.in);
      }
      return ret;
    }
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    char[] buffer = new char[b.length];
    int realRead = -1;
    synchronized (this) {
      if (available() > 0) {
        realRead = sr.read(buffer, 0, len);
      } else {
        int data = read();
        if (data >= 0) {
          buffer[0] = (char)data;
          realRead = sr.read(buffer, 1, len-1) + 1;
        }
      }
      for (int index = 0; index < realRead; index++) {
        char c = buffer[index];
        b[off + index] = (byte)c;
      }
      return realRead;
    }
  }

}


