package com.company.example2.nio;

import com.company.example2.TimePrinter;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

public class NIOExperiment {

    @Test
    public void runNIOTest() throws Exception {

        PrintWriter experimentWriter = new PrintWriter("experimentOutputNIO.txt", "UTF-8");
        List<ExperimentData> experimentData = new ArrayList<>();

        int clientsAmount = 5;
        int clientBufferSize = 100;

        experimentData.add(new ExperimentData(clientsAmount, null, clientBufferSize, 50000));
        experimentData.add(new ExperimentData(clientsAmount, null, clientBufferSize, 100000));
        experimentData.add(new ExperimentData(clientsAmount, null, clientBufferSize, 200000));
        experimentData.add(new ExperimentData(clientsAmount, null, clientBufferSize, 400000));
        experimentData.add(new ExperimentData(clientsAmount, null, clientBufferSize, 800000));
        experimentData.add(new ExperimentData(clientsAmount, null, clientBufferSize, 1600000));

        for (ExperimentData data : experimentData) {
            Random rn = new Random();
            int port = rn.nextInt(9999);
            List<Process> clients = new ArrayList<>();
            for (int i = 0; i < data.clientsAmount; i++) {
                clients.add(NIOCharacterShiftClient.start(data.dataAmountPerClient, data.clientBufferSize, port));
            }
            NIOCharacterShiftServer NIOCharacterShiftServer = new NIOCharacterShiftServer();
            long startTime = System.nanoTime();
            NIOCharacterShiftServer.start(data.clientsAmount, data.clientBufferSize, port);
            long stopTime = System.nanoTime();
            experimentWriter.println(format("[%s][%s][%s]", data.clientBufferSize, data.dataAmountPerClient, TimePrinter.getTime(startTime, stopTime)));
            clients.forEach(Process::destroy);
        }
        experimentWriter.close();
    }

    private class ExperimentData {

        public Integer clientsAmount;
        public Integer serverThreadsAmount;
        public Integer clientBufferSize;
        public Integer dataAmountPerClient;

        public ExperimentData(Integer clientsAmount, Integer serverThreadsAmount, Integer clientBufferSize, Integer dataAmountPerClient) {
            this.clientsAmount = clientsAmount;
            this.serverThreadsAmount = serverThreadsAmount;
            this.clientBufferSize = clientBufferSize;
            this.dataAmountPerClient = dataAmountPerClient;
        }
    }
}
