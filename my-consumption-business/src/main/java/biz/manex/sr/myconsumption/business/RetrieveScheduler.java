package biz.manex.sr.myconsumption.business;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Tool used to schedule data retrievement operations
 */
public class RetrieveScheduler {

    // @TODO: put these parameters in a XML file
    private long maxRetrieveInterval = 600000L; // 10 minutes
    private long minPauseInterval = 1000L; // 1 second pause
    private DataRetriever retriever;

    private long nextRetrieve = 0;

    public RetrieveScheduler(DataRetriever retriever) {
        this.retriever = retriever;
    }

    /**
     * Gets the max retrieve interval
     *
     * @return the interval (in ms)
     */
    public long getMaxRetrieveInterval() {
        return maxRetrieveInterval;
    }

    /**
     * Sets the max retrieve interval.
     *
     * @param maxRetrieveInterval the interval
     */
    public void setMaxRetrieveInterval(long maxRetrieveInterval) {
        this.maxRetrieveInterval = maxRetrieveInterval;
    }

    public long getMinPauseInterval() {
        return minPauseInterval;
    }

    public void setMinPauseInterval(long minPauseInterval) {
        this.minPauseInterval = minPauseInterval;
    }

    /**
     * Starts scheduling data retrieving
     */
    public void schedule() {
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
