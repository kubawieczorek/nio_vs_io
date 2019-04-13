package com.company.example2.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class IOCharacterShiftServer {

    private final char shift = 1;
    private final static String POISON_CODE = "#POISON";
    private AtomicInteger poisonPills = new AtomicInteger(0);

    public void start(int threadsAmount, int clientsAmount, int port) throws Exception {
        try (ServerSocket listener = new ServerSocket(port)) {
            listener.setSoTimeout(100);
            ExecutorService pool = Executors.newFixedThreadPool(threadsAmount);
            while (poisonPills.get() < clientsAmount) {
                try {
                    pool.execute(new LettersShifter(listener.accept()));
                } catch (SocketTimeoutException ex) {
                }
            }
        }
    }

    private class LettersShifter implements Runnable {
        private Socket socket;

        LettersShifter(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                while (in.hasNextLine()) {
                    String nextLine = in.nextLine();
                    if (nextLine.equals(POISON_CODE)) {
                        poisonPills.getAndAdd(1);
                        break;
                    }
                    out.println(shift(nextLine));
                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
                System.out.println("Closed: " + socket);
            }
            System.out.println("Exit: " + socket);
        }

        private String shift(String str) {
            StringBuilder afterShift = new StringBuilder();
            for (char ch : str.toCharArray()) {
                char shifted = (char) (ch + shift);
                if (shifted > 'z') {
                    afterShift.append((char) (ch + shift - (26 - shift)));
                } else {
                    afterShift.append(shifted);
                }
            }
            return afterShift.toString();
        }

    }
}
