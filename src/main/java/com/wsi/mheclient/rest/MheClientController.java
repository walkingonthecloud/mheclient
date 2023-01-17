package com.wsi.mheclient.rest;

import com.wsi.mheclient.impl.SocketClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class MheClientController {
    private final SocketClientImpl socketClient;

    private final Logger log = LoggerFactory.getLogger(MheClientController.class);
    @Autowired
    public MheClientController(SocketClientImpl socketClient)
    {
        this.socketClient = socketClient;
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Map<String, Long>> send(@RequestBody List<String> oLPNs) throws IOException, InterruptedException {
        Map<String, Long> rountTripStats = socketClient.send(oLPNs);
        //socketClient.clearStats();

        log.info("------------------------------------------------------------------------------");
        log.info("------------------------     TEST RESULTS    ---------------------------------");
        log.info("------------------------------------------------------------------------------");

        rountTripStats.forEach( (s,l) -> {

            log.info(s + ":" + String.valueOf(l));

        });
        log.info("------------------------------------------------------------------------------");

        return new ResponseEntity<>(rountTripStats, HttpStatus.OK);
    }

    @GetMapping(value = "/send/{msg}")
    public ResponseEntity<String> sendMessage(@PathVariable String msg) throws IOException, InterruptedException {
        socketClient.sendMsg(msg, null);
        socketClient.clearStats();
        return new ResponseEntity<String>("Message successfully sent to server!",null, HttpStatus.OK);
    }

    @GetMapping(value = "/close")
    public ResponseEntity<String> closeConnection() throws IOException {
        socketClient.closeConnection();
        return new ResponseEntity<String>("Connection to the server closed.",null, HttpStatus.OK);
    }

    @GetMapping(value = "/getStats")
    public ResponseEntity<Map<String, Long>> getStats() throws IOException {
        Map<String, Long> roundTripDetails = socketClient.getRunStats();
        socketClient.clearStats();
        return new ResponseEntity<>(roundTripDetails, HttpStatus.OK);
    }

    @GetMapping(value = "/clearStats")
    public ResponseEntity<String> clearStats()  {
        socketClient.clearStats();
        return new ResponseEntity<>("Stats cleared!", HttpStatus.OK);
    }

}
