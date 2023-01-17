package com.wsi.mheclient.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public interface LoadTestService {

    void sendMessages(Socket socket, DataOutputStream out, BufferedReader in) throws IOException;

}
