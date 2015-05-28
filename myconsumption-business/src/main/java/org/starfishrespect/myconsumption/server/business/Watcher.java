package org.starfishrespect.myconsumption.server.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.starfishrespect.myconsumption.server.business.notifications.Notifier;
import org.starfishrespect.myconsumption.server.business.repositories.*;
import org.starfishrespect.myconsumption.server.business.sensors.SensorsDataRetriever;
import org.starfishrespect.myconsumption.server.business.stats.StatisticsUpdater;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Entry point for the data retriever service
 */
@SpringBootApplication
public class Watcher implements CommandLineRunner {

    private final long maxNotifInterval = 86400000L; // 1 day
    private final long maxRetrieveInterval = 600000L; // 10 minutes
    private final long minPauseInterval = 1000L; // 1 second pause
    private long nextRetrieve = 0;

    @Autowired
    private SensorRepository mSensorRepository;
    @Autowired
    private ValuesRepository mValuesRepository;
    @Autowired
    private PeriodStatRepository mPeriodStatRepository;
    @Autowired
    private DayStatRepository mDayStatRepository;
    @Autowired
    private UserRepository mUserRepository;

    @Value("${api.key}")
    private String mApiKey;

    private SensorsDataRetriever retriever;
    private StatisticsUpdater statUpdater;
    private Notifier notifier;

    public static void main(String args[]) {
        SpringApplication.run(Watcher.class, args);
    }

    /**
     * Starts scheduling data retrieving
     */
    @Override
    public void run(String... args) throws Exception {
        retriever = new SensorsDataRetriever(mSensorRepository, mValuesRepository);
        statUpdater = new StatisticsUpdater(mSensorRepository, mPeriodStatRepository, mDayStatRepository);
        notifier = new Notifier(mApiKey, mSensorRepository, mPeriodStatRepository, mUserRepository);

        nextRetrieve = System.currentTimeMillis() + maxRetrieveInterval;

        Timer retrieveTimer = new Timer();
        retrieveTimer.schedule(new WatcherTask(), 0);

        Timer notificationSender = new Timer();
        notificationSender.schedule(new NotificationTask(), 0);
    }

    private class WatcherTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Watcher timer task started: " + new Date().toString());
            retriever.retrieveAll();
            statUpdater.computeAll();
            System.out.println("Watcher timer task ended: " + new Date().toString());

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

    private class NotificationTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Notification sender task started: " + new Date().toString());
            notifier.checkForNotifications();
            System.out.println("Notification sender task ended: " + new Date().toString());

            // Next iteration
            long delay = nextRetrieve - System.currentTimeMillis();

            if (delay < minPauseInterval) {
                System.out.println("Next iteration will occur at " + new Date(System.currentTimeMillis() + minPauseInterval).toString());
                new Timer().schedule(new NotificationTask(), minPauseInterval);
            } else {
                System.out.println("Next iteration will occur at " + new Date(System.currentTimeMillis() + delay).toString());
                new Timer().schedule(new NotificationTask(), delay);
            }
            nextRetrieve = System.currentTimeMillis() + maxNotifInterval;
        }
    }
}
