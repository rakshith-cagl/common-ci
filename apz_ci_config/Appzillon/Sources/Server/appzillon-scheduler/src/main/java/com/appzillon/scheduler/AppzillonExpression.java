package com.appzillon.scheduler;

import com.iexceed.appzillon.domain.entity.TbAsmiJobExpression;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class AppzillonExpression {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
            ServerConstants.LOGGER_SCHEDULER,
            AppzillonScheduler.class.getName());

    private AppzillonExpression() {

    }

    public static String constructExpression(TbAsmiJobExpression tbAsmiQuartzExpression) {
        LOG.debug("{} Inside constructExpression", ServerConstants.LOGGER_SCHEDULER);
        String expression = null;
        if (Utils.isNotNullOrEmpty(tbAsmiQuartzExpression.getExpression())) {
            expression = tbAsmiQuartzExpression.getExpression();
        } else {
            expression = getExceptionWhenQuartzObjIsNull(tbAsmiQuartzExpression);
        }
        return expression;
    }

    private static String getExceptionWhenQuartzObjIsNull(TbAsmiJobExpression tbAsmiQuartzExpression) {
        String expStartSec = "";
        String expIntervalSec = "";
        String expMin = "";
        String expHr = "";
        String expDom = "";
        String expMon = "";
        String expDow = "";
        String expYear = "";
        if (tbAsmiQuartzExpression.getExpStartSec() != null) {
            expStartSec = tbAsmiQuartzExpression.getExpStartSec();
        }
        if (tbAsmiQuartzExpression.getExpSec() != null) {
            expIntervalSec = tbAsmiQuartzExpression.getExpSec();
        }
        if (tbAsmiQuartzExpression.getExpMin() != null) {
            expMin = tbAsmiQuartzExpression.getExpMin();
        }
        if (tbAsmiQuartzExpression.getExpHr() != null) {
            expHr = tbAsmiQuartzExpression.getExpHr();
        }
        if (tbAsmiQuartzExpression.getExpDom() != null) {
            expDom = tbAsmiQuartzExpression.getExpDom();
        }
        if (tbAsmiQuartzExpression.getExpMonth() != null) {
            expMon = tbAsmiQuartzExpression.getExpMonth();
        }
        if (tbAsmiQuartzExpression.getExpDow() != null) {
            expDow = tbAsmiQuartzExpression.getExpDow();
        }
        if (tbAsmiQuartzExpression.getExpYear() != null) {
            expYear = tbAsmiQuartzExpression.getExpYear();
        }
        return expStartSec + "/" + expIntervalSec + " " + expMin + " " + expHr + " " + expDom + " " + expMon + " " + expDow + " " + expYear;
    }
}