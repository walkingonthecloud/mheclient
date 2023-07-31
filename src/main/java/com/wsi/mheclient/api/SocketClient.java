package com.wsi.mheclient.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SocketClient {

    void init(String ip, String port) throws IOException;
    void sendMsg(String msg, String olpn) throws IOException, InterruptedException;

    void sendKeep() throws IOException, InterruptedException;

    Map<String, Long> send(List<String> oLPNs) throws IOException;
    void closeConnection() throws IOException;

    Map<String, Long> getRunStats();

    void clearStats();
}
