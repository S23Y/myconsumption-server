package org.starfishrespect.myconsumption.server.business.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.business.entities.PeriodStat;
import org.starfishrespect.myconsumption.server.business.entities.Sensor;
import org.starfishrespect.myconsumption.server.business.entities.User;
import org.starfishrespect.myconsumption.server.business.repositories.DayStatRepository;
import org.starfishrespect.myconsumption.server.business.repositories.PeriodStatRepository;
import org.starfishrespect.myconsumption.server.business.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.business.repositories.UserRepository;
import org.starfishrespect.myconsumption.server.business.stats.StatUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Sends notifications to GCM.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class Notifier {
    @Autowired
    private SensorRepository mSensorRepository;
    @Autowired
    private PeriodStatRepository mPeriodStatRepository;
    @Autowired
    private UserRepository mUserRepository;

    private String mApiKey;
    private final double threshold = 1.15;

    private final Logger mLogger = LoggerFactory.getLogger(Notifier.class);

    public Notifier(String apiKey, SensorRepository seRepo, PeriodStatRepository stRepo, UserRepository uRepo)  {
        mApiKey = apiKey;
        this.mSensorRepository = seRepo;
        this.mPeriodStatRepository = stRepo;
        this.mUserRepository = uRepo;
    }

    public void checkForNotifications() {
        List<Sensor> sensors = mSensorRepository.getAllSensors();

        for (Sensor sensor : sensors) {
            List<PeriodStat> dayStats = mPeriodStatRepository.findBySensorIdAndPeriod(sensor.getId(), Period.DAY);
            List<PeriodStat> weekStats = mPeriodStatRepository.findBySensorIdAndPeriod(sensor.getId(), Period.WEEK);

            if (dayStats == null || dayStats.size() == 0 || weekStats == null || weekStats.size() == 0)
                return;
            else
                sendNotification(dayStats.get(0), weekStats.get(0));

        }
    }

    private void sendNotification(PeriodStat dayStat, PeriodStat weekStat) {

        if (dayStat.getDiffLastTwo() == 0)
            return;

        String sensorId = dayStat.getSensorId();
        String sensorName = mSensorRepository.getSensor(sensorId).getName();

        // Get the user associated to this sensor id
        List<User> users = mUserRepository.findBySensorId(sensorId);

        String msgNotif;

        // If the day consumption will lead to a week consumption greater/smaller by 15%
        if ((dayStat.getConsumption() * 7) > (weekStat.getConsumption() * threshold))
            msgNotif = "Your daily consumption is increasing for the sensor " + sensorName;
        else if ((dayStat.getConsumption() * 7 * threshold) < weekStat.getConsumption())
            msgNotif = "Your daily consumption is decreasing for the sensor " + sensorName;
        else
            return;

        for(User user : users) {
            if (user.getRegisterId() == null || user.getRegisterId().isEmpty())
                continue;

            NotificationSender sender = new NotificationSender(mApiKey);
            NotificationMessage message = new NotificationMessage.Builder()
                    .timeToLive(24*60*60*7) // A week in seconds
                    .delayWhileIdle(true)
                    .collapseKey(sensorId)
                    .addData("message", msgNotif)
                    .addData("sensor", sensorId)
                    .build();
            try {
                sender.sendNoRetry(message, user.getRegisterId());
            } catch (IOException e) {
                mLogger.error("Notification not sent: " + e.toString());
            }
        }
    }
}
