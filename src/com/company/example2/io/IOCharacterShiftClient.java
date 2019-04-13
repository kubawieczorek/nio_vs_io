package com.company.example2.io;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.String.format;

public class IOCharacterShiftClient {

    private final static String serverIp = "127.0.0.1";
    private final static String POISON_CODE = "#POISON";

    public static void main(String[] args) throws Exception {

        int dataToSend = Integer.parseInt(args[0]);
        int bufferSize = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[2]);
        int dataSent = 0;

        try (Socket socket = new Socket(serverIp, port)) {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (dataToSend < dataSent) {
                out.println(generateRandomString(bufferSize));
                dataSent += bufferSize;
                String nextLine = in.nextLine();
                System.out.println(format("client received %s bytes", nextLine.length()));
            }
            out.println(POISON_CODE);
        }
    }

    private static String generateRandomString(int size) {
        return RandomStringUtils.random(size, true, false).toLowerCase();
    }

    public static Process start(int dataAmount, int bufferSize, int port) throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = IOCharacterShiftClient.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className,
                String.valueOf(dataAmount), String.valueOf(bufferSize), String.valueOf(port));
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder.start();
    }
}
