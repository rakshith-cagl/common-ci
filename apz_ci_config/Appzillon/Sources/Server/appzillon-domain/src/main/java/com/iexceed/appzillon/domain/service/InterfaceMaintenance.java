package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.domain.spec.InterfaceMasterSpecification;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.maputils.MapUtils;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.iexceed.appzillon.domain.utils.Constants.DOUBLE_BRACES;

/**
 * @author Vinod Rawat
 */
@Named(ServerConstants.SERVICE_INTERFACE_MAINTENANCE)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class InterfaceMaintenance {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            InterfaceMaintenance.class.getName());
    @Inject
    TbAsmiIntfMasterRepository cintfMasterRepo;

    public void createInterfaceMaster(Message pMessage) {
        try {
            JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZCREATEINFMASTERREQUEST);
            LOG.debug("{} Entered create interface details", ServerConstants.LOGGER_PREFIX_DOMAIN);
            String appId = lrequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
            String interfaceId = lrequest.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();
            String createUserId = pMessage.getHeader().getUserId();
            if (Utils.isNullOrEmpty(appId) && Utils.isNullOrEmpty(interfaceId)) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
                dexp.setCode(DomainException.Code.APZ_DM_009.toString());
                dexp.setPriority("1");
                LOG.error("{} Primary columns values not found", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
            TbAsmiIntfMasterPK lfrmiIntfMasterPK = new TbAsmiIntfMasterPK(
                    lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    lrequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
            Optional<TbAsmiIntfMaster> lfrmiIntfMasterOpt = cintfMasterRepo.findById(lfrmiIntfMasterPK);
            TbAsmiIntfMaster lfrmiIntfMaster;
            if (lfrmiIntfMasterOpt.isPresent()) {
                LOG.debug("{} interface details is already found in interface master.",
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ldomainException = DomainException.getDomainExceptionInstance();
                ldomainException
                        .setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_024));
                ldomainException.setCode(DomainException.Code.APZ_DM_024.toString());
                ldomainException.setPriority("1");
                throw ldomainException;
            } else {
                lfrmiIntfMaster = new TbAsmiIntfMaster();
                lfrmiIntfMaster.setCategory(lrequest.getString(ServerConstants.CATEGORY));
                lfrmiIntfMaster.setCreateTs(new Date());
                lfrmiIntfMaster.setCreateUserId(createUserId);
                lfrmiIntfMaster.setDescription(lrequest.getString(ServerConstants.DESCRIPTION));
                lfrmiIntfMaster.setTbAsmiIntfMasterPK(lfrmiIntfMasterPK);
                lfrmiIntfMaster.setType(lrequest.getString(ServerConstants.TYPE));
                lfrmiIntfMaster.setVersionNo(1);
                cintfMasterRepo.save(lfrmiIntfMaster);

                JSONObject lintfResponseObj = new JSONObject();
                JSONObject statusobj = new JSONObject();
                statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
                lintfResponseObj.put(ServerConstants.APPZCREATEINFMASTERRESPONSE, statusobj);

                LOG.debug("{} Built success response.", ServerConstants.LOGGER_PREFIX_DOMAIN);
                pMessage.getResponseObject().setResponseJson(lintfResponseObj);

            }
        } catch (JSONException ex) {
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage("Failed creating a new Interface.");
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");
            LOG.error("{} Failed creating a new Interface : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, ex.getMessage(),
                    ldomainException);
            throw ldomainException;
        }

    }

    public void fetchInterfaceMaster(Message pMessage) {
        try {
            LOG.debug("{} Entered fetch interface details", ServerConstants.LOGGER_PREFIX_DOMAIN);

            TbAsmiIntfMasterPK lfrmiIntfMasterPK = new TbAsmiIntfMasterPK(pMessage.getHeader().getAppId(),
                    pMessage.getHeader().getInterfaceId());
            Optional<TbAsmiIntfMaster> lfrmiIntfMaster = cintfMasterRepo.findById(lfrmiIntfMasterPK);
            if (lfrmiIntfMaster.isPresent()) {
                JSONObject res = new JSONObject(MapUtils.convertObjectToMap(lfrmiIntfMaster));
                pMessage.getResponseObject().setResponseJson(res);
            } else {
                DomainException ldomainException = DomainException.getDomainExceptionInstance();
                ldomainException
                        .setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_023));
                ldomainException.setCode(DomainException.Code.APZ_DM_023.toString());
                ldomainException.setPriority("1");
                LOG.error("{} Header's interface details are not found in interface master..",
                        ServerConstants.LOGGER_PREFIX_DOMAIN, ldomainException);
                throw ldomainException;
            }

        } catch (IllegalArgumentException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.ILLEGAL_ARGUMENT_EXCEPTION,
                    ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_006));
            ldomainException.setCode(DomainException.Code.APZ_DM_006.toString());
            ldomainException.setPriority("1");
            throw ldomainException;
        }

    }

    public void searchInterfaceMaster(Message pMessage) {
        JSONObject lrequest = null;
        JSONObject lresponse = null;

        try {
            LOG.debug("{} Searching interface details", ServerConstants.LOGGER_PREFIX_DOMAIN);
            lrequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZSEARCHINFMASTERREQUEST);
            String appId = lrequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
            String interfaceId = lrequest.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();

            final String fappId;
            final String fintfId;
            if (Utils.isNullOrEmpty(appId)) {
                fappId = ServerConstants.PERCENT;
            } else {
                fappId = appId;
            }

            if (Utils.isNullOrEmpty(interfaceId)) {
                fintfId = ServerConstants.PERCENT;
            } else {
                fintfId = interfaceId;
            }

            List<TbAsmiIntfMaster> reslist = new ArrayList<TbAsmiIntfMaster>();
            reslist = cintfMasterRepo.findAll(Specification.where(InterfaceMasterSpecification.likeAppId(fappId))
                    .and(InterfaceMasterSpecification.likeInterfaceId(fintfId)));
            if (reslist.isEmpty()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error("{} No record found ", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
            JSONArray jsonarray = new JSONArray();
            int i = 0;
            while (i < reslist.size()) {
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, reslist.get(i).getTbAsmiIntfMasterPK().getAppId());
                obj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID,
                        reslist.get(i).getTbAsmiIntfMasterPK().getInterfaceId());
                obj.put(ServerConstants.DESCRIPTION, reslist.get(i).getDescription());
                obj.put(ServerConstants.TYPE, reslist.get(i).getType());

                obj.put(ServerConstants.CATEGORY, reslist.get(i).getCategory());

                jsonarray.put(obj);
                i++;
            }

            lresponse = new JSONObject();
            lresponse.put(ServerConstants.APPZSEARCHINFMASTERRESPONSE, jsonarray);
            pMessage.getResponseObject().setResponseJson(lresponse);
        } catch (JSONException ex) {
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");
            LOG.error("{} Exception while searchingInterface details {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    ex.getMessage(), ldomainException);
            throw ldomainException;
        }
    }

    public void updateInterfaceMaster(Message pMessage) throws DomainException {
        try {
            JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZUPDATEINFMASTERREQUEST);

            LOG.debug("{} Entered update interface details", ServerConstants.LOGGER_PREFIX_DOMAIN);
            String createUserId = pMessage.getHeader().getUserId();
            TbAsmiIntfMasterPK lfrmiIntfMasterPK = new TbAsmiIntfMasterPK(
                    lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    lrequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
            Optional<TbAsmiIntfMaster> lfrmiIntfMaster = cintfMasterRepo.findById(lfrmiIntfMasterPK);
            if (lfrmiIntfMaster.isPresent()) {
                lfrmiIntfMaster.get().setCategory(lrequest.getString(ServerConstants.CATEGORY));
                lfrmiIntfMaster.get().setType(lrequest.getString(ServerConstants.TYPE));
                lfrmiIntfMaster.get().setCreateUserId(createUserId);
                lfrmiIntfMaster.get().setDescription(lrequest.getString(ServerConstants.DESCRIPTION));
                lfrmiIntfMaster.get().setTbAsmiIntfMasterPK(lfrmiIntfMasterPK);
                lfrmiIntfMaster.get().setVersionNo(lfrmiIntfMaster.get().getVersionNo() + 1);
                cintfMasterRepo.save(lfrmiIntfMaster.get());
            } else {
                DomainException ldomainException = DomainException.getDomainExceptionInstance();
                ldomainException
                        .setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_025));
                ldomainException.setCode(DomainException.Code.APZ_DM_025.toString());
                ldomainException.setPriority("1");
                LOG.error("{} Interface details doesn't not exist..", ServerConstants.LOGGER_PREFIX_DOMAIN,
                        ldomainException);
                throw ldomainException;
            }
            JSONObject lintfresponseobj = new JSONObject();
            JSONObject statusobj = new JSONObject();
            statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            lintfresponseobj.put(ServerConstants.APPZUPDATEINFMASTERRESPONSE, statusobj);

            pMessage.getResponseObject().setResponseJson(lintfresponseobj);
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage("Failed creating a new Interface....");
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");

            throw ldomainException;
        }

    }

    public void deleteInterfaceMaster(Message pMessage) {
        try {
            JSONObject lreqJSON = null;
            Object lRequest = pMessage.getRequestObject().getRequestJson().get("appzillonDeleteIntfMasterRequest");
            if (lRequest instanceof JSONArray) {
                int i = 0;
                while (i < ((JSONArray) lRequest).length()) {
                    lreqJSON = ((JSONArray) lRequest).getJSONObject(i);
                    deleteInterface(pMessage, lreqJSON);
                    i++;
                }
            } else {
                deleteInterface(pMessage, (JSONObject) lRequest);
            }
            JSONObject status = new JSONObject();
            status.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            JSONObject lresponse = new JSONObject();
            lresponse.put(ServerConstants.APPZDELETEINFMASTERRESPONSE, status);
            pMessage.getResponseObject().setResponseJson(lresponse);

        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage("Failed deleting Interface..");
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");

            throw ldomainException;
        }
    }

    public void deleteInterface(Message pMessage, JSONObject pjson) {
        LOG.debug("{} inside deleteInterface", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            String appId = pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String interfaceId = pjson.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
            TbAsmiIntfMasterPK id = new TbAsmiIntfMasterPK(appId, interfaceId);

            Optional<TbAsmiIntfMaster> res = cintfMasterRepo.findById(id);
            if (res.isPresent()) {
                cintfMasterRepo.delete(res.get());
            } else {
                DomainException ldomainException = DomainException.getDomainExceptionInstance();
                ldomainException
                        .setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_026));
                ldomainException.setCode(DomainException.Code.APZ_DM_026.toString());
                ldomainException.setPriority("1");

                throw ldomainException;
            }
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");
            throw ldomainException;
        }
    }

    public void getInterfaceDefinition(Message pMessage) {
        JSONObject lrequest;
        JSONObject lresponse = new JSONObject();

        LOG.debug("{} Get interface definition details", ServerConstants.LOGGER_PREFIX_DOMAIN);
        lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_INTF_DEF_REQUEST);
        String appId = lrequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
        JSONArray jsonArray = lrequest.getJSONArray(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
        List<String> interfaceIds = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            interfaceIds.add((String) jsonArray.get(i));
        }
        List<Object[]> interfaceDefs = cintfMasterRepo.findIntfDefbyAppIdAndInterfaceId(appId, interfaceIds);
        JSONArray array = new JSONArray();
        for (Object[] obj : interfaceDefs) {
            JSONObject jsonObject = new JSONObject();
            String interfaceId = (String) obj[0];
            String interfaceDef = (String) obj[1];
            jsonObject.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, interfaceId);
            jsonObject.put(ServerConstants.INTERFACE_DEFINITION, interfaceDef);
            array.put(jsonObject);
        }

        lresponse.put(ServerConstants.APPZILLON_ROOT_GET_INTF_DEF_RESPONSE, array);
        pMessage.getResponseObject().setResponseJson(lresponse);

    }
}
