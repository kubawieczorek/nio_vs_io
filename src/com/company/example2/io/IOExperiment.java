package com.company.example2.io;

import com.company.example2.TimePrinter;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

public class IOExperiment {

    @Test
    public void runIOTest() throws Exception {
        PrintWriter experimentWriter = new PrintWriter("experimentOutputIO.txt", "UTF-8");
        List<ExperimentData> experimentData = new ArrayList<>();

        int clientsAmount = 5;
        int serverThreadsAmount = 5;
        int clientBufferSize = 1000;

        experimentData.add(new ExperimentData(clientsAmount, serverThreadsAmount, clientBufferSize, 50000));
        experimentData.add(new ExperimentData(clientsAmount, serverThreadsAmount, clientBufferSize, 100000));
        experimentData.add(new ExperimentData(clientsAmount, serverThreadsAmount, clientBufferSize, 200000));
        experimentData.add(new ExperimentData(clientsAmount, serverThreadsAmount, clientBufferSize, 400000));
        experimentData.add(new ExperimentData(clientsAmount, serverThreadsAmount, clientBufferSize, 800000));
        experimentData.add(new ExperimentData(clientsAmount, serverThreadsAmount, clientBufferSize, 1600000));

        for (ExperimentData data : experimentData) {
            Random rn = new Random();
            int port = rn.nextInt(9999);
            List<Process> clients = new ArrayList<>();
            for (int i = 0; i < data.clientsAmount; i++) {
                clients.add(IOCharacterShiftClient.start(data.dataAmountPerClient, data.clientBufferSize, port));
            }
            IOCharacterShiftServer IOCharacterShiftServer = new IOCharacterShiftServer();
            long startTime = System.nanoTime();
            IOCharacterShiftServer.start(data.serverThreadsAmount, data.clientsAmount, port);
            long stopTime = System.nanoTime();
            experimentWriter.println(format("[%s][%s][%s]", data.clientBufferSize, data.dataAmountPerClient, TimePrinter.getTime(startTime, stopTime)));
            clients.forEach(Process::destroy);
        }
        experimentWriter.close();
    }

    private class ExperimentData {

        public int clientsAmount;
        public int serverThreadsAmount;
        public int clientBufferSize;
        public int dataAmountPerClient;

        public ExperimentData(int clientsAmount, int serverThreadsAmount, int clientBufferSize, int dataAmountPerClient) {
            this.clientsAmount = clientsAmount;
            this.serverThreadsAmount = serverThreadsAmount;
            this.clientBufferSize = clientBufferSize;
            this.dataAmountPerClient = dataAmountPerClient;
        }
    }
}
