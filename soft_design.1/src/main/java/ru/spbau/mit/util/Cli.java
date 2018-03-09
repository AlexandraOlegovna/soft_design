
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;


public class Cli {
  private volatile InputStream inp;
  private Executor executor;

  public Cli(InputStream inp, Executor executor) {
    this.inp = new SpaceAddInputStream(inp);
    this.executor = executor;
  }

  public void loop() throws IOException {
    Scanner sc = new Scanner(inp);
    while (true) {
      System.out.printf("%s > ", executor.getPath());
      if (sc.hasNextLine()) {
        String line = sc.nextLine();
        executor.execute(line, inp, System.out);
      } else {
        System.out.println("no more data \n");
        break;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    Executor executor = new Executor();
    executor.put("echo", new EchoFunction());
    executor.put("cat", new CatFunction());
    Cli cli = new Cli(System.in, executor);
    cli.loop();
  }

}

