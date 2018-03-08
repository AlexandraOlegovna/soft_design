
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;
import java.nio.file.Paths;


public final class Executor {
  private Map<String, AbstractFunction> functionBox = new HashMap<String, AbstractFunction>();
  private AbstractFunction defaultFunction = new ProcessFunction();
  private String path = Paths.get(".").toAbsolutePath().normalize().toString() + '/';

  public AbstractFunction put(String functionName, AbstractFunction function) {
    return functionBox.put(functionName, function);
  }

  public AbstractFunction remove(String functionName) {
    return functionBox.remove(functionName);
  }

  public String getPath() {
    return path;
  }

  public void execute(ArrayList<String[]> mtrx, InputStream startInp, OutputStream endOutp) throws IOException {
    if (!isNeedExternalExecute(mtrx)) {
      return;
    }

    List<Thread> subProcessList = new ArrayList<Thread>();
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
        cmd.init(args, inp, outp, path);
        Thread subProcess = new Thread(cmd);
        subProcessList.add(subProcess);
        subProcess.start();

        inp = (InputStream) new PipedInputStream(outp);
      }
    } catch (IOException e) {
      System.out.println("error in pipes");
      inp = null;
    }

    InputStream lastStream = inp;
    if (lastStream != null) {
      Scanner sc = new Scanner(lastStream);
      while (sc.hasNextLine()) {
        String nextLine = sc.nextLine() + "\n";
        endOutp.write(nextLine.getBytes());
        endOutp.flush();
      }
    }
    interruptAndJoin(subProcessList);
  }


  private boolean isNeedExternalExecute(ArrayList<String[]> mtrx) {
    for (String[] shell : mtrx) {
      if (shell == null || shell.length == 0) {
        System.out.println("shell: empty command...");
        return false;
      }
    }

    if (mtrx.size() == 0) {
      return false;
    }

    if (mtrx.size() == 1) {
      if (mtrx.get(0)[0].equals("exit")) {
        System.out.println("good bye. I will miss you...");
        System.out.flush();
        System.exit(0);
      }
      if (mtrx.get(0)[0].equals("cd")) {
        String newDirName = mtrx.get(0)[1];
        File newDir = new File(path + newDirName);
        if (!newDir.exists()) {
          System.out.println("cd: no such file or directory...");
        } else if (!newDir.isDirectory()) {
          System.out.printf("cd: \"%s\" is not a directory...\n", newDirName);
        } else {
          path = newDir.toPath().toAbsolutePath().normalize().toString() + '/';
        }
        return false;
      }
    }
    return true;
  }


  private void interruptAndJoin(List<Thread> subProcessList) {
    for (Thread subProcess : subProcessList) {
      if (subProcess != null) {
        subProcess.interrupt();
      }
    }
    for (Thread subProcess : subProcessList) {
      try {
        subProcess.join();
      } catch (InterruptedException e) {
      }
    }
  }

}


