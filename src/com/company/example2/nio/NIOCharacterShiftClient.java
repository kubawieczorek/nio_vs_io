package com.company.example2.nio;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.lang.String.format;

public class NIOCharacterShiftClient {

    private static SocketChannel client;
    private static ByteBuffer buffer;
    private final static String POISON_CODE = "#POISON";

    public static void main(String[] args) throws IOException {

        int dataToSend = Integer.parseInt(args[0]);
        int bufferSize = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[2]);

        int dataSent = 0;

        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", port));
            buffer = ByteBuffer.allocate(bufferSize);
            while (dataToSend < dataSent) {
                sendMessage(generateRandomString(bufferSize));
                String response = readMessage();
                System.out.println(format("client received %s bytes", response.length()));
                dataSent += bufferSize;
            }
            sendMessage(POISON_CODE);

        } catch (IOException e) {
            e.printStackTrace();
        }
        stop();
    }

    private static void stop() throws IOException {
        client.close();
        buffer = null;
    }

    public static void sendMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        try {
            client.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readMessage() {
        String response = "";
        try {
            client.read(buffer);
            response = new String(buffer.array()).trim();
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static String generateRandomString(int size) {
        return RandomStringUtils.random(size, true, false).toLowerCase();
    }

    public static Process start(int dataAmount, int bufferSize, int port) throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = NIOCharacterShiftClient.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className,
                String.valueOf(dataAmount), String.valueOf(bufferSize), String.valueOf(port));

        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder.start();
    }
}
