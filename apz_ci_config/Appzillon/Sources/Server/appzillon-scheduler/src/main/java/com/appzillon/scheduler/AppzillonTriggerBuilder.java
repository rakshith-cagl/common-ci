package com.appzillon.scheduler;

import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppzillonTriggerBuilder {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
            ServerConstants.LOGGER_SCHEDULER,
            AppzillonScheduler.class.getName());

    private AppzillonTriggerBuilder() {

    }

    public static Trigger createTrigger(String pStartDate, String pEndDate, String expression, String jobName) {
        Trigger trigger = null;
        Date startDate = null;
        Date endDate = null;
        String status = "";

        try {
            if (Utils.isNotNullOrEmpty(pStartDate) && Utils.isNotNullOrEmpty(pEndDate)) {
                status = "B";
                startDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pStartDate);
                endDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pEndDate);
                LOG.debug("{} Both start and end date are set", ServerConstants.LOGGER_SCHEDULER);

            } else if (Utils.isNotNullOrEmpty(pStartDate)) {
                status = "S";
                startDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pStartDate);
                LOG.debug("{} Only start date is set", ServerConstants.LOGGER_SCHEDULER);

            } else if (Utils.isNotNullOrEmpty(pEndDate)) {
                status = "E";
                endDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pEndDate);
                LOG.debug("{} Only end date is set", ServerConstants.LOGGER_SCHEDULER);
            }

            if ("B".equalsIgnoreCase(status)) {
                LOG.debug("{} Trigger is getting built for the both start and end date with expression", ServerConstants.LOGGER_SCHEDULER);
                trigger = TriggerBuilder
                        .newTrigger()
                        .withIdentity(ServerConstants.TRIGGER + jobName, ServerConstants.GROUP_NAME)
                        .startAt(startDate)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .endAt(endDate)
                        .build();

            } else if ("S".equalsIgnoreCase(status)) {
                LOG.debug("{} Trigger is getting built for only start date with expression", ServerConstants.LOGGER_SCHEDULER);
                trigger = TriggerBuilder
                        .newTrigger()
                        .withIdentity(ServerConstants.TRIGGER + jobName, ServerConstants.GROUP_NAME)
                        .startAt(startDate)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .build();

            } else if ("E".equalsIgnoreCase(status)) {
                LOG.debug("{} Trigger is getting built for only end date with expression", ServerConstants.LOGGER_SCHEDULER);
                trigger = TriggerBuilder
                        .newTrigger()
                        .withIdentity(ServerConstants.TRIGGER + jobName, ServerConstants.GROUP_NAME)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .endAt(endDate)
                        .build();

            } else {
                LOG.debug("{} Trigger is getting built only with expression", ServerConstants.LOGGER_SCHEDULER);
                if (CronExpression.isValidExpression(expression)) {
                    trigger = TriggerBuilder
                            .newTrigger()
                            .withIdentity(ServerConstants.TRIGGER + jobName, ServerConstants.GROUP_NAME)
                            .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                            .build();
                } else {
                    DomainException dexp = DomainException.getDomainExceptionInstance();
                    dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_049));
                    dexp.setCode(DomainException.Code.APZ_DM_049.toString());
                    dexp.setPriority("1");
                    LOG.error("{} '?' can only be specfied for Day-of-Month or Day-of-Week {}", ServerConstants.LOGGER_SCHEDULER, dexp);
                    throw dexp;
                }
            }
        } catch (ParseException parsex) {
            LOG.error(ServerConstants.LOGGER_SCHEDULER, parsex);

        }
        return trigger;
    }
}
