package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMasterPK;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.IntfMasterDtls;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named("InterfaceMasterService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class InterfaceMasterService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            InterfaceMasterService.class.getName());
    private static Map<String, Map<String, IntfMasterDtls>> interfaceMasterMap = new HashMap<String, Map<String, IntfMasterDtls>>();
    @Inject
    TbAsmiIntfMasterRepository cAsmiIntfMasterRepo;

    public static Map<String, Map<String, IntfMasterDtls>> getInterfaceMasterMap() {
        return interfaceMasterMap;
    }

    public static void setInterfaceMasterMap(Map<String, Map<String, IntfMasterDtls>> interfaceMasterMap) {
        InterfaceMasterService.interfaceMasterMap = interfaceMasterMap;
    }

    public void fetchInterfaceMap() {
        Map<String, IntfMasterDtls> interfaceMap = new HashMap<String, IntfMasterDtls>();
        List<TbAsmiIntfMaster> interfaceList = cAsmiIntfMasterRepo.findAll();
        for (TbAsmiIntfMaster interfaceDtls : interfaceList) {
            String appId = interfaceDtls.getTbAsmiIntfMasterPK().getAppId();
            String interfaceId = interfaceDtls.getTbAsmiIntfMasterPK().getInterfaceId();
            String category = interfaceDtls.getCategory();
            String type = interfaceDtls.getType();
            String description = interfaceDtls.getDescription();
            String createUserId = interfaceDtls.getCreateUserId();
            Date createTs = interfaceDtls.getCreateTs();
            int versionNo = interfaceDtls.getVersionNo();
            String captchaReq = interfaceDtls.getCaptchaReq();
            IntfMasterDtls intfMasterDtls = new IntfMasterDtls();
            if (interfaceMasterMap.containsKey(appId)) {
                interfaceMap = interfaceMasterMap.get(appId);
            } else {
                interfaceMap = new HashMap<String, IntfMasterDtls>();
            }
            intfMasterDtls.setAppId(appId);
            intfMasterDtls.setCategory(category);
            intfMasterDtls.setType(type);
            intfMasterDtls.setDescription(description);
            intfMasterDtls.setCreateUserId(createUserId);
            intfMasterDtls.setCreateTs(createTs);
            intfMasterDtls.setVersionNo(versionNo);
            intfMasterDtls.setCaptchaReq(captchaReq);
            intfMasterDtls.setDgTxnLogRequired(interfaceDtls.getDgTxnLogReq());
            intfMasterDtls.setTxnLogReq(interfaceDtls.getTxnLogReq());
            intfMasterDtls.setTxnLogPayLoadReq(interfaceDtls.getTxnLogPayLoadReq());
            intfMasterDtls.setFmwTxnReq(interfaceDtls.getFmTxnReq());
            intfMasterDtls.setFmwTxnPayloadReq(interfaceDtls.getFmwTxnPayloadReq());
            intfMasterDtls.setAuthorizationReq(interfaceDtls.getAuthrzReq());
            intfMasterDtls.setCaptchaType(interfaceDtls.getCaptchaType());
            interfaceMap.put(interfaceId, intfMasterDtls);
            interfaceMasterMap.put(appId, interfaceMap);
        }
        LOG.info("{} Populated Interface Map: {}", ServerConstants.LOGGER_PREFIX_DOMAIN, interfaceMasterMap);
    }

    public void updateIntfMaster(Message pMessage) {
        LOG.debug("{} Updating interface master table intf definations", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> interfaceIdList = cAsmiIntfMasterRepo.getListOfExternalInterfaces(pMessage.getHeader().getAppId());
        for (String interfaceId : interfaceIdList) {
            if (Utils.isNotNullOrEmpty(PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), interfaceId))) {
                TbAsmiIntfMasterPK tbAsmiIntfMasterPK = new TbAsmiIntfMasterPK();
                tbAsmiIntfMasterPK.setAppId(pMessage.getHeader().getAppId());
                tbAsmiIntfMasterPK.setInterfaceId(interfaceId);
                Optional<TbAsmiIntfMaster> tbAsmiIntfMaster = cAsmiIntfMasterRepo.findById(tbAsmiIntfMasterPK);
                String interfaceDef = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), interfaceId);
                tbAsmiIntfMaster.get().setInterfaceDef(interfaceDef);
                cAsmiIntfMasterRepo.save(tbAsmiIntfMaster.get());
            }
        }
    }
}
