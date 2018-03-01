
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;


public final class Executor {
  private Map<String, AbstractFunction> functionBox = new HashMap<String, AbstractFunction>();
  private AbstractFunction defaultFunction = new ProcessFunction();

  public AbstractFunction put(String functionName, AbstractFunction function) {
    return functionBox.put(functionName, function);
  }

  public AbstractFunction remove(String functionName) {
    return functionBox.remove(functionName);
  }

  public void execute(ArrayList<String[]> mtrx, InputStream startInp, OutputStream endOutp) throws IOException {
    for (String[] shell : mtrx) {
      if (shell == null || shell.length == 0) {
        System.out.println("shell: empty command...");
        return;
      }
    }

    if (mtrx.size() == 1 && mtrx.get(0)[0].equals("exit")) {
      throw new RuntimeException("good bye. I will miss you...");
    }

    InputStream inp = startInp;
    try {
      for (final String[] shell : mtrx) {
        AbstractFunction cmd = null;
        String[] args = null;
        if (functionBox.containsKey(shell[0])) {
          cmd = functionBox.get(shell[0]).clone();
          LinkedList<String> listArgs = new LinkedList<String>(Arrays.asList(shell));
          listArgs.remove(0);
          args = new String[listArgs.size()];
          args = listArgs.toArray(args);
        } else {
          cmd = defaultFunction.clone();
          args = shell;
        }

        PipedOutputStream outp = new PipedOutputStream();
        cmd.init(args, inp, outp);
        new Thread(cmd).start();

        inp = (InputStream) new PipedInputStream(outp);
      }
    } catch (IOException e) {
      System.out.println("error in pipes");
      return;
    }

    if (inp != null) {
      Scanner sc = new Scanner(inp);
      while (sc.hasNextLine()) {
        String nextLine = sc.nextLine() + "\n";
        endOutp.write(nextLine.getBytes());
        endOutp.flush();
      }
    }
  }


  public static void main(String[] args) throws IOException {
    Executor fs = new Executor();
    fs.put("echo", new EchoFunction());
    fs.put("cat", new CatFunction());

    String[] cmd1 = {"echo", "5 + ", "    18"};
    String[] cmd2 = {"bc"};
    ArrayList<String[]> expression = new ArrayList<String[]>();
    expression.add(cmd1);
    expression.add(cmd2);

    fs.execute(expression, System.in, System.out);
  }

}


