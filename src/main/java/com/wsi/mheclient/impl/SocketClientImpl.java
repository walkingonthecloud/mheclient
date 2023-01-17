package com.wsi.mheclient.impl;

import com.wsi.mheclient.api.SocketClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class SocketClientImpl implements SocketClient, CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(SocketClientImpl.class);

    private Map<String, Long> roundTrips = new HashMap<>();

    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;

    private String serverIP;
    private String serverPort;

    private boolean mockMode;

    private boolean connectionOpen = false;

    private HeartBeatServiceImpl heartBeatService;

    private final LoadTestServiceImpl loadTestService;

    @Autowired
    public SocketClientImpl(LoadTestServiceImpl loadTestService)
    {
        this.loadTestService = loadTestService;
    }

    @Override
    public void init(String ip, String port) throws IOException {
        clientSocket = new Socket(ip, Integer.parseInt(port));
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        log.info("Connected to server!");
        //connectionOpen = true;
        //heartBeatService = new HeartBeatServiceImpl();
        //heartBeatService.setParams(out, in, clientSocket);
        //log.info("Starting heart beat listener thread...");
        //heartBeatService.start();
        if (mockMode) {
            log.info("Starting load test from file...");
            loadTestService.sendMessages(clientSocket, out, in);
        }
        else
            log.info("Load test mode is OFF, waiting for REST call to send socket message...");
    }

    @Override
    public void sendMsg(String msg, String olpn) throws IOException, InterruptedException {
//        if (!connectionOpen)
//            init(serverIP, serverPort);
        Instant before = Instant.now();
        byte stx = 0x02;
        byte etx = 0x03;
        log.info("Sending msg: {}", msg);
        out.writeByte(stx);
        out.writeBytes(msg + "\n");
        out.writeByte(etx);
        log.info("Message sent!");
        Thread.sleep(200);
        readAck(clientSocket);
        Instant after = Instant.now();
        long millis = Duration.between(before, after).toMillis();
        log.info("Time taken in milliseconds for the round-trip --------------------------> {}", millis);
        roundTrips.put(olpn, millis);
    }

    @Override
    public Map<String, Long> send(List<String> oLPNs) throws IOException {

        log.info("Starting to send MHE Messages for oLPN List...");
        roundTrips.put("TEST_STARTED", Instant.now().toEpochMilli());

        ClassLoader classLoader = getClass().getClassLoader();
        String indshSample = IOUtils.toString(Objects.requireNonNull(classLoader.getResourceAsStream("INDSH.txt")));

        oLPNs.forEach(olpn -> {
            try {
                sendMsg(indshSample.replaceFirst("<oLPN>", olpn), olpn);
                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("Test ended...use getStats() API to check timings.");
        roundTrips.put("TEST_ENDED", Instant.now().toEpochMilli());
        return roundTrips;
    }

    private void readAck(Socket clientSocket) {
        InputStream inputStream= null;
        InputStreamReader inputStreamReader = null;
        StringBuffer stringBuffer = null;
        int x;
        try {
            inputStream = clientSocket.getInputStream();

            inputStreamReader = new InputStreamReader(inputStream);
            stringBuffer = new StringBuffer();
            while (true) {
                x = inputStreamReader.read();
                if (x == 0x03 || x == -1)break; // read until ETX
                if (x != 0x02)
                    stringBuffer.append((char) x);
            }
        }
         catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = stringBuffer.toString();
        log.info("ACK Received: {}", response);
    }


    @Override
    public void closeConnection() throws IOException {
        log.info("Closing connection to server...");
        in.close();
        out.close();
        clientSocket.close();
        heartBeatService.setEndThread();
        log.info("Connection closed.");
    }

    @Override
    public Map<String, Long> getRunStats() {

        log.info("Getting previous run details...");
        return roundTrips;
    }

    @Override
    public void clearStats() {
        roundTrips.clear();
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            serverIP = args[0];
            serverPort = args[1];
            mockMode = Boolean.parseBoolean(args[2]);
            log.info("Attempting to connect to server at IP: {} and port: {}", serverIP, serverPort);
            init(serverIP, serverPort);
        } else {
            log.error("No server IP, port or mockMode specified or error parsing parameters, exiting:");
            Arrays.stream(args).forEach(System.out::println);
        }
    }
}
