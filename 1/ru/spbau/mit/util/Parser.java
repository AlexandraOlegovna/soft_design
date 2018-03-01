
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;
import java.nio.file.Paths;


public class Parser {
  private Map<String, String> view = new HashMap<String, String>();
  private Executor executor = new Executor();
  private boolean debug = false;

  public Parser() {
    executor.put("echo", new EchoFunction());
    executor.put("cat", new CatFunction());
  }
  public Parser(boolean debug) {
    this();
    this.debug = debug;
  }

  public void loop() throws IOException {
    Scanner sc = new Scanner(System.in);
    while (true) {
      String dir = Paths.get(".").toAbsolutePath().normalize().toString();
      System.out.printf("%s > ", dir);
      if (sc.hasNextLine()) {
        String line = sc.nextLine();
        ArrayList<String[]> mtrx = parse(line);
        if (mtrx.size() == 0) {
          continue;
        }

        if (debug) {
          for (String[] shell : mtrx) {
            System.out.printf("shell start: shell.length = %s\n", shell.length);
            for (String word : shell) {
              System.out.printf("(%s) ", word);
            }
            System.out.println("\n");
          }
        }

        executor.execute(mtrx, System.in, System.out);
      } else {
        System.out.println("no more data \n");
        break;
      }
    }
  }

  public ArrayList<String[]> parse(String s) {
    ArrayList<String[]> mtrx = split(s);
    boolean hasPipe = mtrx.size() > 1;

    for (int shellIndex = 0; shellIndex < mtrx.size(); shellIndex++) {
      String[] shell = mtrx.get(shellIndex);
      for (int i = 0; i < shell.length; i++) {
        String word = shell[i];
        if (word.length() > 0 && word.charAt(0) == '$') {
          String key = word.substring(1);
          String newWord = view.get(key);
          if (newWord == null) {
            newWord = "";
          }
          shell[i] = newWord;
        }
      }
      if (0 < shell.length) {
        String cmd = shell[0];
        int indexOf = cmd.indexOf('=');
        if (0 < indexOf) {
          mtrx.remove(shellIndex);
          shellIndex--;
          if (!hasPipe && indexOf < (cmd.length() - 1)) {
            String newVar = cmd.substring(0, indexOf);
            String newValue = cmd.substring(indexOf + 1);
            view.put(newVar, newValue);
          }
        }
      }
    }
    return mtrx;
  }

  private static ArrayList<String[]> split(String raw) {
    ArrayList<String[]> structured = new ArrayList<String[]>();
    ArrayList<String> localShell = new ArrayList<String>();
    int startQuotesIndex = raw.length();
    int startShellIndex = 0;
    int startWordIndex = 0;
    boolean inSingleQuotes = false;
    boolean inDoubleQoutes = false;
    boolean inWord = false;
    for (int index = 0; index < raw.length(); index++) {
      if (index == 0 || raw.charAt(index-1) != '\\') {
        if (raw.charAt(index) == '\'' && !inDoubleQoutes) {
          inSingleQuotes = !inSingleQuotes;
          if (inSingleQuotes) {
            startQuotesIndex = index + 1;
          } else if (!inWord) {
            localShell.add(raw.substring(startQuotesIndex, index));
          }
        }
        else if (raw.charAt(index) == '"' && !inSingleQuotes) {
          inDoubleQoutes = !inDoubleQoutes;
          if (inDoubleQoutes) {
            startQuotesIndex = index + 1;
          } else if (!inWord) {
            localShell.add(raw.substring(startQuotesIndex, index));
          }
        }
        else if (!inSingleQuotes && !inDoubleQoutes) {
          if (Character.isWhitespace(raw.charAt(index))) {
            if (inWord) {
              localShell.add(raw.substring(startWordIndex, index));
              inWord = false;
            }
          }
          else if (raw.charAt(index) == '|') {
            String[] shell = new String[localShell.size()];
            structured.add(localShell.toArray(shell));
            localShell.clear();
          }
          else {
            if (!inWord) {
              inWord = true;
              startWordIndex = index;
            }
          }
        }
      }
    }
    if (inWord) {
      localShell.add(raw.substring(startWordIndex));
    }
    if (inSingleQuotes || inDoubleQoutes) {
      System.out.println("error parse: not closed qoute");
      return new ArrayList<String[]>();
    }
    if (localShell.size() != 0) {
      String[] shell = new String[localShell.size()];
      structured.add(localShell.toArray(shell));
    }
    return structured;
  }

  public static void main(String[] args) {
    Parser p = new Parser();
    try {
      p.loop();
    } catch (Throwable e) {
      System.out.println(e.getMessage());
    }
  }
}



