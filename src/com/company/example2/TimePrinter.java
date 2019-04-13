package com.company.example2;

public class TimePrinter {
    public static String getTime(long start, long end){
        double time = end - start;
        time = time/ 1000000000.0;
        return String.valueOf(time).replace('.', ',');
    }
}
