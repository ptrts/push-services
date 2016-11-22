package io.fourfinanceit.push.receiver.service.components;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

@Service
public class CronUtils {

    public Date getNextDateByCronSchedule(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date());
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse cron expression", e);
        }
    }
}
