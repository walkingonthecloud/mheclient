package com.wsi.mheclient.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

public interface HeartBeatService {

    void handleHeartBeat(DataOutputStream out, BufferedReader in, Socket clientSocket);

}
