package com.company.example1;

public class TimePrinter {
    public static void printTime(long start, long end){
         printTime(start, end, "");
    }
    public static void printTime(long start, long end, String prefix){
        double time = end - start;
        time = time/ 1000000000.0;
        System.out.println(prefix + " Time[s]: " + time );
    }
}
