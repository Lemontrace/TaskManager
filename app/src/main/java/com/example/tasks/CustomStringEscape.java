package com.example.tasks;

import java.util.ArrayList;

public class CustomStringEscape {
    //escapes \n and %(custom escaping)
    static String escaped(String str) {
        return str.replace("%", "%%").replace("\n", "%n");
    }

    //unescape
    static String unescaped(String str) {
        char current;
        char next;
        java.util.ArrayList<String> unescaped = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            current = str.charAt(i);
            if (i == str.length() - 1) {
                next = ' ';
            } else {
                next = str.charAt(i + 1);
            }

            if (current == '%') {
                if (next == '%') {
                    unescaped.add("%");
                    i++;
                } else if (next == 'n') {
                    unescaped.add("\n");
                    i++;
                }
            } else {
                unescaped.add(String.valueOf(current));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String ch : unescaped) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
