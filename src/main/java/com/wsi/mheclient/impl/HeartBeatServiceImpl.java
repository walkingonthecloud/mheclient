package com.wsi.mheclient.impl;

import com.wsi.mheclient.api.HeartBeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HeartBeatServiceImpl extends Thread {

    private final Logger log = LoggerFactory.getLogger(HeartBeatServiceImpl.class);
    private DataOutputStream out;
    private BufferedReader in;
    private Socket clientSocket;
    private final String HEARTBEAT = "HEARTBEAT";
    private final String HEARTBEAT_ACK = "ALIVE";

    private boolean endThread = false;

    public void setParams(DataOutputStream out, BufferedReader in, Socket clientSocket){
        this.out = out;
        this.in = in;
        this.clientSocket = clientSocket;
    }

    public void setEndThread()
    {
        this.endThread = true;
    }

    @Override
    public void start() {
        while (!endThread) {
            try {
                String heartMsg = in.readLine();
                if (HEARTBEAT.equals(heartMsg)) {
                    log.info("Heart beat received from server.");
                    byte stx = 0x02;
                    byte etx = 0x03;
                    //out.writeByte(stx);
                    out.writeBytes(stx + HEARTBEAT_ACK + etx);
                    //out.writeByte(etx);
                    log.info("Heart beat acknowledged.");
                }
                Thread.sleep(1000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
