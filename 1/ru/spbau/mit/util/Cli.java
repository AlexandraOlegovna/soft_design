
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;


public class Cli {
  private Executor executor;
  private Parser parser = new Parser();
  private volatile InputStream inp = new SpaceAddInputStream();

  public Cli(Executor executor) {
    this.executor = executor;
  }

  public void loop() throws IOException {
    Scanner sc = new Scanner(inp);
    while (true) {
      System.out.printf("%s > ", executor.getPath());
      if (sc.hasNextLine()) {
        String line = sc.nextLine();
        ArrayList<String[]> mtrx = parser.parse(line);
        if (mtrx.size() == 0) {
          continue;
        }
        executor.execute(mtrx, inp, System.out);
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
    Cli cli = new Cli(executor);
    cli.loop();
  }

}



