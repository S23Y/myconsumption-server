package org.starfishrespect.myconsumption.server.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.starfishrespect.myconsumption.server.business.repositories.*;
import org.starfishrespect.myconsumption.server.business.stats.StatisticsUpdater;

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
    private SensorRepository mSensorRepository;
    @Autowired
    private ValuesRepository mValuesRepository;
    @Autowired
    private PeriodStatRepository mPeriodStatRepository;
    @Autowired
    private DayStatRepository mDayStatRepository;
    @Autowired
    private UserRepository mUserRepository;

    // TODO
//    @Value("${api.key}")
//    private String mApiKey;

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
        retriever = new SensorsDataRetriever(mSensorRepository, mValuesRepository);
        //statUpdater = new StatisticsUpdater(mApiKey, mSensorRepository, mPeriodStatRepository, mDayStatRepository, mUserRepository);
        statUpdater = new StatisticsUpdater("AIzaSyAXxQHFNI783jfWY1RRu2gotxUKvanys0U", mSensorRepository, mPeriodStatRepository, mDayStatRepository, mUserRepository);

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
