package org.starfishrespect.myconsumption.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.starfishrespect.myconsumption.server.business.SensorsDataRetriever;
import org.starfishrespect.myconsumption.server.notifications.NotificationMessage;
import org.starfishrespect.myconsumption.server.notifications.NotificationSender;
import org.starfishrespect.myconsumption.server.repositories.DayStatRepository;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.PeriodStatRepository;
import org.starfishrespect.myconsumption.server.repositories.ValuesRepository;
import org.starfishrespect.myconsumption.server.stats.StatisticsUpdater;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Entry point for the data retriever service
 */
@SpringBootApplication
public class Watcher implements CommandLineRunner {

    private final long maxRetrieveInterval = 600000L; // 10 minutes
    private final long minPauseInterval = 1000L; // 1 second pause
    private long nextRetrieve = 0;

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private ValuesRepository valuesRepository;
    @Autowired
    private PeriodStatRepository mPeriodStatRepository;
    @Autowired
    private DayStatRepository dayStatRepository;

    private SensorsDataRetriever retriever;
    private StatisticsUpdater statUpdater;

    public static void main(String args[]) {
        SpringApplication.run(Watcher.class, args);
    }

    /**
     * Starts scheduling data retrieving
     */
    @Override
    public void run(String... args) throws Exception {
        NotificationSender sender = new NotificationSender("AIzaSyAXxQHFNI783jfWY1RRu2gotxUKvanys0U");
        NotificationMessage message = new NotificationMessage.Builder()
                .timeToLive(24*60*60*7)
                .delayWhileIdle(true)
                .addData("message", "this is my message")
                .build();
        sender.sendNoRetry(message, "APA91bFUIhjTdJUKAQgVJLpPggtjKkmzSp7XUqWZudg4AW_bFky5yZ2OMf_OH3hrHlPEfega4gQY9V4CkfxkrhLRFAad4fY-LkyOFyCUwOUsbD4Yiy-eZrSCjiBganaDi0L-iAPcdiBsure7yRr2wUhvsQcLrPCKIW_AamD0rXZAUEdFQaZXfV8");

        retriever = new SensorsDataRetriever(sensorRepository, valuesRepository);
        statUpdater = new StatisticsUpdater(sensorRepository, mPeriodStatRepository, dayStatRepository);

        nextRetrieve = System.currentTimeMillis() + maxRetrieveInterval;

        Timer retrieveTimer = new Timer();
        retrieveTimer.schedule(new WatcherTask(), 0);
    }

    private class WatcherTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Watcher timer task started : " + new Date().toString());
            retriever.retrieveAll();
            statUpdater.computeAll();
            System.out.println("Watch timer task ended : " + new Date().toString());

            // Next iteration
            long delay = nextRetrieve - System.currentTimeMillis();

            if (delay < minPauseInterval) {
                System.out.println("Next iteration will occur at " + new Date(System.currentTimeMillis() + minPauseInterval).toString());
                new Timer().schedule(new WatcherTask(), minPauseInterval);
            } else {
                System.out.println("Next iteration will occur at " + new Date(System.currentTimeMillis() + delay).toString());
                new Timer().schedule(new WatcherTask(), delay);
            }
            nextRetrieve = System.currentTimeMillis() + maxRetrieveInterval;
        }
    }
}
