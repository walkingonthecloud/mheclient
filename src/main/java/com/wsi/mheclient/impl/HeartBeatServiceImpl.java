package com.wsi.mheclient.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class HeartBeatServiceImpl extends Thread implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(HeartBeatServiceImpl.class);

    private final SocketClientImpl socketClientService;

    private boolean endThread = false;

    public HeartBeatServiceImpl(SocketClientImpl socketClientService) {
        this.socketClientService = socketClientService;
    }

    public void setEndThread() {
        this.endThread = true;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting heart beats...");
        while (!endThread) {
            try {
                socketClientService.sendMsg("KEEPALIVE", null);
                Thread.sleep(30000);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

