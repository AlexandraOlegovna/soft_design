
package ru.spbau.mit.util;

import java.util.*;


public class Parser {
  private Map<String, String> view = new HashMap<String, String>();

  public ArrayList<String[]> parse(String rawInputString) {
    ArrayList<String[]> mtrx = split(rawInputString);
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

  /**
   * @param raw input string
   * @return pipe splitted Array of shell command array
   */
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
}



