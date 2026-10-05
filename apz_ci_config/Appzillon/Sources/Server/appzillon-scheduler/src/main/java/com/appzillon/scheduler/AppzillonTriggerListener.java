package com.appzillon.scheduler;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.MDC;

import java.sql.Timestamp;
import java.util.Date;

import static com.iexceed.appzillon.utils.ServerConstants.*;

public class AppzillonTriggerListener implements TriggerListener {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
            ServerConstants.LOGGER_SCHEDULER,
            AppzillonTriggerListener.class.getName());

    private static final String TRIGGER_LISTENER_NAME = "GlobalTriggerListener";

    public String getName() {
        return TRIGGER_LISTENER_NAME;
    }

    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        MDC.put(LOG_ROUTER, SCHEDULER_SUPPORT);

        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);
        String jobName = context.getJobDetail().getKey().getName();
        String triggerName = context.getJobDetail().getKey().toString();
        LOG.debug("{} trigger : {} is fired", ServerConstants.LOGGER_SCHEDULER, triggerName);
        AppzillonScheduler.getSchedulerInstance().updateJobDetail(jobName, ServerConstants.JOB_WORKING, new Timestamp(new Date().getTime()));
    }

    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        MDC.put(LOG_ROUTER, SCHEDULER_SUPPORT);
        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);
        boolean veto = false;
        LOG.debug("{} Veto Job Execution trigger: {}", ServerConstants.LOGGER_SCHEDULER, veto);
        return veto;
    }

    public void triggerMisfired(Trigger trigger) {
        MDC.put(LOG_ROUTER, SCHEDULER_SUPPORT);
        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);
        LOG.debug("{} {} trigger: {} misfired at {}", ServerConstants.LOGGER_SCHEDULER, getName(), trigger.getKey(), trigger.getStartTime());
    }

    public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                CompletedExecutionInstruction triggerInstructionCode) {
        MDC.put(LOG_ROUTER, SCHEDULER_SUPPORT);
        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);
        String jobName = context.getJobDetail().getKey().getName();
        LOG.debug("{} {} trigger: {} triggerInstructionCode : {} and completed at {}", ServerConstants.LOGGER_SCHEDULER, getName(), trigger.getKey(), triggerInstructionCode, trigger.getStartTime());
        if (triggerInstructionCode == CompletedExecutionInstruction.DELETE_TRIGGER) {
            AppzillonScheduler.getSchedulerInstance().updateJobDetail(jobName, ServerConstants.JOB_COMPLETED, new Timestamp(new Date().getTime()));
        } else {
            AppzillonScheduler.getSchedulerInstance().updateJobDetail(jobName, ServerConstants.JOB_WORKING, new Timestamp(new Date().getTime()));
        }
    }
}
