package ru.spbau.mit.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ParserTest {
    private Map<String, ArrayList<String[]>> testSuitMap;
    private Map<String, ArrayList<String[]>> viewCheckMap;

    {
        testSuitMap = new HashMap<>();
        String key = "echo hello";
        String[] shellValue = {"echo", "hello"};
        ArrayList<String[]> value = new ArrayList<>();
        value.add(shellValue);
        testSuitMap.put(key, value);
        key = "    echo        hello     ";
        testSuitMap.put(key, value);
        key = "echo \"hello\" ";
        testSuitMap.put(key, value);
        key = "echo \'hello\'";
        testSuitMap.put(key, value);

        shellValue = new String[]{"echo", " hello "};
        value = new ArrayList<>();
        value.add(shellValue);
        key = "echo \" hello \"";
        testSuitMap.put(key, value);
        key = "echo \' hello \'";
        testSuitMap.put(key, value);

        shellValue = new String[]{"echo", "hello | cat", "dog"};
        value = new ArrayList<>();
        value.add(shellValue);
        key = "echo \"hello | cat\" dog";
        testSuitMap.put(key, value);
        key = "echo \'hello | cat\' dog";
        testSuitMap.put(key, value);

        value = new ArrayList<>();
        shellValue = new String[]{"echo", "hello"};
        value.add(shellValue);
        shellValue = new String[]{"cat"};
        value.add(shellValue);
        shellValue = new String[]{"dog", "gav | gav"};
        value.add(shellValue);
        key = "echo hello | cat | dog \'gav | gav\'";
        testSuitMap.put(key, value);

        key = "x=_42_";
        value = new ArrayList<>();
        testSuitMap.put(key, value);

        viewCheckMap = new HashMap<>();
        key = "x fff $x xx  $y rr $x";
        shellValue = new String[]{"x", "fff", "_42_", "xx", "", "rr", "_42_"};
        value = new ArrayList<>();
        value.add(shellValue);
        viewCheckMap.put(key, value);
    }

    @Test
    public void parse() {
        Parser parser = new Parser();
        testSuitMap.forEach((key, value) -> {
            ArrayList<String[]> response = parser.parse(key);
            assertEquals(value.size(), response.size());
            for (int i = 0; i < value.size(); i++) {
                assertArrayEquals(value.get(i), response.get(i));
            }
        });
        viewCheckMap.forEach((key, value) -> {
            ArrayList<String[]> response = parser.parse(key);
            assertEquals(value.size(), response.size());
            for (int i = 0; i < value.size(); i++) {
                assertArrayEquals(value.get(i), response.get(i));
            }
        });
    }
}
