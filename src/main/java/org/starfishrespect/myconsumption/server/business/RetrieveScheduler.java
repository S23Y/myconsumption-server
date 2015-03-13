package org.starfishrespect.myconsumption.server.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Entry point for the data retriever service
 */
@SpringBootApplication
public class RetrieveScheduler implements CommandLineRunner {

    private final long maxRetrieveInterval = 600000L; // 10 minutes
    private final long minPauseInterval = 1000L; // 1 second pause
    private long nextRetrieve = 0;

    @Autowired
    private DataRetriever retriever;

    public static void main(String args[]) {
        SpringApplication.run(RetrieveScheduler.class, args);
    }

    /**
     * Starts scheduling data retrieving
     */
    @Override
    public void run(String... args) throws Exception {
        nextRetrieve = System.currentTimeMillis() + maxRetrieveInterval;
        Timer retrieveTimer = new Timer();
        retrieveTimer.schedule(new RetrieveTask(), 0);
    }

    private class RetrieveTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Schedule retrieve started : " + new Date().toString());
            retriever.retrieveAll();
            System.out.println("Schedule retrieve ended : " + new Date().toString());

            // next iteration
            long delay = nextRetrieve - System.currentTimeMillis();

            if (delay < minPauseInterval) {
                System.out.println("Next iteration will occur at " + new Date(System.currentTimeMillis() + minPauseInterval).toString());
                new Timer().schedule(new RetrieveTask(), minPauseInterval);
            } else {
                System.out.println("Next iteration will occur at " + new Date(System.currentTimeMillis() + delay).toString());
                new Timer().schedule(new RetrieveTask(), delay);
            }
            nextRetrieve = System.currentTimeMillis() + maxRetrieveInterval;
        }
    }
}
