package com.wsi.mheclient.api;

import java.io.IOException;

public interface SocketClient {

    void init(String ip, String port) throws IOException;
    void sendMsg(String msg) throws IOException;
    void closeConnection() throws IOException;
}
