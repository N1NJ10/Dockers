package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/log")
    public String log() {
        logger.info("This is an info log message.");
        logger.warn("This is a warning log message.");
        logger.error("This is an error log message.");
        return "Logs have been generated. Check the console.";
    }

    @GetMapping("/error")
    public String error() {
        try {
            throw new IllegalStateException("This is a test exception.");
        } catch (IllegalStateException e) {
            logger.error("Caught an exception", e);
            return "An exception has been thrown. Check the logs.";
        }
    }

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        logger.info("Starting a slow transaction...");
        Thread.sleep(5000);
        logger.info("Slow transaction finished.");
        return "Slow transaction completed after 5 seconds.";
    }
}
