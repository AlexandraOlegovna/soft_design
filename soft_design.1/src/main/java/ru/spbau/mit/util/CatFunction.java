
package ru.spbau.mit.util;

import java.io.*;


final class CatFunction extends AbstractFunction {
  private String[] args;
  private InputStream inp;
  private OutputStream outp;
  private String path;

  @Override
  public AbstractFunction clone() {
    return new CatFunction();
  }

  @Override
  public void init(String[] args, InputStream inp, OutputStream outp, String path) {
    this.args = args;
    this.inp = inp;
    this.outp = outp;
    this.path = path;
  }

  public void run() {
    try {
      if (args == null || args.length == 0) {
        Remapper.pushFlow(inp, outp);
      } else {
        for (String fileName : args) {
          String fullFileName = path + fileName;
          try (InputStream finp = new FileInputStream(fullFileName)) {
            Remapper.pushFlow(finp, outp);
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


