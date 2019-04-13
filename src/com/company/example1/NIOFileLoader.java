package com.company.example1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileLoader {
    public static int countYellow(String filename) {
        int counter = 0;
        try (FileChannel inChannel = new RandomAccessFile(filename, "r").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                for (int i = 0; i < buffer.limit(); i++) {
                    if ((char) buffer.get() == 'y') {
                        counter++;
                    }
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counter;
    }
}
