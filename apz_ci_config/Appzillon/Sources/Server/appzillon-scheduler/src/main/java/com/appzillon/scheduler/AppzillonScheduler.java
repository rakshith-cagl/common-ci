package com.appzillon.scheduler;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.TbAsmiJobExpression;
import com.iexceed.appzillon.domain.entity.TbAsmiJobMaster;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.iexceed.appzillon.utils.ServerConstants.JOB_NAME;

public class AppzillonScheduler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
            ServerConstants.LOGGER_SCHEDULER,
            AppzillonScheduler.class.getName());
    private static AppzillonScheduler appzillonScheduler = null;
    private Scheduler scheduler = null;

    public static AppzillonScheduler getSchedulerInstance() {
        if (appzillonScheduler == null) {
            appzillonScheduler = new AppzillonScheduler();
            LOG.debug("{} New Appzillon Scheduler instance created", ServerConstants.LOGGER_SCHEDULER);
        }
        return appzillonScheduler;
    }

    public void processRequest(Message pMessage) {
        LOG.debug("{} inside Scheduler process request : {}", ServerConstants.LOGGER_SCHEDULER, pMessage);

        JSONObject schedulerReq = pMessage
                .getRequestObject()
                .getRequestJson()
                .getJSONObject(ServerConstants.SCHEDULER);

        JSONObject status = new JSONObject();
        try {
            if (ServerConstants.SCHEDULER_START.equalsIgnoreCase(schedulerReq.getString(ServerConstants.STATUS))) {
                status = startSchedulerAndGetStatus(pMessage);
            } else if (ServerConstants.START_JOB.equalsIgnoreCase(schedulerReq.getString(ServerConstants.STATUS))) {
                if (scheduler.isStarted()) {
                    LOG.info("{} Scheduler has started already, not required to start for this job", ServerConstants.LOGGER_SCHEDULER);
                    pMessage.getHeader().setServiceType("appzillonFetchJobDetail");
                    DomainStartup.getInstance().processRequest(pMessage);

                    TbAsmiJobMaster jobDetail = getJobDetail(pMessage);
                    if (jobDetail != null) {
                        scheduleJobs(jobDetail, pMessage);
                        status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
                    } else {
                        LOG.info("{} No jobs are found to start the scheduler", ServerConstants.LOGGER_SCHEDULER);
                        status.put(ServerConstants.STATUS, ServerConstants.NO_JOBS);
                    }
                } else {
                    LOG.info("{} Scheduler has not started yet ", ServerConstants.LOGGER_SCHEDULER);
                    status.put(ServerConstants.STATUS, ServerConstants.SCHEDULER_NOT_STARTED);
                }
            } else if (ServerConstants.DELETE_JOB.equalsIgnoreCase(schedulerReq.getString(ServerConstants.STATUS))) {
                deleteJob(pMessage);
                status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
            } else {
                LOG.info("{} ********** Scheduler is shutting down ***********", ServerConstants.LOGGER_SCHEDULER);
                if (scheduler != null) {
                    scheduler.shutdown();
                    scheduler = null;
                    status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
                    LOG.debug("{} Scheduler has stopped", ServerConstants.LOGGER_SCHEDULER);
                } else {
                    DomainException dexp = DomainException.getDomainExceptionInstance();
                    dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_051));
                    dexp.setCode(DomainException.Code.APZ_DM_051.toString());
                    dexp.setPriority("1");
                    LOG.error("{} Scheduler has stopped already {}", ServerConstants.LOGGER_SCHEDULER, dexp);
                    throw dexp;
                }
            }

            pMessage.getResponseObject().setResponseJson(status);

        } catch (SchedulerException se) {
            LOG.error(ServerConstants.LOGGER_SCHEDULER, se);
        }
    }

    private JSONObject startSchedulerAndGetStatus(Message pMessage) throws SchedulerException {
        JSONObject status = new JSONObject();
        LOG.info("{} ********** Scheduler is getting started ***********", ServerConstants.LOGGER_SCHEDULER);
        List<TbAsmiJobMaster> lJobMasterDetails = null;

        pMessage.getHeader().setServiceType("appzillonFetchJobDetails");
        DomainStartup.getInstance().processRequest(pMessage);

        JSONArray jobArrayDetails = pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.APPZILLONJOBDETAILSRESPONSE);

        if (jobArrayDetails.length() > 0) {
            lJobMasterDetails = new ArrayList<>();
            for (int i = 0; i < jobArrayDetails.length(); i++) {
                JSONObject json = jobArrayDetails.getJSONObject(i);
                TbAsmiJobMaster jobMaster = new TbAsmiJobMaster();
                jobMaster.setJobName(json.getString(JOB_NAME));
                jobMaster.setAppId(json.getString("appId"));
                jobMaster.setJobClass(json.getString("jobClass"));
                jobMaster.setJobData(json.getString("jobData"));
                jobMaster.setJobStatus(json.getString("jobStatus"));
                lJobMasterDetails.add(jobMaster);
            }

            for (TbAsmiJobMaster jobDetail : lJobMasterDetails) {
                scheduleJobs(jobDetail, pMessage);
                LOG.info("{} Jobs are scheduled and  starting", ServerConstants.LOGGER_SCHEDULER);
            }

            if (this.scheduler == null) {
                this.scheduler = getSchedulerFactoryInstance();
            }

            if (this.scheduler != null) {
                if (this.scheduler.isStarted()) {
                    LOG.info("{} Scheduler has started already", ServerConstants.LOGGER_SCHEDULER);
                } else {
                    this.scheduler.start();
                }
                status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
            } else {
                LOG.info("{} Scheduler has not started yet ", ServerConstants.LOGGER_SCHEDULER);
                status.put(ServerConstants.STATUS, ServerConstants.SCHEDULER_NOT_STARTED);
            }
        } else {
            LOG.info("{} No pending jobs are found to start the scheduler", ServerConstants.LOGGER_SCHEDULER);
            status.put(ServerConstants.STATUS, ServerConstants.NO_JOBS);
        }
        return status;
    }

    private TbAsmiJobMaster getJobDetail(Message pMessage) {
        JSONObject response = null;
        Object jsonObj = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLONJOBDETAILRESPONSE);
        if (jsonObj instanceof JSONArray) {
            response = pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.APPZILLONJOBDETAILRESPONSE);
        } else if (jsonObj instanceof JSONObject) {
            response = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLONJOBDETAILRESPONSE);
        }

        TbAsmiJobMaster jobMaster = null;
        if (response != null) {
            jobMaster = new TbAsmiJobMaster();
            jobMaster.setJobName(response.getString(JOB_NAME));
            jobMaster.setAppId(response.getString("appId"));
            jobMaster.setJobClass(response.getString("jobClass"));
            jobMaster.setJobData(response.getString("jobData"));
            jobMaster.setJobStatus(response.getString("jobStatus"));
        }
        return jobMaster;
    }

    private void scheduleJobs(TbAsmiJobMaster jobDetail, Message pMessage) {
        // Load the target class using its binary name
        Class<Job> c = null;
        try {
            c = (Class<Job>) AppzillonScheduler.class.getClassLoader().loadClass(jobDetail.getJobClass());
            LOG.info("{} Loading job class ... {}", ServerConstants.LOGGER_SCHEDULER, c.getName());

            JSONObject jExpRequest = new JSONObject();
            JSONObject jExpRequestNode = new JSONObject();
            jExpRequest.put(JOB_NAME, jobDetail.getJobName());
            jExpRequestNode.put("fetchJobExpDetail", jExpRequest);

            JobDataMap data = new JobDataMap();
            JSONObject jObj = new JSONObject(jobDetail.getJobData());

            if (jObj.has(ServerConstants.DATA_JSON)) {
                String jsonArray = jObj.get(ServerConstants.DATA_JSON).toString();
                LOG.debug("{} DATA JSON Array : {}", ServerConstants.LOGGER_SCHEDULER, jsonArray);
                JSONArray jobDataArray = JSONUtils.getJsonArrayFromString(jsonArray);

                for (int j = 0; j < jobDataArray.length(); j++) {
                    JSONObject rec = jobDataArray.getJSONObject(j);
                    data.put(rec.getString("key"), rec.getString("value"));
                }
            }

            pMessage.getHeader().setServiceType("appzillonFetchJobExpDetail");
            pMessage.getRequestObject().setRequestJson(jExpRequestNode);
            DomainStartup.getInstance().processRequest(pMessage);

            TbAsmiJobExpression lJobExp = getJobExpressionDetail(pMessage);

            // Define the job and tie it to given class
            JobDetail job = JobBuilder.newJob(c)
                    .withIdentity(lJobExp.getJobName(), ServerConstants.GROUP_NAME)
                    .usingJobData(data)
                    .build();

            String startDate = lJobExp.getStartDate();
            String endDate = lJobExp.getEndDate();

            //construct expression
            String expression = AppzillonExpression.constructExpression(lJobExp);
            LOG.debug("{} Job expression : {}", ServerConstants.LOGGER_SCHEDULER, expression);

            //create trigger
            Trigger trigger = AppzillonTriggerBuilder.createTrigger(startDate, endDate, expression, lJobExp.getJobName());

            //Schedule it
            LOG.debug("{} Scheduling job : {} Adding trigger listener :{}", ServerConstants.LOGGER_SCHEDULER, job.getKey().getName(), trigger.getKey());

            scheduler.getListenerManager().addTriggerListener(new AppzillonTriggerListener(), GroupMatcher.triggerGroupEquals(ServerConstants.GROUP_NAME));
            scheduler.scheduleJob(job, trigger);

        } catch (ObjectAlreadyExistsException obae) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_050));
            dexp.setCode(DomainException.Code.APZ_DM_050.toString());
            dexp.setPriority("1");
            LOG.error("{} Unable to store Job, because one already exists with this identification {}", ServerConstants.LOGGER_SCHEDULER, dexp);
            throw dexp;
        } catch (ClassNotFoundException | SchedulerException cnfe) {
            LOG.error(ServerConstants.LOGGER_SCHEDULER, cnfe);
        }
    }

    private TbAsmiJobExpression getJobExpressionDetail(Message pMessage) {
        JSONObject response = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLONJOBEXPRESSIONRESPONSE);
        TbAsmiJobExpression jobExp = new TbAsmiJobExpression();
        jobExp.setJobName(response.getString(JOB_NAME));
        if (Utils.isNotNullOrEmpty(response.getString("jobExpression"))) {
            jobExp.setExpression(response.getString("jobExpression"));
        } else {
            jobExp.setExpHr(response.getString("jobExpHr"));
            jobExp.setExpSec(response.getString("jobExpSec"));
            jobExp.setExpStartSec(response.getString("jobExpStartSec"));
            jobExp.setExpMin(response.getString("jobExpMin"));
            jobExp.setExpDom(response.getString("jobExpDom"));
            jobExp.setExpDow(response.getString("jobExpDow"));
            jobExp.setExpMonth(response.getString("jobExpMon"));
            jobExp.setExpYear(response.getString("jobExpYear"));
        }
        return jobExp;
    }

    private void deleteJob(Message pMessage) {
        JSONObject schedulerReq = pMessage
                .getRequestObject()
                .getRequestJson()
                .getJSONObject(ServerConstants.SCHEDULER);

        String jobName = schedulerReq.getString(JOB_NAME);
        JobKey jobKey = new JobKey(jobName, ServerConstants.GROUP_NAME);
        LOG.info("{} ********** Deleting job : {} ***********", ServerConstants.LOGGER_SCHEDULER, jobName);
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                LOG.debug("{} Job key : {} is deleted, so Updating as Deleted ***********", ServerConstants.LOGGER_SCHEDULER, jobKey);
                pMessage.getHeader().setServiceType("appzillonDeleteJob");
                DomainStartup.getInstance().processRequest(pMessage);
            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_052));
                dexp.setCode(DomainException.Code.APZ_DM_052.toString());
                dexp.setPriority("1");
                LOG.error("{} Job : {} is already deleted : {}", ServerConstants.LOGGER_SCHEDULER, jobKey, dexp);
                throw dexp;
            }
        } catch (SchedulerException e) {
            LOG.error(ServerConstants.LOGGER_SCHEDULER, e);
        }
    }

    private Scheduler getSchedulerFactoryInstance() {
        if (scheduler == null) {
            try {
                scheduler = new StdSchedulerFactory().getScheduler();
                LOG.debug("{} New Scheduler Factory instance created", ServerConstants.LOGGER_SCHEDULER);
            } catch (SchedulerException e) {
                LOG.error(ServerConstants.LOGGER_SCHEDULER, e);
            }
        }
        return scheduler;
    }

    public void updateJobDetail(String jobName, String jobStatus, Timestamp time) {
        LOG.debug("{} Updating Job status : {} for the Job Name : {}", ServerConstants.LOGGER_SCHEDULER, jobStatus, jobName);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JOB_NAME, jobName);
        jsonObject.put(ServerConstants.JOB_STATUS, jobStatus);
        jsonObject.put(ServerConstants.TIME, time);
        JSONObject request = new JSONObject();
        request.put(ServerConstants.APPZILLON_ROOT_SCHEDULER_UPDATE_JOBSTATUS_REQ, jsonObject);
        Message message = Message.getInstance();
        message.getHeader().setServiceType(ServerConstants.UPDATE_JOB_SERVICE);
        message.getRequestObject().setRequestJson(request);
        message.getResponseObject().setResponseJson(request);
        DomainStartup.getInstance().processRequest(message);
    }

}
