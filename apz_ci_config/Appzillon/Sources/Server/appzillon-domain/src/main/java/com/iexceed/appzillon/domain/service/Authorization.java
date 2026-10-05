package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsmiRoleMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.*;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static com.iexceed.appzillon.domain.utils.Constants.*;

/**
 * @author Vinod Rawat
 */
@Named("authorizationService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class Authorization {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            Authorization.class.getName());

    @Inject
    TbAsmiUserRoleRepository cAsmiUserRoleRepository;
    @Inject
    TbAsmiUserRepository cAsmiUserDetRepository;
    @Inject
    TbAsmiRoleIntfRepository cAsmiRoleIntfRepository;
    @Inject
    TbAsmiRoleScrRepository cAsmiRoleScrRepository;
    @Inject
    TbAsmiRoleControlsRepository cAsmiRolControlsRepository;
    @Inject
    TbAsmiUserAppAccessRepository cAsmiUserAppAccessRepository;
    @Inject
    TbAsmiRoleMasterRespository cAsmiRoleMasterRepo;
    @Inject
    TbAsmiScrMasterRepository cAsmiScrMasterRepo;
    @Inject
    TbAsmiControlsMasterRepository cAsmiControlMasterRepo;


    private List<TbAsmiUserRole> finduserrolebyuserIdandappId(String pUserId, String pAppId) {
        return cAsmiUserRoleRepository.findRolesByAppIdUserId(pAppId, pUserId);
    }

    public void getUserRoles(Message pMessage) {
        LOG.info("inside getUserRoles");
        JSONObject response = null;
        try {
            JSONObject lUserRequest = pMessage.getRequestObject().getRequestJson();
            String lRoles = "";
            List<TbAsmiUserRole> lTbAsmiUserRoles = finduserrolebyuserIdandappId(
                    lUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                    lUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            if (!lTbAsmiUserRoles.isEmpty()) {
                Iterator<TbAsmiUserRole> iterate = lTbAsmiUserRoles.iterator();
                TbAsmiUserRole rec = null;
                String[] roleid = new String[lTbAsmiUserRoles.size()];
                int i = 0;
                while (iterate.hasNext()) {
                    rec = iterate.next();
                    roleid[i] = rec.getTbAsmiUserRolePK().getRoleId();
                    if (i == 0) {
                        lRoles = rec.getTbAsmiUserRolePK().getRoleId();
                    } else {
                        lRoles = lRoles.concat(ServerConstants.AMD);
                        lRoles = lRoles.concat(rec.getTbAsmiUserRolePK().getRoleId());
                    }
                    i++;
                }
                response = new JSONObject();
                response.put(ServerConstants.GET_USER_ROLES, response);
            }
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
        pMessage.getResponseObject().setResponseJson(response);
    }

    public void getRolesByUserId(Message pMessage) {
        LOG.debug("{} inside getRolesByUserId", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject response = null;
        try {
            String lUserId = pMessage.getHeader().getUserId();
            if (lUserId.contains("~")) {
                String[] lUserIdTilda = Utils.split(lUserId, "~");
                String lUserIdTimmed = lUserIdTilda[0].trim();
                LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "getRolesByUserId lUserId after removing tilda-:" + lUserIdTimmed);
                this.getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserIdTimmed);
            } else {
                this.getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserId);
            }
            List<TbAsmiUserRole> result = cAsmiUserRoleRepository.findRolesByAppIdUserId(pMessage.getHeader().getAppId(), lUserId);
            LOG.debug("{} No Of Roles : {}, for userId : {} and appId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    result.size(), lUserId, pMessage.getHeader().getAppId());
            JSONArray listOfRole = null;
            if (!result.isEmpty()) {
                listOfRole = new JSONArray();
                for (TbAsmiUserRole tbAsmiUserRole : result) {
                    listOfRole.put(tbAsmiUserRole.getTbAsmiUserRolePK().getRoleId());
                }
                LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Roles Exist For the User : " + listOfRole);
            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_018));
                dexp.setCode(DomainException.Code.APZ_DM_018.toString());
                dexp.setPriority("1");
                LOG.error(NO_ROLE_EXISTS, ServerConstants.LOGGER_PREFIX_DOMAIN, lUserId);
                throw dexp;
            }
            response = new JSONObject();
            response.put(ServerConstants.GET_USER_ROLES, listOfRole);
        } catch (JSONException ex) {
            LOG.error("{} {} ", ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
        pMessage.getResponseObject().setResponseJson(response);
    }

    private String getUserByAppIdMatchWithReqUserId(String pappid, String puserid) {
        LOG.debug("{} inside getUserByAppIdMatchWithReqUserId(), AppID : {}, UserId : {}",
                ServerConstants.LOGGER_PREFIX_DOMAIN, pappid, puserid);
        String userResp = "";
        TbAsmiUser result = cAsmiUserDetRepository.findUsersByAppIdUserId(puserid, pappid);
        if (result == null) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_012));
            dexp.setCode(DomainException.Code.APZ_DM_012.toString());
            dexp.setPriority("1");
            LOG.error("{} This user does not exist so no role exists for this user",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
            throw dexp;
        } else {
            if (ServerConstants.YES.equalsIgnoreCase(result.getUserActive())) {
                LOG.debug("{} user exists in tbasmi and is active", ServerConstants.LOGGER_PREFIX_DOMAIN);
                userResp = ServerConstants.EXISTS;
            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_054));
                dexp.setCode(DomainException.Code.APZ_DM_054.toString());
                dexp.setPriority("1");
                LOG.error("{} User is Inactive", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
            return userResp;
        }
    }


    public void isExistInterfaceIdforRoleId(Message pMessage) {
        JSONObject response = null;
        try {
            JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson();
            LOG.debug("{} inside isExistInterfaceIdforRoleId()", ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONArray roleArray = pUserRequest.getJSONArray(ServerConstants.ROLEINTERFACEAPP);


            response = new JSONObject();
            List<String> lRoleslist = new ArrayList<>();
            for (int s = 0; s < roleArray.length(); s++) {
                lRoleslist.add(roleArray.getString(s));
            }
            String[] interfaceids = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIds(lRoleslist,
                    pMessage.getHeader().getAppId());

            JSONArray resArray = new JSONArray();
            if (interfaceids != null) {
                for (int i = 0; i < interfaceids.length; i++) {
                    resArray.put(interfaceids[i]);
                }
            } else {
                resArray.put("");
            }
            response.put(ServerConstants.APPZISEXISTSINTFID, resArray);
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }

        pMessage.getResponseObject().setResponseJson(response);
    }

    public void getScreenByFunId(Message pMessage) {
        LOG.debug("{} inside getScreenByFunId()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject response = null;
        try {
            JSONObject rolerrscreenapp = pMessage.getRequestObject().getRequestJson();

            JSONArray rolesArray = rolerrscreenapp.getJSONArray(ServerConstants.ROLESCREENAPP);


            response = new JSONObject();

            List<String> lRoleslist = new ArrayList<>();
            for (int s = 0; s < rolesArray.length(); s++) {
                lRoleslist.add(rolesArray.getString(s));
            }
            String[] screenids = cAsmiRoleScrRepository.findScreenIdsForRoleIds(lRoleslist,
                    pMessage.getHeader().getAppId());
            JSONArray resArray = new JSONArray();
            if (screenids != null) {
                for (int i = 0; i < screenids.length; i++) {
                    resArray.put(screenids[i]);
                }
            } else {
                resArray.put("");
            }
            response.put(ServerConstants.APPZSMSGETSCREENBYFUNID, resArray);
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }

        pMessage.getResponseObject().setResponseJson(response);
    }

    /**
     * Below Method is added by Abhishek on 4-03-2015 to check if default
     * authorization is enabled and InterfaceId Exist for RoleID
     *
     * @param pMessage
     */
    public void checkDefaultAuth(Message pMessage) {
        LOG.debug("{} inside checkDefaultAuth", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean status = false;
        JSONObject response = null;
        if (pMessage.getSecurityParams().getDefaultAuthorization().equals(ServerConstants.YES)) {
            String rolestring = this.getUserRolesString(pMessage);
            String[] roles = this.formRolesArrayFromRolesString(rolestring);
            String lRolerr = "";
            lRolerr = roles[0];
            for (int r = 1; r < roles.length; r++) {
                lRolerr = lRolerr.concat(ServerConstants.AMD + roles[r]);
            }
            lRolerr = lRolerr.concat(ServerConstants.AMD + pMessage.getHeader().getInterfaceId());
            lRolerr = lRolerr.concat(ServerConstants.AMD + pMessage.getHeader().getAppId());
            JSONObject interfaceobj = new JSONObject();
            interfaceobj.put(ServerConstants.ROLEINTERFACEAPP, lRolerr);
            pMessage.getResponseObject().setResponseJson(interfaceobj);
            this.isExistInterfaceIdforRoleId(pMessage);
            String lInterfaceexists = pMessage.getResponseObject().getResponseJson()
                    .getString("isExistInterfaceIdforRoleId");
            if (lInterfaceexists.equals("YES")) {
                status = true;
            }
        } else {
            status = true;
        }
        if (status) {
            response = new JSONObject();
            response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        } else {
            response = new JSONObject();
            response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.FAILURE);
        }
        pMessage.getResponseObject().setResponseJson(response);
    }

    /**
     * Below Method is added by Abhishek on 4-03-2015 to get roleString
     *
     * @param pMessage
     * @return
     */
    private String getUserRolesString(Message pMessage) {
        LOG.debug("{} Get UserRoles", ServerConstants.LOGGER_PREFIX_DOMAIN);
        this.getRolesByUserId(pMessage);
        return pMessage.getResponseObject().getResponseJson().getString("getUserRoles");
    }

    /**
     * Below Method is added by Abhishek on 4-03-2015 to get RoleString array
     *
     * @param pRolestring
     * @return
     */
    private String[] formRolesArrayFromRolesString(String pRolestring) {
        LOG.debug("{} Form Roles Array from RolesString", ServerConstants.LOGGER_PREFIX_DOMAIN);
        ArrayList<String> lRoleslist = new ArrayList<>();
        String lRolestr = "";
        String lRolestrend = "";
        String[] lRoles = null;
        if (!(pRolestring.contains(ServerConstants.AMD))) {
            lRoles = new String[1];
            lRoles[0] = pRolestring;
        } else {
            while (pRolestring.contains(ServerConstants.AMD)) {
                int ind = pRolestring.indexOf(ServerConstants.AMD);
                lRolestr = pRolestring.substring(0, ind);
                lRolestrend = pRolestring.substring(ind + 1, pRolestring.length());
                lRoleslist.add(lRolestr);

                pRolestring = lRolestrend;
            }
            lRoleslist.add(lRolestrend);
            lRoles = new String[lRoleslist.size()];
            for (int str = 0; str < lRoleslist.size(); str++) {

                lRoles[str] = lRoleslist.get(str);
            }
        }
        return lRoles;
    }


    public void authorizationService(Message pMessage) {
        LOG.debug("{} inside authorizationService()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lRequest;
        if (pMessage.getRequestObject().getRequestJson().has(ServerConstants.AUTHORIZATION_REQUEST)) {
            lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.AUTHORIZATION_REQUEST);
        } else {
            lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
        }
        if (!((JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE))
                .equalsIgnoreCase(ServerConstants.USER_PRIVS_NOT_REQUIRED)
                && (JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE))
                .equalsIgnoreCase(ServerConstants.USER_PRIVS_NOT_REQUIRED)
                && (JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE))
                .equalsIgnoreCase(ServerConstants.USER_PRIVS_NOT_REQUIRED))) {
            JSONObject jsonObj = fetchUserPrivs(pMessage, lRequest);
            if (pMessage.getRequestObject().getRequestJson().has(ServerConstants.AUTHORIZATION_REQUEST)) {
                pMessage.getResponseObject()
                        .setResponseJson(new JSONObject().put(ServerConstants.AUTHORIZATION_RESPONSE, jsonObj));
            } else {
                pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES)
                        .getJSONObject(ServerConstants.USERDET).put(ServerConstants.USER_PRIVS, jsonObj);
            }

        }
    }

    private JSONObject fetchUserPrivs(Message pMessage, JSONObject lRequest) {
        JSONObject jsonObject = new JSONObject();
        String lUserId = JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.MESSAGE_HEADER_USER_ID);
        String lifacesAccessType = JSONUtils.getJsonValueFromObject(lRequest,
                ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE);
        String lscrsAccessType = JSONUtils.getJsonValueFromObject(lRequest,
                ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE);
        String lcontrolsAccessType = JSONUtils.getJsonValueFromObject(lRequest,
                ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE);
        List<String> lRoleList = cAsmiUserRoleRepository.findRoleListByAppIdUserId(pMessage.getHeader().getAppId(),
                lUserId);
        LOG.debug("{} List Of Roles : {} assigned to the user : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lRoleList,
                lUserId);
        if (lRoleList != null && !lRoleList.isEmpty()) {
            Map<String, List<String>> lIntfScrnCntrlsRolesMp = getAllowedDeniedIntfScrnCntrlsRoles(pMessage, lUserId,
                    lRoleList);
            List<String> lInterfaceList = getInterfaceList(pMessage, jsonObject, lifacesAccessType, lIntfScrnCntrlsRolesMp);
            List<String> lScreenList = getScreenList(pMessage, jsonObject, lscrsAccessType, lIntfScrnCntrlsRolesMp);
            List<String> lControlList = getControlList(pMessage, jsonObject, lcontrolsAccessType, lIntfScrnCntrlsRolesMp);

            jsonObject.put(ServerConstants.APPZILLON_ROOT_ROLES, lRoleList);
            jsonObject.put(ServerConstants.USER_PRIVS_SCREENS, lScreenList);
            jsonObject.put(ServerConstants.USER_PRIVS_INTERFACES, lInterfaceList);
            jsonObject.put(ServerConstants.CONTROLS, lControlList);

        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_018) + lUserId);
            dexp.setCode(DomainException.Code.APZ_DM_018.toString());
            dexp.setPriority("1");
            LOG.error(NO_ROLE_EXISTS, ServerConstants.LOGGER_PREFIX_DOMAIN, lUserId);
            throw dexp;
        }
        return jsonObject;
    }

    private List<String> getControlList(Message pMessage, JSONObject jsonObject, String lcontrolsAccessType, Map<String, List<String>> lIntfScrnCntrlsRolesMp) {
        List<String> lControlList = null;
        if (lcontrolsAccessType != null && lcontrolsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_DENIED)) {
            lControlList = getDeniedControlsList(pMessage,
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_CONTROL_ROLE),
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_CONTROL_ROLE));
            jsonObject.put(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE,
                    ServerConstants.USER_PRIVS_ACCESS_DENIED);
        } else if (lcontrolsAccessType != null && lcontrolsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_ALLOWED)) {
            lControlList = getAllowedControlsList(pMessage,
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_CONTROL_ROLE),
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_CONTROL_ROLE));
            jsonObject.put(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE,
                    ServerConstants.USER_PRIVS_ACCESS_ALLOWED);
        }
        return lControlList;
    }

    private List<String> getScreenList(Message pMessage, JSONObject jsonObject, String lscrsAccessType, Map<String, List<String>> lIntfScrnCntrlsRolesMp) {
        List<String> lScreenList = null;
        if (lscrsAccessType != null && lscrsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_DENIED)) {
            lScreenList = getDeniedScreensList(pMessage,
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_SCREEN_ROLE),
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_SCREEN_ROLE));
            jsonObject.put(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE,
                    ServerConstants.USER_PRIVS_ACCESS_DENIED);
        } else if (lscrsAccessType != null && lscrsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_ALLOWED)) {
            lScreenList = getAllowedScreensList(pMessage,
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_SCREEN_ROLE),
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_SCREEN_ROLE));
            jsonObject.put(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE,
                    ServerConstants.USER_PRIVS_ACCESS_ALLOWED);
        }
        return lScreenList;
    }

    private List<String> getInterfaceList(Message pMessage, JSONObject jsonObject, String lifacesAccessType, Map<String, List<String>> lIntfScrnCntrlsRolesMp) {
        List<String> lInterfaceList = null;
        if (lifacesAccessType != null && lifacesAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_DENIED)) {
            lInterfaceList = getDeniedInterfacesList(pMessage,
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_INTFERFACE_ROLE),
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_INTERFACE_ROLE));
            jsonObject.put(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE,
                    ServerConstants.USER_PRIVS_ACCESS_DENIED);
        } else if (lifacesAccessType != null && lifacesAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_ALLOWED)) {
            lInterfaceList = getAllowedInterfacesList(pMessage,
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_INTFERFACE_ROLE),
                    lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_INTERFACE_ROLE));
            jsonObject.put(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE,
                    ServerConstants.USER_PRIVS_ACCESS_ALLOWED);
        }
        return lInterfaceList;
    }

    private List<String> getDeniedControlsList(Message pMessage, List<String> roleControlA, List<String> roleControlD) {
        List<String> lControlList = null;
        if ((roleControlD != null && !roleControlD.isEmpty()) && (roleControlA == null || roleControlA.isEmpty())) {
            LOG.debug("{} Going to fetch list of denied controlId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            lControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD,
                    pMessage.getHeader().getAppId());
            LOG.debug("{} List of Denied control id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lControlList.size());
        } else if ((roleControlA != null && !roleControlA.isEmpty())
                && (roleControlD == null || roleControlD.isEmpty())) {
            lControlList = getDeniedCtrlLstFrmRoleCtrlA(pMessage, roleControlA);
        } else if (roleControlA != null && !roleControlA.isEmpty()) {
            lControlList = getDeniedCtrlLstFrmBothRoleCtrl(pMessage, roleControlA, roleControlD);
        }
        return lControlList;
    }

    private List<String> getDeniedCtrlLstFrmBothRoleCtrl(Message pMessage, List<String> roleControlA, List<String> roleControlD) {
        List<String> lControlList;
        List<String> lAllowedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Allowed Control id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAllowedControlList.size());
        List<String> lDeniedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Denied Control id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedControlList.size());

        if (!lAllowedControlList.isEmpty()) {
            lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
            lControlList.removeAll(lAllowedControlList);
        } else if (!lDeniedControlList.isEmpty()) {
            lControlList = lDeniedControlList;
        } else {
            lControlList = new ArrayList<>();
        }
        return lControlList;
    }

    private List<String> getDeniedCtrlLstFrmRoleCtrlA(Message pMessage, List<String> roleControlA) {
        List<String> lControlList;
        LOG.debug("{} Going to fetch list of Authorized controlId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lAllowedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Authorized control id : {} ", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAllowedControlList.size());
        if (!lAllowedControlList.isEmpty()) {
            LOG.debug(
                    "{} control Id mapped under authorized role. Now Fetching Denied control from InterfaceMaster table minus(-) control mapped under authorized case",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            lControlList = cAsmiRolControlsRepository
                    .getListOfMasterControlMinusGivenControl(pMessage.getHeader().getAppId(), lAllowedControlList);
            LOG.debug("{} denied lControlList sized : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lControlList.size());
        } else {
            lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
        }
        return lControlList;
    }

    private List<String> getAllowedControlsList(Message pMessage, List<String> roleControlA,
                                                List<String> roleControlD) {
        List<String> lControlList = null;
        if ((roleControlA != null && !roleControlA.isEmpty()) && (roleControlD == null || roleControlD.isEmpty())) {
            LOG.debug("{} Going to fetch list of authorized controlId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            lControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA,
                    pMessage.getHeader().getAppId());
            LOG.debug("{} List of Authorized control id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lControlList.size());
        } else if ((roleControlD != null && !roleControlD.isEmpty())
                && (roleControlA == null || roleControlA.isEmpty())) {
            lControlList = getAllowedCtrlLstFrmRoleCtrlD(pMessage, roleControlD);
        } else if (roleControlA != null && !roleControlA.isEmpty()) {
            lControlList = getAllowedCtrlLstFrmBothRoleCtrl(pMessage, roleControlA, roleControlD);
        }
        return lControlList;
    }

    private List<String> getAllowedCtrlLstFrmBothRoleCtrl(Message pMessage, List<String> roleControlA, List<String> roleControlD) {
        List<String> lControlList;
        List<String> lAllowedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Allowed Control id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAllowedControlList.size());
        List<String> lDeniedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Denied Control id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedControlList.size());

        if (!lDeniedControlList.isEmpty()) {
            lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
            lControlList.removeAll(lDeniedControlList);
        } else if (!lAllowedControlList.isEmpty()) {
            lControlList = lAllowedControlList;
        } else {
            lControlList = new ArrayList<>();
        }
        return lControlList;
    }

    private List<String> getAllowedCtrlLstFrmRoleCtrlD(Message pMessage, List<String> roleControlD) {
        List<String> lControlList;
        LOG.debug("{} Going to fetch list of Denied controlId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lDeniedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Denied control id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedControlList.size());
        if (!lDeniedControlList.isEmpty()) {
            LOG.debug(
                    "{} control Id mapped under denied role. Now Fetching Authorized control from InterfaceMaster table minus(-) control mapped under denied case",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            lControlList = cAsmiRolControlsRepository
                    .getListOfMasterControlMinusGivenControl(pMessage.getHeader().getAppId(), lDeniedControlList);
            LOG.debug("{} lControlList  : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lControlList.size());
        } else {
            lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
        }
        return lControlList;
    }

    private List<String> getDeniedScreensList(Message pMessage, List<String> roleScreenA, List<String> roleScreenD) {
        List<String> lScreenList = null;
        if ((roleScreenD != null && !roleScreenD.isEmpty()) && (roleScreenA == null || roleScreenA.isEmpty())) {
            LOG.debug("{} Going to fetch list of denied screenId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            lScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD, pMessage.getHeader().getAppId());
            LOG.debug("{} List of Denied screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lScreenList.size());
        } else if ((roleScreenA != null && !roleScreenA.isEmpty()) && (roleScreenD == null || roleScreenD.isEmpty())) {
            lScreenList = getDeniedScrnLstFrmRoleScrnA(pMessage, roleScreenA);
        } else if (roleScreenA != null && !roleScreenA.isEmpty()) {
            lScreenList = getDeniedScrnLstFrmBothRoleScrn(pMessage, roleScreenA, roleScreenD);
        }
        return lScreenList;
    }

    private List<String> getDeniedScrnLstFrmBothRoleScrn(Message pMessage, List<String> roleScreenA, List<String> roleScreenD) {
        List<String> lScreenList;
        List<String> lAllowedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Allowed Screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAllowedScreenList.size());
        List<String> lDeniedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Denied Screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedScreenList.size());

        if (!lAllowedScreenList.isEmpty()) {
            lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
            lScreenList.removeAll(lAllowedScreenList);
        } else if (!lDeniedScreenList.isEmpty()) {
            lScreenList = lDeniedScreenList;
        } else {
            lScreenList = new ArrayList<>();
        }
        return lScreenList;
    }

    private List<String> getDeniedScrnLstFrmRoleScrnA(Message pMessage, List<String> roleScreenA) {
        List<String> lScreenList;
        LOG.debug("{} Going to fetch list of Authorized screenId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lAuthorizedIntrfaceList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Authorized screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAuthorizedIntrfaceList.size());
        if (!lAuthorizedIntrfaceList.isEmpty()) {
            LOG.debug(
                    "{} screen Id mapped under authorized role. Now Fetching Denied screen from InterfaceMaster table minus(-) screen mapped under authorized case",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            lScreenList = cAsmiRoleScrRepository.getListOfMasterScreensMinusGivenScreens(
                    pMessage.getHeader().getAppId(), lAuthorizedIntrfaceList);
            LOG.debug("{} denied lScreenList sized : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lScreenList.size());
        } else {
            lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
        }
        return lScreenList;
    }

    private List<String> getAllowedScreensList(Message pMessage, List<String> roleScreenA, List<String> roleScreenD) {
        List<String> lScreenList = null;
        if ((roleScreenA != null && !roleScreenA.isEmpty()) && (roleScreenD == null || roleScreenD.isEmpty())) {
            LOG.debug("{} Going to fetch list of authorized screenId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            lScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
            LOG.debug("{} List of Authorized screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lScreenList.size());
        } else if ((roleScreenD != null && !roleScreenD.isEmpty()) && (roleScreenA == null || roleScreenA.isEmpty())) {
            lScreenList = getAllowedScrnLstFrmRoleScrnD(pMessage, roleScreenD);
        } else if (roleScreenA != null && !roleScreenA.isEmpty()) {
            lScreenList = getAllowedScrnLstFrmBothRoleScrn(pMessage, roleScreenA, roleScreenD);
        }
        return lScreenList;
    }

    private List<String> getAllowedScrnLstFrmBothRoleScrn(Message pMessage, List<String> roleScreenA, List<String> roleScreenD) {
        List<String> lScreenList;
        List<String> lAllowedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Allowed Screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAllowedScreenList.size());
        List<String> lDeniedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Denied Screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedScreenList.size());

        if (!lDeniedScreenList.isEmpty()) {
            lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
            lScreenList.removeAll(lDeniedScreenList);
        } else if (!lAllowedScreenList.isEmpty()) {
            lScreenList = lAllowedScreenList;
        } else {
            lScreenList = new ArrayList<>();
        }
        return lScreenList;
    }

    private List<String> getAllowedScrnLstFrmRoleScrnD(Message pMessage, List<String> roleScreenD) {
        List<String> lScreenList;
        LOG.debug("{} Going to fetch list of Denied screenId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lDeniedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD,
                pMessage.getHeader().getAppId());
        LOG.debug("{} List of Denied screen id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedScreenList.size());
        if (!lDeniedScreenList.isEmpty()) {
            LOG.debug(
                    "{} screen Id mapped under denied role. Now Fetching Authorized screen from InterfaceMaster table minus(-) screen mapped under denied case",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            lScreenList = cAsmiRoleScrRepository
                    .getListOfMasterScreensMinusGivenScreens(pMessage.getHeader().getAppId(), lDeniedScreenList);
            LOG.debug("{} lScreenList  : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lScreenList.size());
        } else {
            lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
        }
        return lScreenList;
    }

    private List<String> getDeniedInterfacesList(Message pMessage, List<String> roleInterfaceA,
                                                 List<String> roleInterfaceD) {
        LOG.debug("{} Fetching Denied Interface List.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lInterfaceList = null;
        if ((roleInterfaceD != null && !roleInterfaceD.isEmpty())
                && (roleInterfaceA == null || roleInterfaceA.isEmpty())) {
            LOG.debug("{} Going to fetch list of denied interfaceId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceD,
                    pMessage.getHeader().getAppId());
            LOG.debug(LIST_DENIED_INTERFACE_ID, ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lInterfaceList.size());
        } else if ((roleInterfaceA != null && !roleInterfaceA.isEmpty())
                && (roleInterfaceD == null || roleInterfaceD.isEmpty())) {
            lInterfaceList = getDeniedIntfLstFrmRoleIntfA(pMessage, roleInterfaceA);
        } else if (roleInterfaceA != null && !roleInterfaceA.isEmpty()) {
            lInterfaceList = getDeniedIntfLstFrmBothRoleIntf(pMessage, roleInterfaceA, roleInterfaceD);
        }
        return lInterfaceList;
    }

    private List<String> getDeniedIntfLstFrmBothRoleIntf(Message pMessage, List<String> roleInterfaceA, List<String> roleInterfaceD) {
        List<String> lInterfaceList;
        List<String> lAllowedInterfaceList = cAsmiRoleIntfRepository
                .findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
        LOG.debug("{} List of Allowed inteface id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAllowedInterfaceList.size());
        List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository
                .findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
        LOG.debug(LIST_DENIED_INTERFACE_ID, ServerConstants.LOGGER_PREFIX_DOMAIN, lDeniedInterfaceList.size());

        if (!lAllowedInterfaceList.isEmpty()) {
            lInterfaceList = cAsmiRoleIntfRepository.
                    findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
            lInterfaceList.removeAll(lAllowedInterfaceList);
        } else if (!lDeniedInterfaceList.isEmpty()) {
            lInterfaceList = lDeniedInterfaceList;
        } else {
            lInterfaceList = new ArrayList<>();
        }
        return lInterfaceList;
    }

    private List<String> getDeniedIntfLstFrmRoleIntfA(Message pMessage, List<String> roleInterfaceA) {
        List<String> lInterfaceList;
        LOG.debug("{} Going to fetch list of Authorized interfaceId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lAuthorizedIntrfaceList = cAsmiRoleIntfRepository
                .findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
        LOG.debug("{} List of Authorized inteface id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAuthorizedIntrfaceList.size());
        if (!lAuthorizedIntrfaceList.isEmpty()) {
            LOG.debug(
                    "{} Interface Id mapped under authorized role. Now Fetching Denied Interface from InterfaceMaster table minus(-) Interface mapped under authorized case",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            lInterfaceList = cAsmiRoleIntfRepository.getListOfMasterInterfaceMinusGivenInterface(
                    pMessage.getHeader().getAppId(), lAuthorizedIntrfaceList);
            LOG.debug("{} denied lInterfaceList sized : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lInterfaceList.size());
        } else {
            lInterfaceList = cAsmiRoleIntfRepository
                    .findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
        }
        return lInterfaceList;
    }

    private List<String> getAllowedInterfacesList(Message pMessage, List<String> roleInterfaceA,
                                                  List<String> roleInterfaceD) {
        LOG.debug("{} Fetching Allowed Interface List.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lInterfaceList = null;
        if ((roleInterfaceA != null && !roleInterfaceA.isEmpty())
                && (roleInterfaceD == null || roleInterfaceD.isEmpty())) {
            LOG.debug("{} Going to fetch list of authorized interfaceId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA,
                    pMessage.getHeader().getAppId());
            LOG.debug("{} List of Authorized inteface id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lInterfaceList.size());
        } else if ((roleInterfaceD != null && !roleInterfaceD.isEmpty())
                && (roleInterfaceA == null || roleInterfaceA.isEmpty())) {
            lInterfaceList = getAllowedIntfLstFrmRoleIntfD(pMessage, roleInterfaceD);
        } else if ((roleInterfaceA != null && !roleInterfaceA.isEmpty())) {
            lInterfaceList = getAllowedIntfLstFrmBothRoleIntf(pMessage, roleInterfaceA, roleInterfaceD);
        }
        return lInterfaceList;
    }

    private List<String> getAllowedIntfLstFrmBothRoleIntf(Message pMessage, List<String> roleInterfaceA, List<String> roleInterfaceD) {
        List<String> lInterfaceList;
        List<String> lAllowedInterfaceList = cAsmiRoleIntfRepository
                .findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
        LOG.debug("{} List of Allowed inteface id : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lAllowedInterfaceList.size());
        List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository
                .findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
        LOG.debug(LIST_DENIED_INTERFACE_ID, ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedInterfaceList.size());

        if (!lDeniedInterfaceList.isEmpty()) {
            lInterfaceList = cAsmiRoleIntfRepository
                    .findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
            lInterfaceList.removeAll(lDeniedInterfaceList);
        } else if (!lAllowedInterfaceList.isEmpty()) {
            lInterfaceList = lAllowedInterfaceList;
        } else {
            lInterfaceList = new ArrayList<>();
        }
        return lInterfaceList;
    }

    private List<String> getAllowedIntfLstFrmRoleIntfD(Message pMessage, List<String> roleInterfaceD) {
        List<String> lInterfaceList;
        LOG.debug("{} Going to fetch list of Denied interfaceId.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository
                .findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
        LOG.debug(LIST_DENIED_INTERFACE_ID, ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDeniedInterfaceList.size());
        if (!lDeniedInterfaceList.isEmpty()) {
            LOG.debug(
                    "{} Interface Id mapped under denied role. Now Fetching Authorized Interface from InterfaceMaster table minus(-) Interface mapped under denied case",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            lInterfaceList = cAsmiRoleIntfRepository.getListOfMasterInterfaceMinusGivenInterface(
                    pMessage.getHeader().getAppId(), lDeniedInterfaceList);
            LOG.debug("{} lInterfaceList  : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lInterfaceList.size());
        } else {
            lInterfaceList = cAsmiRoleIntfRepository
                    .findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
        }
        return lInterfaceList;
    }

    /**
     * Added by ripu Below method added to check for default authorization if
     * default_authorization is 'N' in security parameters
     *
     * @param pMessage
     */
    public void checkDefaultAuthorizationService(Message pMessage) {
        LOG.debug("{} inside checkDefaultAuthorizationService", ServerConstants.LOGGER_PREFIX_DOMAIN);
        LOG.debug("{} Checking authorization for interfaceId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                pMessage.getHeader().getInterfaceId());
        String lDefaultAuthorz = pMessage.getSecurityParams().getDefaultAuthorization();
        LOG.debug("{} Default Authorization From Security Parameter : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                lDefaultAuthorz);

        if (ServerConstants.YES.equals(lDefaultAuthorz)
                && !ServerConstants.NO.equals(pMessage.getIntfDtls().getAuthorizationReq())) {
            String lUserExist = getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(),
                    pMessage.getHeader().getUserId());
            LOG.debug("{} UserExist : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lUserExist);

            LOG.debug("{} Going to fetch list of Roles assigned to the user : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    pMessage.getHeader().getUserId());
            List<String> lRoleList = cAsmiUserRoleRepository.findRoleListByAppIdUserId(pMessage.getHeader().getAppId(),
                    pMessage.getHeader().getUserId());
            LOG.debug("{} List Of Roles : {}, assigned to the user : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lRoleList, pMessage.getHeader().getUserId());
            isRoleAndInterfaceAuthorized(pMessage, lRoleList);
        } else {
            LOG.debug("{} Since this appId is default authorized so validation will be bypassed.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        }
    }

    private void isRoleAndInterfaceAuthorized(Message pMessage, List<String> lRoleList) {
        if (lRoleList != null && !lRoleList.isEmpty()) {

            // collecting role list for interface under 'A/D' category
            List<String> roleInterfaceA = new ArrayList<>();
            List<String> roleInterfaceD = new ArrayList<>();
            for (String lRoleId : lRoleList) {
                TbAsmiRoleMaster lRoleMaster = cAsmiRoleMasterRepo.findRolesByRoleIdAppId(lRoleId,
                        pMessage.getHeader().getAppId());
                if (lRoleMaster.getInterfaceAllowed().equals("A")) {
                    roleInterfaceA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
                } else if (lRoleMaster.getInterfaceAllowed().equals("D")) {
                    roleInterfaceD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
                }
            }
            List<String> lInterfaceList = getAllowedInterfacesList(pMessage, roleInterfaceA, roleInterfaceD);
            LOG.debug("{} Interface List size : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lInterfaceList != null ? lInterfaceList.size() : 0);
            if (lInterfaceList != null && !lInterfaceList.isEmpty()
                    && lInterfaceList.contains(pMessage.getHeader().getInterfaceId())) {
                LOG.error("{} InterfaceId is '{}' authorized for user : {}",
                        ServerConstants.LOGGER_PREFIX_DOMAIN, lInterfaceList, pMessage.getHeader().getUserId());
            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_055)
                        + pMessage.getHeader().getUserId());
                dexp.setCode(DomainException.Code.APZ_DM_055.toString());
                dexp.setPriority("1");
                LOG.error("{} InterfaceId is not authorized for user : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, pMessage.getHeader().getUserId());
                throw dexp;
            }
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_018)
                    + pMessage.getHeader().getUserId());
            dexp.setCode(DomainException.Code.APZ_DM_018.toString());
            dexp.setPriority("1");
            LOG.error(NO_ROLE_EXISTS, ServerConstants.LOGGER_PREFIX_DOMAIN, pMessage.getHeader().getUserId());
            throw dexp;
        }
    }

    /**
     * default authorization changes end here
     */

    public void getAuthorizedScreen(Message pMessage) {
        LOG.debug("{} inside getAuthorizedScreen");
        String lUserId = pMessage.getRequestObject().getRequestJson().get(ServerConstants.MESSAGE_HEADER_USER_ID) + "";

        List<String> lAuthorizedScreenList = cAsmiRoleScrRepository.getListOfAuthorizedScreens(lUserId, pMessage.getHeader().getAppId());
        if (lAuthorizedScreenList != null && !lAuthorizedScreenList.isEmpty()) {
            JSONArray jsonArr = new JSONArray();
            for (String screen : lAuthorizedScreenList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, screen);
                jsonObject.put(ServerConstants.STATUS, ServerConstants.YES);
                jsonArr.put(jsonObject);
            }
            JSONObject json = new JSONObject();
            json.put(ServerConstants.SCREEN_IDS, jsonArr);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ServerConstants.AUTHRESPONSE, json);
            jsonObject.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
            pMessage.getResponseObject().setResponseJson(jsonObject);
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_056));
            dexp.setCode(DomainException.Code.APZ_DM_056.toString());
            dexp.setPriority("1");
            LOG.error("{} No Screen Authorized for this userId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lUserId);
            throw dexp;
        }
    }


    public void getAuthorizedInterfaceId(Message pMessage) {
        LOG.debug("{} inside getAuthorizedInterfaceId", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String lUserId = pMessage.getRequestObject().getRequestJson().get(ServerConstants.MESSAGE_HEADER_USER_ID) + "";
        List<String> lAuthorizedInterfaceList = cAsmiRoleIntfRepository.getListOfAuthorizedInterfaceId(lUserId,
                pMessage.getHeader().getAppId());
        if (lAuthorizedInterfaceList != null && !lAuthorizedInterfaceList.isEmpty()) {
            JSONArray jsonArr = new JSONArray();
            for (String interfaceId : lAuthorizedInterfaceList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, interfaceId);
                jsonObject.put(ServerConstants.STATUS, ServerConstants.YES);
                jsonArr.put(jsonObject);
            }
            JSONObject json = new JSONObject();
            json.put(ServerConstants.INTERFACES, jsonArr);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ServerConstants.AUTHRESPONSE, json);
            jsonObject.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
            pMessage.getResponseObject().setResponseJson(jsonObject);
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_057));
            dexp.setCode(DomainException.Code.APZ_DM_057.toString());
            dexp.setPriority("1");
            LOG.error("{} No Interface Authorized for this userId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lUserId);
            throw dexp;
        }
    }

    /**
     * Below method written for checking access allowed for application Date : -
     * 17-Aug-2016
     */
    public void checkUserAccessAllowedForAppId(Message pMessage) {
        JSONObject lRequest = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} inside checkAccessAllowedForAppId", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<String> appIdFromDB = cAsmiUserAppAccessRepository.getAllwedAppIdByUserIdAndAppAllowed(
                pMessage.getHeader().getUserId(), "A", pMessage.getHeader().getAppId());
        String lAppId = "";
        if ("SecurityParamatersQuery_Query".equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
            lAppId = lRequest.getJSONObject("passwordRuleRequest").getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        } else if ("RoleProfileQuery_Query".equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
            lAppId = lRequest.getJSONObject("TbAsmiRoleMaster").getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        } else if ("UserProfileQuery_Query".equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
            lAppId = lRequest.getJSONObject("tbAsmiUser").getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        }

        JSONObject res = new JSONObject();
        if (!lAppId.isEmpty() && !lAppId.equals(ServerConstants.PERCENT)) {
            if (appIdFromDB.contains(lAppId)) {
                res.put(USER_ALLOWED_TO_ACCESS_APP, ServerConstants.YES);
            } else {
                res.put(USER_ALLOWED_TO_ACCESS_APP, ServerConstants.NO);
                LOG.debug("{} Requested AppId is Not Allowed for the user", ServerConstants.LOGGER_PREFIX_DOMAIN);
            }
        } else {
            res.put(USER_ALLOWED_TO_ACCESS_APP, "appIdEmpty");
        }
        pMessage.getResponseObject().setResponseJson(res);

    }
    /**changes end here for access allowed */

    /**
     * @param pMessage
     * @param pUserId
     * @return
     */
    public Map<String, List<String>> getAllowedDeniedIntfScrnCntrlsRoles(Message pMessage, String pUserId,
                                                                         List<String> lRoleList) {
        LOG.debug("{} Fetching List of allowed or denied Interface Screen Control List", ServerConstants.LOGGER_PREFIX_DOMAIN);
        Map<String, List<String>> allowedDeniedIntfRoles = new HashMap<>();

        if (lRoleList != null && !lRoleList.isEmpty()) {
            List<String> roleInterfaceA = new ArrayList<>();
            List<String> roleInterfaceD = new ArrayList<>();
            List<String> roleScreenA = new ArrayList<>();
            List<String> roleScreenD = new ArrayList<>();
            List<String> roleControlA = new ArrayList<>();
            List<String> roleControlD = new ArrayList<>();

            for (String lRoleId : lRoleList) {
                TbAsmiRoleMaster lRoleMaster = cAsmiRoleMasterRepo.findRolesByRoleIdAppId(lRoleId,
                        pMessage.getHeader().getAppId());
                setRoleIdforIntfScrnCtrl(roleInterfaceA, roleInterfaceD, roleScreenA, roleScreenD, roleControlA, roleControlD, lRoleMaster);
            }
            allowedDeniedIntfRoles.put(ServerConstants.ALLOWED_INTFERFACE_ROLE, roleInterfaceA);
            allowedDeniedIntfRoles.put(ServerConstants.DENIED_INTERFACE_ROLE, roleInterfaceD);
            allowedDeniedIntfRoles.put(ServerConstants.ALLOWED_SCREEN_ROLE, roleScreenA);
            allowedDeniedIntfRoles.put(ServerConstants.DENIED_SCREEN_ROLE, roleScreenD);
            allowedDeniedIntfRoles.put(ServerConstants.ALLOWED_CONTROL_ROLE, roleControlA);
            allowedDeniedIntfRoles.put(ServerConstants.DENIED_CONTROL_ROLE, roleControlD);
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_018));
            dexp.setCode(DomainException.Code.APZ_DM_018.toString());
            dexp.setPriority("1");
            LOG.error(NO_ROLE_EXISTS, ServerConstants.LOGGER_PREFIX_DOMAIN, pUserId);
            throw dexp;
        }
        return allowedDeniedIntfRoles;

    }

    private void setRoleIdforIntfScrnCtrl(List<String> roleInterfaceA, List<String> roleInterfaceD, List<String> roleScreenA, List<String> roleScreenD, List<String> roleControlA, List<String> roleControlD, TbAsmiRoleMaster lRoleMaster) {
        if (lRoleMaster.getInterfaceAllowed().equals("A")) {
            roleInterfaceA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
        } else if (lRoleMaster.getInterfaceAllowed().equals("D")) {
            roleInterfaceD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
        }

        if (lRoleMaster.getScreenAllowed().equals("A")) {
            roleScreenA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
        } else if (lRoleMaster.getScreenAllowed().equals("D")) {
            roleScreenD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
        }

        if (lRoleMaster.getControlAllowed().equals("A")) {
            roleControlA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
        } else if (lRoleMaster.getControlAllowed().equals("D")) {
            roleControlD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
        }
    }

}
