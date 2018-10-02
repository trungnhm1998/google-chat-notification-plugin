package io.cnaik.controller;

import io.cnaik.model.NotificationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReceiveNotification {

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public ResponseEntity<String> receiveNotification(@RequestParam String key, @RequestBody NotificationRequest notificationRequest) {
        try {

            int sleepTime = Integer.parseInt(key);

            System.out.println("sleepTime: " + sleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Ok" , HttpStatus.OK);
    }
}
