package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.entity.history.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.*;
import com.iexceed.appzillon.domain.spec.InterfaceMasterSpecification;
import com.iexceed.appzillon.domain.spec.RoleMasterSpecification;
import com.iexceed.appzillon.domain.spec.ScreenSpecification;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Vinod Rawat
 */
@Named("RoleMaintenanceService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class RoleMaintenance {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            RoleMaintenance.class.toString());
    @Inject
    private TbAsmiRoleMasterRespository roleMasterRepo;
    @Inject
    private TbAsmiRoleIntfRepository roleIntfRepo;
    @Inject
    private TbAsmiRoleScrRepository roleScrRepo;

    @Inject
    private TbAsmiRoleControlsRepository roleControlsRepo;

    @Inject
    private TbAsmiScrMasterRepository screenMasterRepo;
    @Inject
    private TbAsmiIntfMasterRepository interfaceMasterRepo;
    @Inject
    private TbAshsRoleMasterRepository roleMasterHistoryRepo;
    @Inject
    private TbAshsRoleControlsRepository roleControlHistoryRepo;
    @Inject
    private TbAshsRoleIntfRepository roleIntfHistoryRepo;
    @Inject
    private TbAshsRoleScrRepository roleScrHistoryRepo;

    public void createRole(Message pMessage) {
        LOG.debug("{} inside createRole()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject mResponse = null;
        try {
            if (pMessage.getRequestObject().getRequestJson()
                    .get(ServerConstants.CREATE_ROLEMASTER_REQUEST) instanceof JSONArray) {
                JSONArray arr = (JSONArray) pMessage.getRequestObject().getRequestJson()
                        .get(ServerConstants.CREATE_ROLEMASTER_REQUEST);
                for (int i = 0; i < arr.length(); i++) {
                    this.createRoleIntfScr(pMessage, (JSONObject) arr.get(i));
                }
            } else if (pMessage.getRequestObject().getRequestJson()
                    .get(ServerConstants.CREATE_ROLEMASTER_REQUEST) instanceof JSONObject) {
                JSONObject json = (JSONObject) pMessage.getRequestObject().getRequestJson()
                        .get(ServerConstants.CREATE_ROLEMASTER_REQUEST);
                this.createRoleIntfScr(pMessage, json);
            }
            mResponse = new JSONObject();
            JSONObject statusobj = new JSONObject();
            statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            mResponse.put(ServerConstants.CREATE_ROLEMASTER_RESPONSE, statusobj);
        } catch (JSONException jsone) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, dexp);
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(mResponse);
    }

    private void createRoleIntfScr(Message pMessage, JSONObject pRequest) {
        LOG.debug("{} inside createRoleIntfScr()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(pRequest.getString(ServerConstants.ROLEID),
                    pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            if (!roleMasterRepo.findById(id).isPresent()) {
                if (pRequest.has(ServerConstants.INTERFACE_IDS)) {
                    JSONArray interfacearray = pRequest.getJSONArray(ServerConstants.INTERFACE_IDS);
                    JSONObject interfaceidobj = null;
                    String lInterfaceid = "";
                    TbAsmiRoleIntfPK idintf = null;
                    for (int i = 0; i < interfacearray.length(); i++) {
                        interfaceidobj = (JSONObject) interfacearray.get(i);
                        lInterfaceid = interfaceidobj.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();
                        if (!lInterfaceid.isEmpty()) {
                            idintf = new TbAsmiRoleIntfPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                                    pRequest.getString(ServerConstants.ROLEID), lInterfaceid);
                            if (!roleIntfRepo.findById(idintf).isPresent()) {
                                createRoleInterface(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                                        pRequest.getString(ServerConstants.ROLEID), lInterfaceid,
                                        pMessage.getHeader().getUserId());
                            }
                        }
                    }
                }

                if (pRequest.has(ServerConstants.SCREEN_IDS)) {
                    JSONArray screenarray = pRequest.getJSONArray(ServerConstants.SCREEN_IDS);
                    JSONObject screenidobj = null;
                    String lScreenid = "";
                    TbAsmiRoleScrPK idscr = null;
                    for (int i = 0; i < screenarray.length(); i++) {
                        screenidobj = (JSONObject) screenarray.get(i);
                        lScreenid = screenidobj.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
                        if (!lScreenid.isEmpty()) {
                            idscr = new TbAsmiRoleScrPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                                    lScreenid, pRequest.getString(ServerConstants.ROLEID));
                            if (!roleScrRepo.findById(idscr).isPresent()) {
                                createRoleScreen(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lScreenid,
                                        pRequest.getString(ServerConstants.ROLEID), pMessage.getHeader().getUserId());
                            }
                        }
                    }
                }
                if (pRequest.has(ServerConstants.CONTROL_IDS)) {
                    JSONArray controlarray = pRequest.getJSONArray(ServerConstants.CONTROL_IDS);
                    JSONObject controlidobj = null;
                    String lControlid = "";
                    TbAsmiRoleControlsPK idctrl = null;
                    for (int i = 0; i < controlarray.length(); i++) {
                        controlidobj = (JSONObject) controlarray.get(i);
                        lControlid = controlidobj.get(ServerConstants.MESSAGE_CONTROL_ID).toString();

                        if (!lControlid.isEmpty()) {
                            idctrl = new TbAsmiRoleControlsPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                                    pRequest.getString(ServerConstants.ROLEID), lControlid);
                            if (!roleControlsRepo.findById(idctrl).isPresent()) {
                                createRoleControl(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lControlid,
                                        pRequest.getString(ServerConstants.ROLEID), pMessage.getHeader().getUserId());
                            }
                        }
                    }
                }
                createRoleMaster(pRequest, pMessage.getHeader().getUserId(), ServerConstants.CREATE, 1);

            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_021);
                dexp.setCode(DomainException.Code.APZ_DM_021.toString());
                dexp.setPriority("1");
                LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
                                + "Record already exists in rolemaster.Do u want to update screens interfaces then go to update section",
                        dexp);
                throw dexp;
            }
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");

            throw dexp;
        }
    }

    private boolean createRoleMaster(JSONObject pRequest, String pUserId, String pAction, Integer versionNo) {
        LOG.debug("{} inside createRoleMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean status = false;
        TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(pRequest.getString(ServerConstants.ROLEID),
                pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        TbAsmiRoleMaster roleMaster = new TbAsmiRoleMaster();
        try {
            roleMaster.setTbAsmiRoleMasterPK(id);
            roleMaster.setRoleDesc(pRequest.getString(ServerConstants.ROLEDESCRIPTION));
            roleMaster.setCreateTs(new Timestamp(System.currentTimeMillis()));
            roleMaster.setCreateUserId(pUserId);
            roleMaster.setScreenAllowed(pRequest.getString(ServerConstants.SCREEN_ALLOWED));
            roleMaster.setInterfaceAllowed(pRequest.getString(ServerConstants.INTERFACE_ALLOWED));
            roleMaster.setControlAllowed(pRequest.getString(ServerConstants.CONTROL_ALLOWED));
            roleMaster.setMakerId(pUserId);
            roleMaster.setMakerTs(new Timestamp(System.currentTimeMillis()));
            roleMaster.setAuthStat("U");
            if (pAction.equalsIgnoreCase(ServerConstants.CREATE)) {
                versionNo = roleMasterHistoryRepo.findMaxVersionNo(
                        pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                        pRequest.getString(ServerConstants.ROLEID));
                roleMaster.setVersionNo(versionNo != null ? versionNo : 1);
            } else {
                LOG.debug("{} Updating Role Master Record", ServerConstants.LOGGER_PREFIX_DOMAIN);
                roleMaster.setVersionNo(versionNo + 1);
            }
            roleMasterRepo.save(roleMaster);
            status = true;
        } catch (Exception exp) {
            LOG.error("Error in Create/Update Role", exp);
            status = false;
        }
        return status;
    }

    private boolean createRoleInterface(String pAppId, String pRoleId, String pInterfaceid, String pUserId) {
        LOG.debug("{} inside createRoleInterface()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean status = false;
        TbAsmiRoleIntf recordintf = null;
        TbAsmiRoleIntfPK idintf = new TbAsmiRoleIntfPK(pAppId, pRoleId, pInterfaceid);
        try {
            recordintf = new TbAsmiRoleIntf();
            recordintf.setTbAsmiRoleIntfPK(idintf);
            recordintf.setCreateTs(new Timestamp(System.currentTimeMillis()));
            recordintf.setCreateUserId(pUserId);
            Integer versionNo = roleIntfHistoryRepo.findMaxVersionNo(pAppId, pRoleId, pInterfaceid);
            recordintf.setVersionNo(versionNo != null ? versionNo : 1);
            roleIntfRepo.save(recordintf);
            status = true;
        } catch (Exception exp) {
            status = false;
        }
        return status;
    }

    private boolean createRoleScreen(String pAppId, String pScreenid, String pRoleId, String pUserId) {
        LOG.debug("{} inside createRoleScreen()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean status = false;
        TbAsmiRoleScr recordscr = null;
        TbAsmiRoleScrPK idscr = new TbAsmiRoleScrPK(pAppId, pScreenid, pRoleId);
        try {
            recordscr = new TbAsmiRoleScr();
            recordscr.setTbAsmiRoleScrPK(idscr);
            recordscr.setCreateTs(new Timestamp(System.currentTimeMillis()));
            recordscr.setCreateUserId(pUserId);
            Integer versionNo = roleScrHistoryRepo.findMaxVersionNo(pAppId, pRoleId, pScreenid);
            recordscr.setVersionNo(versionNo != null ? versionNo : 1);
            roleScrRepo.save(recordscr);
            status = true;
        } catch (Exception exp) {
            status = false;
        }
        return status;
    }

    private boolean createRoleControl(String pAppId, String pControlid, String pRoleId, String pUserId) {
        LOG.debug("{} inside createRoleScreen()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean status = false;
        TbAsmiRoleControls recorctrl = null;
        TbAsmiRoleControlsPK idctrl = new TbAsmiRoleControlsPK(pAppId, pRoleId, pControlid);
        try {
            recorctrl = new TbAsmiRoleControls();
            recorctrl.setId(idctrl);
            recorctrl.setCreatedTs(new Timestamp(System.currentTimeMillis()));
            recorctrl.setCreatedBy(pUserId);
            Integer versionNo = roleControlHistoryRepo.findMaxVersionNo(pAppId, pRoleId, pControlid);
            recorctrl.setVersionNo(versionNo != null ? versionNo + 1 : 1);
            roleControlsRepo.save(recorctrl);
            status = true;
        } catch (Exception exp) {
            status = false;
        }
        return status;
    }

    /*
     * 10-6-2014 : Updated to handle delete and update record, NPE, ignoring empty
     * values from intf and screen id's
     */
    public void updateRole(Message pMessage) {
        LOG.debug("{} inside updateRole()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject mRequest = null;
        JSONObject mResponse = null;
        TbAsmiRoleMaster roleMaster = null;
        try {
            mRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.UPDATE_ROLEMASTER_REQUEST);
            String lAppid = mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String lRoleid = mRequest.getString(ServerConstants.ROLEID);
            String luserId = pMessage.getHeader().getUserId();

            TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(lRoleid, lAppid);
            Optional<TbAsmiRoleMaster> recordOpt = roleMasterRepo.findById(id);
            if (recordOpt.isPresent()) {
                roleMaster = recordOpt.get();
                // insert data into history table starts
                TbAshsRoleMasterPK masterPK = new TbAshsRoleMasterPK();
                masterPK.setAppId(roleMaster.getTbAsmiRoleMasterPK().getAppId());
                masterPK.setRoleId(roleMaster.getTbAsmiRoleMasterPK().getRoleId());
                masterPK.setVersionNo(roleMaster.getVersionNo());

                TbAshsRoleMaster ashsRoleMaster = new TbAshsRoleMaster(masterPK);
                ashsRoleMaster.setAuthStat(roleMaster.getAuthStat());
                ashsRoleMaster.setCheckerId(roleMaster.getCheckerId());
                ashsRoleMaster.setCheckerTs(roleMaster.getCheckerTs());
                ashsRoleMaster.setCreateUserId(roleMaster.getCreateUserId());
                ashsRoleMaster.setMakerId(roleMaster.getMakerId());
                ashsRoleMaster.setMakerTs(roleMaster.getMakerTs());
                ashsRoleMaster.setRoleDesc(roleMaster.getRoleDesc());
                ashsRoleMaster.setCreateTs(roleMaster.getCreateTs());
                ashsRoleMaster.setInterfaceAllowed(roleMaster.getInterfaceAllowed());
                ashsRoleMaster.setScreenAllowed(roleMaster.getScreenAllowed());
                ashsRoleMaster.setControlAllowed(roleMaster.getControlAllowed());

                roleMasterHistoryRepo.save(ashsRoleMaster);

                // insert data into history table ends
                createRoleMaster(mRequest, luserId, ServerConstants.UPDATE, roleMaster.getVersionNo());

                if (mRequest.has(ServerConstants.INTERFACE_IDS)) {
                    JSONArray interfacearray = mRequest.getJSONArray(ServerConstants.INTERFACE_IDS);

                    JSONObject interfaceidobj = null;
                    String interfaceid = "";
                    Optional<TbAsmiRoleIntf> recordintf;
                    TbAsmiRoleIntf recordintflist;
                    TbAsmiRoleIntfPK idintf;

                    List<TbAsmiRoleIntf> intflist = roleIntfRepo.findIntfByAppIdRoleId(lRoleid, lAppid);

                    for (int j = 0; j < intflist.size(); j++) {
                        recordintflist = (TbAsmiRoleIntf) intflist.get(j);
                        if (interfacearray.length() == 0) {
                            this.deleteIntf(recordintflist);
                        } else {
                            if (interfacearray.length() == 1) {
                                this.deleteIntf(recordintflist);
                            } else {
                                for (int i = 0; i < interfacearray.length(); i++) {
                                    interfaceidobj = (JSONObject) interfacearray.get(i);

                                    interfaceid = interfaceidobj.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)
                                            .toString();
                                    idintf = new TbAsmiRoleIntfPK(lAppid, lRoleid, interfaceid);
                                    recordintf = roleIntfRepo.findById(idintf);

                                    if (recordintf.isPresent() && (!(recordintf.get().getTbAsmiRoleIntfPK().getInterfaceId()
                                            .equals(recordintflist.getTbAsmiRoleIntfPK().getInterfaceId())))) {
                                        this.deleteIntf(recordintflist);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for (int i = 0; i < interfacearray.length(); i++) {
                        interfaceidobj = (JSONObject) interfacearray.get(i);
                        interfaceid = interfaceidobj.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();
                        idintf = new TbAsmiRoleIntfPK(lAppid, lRoleid, interfaceid);
                        recordintf = roleIntfRepo.findById(idintf);
                        if (!recordintf.isPresent()
                                && Utils.isNotNullOrEmpty(interfaceid)) {
                            createRoleInterface(lAppid, lRoleid, interfaceid, luserId);

                        }
                    }
                }
                if (mRequest.has(ServerConstants.SCREEN_IDS)) {
                    JSONArray screenarray = mRequest.getJSONArray(ServerConstants.SCREEN_IDS);
                    JSONObject screenidobj = null;
                    String screenid = "";
                    Optional<TbAsmiRoleScr> recordscr = null;
                    TbAsmiRoleScr recordscrlist = null;
                    TbAsmiRoleScrPK idscr = null;

                    List<TbAsmiRoleScr> scrlist = roleScrRepo.findScreensByAppIdRoleId(lRoleid, lAppid);

                    for (int j = 0; j < scrlist.size(); j++) {
                        recordscrlist = (TbAsmiRoleScr) scrlist.get(j);
                        if (screenarray.length() == 0) {
                            this.deleteScr(recordscrlist);
                        } else {
                            if (screenarray.length() == 1) {
                                // LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting the only avaible
                                // screen from DB since a new screen is created by deleting the exisiting
                                // one.");
                                this.deleteScr(recordscrlist);
                            } else {
                                for (int i = 0; i < screenarray.length(); i++) {
                                    screenidobj = (JSONObject) screenarray.get(i);

                                    screenid = screenidobj.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
                                    idscr = new TbAsmiRoleScrPK(lAppid, screenid, lRoleid);
                                    recordscr = roleScrRepo.findById(idscr);

                                    // LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "recordscr..." + recordscr);
                                    if (recordscr.isPresent()) {
                                        if (!(recordscr.get().getTbAsmiRoleScrPK().getScreenId()
                                                .equals(recordscrlist.getTbAsmiRoleScrPK().getScreenId()))) {
                                            // LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " deleted.scr.."+
                                            // recordscrlist.getTbAsmiRoleScrPK().getScreenId());
                                            this.deleteScr(recordscrlist);
                                            break; // added loop break not to check again for the same record
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (int i = 0; i < screenarray.length(); i++) {
                        screenidobj = (JSONObject) screenarray.get(i);
                        screenid = screenidobj.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
                        idscr = new TbAsmiRoleScrPK(lAppid, screenid, lRoleid);
                        recordscr = roleScrRepo.findById(idscr);
                        if (!recordscr.isPresent()
                                && !Utils.isNullOrEmpty(screenid)) {
                            createRoleScreen(lAppid, screenid, lRoleid, luserId);
                        }
                    }
                }
                if (mRequest.has(ServerConstants.CONTROL_IDS)) {
                    JSONArray controlarray = mRequest.getJSONArray(ServerConstants.CONTROL_IDS);
                    JSONObject controlidobj = null;
                    String controlid = "";
                    Optional<TbAsmiRoleControls> recordcontrol;
                    TbAsmiRoleControls recordcontrollist = null;
                    TbAsmiRoleControlsPK idcontrol = null;

                    List<TbAsmiRoleControls> controllist = roleControlsRepo.findControlsForRoleIdAndAppId(lRoleid,
                            lAppid);

                    for (int j = 0; j < controllist.size(); j++) {
                        recordcontrollist = (TbAsmiRoleControls) controllist.get(j);
                        if (controlarray.length() == 0) {
                            this.deleteControl(recordcontrollist);
                        } else {
                            if (controlarray.length() == 1) {
                                this.deleteControl(recordcontrollist);
                            } else {
                                for (int i = 0; i < controlarray.length(); i++) {
                                    controlidobj = (JSONObject) controlarray.get(i);

                                    controlid = controlidobj.get(ServerConstants.MESSAGE_CONTROL_ID).toString();
                                    idcontrol = new TbAsmiRoleControlsPK(lAppid, controlid, lRoleid);
                                    recordcontrol = roleControlsRepo.findById(idcontrol);

                                    if (recordcontrol.isPresent()
                                            && !(recordcontrol.get().getId().getControlId()
                                            .equals(recordcontrollist.getId().getControlId()))) {
                                        this.deleteControl(recordcontrollist);
                                        break; // added loop break not to check again for the same record
                                    }
                                }
                            }
                        }
                    }
                    for (int i = 0; i < controlarray.length(); i++) {
                        controlidobj = (JSONObject) controlarray.get(i);
                        controlid = controlidobj.get(ServerConstants.MESSAGE_CONTROL_ID).toString();
                        idcontrol = new TbAsmiRoleControlsPK(lAppid, controlid, lRoleid);
                        recordcontrol = roleControlsRepo.findById(idcontrol);
                        if (!recordcontrol.isPresent()
                                && !Utils.isNullOrEmpty(controlid)) {
                            createRoleControl(lAppid, controlid, lRoleid, luserId);
                        }
                    }
                }
                mResponse = new JSONObject();
                JSONObject statusobj = new JSONObject();
                statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
                mResponse.put(ServerConstants.UPDATE_ROLEMASTER_RESPONSE, statusobj);

            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
                dexp.setCode(DomainException.Code.APZ_DM_010.toString());
                dexp.setPriority("1");
                LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record Does not exists", dexp);
                throw dexp;
            }
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");

            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(mResponse);
    }

    public void deleteRoleRequest(Message pMessage) {
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();
        JSONObject mResponse = null;
        try {
            if (lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST) instanceof JSONArray) {
                JSONArray arr = (JSONArray) lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST);
                for (int i = 0; i < arr.length(); i++) {
                    this.deleteRole((JSONObject) arr.get(i));
                }
            } else if (lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST) instanceof JSONObject) {
                JSONObject json = (JSONObject) lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST);
                this.deleteRole(json);
            }
            mResponse = new JSONObject();
            JSONObject statusobj = new JSONObject();
            statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            mResponse.put(ServerConstants.DELETE_ROLEMASTER_RESPONSE, statusobj);
        } catch (JSONException jsone) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(mResponse);
    }

    private String deleteRole(JSONObject pjson) {
        LOG.debug("{} inside delete role", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String status = "";
        try {
            TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(pjson.getString(ServerConstants.ROLEID),
                    pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            TbAsmiRoleMaster roleMaster;
            Optional<TbAsmiRoleMaster> recordOpt = roleMasterRepo.findById(id);
            if (recordOpt.isPresent()) {
                roleMaster = recordOpt.get();
                // insert data into history table starts
                TbAshsRoleMasterPK masterPK = new TbAshsRoleMasterPK();
                masterPK.setAppId(roleMaster.getTbAsmiRoleMasterPK().getAppId());
                masterPK.setRoleId(roleMaster.getTbAsmiRoleMasterPK().getRoleId());
                masterPK.setVersionNo(roleMaster.getVersionNo());

                TbAshsRoleMaster ashsRoleMaster = new TbAshsRoleMaster(masterPK);
                ashsRoleMaster.setAuthStat(roleMaster.getAuthStat());
                ashsRoleMaster.setCheckerId(roleMaster.getCheckerId());
                ashsRoleMaster.setCheckerTs(roleMaster.getCheckerTs());
                ashsRoleMaster.setCreateUserId(roleMaster.getCreateUserId());
                ashsRoleMaster.setMakerId(roleMaster.getMakerId());
                ashsRoleMaster.setMakerTs(roleMaster.getMakerTs());
                ashsRoleMaster.setRoleDesc(roleMaster.getRoleDesc());
                ashsRoleMaster.setCreateTs(roleMaster.getCreateTs());

                roleMasterHistoryRepo.save(ashsRoleMaster);

                // insert data into history table ends
                roleMasterRepo.delete(roleMaster);
                List<TbAsmiRoleScr> reslist = roleScrRepo.findScreensByAppIdRoleId(
                        pjson.getString(ServerConstants.ROLEID),
                        pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                if (!reslist.isEmpty()) {
                    for (int i = 0; i < reslist.size(); i++) {
                        TbAsmiRoleScr tbscrmasterobj = reslist.get(i);
                        if (tbscrmasterobj != null) {
                            // insert data into history table starts
                            TbAshsRoleScrPK ashsRoleScrPK = new TbAshsRoleScrPK();
                            ashsRoleScrPK.setAppId(tbscrmasterobj.getTbAsmiRoleScrPK().getAppId());
                            ashsRoleScrPK.setRoleId(tbscrmasterobj.getTbAsmiRoleScrPK().getRoleId());
                            ashsRoleScrPK.setScreenId(tbscrmasterobj.getTbAsmiRoleScrPK().getScreenId());
                            ashsRoleScrPK.setVersionNo(tbscrmasterobj.getVersionNo());

                            TbAshsRoleScr ashsRoleScr = new TbAshsRoleScr(ashsRoleScrPK);
                            ashsRoleScr.setCreateTs(tbscrmasterobj.getCreateTs());
                            ashsRoleScr.setCreateUserId(tbscrmasterobj.getCreateUserId());

                            roleScrHistoryRepo.save(ashsRoleScr);
                            // insert data into history table ends
                            roleScrRepo.delete(tbscrmasterobj);
                        }
                    }
                }

                List<TbAsmiRoleIntf> reslist1 = roleIntfRepo.findIntfByAppIdRoleId(
                        pjson.getString(ServerConstants.ROLEID),
                        pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                if (!reslist1.isEmpty()) {
                    for (int i = 0; i < reslist1.size(); i++) {
                        TbAsmiRoleIntf recordintf = (TbAsmiRoleIntf) reslist1.get(i);
                        if (recordintf != null) {

                            TbAshsRoleIntfPK ashsRoleIntfPK = new TbAshsRoleIntfPK();
                            ashsRoleIntfPK.setAppId(recordintf.getTbAsmiRoleIntfPK().getAppId());
                            ashsRoleIntfPK.setInterfaceId(recordintf.getTbAsmiRoleIntfPK().getInterfaceId());
                            ashsRoleIntfPK.setRoleId(recordintf.getTbAsmiRoleIntfPK().getRoleId());
                            ashsRoleIntfPK.setVersionNo(recordintf.getVersionNo());

                            TbAshsRoleIntf ashsRoleIntf = new TbAshsRoleIntf(ashsRoleIntfPK);
                            ashsRoleIntf.setCreateTs(recordintf.getCreateTs());
                            ashsRoleIntf.setCreateUserId(recordintf.getCreateUserId());

                            roleIntfHistoryRepo.save(ashsRoleIntf);

                            // insert data into history table ends
                            roleIntfRepo.delete(recordintf);
                        }
                    }
                }
            } else {
                LOG.debug("{} No record exists ", ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
                dexp.setCode(DomainException.Code.APZ_DM_010.toString());
                dexp.setPriority("1");
                throw dexp;
            }

            status = ServerConstants.SUCCESS;
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return status;
    }

    public void getRoleMaster(Message pMessage) {
        LOG.debug("{} getRoleMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject bodyobj = new JSONObject();
        try {
            JSONArray recarray = this.searchRoleMaster(pMessage);
            bodyobj.put(ServerConstants.GET_ROLEMASTER_RESPONSE, recarray);
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(bodyobj);
    }

    private JSONArray searchRoleMaster(Message pMessage) throws DomainException {
        LOG.debug("{} searchRoleMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lrequestJson = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.GET_ROLEMASTER_REQUEST);
        final String appId;
        final String roleDesc;
        final String roleId;
        List<TbAsmiRoleMaster> recordslist;

        if (Utils.isNullOrEmpty(lrequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
            appId = ServerConstants.PERCENT;
        } else {
            appId = lrequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        }

        if (Utils.isNullOrEmpty(lrequestJson.getString(ServerConstants.ROLEDESCRIPTION))) {
            roleDesc = "null";
        } else {
            roleDesc = lrequestJson.getString(ServerConstants.ROLEDESCRIPTION);
        }
        if (Utils.isNullOrEmpty(lrequestJson.getString(ServerConstants.ROLEID))) {
            roleId = ServerConstants.PERCENT;
        } else {
            roleId = lrequestJson.getString(ServerConstants.ROLEID);
        }

        if (!roleDesc.contains("null")) {
            recordslist = roleMasterRepo.findAll(Specification.where(RoleMasterSpecification.likeAppId(appId))
                    .and(RoleMasterSpecification.likeRoleId(roleId))
                    .and(RoleMasterSpecification.likeRoleDesc(roleDesc)));
        } else {
            recordslist = roleMasterRepo.findAll(Specification.where(RoleMasterSpecification.likeAppId(appId))
                    .and(RoleMasterSpecification.likeRoleId(roleId)));
        }

        JSONArray recarray = new JSONArray();
        JSONObject recobj;
        TbAsmiRoleMaster obj = null;
        if (recordslist.isEmpty()) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record found", dexp);
            throw dexp;
        }
        try {
            for (int a = 0; a < recordslist.size(); a++) {
                recobj = new JSONObject();
                obj = (TbAsmiRoleMaster) recordslist.get(a);
                recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, obj.getTbAsmiRoleMasterPK().getAppId());
                recobj.put(ServerConstants.ROLEID, obj.getTbAsmiRoleMasterPK().getRoleId());
                recobj.put(ServerConstants.ROLEDESCRIPTION, obj.getRoleDesc());
                recarray.put(recobj);
            }
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return recarray;
    }

    public void getScreensIntfByAppID(Message pMessage) {
        LOG.debug("{} appzillonGetScreensIntfByAppID()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject bodyobj = new JSONObject();
        try {
            JSONObject ljson = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject("appzillonGetScreensIntfByAppIDRequest");
            JSONArray recarrayscreens = this.getScreensByAppid(ljson);
            JSONArray recarrayintf = this.getInterfacesByAppid(ljson);

            JSONObject scrobj = new JSONObject();

            scrobj.put(ServerConstants.INTERFACES, recarrayintf);
            scrobj.put(ServerConstants.SCREENS, recarrayscreens);

            bodyobj.put("appzillonGetScreensIntfByAppIDResponse", scrobj);

        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(bodyobj);
    }

    private JSONArray getScreensByAppid(JSONObject pJson) {
        LOG.debug("{} inside getrecordsBasedonmultiplecolsAppid", ServerConstants.LOGGER_PREFIX_DOMAIN);
        final String fappId;
        if (Utils.isNullOrEmpty(pJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
            fappId = ServerConstants.PERCENT;
        } else {
            fappId = pJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        }

        List<TbAsmiScrMaster> recordslist = screenMasterRepo.findAll(ScreenSpecification.likeAppId(fappId));
        JSONArray recarray = new JSONArray();
        JSONObject recobj;
        TbAsmiScrMaster obj = null;
        if (recordslist.isEmpty()) {
            LOG.warn("{} No record found in screen master for appid : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    pJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        }
        try {
            for (int a = 0; a < recordslist.size(); a++) {
                recobj = new JSONObject();
                obj = (TbAsmiScrMaster) recordslist.get(a);
                recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, obj.getTbAsmiScrMasterPK().getAppId());
                recobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, obj.getTbAsmiScrMasterPK().getScreenId());
                recobj.put("screenDescrption", obj.getScreenDesc());
                recobj.put("selected", ServerConstants.NO);

                recarray.put(recobj);
            }
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return recarray;

    }

    private JSONArray getInterfacesByAppid(JSONObject pjson) {
        LOG.debug("{} getResultBasedOncol dao", ServerConstants.LOGGER_PREFIX_DOMAIN);
        final String fappId;

        if (Utils.isNullOrEmpty(pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
            fappId = ServerConstants.PERCENT;
        } else {
            fappId = pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        }

        List<TbAsmiIntfMaster> recordslist = interfaceMasterRepo
                .findAll(InterfaceMasterSpecification.likeAppId(fappId));
        JSONArray recarray = new JSONArray();
        JSONObject recobj;
        TbAsmiIntfMaster obj = null;
        if (recordslist.isEmpty()) {
            LOG.warn("{} No record found in interface master for appid: {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        }
        try {
            for (int a = 0; a < recordslist.size(); a++) {
                recobj = new JSONObject();
                obj = (TbAsmiIntfMaster) recordslist.get(a);
                recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, obj.getTbAsmiIntfMasterPK().getAppId());
                recobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, obj.getTbAsmiIntfMasterPK().getInterfaceId());
                recobj.put(ServerConstants.CATEGORY, obj.getCategory());
                recobj.put(ServerConstants.TYPE, obj.getType());

                recarray.put(recobj);
            }
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return recarray;

    }

    public void getIntfScrByAppIDRoleID(Message pMessage) {
        LOG.debug("{}  getScrFromScrMasterAndRoleScr", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject bodyobj = new JSONObject();
        try {
            JSONObject ljson = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject("appzillonGetIntfScrByAppIDRoleIDRequest");

            String appid = ljson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String roleid = ljson.getString(ServerConstants.ROLEID);
            JSONArray scrarray = this.getScreensForAppIDAndRole(appid, roleid);
            JSONArray intfarray = this.getIntfsForAppIDAndRole(appid, roleid);
            JSONObject scrobj = new JSONObject();

            scrobj.put(ServerConstants.SCREENS, scrarray);
            scrobj.put(ServerConstants.INTERFACES, intfarray);

            bodyobj.put("appzillonGetIntfScrByAppIDRoleIDResponse", scrobj);

        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(bodyobj);

    }

    private JSONArray getScreensForAppIDAndRole(String appid, String roleid) {
        List<TbAsmiRoleScr> result = roleScrRepo.findScreensByAppIdRoleId(roleid, appid);
        String intfDesc = "";
        JSONObject intfjsonobj = null;
        JSONArray intfarray = new JSONArray();

        if (!result.isEmpty()) {
            Iterator<TbAsmiRoleScr> iterate = result.iterator();
            TbAsmiRoleScr rec = null;

            String intidstring = "";

            while (iterate.hasNext()) {

                rec = (TbAsmiRoleScr) iterate.next();
                intidstring = rec.getTbAsmiRoleScrPK().getScreenId();
                intfDesc = this.getScrDescFromScrMaster(appid, intidstring);
                intfjsonobj = new JSONObject();

                intfjsonobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, intidstring);
                intfjsonobj.put("screenDesc", intfDesc);
                intfarray.put(intfjsonobj);
            }
        }

        return intfarray;
    }

    private JSONArray getIntfsForAppIDAndRole(String appid, String roleid) {
        List<TbAsmiRoleIntf> result = roleIntfRepo.findIntfByAppIdRoleId(roleid, appid);
        String intfDesc = "";
        JSONObject intfjsonobj = null;
        JSONArray intfarray = new JSONArray();

        if (!result.isEmpty()) {
            Iterator<TbAsmiRoleIntf> iterate = result.iterator();
            TbAsmiRoleIntf rec = null;

            String intidstring = "";

            while (iterate.hasNext()) {

                rec = (TbAsmiRoleIntf) iterate.next();
                intidstring = rec.getTbAsmiRoleIntfPK().getInterfaceId();
                intfDesc = this.getIntfDescFromIntfMaster(appid, intidstring);
                intfjsonobj = new JSONObject();

                intfjsonobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, intidstring);
                intfjsonobj.put("interfaceDesc", intfDesc);
                intfarray.put(intfjsonobj);
            }
        }

        return intfarray;
    }

    private String getScrDescFromScrMaster(String appid, String screenId) {
        TbAsmiScrMaster obj = screenMasterRepo.findscreenbyappidscrid(appid, screenId);
        if (obj != null) {
            return obj.getScreenDesc();
        }
        return "No Description";
    }

    private String getIntfDescFromIntfMaster(String appid, String pIntfPK) {
        TbAsmiIntfMaster obj = interfaceMasterRepo.findinterfacebyappidinterfaceid(appid, pIntfPK);
        if (obj != null) {
            return obj.getDescription();
        }
        return "No description";
    }

    private void deleteIntf(TbAsmiRoleIntf recordintflist) {
        // insert data into history table starts

        TbAshsRoleIntfPK ashsRoleIntfPK = new TbAshsRoleIntfPK();
        ashsRoleIntfPK.setAppId(recordintflist.getTbAsmiRoleIntfPK().getAppId());
        ashsRoleIntfPK.setInterfaceId(recordintflist.getTbAsmiRoleIntfPK().getInterfaceId());
        ashsRoleIntfPK.setRoleId(recordintflist.getTbAsmiRoleIntfPK().getRoleId());
        ashsRoleIntfPK.setVersionNo(recordintflist.getVersionNo());

        TbAshsRoleIntf ashsRoleIntf = new TbAshsRoleIntf(ashsRoleIntfPK);
        ashsRoleIntf.setCreateTs(recordintflist.getCreateTs());
        ashsRoleIntf.setCreateUserId(recordintflist.getCreateUserId());

        roleIntfHistoryRepo.save(ashsRoleIntf);

        // insert data into history table ends
        roleIntfRepo.delete(recordintflist);
    }

    private void deleteScr(TbAsmiRoleScr recordscrlist) {
        // insert data into history table starts
        TbAshsRoleScrPK ashsRoleScrPK = new TbAshsRoleScrPK();
        ashsRoleScrPK.setAppId(recordscrlist.getTbAsmiRoleScrPK().getAppId());
        ashsRoleScrPK.setRoleId(recordscrlist.getTbAsmiRoleScrPK().getRoleId());
        ashsRoleScrPK.setScreenId(recordscrlist.getTbAsmiRoleScrPK().getScreenId());
        ashsRoleScrPK.setVersionNo(recordscrlist.getVersionNo());

        TbAshsRoleScr ashsRoleScr = new TbAshsRoleScr(ashsRoleScrPK);
        ashsRoleScr.setCreateTs(recordscrlist.getCreateTs());
        ashsRoleScr.setCreateUserId(recordscrlist.getCreateUserId());

        roleScrHistoryRepo.save(ashsRoleScr);
        // insert data into history table ends
        roleScrRepo.delete(recordscrlist);
    }

    private void deleteControl(TbAsmiRoleControls recordControlslist) {
        // insert data into history table starts
        TbAshsRoleControlsPK ashsRoleControlsPK = new TbAshsRoleControlsPK();
        ashsRoleControlsPK.setAppId(recordControlslist.getId().getAppId());
        ashsRoleControlsPK.setRoleId(recordControlslist.getId().getRoleId());
        ashsRoleControlsPK.setControlId(recordControlslist.getId().getControlId());
        ashsRoleControlsPK.setVersionNo(recordControlslist.getVersionNo());

        TbAshsRoleControls ashsRoleControls = new TbAshsRoleControls(ashsRoleControlsPK);
        ashsRoleControls.setCreatedTs(recordControlslist.getCreatedTs());
        ashsRoleControls.setCreatedBy(recordControlslist.getCreatedBy());

        roleControlHistoryRepo.save(ashsRoleControls);
        // insert data into history table ends
        roleControlsRepo.delete(recordControlslist);
    }

}
