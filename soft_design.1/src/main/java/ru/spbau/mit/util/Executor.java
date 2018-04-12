
package ru.spbau.mit.util;

import java.util.*;
import java.io.*;
import java.nio.file.Paths;


public final class Executor {
    private Map<String, AbstractFunction> functionBox = new HashMap<String, AbstractFunction>();
    private AbstractFunction defaultFunction = new ProcessFunction();
    private String path = Paths.get(".").toAbsolutePath().normalize().toString() + '/';
    private Parser parser = new Parser();

    public AbstractFunction put(String functionName, AbstractFunction function) {
        return functionBox.put(functionName, function);
    }

    public AbstractFunction remove(String functionName) {
        return functionBox.remove(functionName);
    }

    public String getPath() {
        return path;
    }


    public void execute(String line, InputStream startInp, OutputStream endOutp) throws IOException {
        ArrayList<String[]> mtrx = parser.parse(line);
        if (!isNeedExternalExecute(mtrx)) {
            return;
        }
        List<Thread> subProcessList = new ArrayList<Thread>();
        InputStream inp = startInp;
        try {
            for (final String[] shell : mtrx) {
                AbstractFunction cmd;
                String[] args;
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

                inp = new PipedInputStream(outp);
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

                File newDir = parseArgs(mtrx, System.getProperty("user.dir"));

                if (isDirectoryOK(newDir)) {
                    path = newDir.toPath().toAbsolutePath().normalize().toString() + '/';
                }
                return false;
            }
            if (mtrx.get(0)[0].equals("ls")) {

                File newDir = parseArgs(mtrx, path);

                if (isDirectoryOK(newDir)) {
                    printFilesFromDir(newDir);
                }
                return false;
            }
        }
        return true;
    }

    private File parseArgs(ArrayList<String[]> mtrx, String pathByDefault) {
        String newDirName;
        if (mtrx.get(0).length >= 2) {
            newDirName = mtrx.get(0)[1];
        } else {
            newDirName = pathByDefault;
        }
        File newDir = new File(newDirName);
        // check relative path
        if (!newDir.isAbsolute()) {
            // change to absolute
            newDir = new File(path + newDirName);
        }
        return newDir;
    }

    private void printFilesFromDir(File dir) {
        File[] listOfFiles = dir.listFiles();
        for (File f : listOfFiles) {
            System.out.println(f.getName());
        }
    }

    private boolean isDirectoryOK(File dir) {
        if (!dir.exists()) {
            System.out.println("no such file or directory...");
            return false;
        }
        if (!dir.isDirectory()) {
            System.out.printf("\"%s\" is not a directory...\n", dir);
            return false;
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

