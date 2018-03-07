
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;

public abstract class AbstractFunction implements Runnable {
  public abstract AbstractFunction clone();
  public abstract void init(String[] args, InputStream inp, OutputStream outp, String path) throws IOException;
}


