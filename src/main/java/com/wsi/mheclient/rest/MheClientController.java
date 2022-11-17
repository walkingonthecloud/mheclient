package com.wsi.mheclient.rest;

import com.wsi.mheclient.impl.SocketClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api")
public class MheClientController {
    private final SocketClientImpl socketClient;
    @Autowired
    public MheClientController(SocketClientImpl socketClient)
    {
        this.socketClient = socketClient;
    }

    @GetMapping(value = "/send/{msg}")
    public ResponseEntity<String> sendMessage(@PathVariable String msg) throws IOException {
        socketClient.sendMsg(msg);
        return new ResponseEntity<String>("Message successfully sent to server!",null, HttpStatus.OK);
    }

    @GetMapping(value = "/close")
    public ResponseEntity<String> closeConnection() throws IOException {
        socketClient.closeConnection();
        return new ResponseEntity<String>("Connection to the server closed.",null, HttpStatus.OK);
    }
}
