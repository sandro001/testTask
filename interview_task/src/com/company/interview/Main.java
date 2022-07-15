package com.company.interview;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Path path = Paths.get("src/myfile.txt");
        List<String> lines = new ArrayList<>();

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        char[][] chars = new char[lines.size()][lines.get(0).length()];

        for(int i=0; i< lines.size(); i++) {
            chars[i] = lines.get(i).toCharArray();
        }

        DijkstraSolver solver = new DijkstraSolver(chars);
        solver.solve();
    }
}
