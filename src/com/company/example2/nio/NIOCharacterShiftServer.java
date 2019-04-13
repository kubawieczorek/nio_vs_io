package com.company.example2.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOCharacterShiftServer {

    private final static char shift = 1;
    private final static String POISON_CODE = "#POISON";
    private int poisonPills = 0;

    public void start(int clientsAmount, int responseBufferSize, int port) throws IOException {

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", port));
        serverSocket.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(responseBufferSize);

        while (true) {

            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }

                if (key.isReadable()) {
                    answerShifted(buffer, key);
                    if (poisonPills == clientsAmount) {
                        serverSocket.close();
                        return;
                    }
                }
                iter.remove();
            }
        }


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

    private void answerShifted(ByteBuffer buffer, SelectionKey key)
            throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        String bufferContent = new String(buffer.array(), "UTF-8");
        if (bufferContent.startsWith(POISON_CODE)) {
            poisonPills++;
            return;
        }
        buffer.clear();
        String shifted = shift(bufferContent);
        buffer.put(shifted.getBytes());
        buffer.flip();
        client.write(buffer);
        buffer.clear();
    }

    private void register(Selector selector, ServerSocketChannel serverSocket)
            throws IOException {

        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
}
