package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsmiSecurityParams;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiSecurityParamsRepository;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.SecurityParams;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

@Named("SecurityParamsService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class SecurityParamsService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            SecurityParamsService.class.getName());

    @Inject
    TbAsmiSecurityParamsRepository cAsmiSecurityParamsRepo;

    public void fetchSecurityParams(Message pMessage) {
        TbAsmiSecurityParams lSecurityParams = cAsmiSecurityParamsRepo
                .findSecurityParamsbyAppId(pMessage.getHeader().getAppId());
        try {
            LOG.debug("{} SecurityParamsService Security Parameters for the AppId :{}",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, pMessage.getHeader().getAppId());
            if (lSecurityParams != null) {
                SecurityParams lMsgSecurityParams = SecurityParams.getInstance();
                lMsgSecurityParams.setAllowUserPassword(lSecurityParams.getAllowUserPasswordEntry());
                lMsgSecurityParams.setAppId(lSecurityParams.getAppId());
                lMsgSecurityParams.setAutoApprove(lSecurityParams.getAutoApprove());
                lMsgSecurityParams.setCreateTs(lSecurityParams.getCreateTs());
                lMsgSecurityParams.setCreateUserId(lSecurityParams.getCreateUserId());
                lMsgSecurityParams.setDefaultAuthorization(lSecurityParams.getDefaultAuthorization());
                lMsgSecurityParams.setFailCountTimeout(lSecurityParams.getFailCountTimeout());
                lMsgSecurityParams.setLogTxn(lSecurityParams.getTransactionLogRequest());
                lMsgSecurityParams.setMultiDviceLoginAlowd(lSecurityParams.getLoginAllowedForMultipleDevice());
                lMsgSecurityParams.setNooffailedcounts(lSecurityParams.getNooffailedcounts());
                lMsgSecurityParams.setPasswordCount(lSecurityParams.getPasswordCount());
                lMsgSecurityParams.setPwdChangeFreq(lSecurityParams.getPassChangeFreq());
                lMsgSecurityParams.setPwdForgotAcceptUsrPwd(lSecurityParams.getPwdRsetAccptPwd());
                lMsgSecurityParams.setPwdForgotCommChannel(lSecurityParams.getPwdRsetComChannel());
                lMsgSecurityParams.setPwdForgotValParams(lSecurityParams.getPwdRsetValidate());
                lMsgSecurityParams.setPwdlastNNotToUse(lSecurityParams.getLastNPassNotToUse());
                lMsgSecurityParams.setPwdMaxLength(lSecurityParams.getMaxLength());
                lMsgSecurityParams.setPwdMinLength(lSecurityParams.getMinLength());
                lMsgSecurityParams.setPwdMinNum(lSecurityParams.getMinNumNum());
                lMsgSecurityParams.setPwdMinSpclChar(lSecurityParams.getMinNumSpclChar());
                lMsgSecurityParams.setPwdMinUpperCaseChar(lSecurityParams.getMinNumUpperCaseChar());
                lMsgSecurityParams.setPwdMinLowerCaseChar(lSecurityParams.getMinNumLowerCaseChar());
                lMsgSecurityParams.setPwdrestrictedSplChars(lSecurityParams.getRestrictedSplChars());
                lMsgSecurityParams.setServerToken(lSecurityParams.getServerToken());
                lMsgSecurityParams.setSessionTimeout(lSecurityParams.getSessionTimeout());
                lMsgSecurityParams.setUsdPwdCommChnl(lSecurityParams.getCredCommunicationChannel());
                lMsgSecurityParams.setVersionNo(lSecurityParams.getVersionNo());
                lMsgSecurityParams.setTransactionLogPayload(lSecurityParams.getTransactionLogPayload());
                // added by sasidhar
                lMsgSecurityParams.setOtpExpiry(lSecurityParams.getOtpExpiry());
                lMsgSecurityParams.setOtpValidationCount(lSecurityParams.getOtpValidationCount());
                lMsgSecurityParams.setOtpFormat(lSecurityParams.getOtpFormat());
                lMsgSecurityParams.setOtpResendCount(lSecurityParams.getOtpResendCount());
                lMsgSecurityParams.setOtpResendLockTimeOut(lSecurityParams.getOtpResendLockTimeOut());
                lMsgSecurityParams.setOtpResend(lSecurityParams.getOtpResend());
                lMsgSecurityParams.setOtpLength(lSecurityParams.getOtpLength());
                lMsgSecurityParams.setPwdChangeCommChannel(lSecurityParams.getPwdChangeCommChannel());
                // added on 03/04/17
                lMsgSecurityParams.setPasswordOnAuthorization(lSecurityParams.getPasswordOnAuthorization());
                lMsgSecurityParams.setDataIntegrity(lSecurityParams.getDataIntegrity());
                lMsgSecurityParams.setFmwTxnReq(lSecurityParams.getFmwTxnReq());
                lMsgSecurityParams.setFmwTxnPayload(lSecurityParams.getFmwTxnPayload());
                lMsgSecurityParams.setOtpRegenCount(lSecurityParams.getOtpRegenCount());
                lMsgSecurityParams.setAccessTokenExpiry(lSecurityParams.getAccessTokenExpiry());
                pMessage.setSecurityParams(lMsgSecurityParams);
                // LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " SecurityParamsService From
                // Message -:" + pMessage.getSecurityParams());
            } else {
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_044)
                        + pMessage.getHeader().getAppId());
                lDomainException.setCode(DomainException.Code.APZ_DM_044.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }

        } catch (IllegalArgumentException ex) {
            LOG.error("{} IllegalArgumentException", ServerConstants.LOGGER_PREFIX_DOMAIN, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_006));
            lDomainException.setCode(DomainException.Code.APZ_DM_006.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }

    }
}
