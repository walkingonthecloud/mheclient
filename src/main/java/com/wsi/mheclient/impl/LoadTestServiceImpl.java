package com.wsi.mheclient.impl;

import com.wsi.mheclient.api.LoadTestService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

@Service
public class LoadTestServiceImpl implements LoadTestService {

    private final Logger log = LoggerFactory.getLogger(LoadTestServiceImpl.class);

    @Override
    public void sendMessages(Socket socket, DataOutputStream out, BufferedReader in) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        String socketMessage = IOUtils.toString(Objects.requireNonNull(classLoader.getResourceAsStream("INDSH.txt")));
    }
}
