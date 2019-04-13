package com.company.example1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IOFileLoader {

    public static int countYellow(String filename) {
        int currentChar;
        int counter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((currentChar = br.read()) != -1) {
                if ((char) currentChar == 'y') {
                    counter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counter;
    }
}
