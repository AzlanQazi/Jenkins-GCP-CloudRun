package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HelloWorld {

    public String getMessage() {
        return "It worked on my machine. And surprisingly, it works here too!<br>" +
                "Jenkins did the heavy lifting; I’m just here for the traffic.<br>" +
                "This app was hand-delivered by a very hardworking pipeline.<br>" +
                "Warning: May contain traces of late-night debugging.";
    }

    @GetMapping("/")
    public String hello() {
        return getMessage();
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloWorld.class, args);
    }
}