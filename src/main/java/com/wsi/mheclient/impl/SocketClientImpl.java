package com.wsi.mheclient.impl;

import com.wsi.mheclient.api.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
@Service
public class SocketClientImpl implements SocketClient, CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(SocketClientImpl.class);

    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;

    private String serverIP;
    private String serverPort;

    private boolean connectionOpen = false;

    private HeartBeatServiceImpl heartBeatService;

    @Override
    public void init(String ip, String port) throws IOException {
        clientSocket = new Socket(ip, Integer.parseInt(port));
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        log.info("Connected to server!");
        //connectionOpen = true;
        heartBeatService = new HeartBeatServiceImpl();
        heartBeatService.setParams(out, in, clientSocket);
        //log.info("Starting heart beat listener thread...");
        //heartBeatService.start();
    }

    @Override
    public void sendMsg(String msg) throws IOException {
//        if (!connectionOpen)
//            init(serverIP, serverPort);
        byte stx = 0x02;
        byte etx = 0x03;
        log.info("Sending msg: {}", msg);
        out.writeByte(stx);
        out.writeBytes(msg + "\n");
        out.writeByte(etx);
//        String resp = in.readLine();
        log.info("Message sent!");
        //log.info("Response received from server: {}", resp);
//        closeConnection();
//        connectionOpen = false;
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
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            serverIP = args[0];
            serverPort = args[1];
            log.info("Attempting to connect to server at IP: {} and port: {}", serverIP, serverPort);
            init(serverIP, serverPort);
        }
        else
            log.error("No server IP and port specified, exiting...");
    }
}
