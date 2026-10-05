package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsczScreenLayouts;
import com.iexceed.appzillon.domain.entity.TbAsczScreenLayoutsPK;
import com.iexceed.appzillon.domain.entity.TbAsczTemplateObjects;
import com.iexceed.appzillon.domain.entity.TbAsczTemplateObjectsPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsczScreenLayoutsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsczTemplateObjectsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiScreenLayoutsRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 4:19 PM
 */

@Named(ServerConstants.SERVICE_CUSTOMIZER)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class CustomizerService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, CustomizerService.class.getName());

    @Inject
    TbAsczTemplateObjectsRepository objectsRepository;

    @Inject
    TbAsczScreenLayoutsRepository repository;

    @Inject
    TbAsmiScreenLayoutsRepository tbAsmiScreensLayoutRepository;

    public void fetchQueryDesignData(Message pMessage) {
        // TODO document why this method is empty
    }

    public void saveCustomizationData(Message pMessage) {
        JSONObject request = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_SAVE_CUSTOMIZATION_DATA_REQUEST);
        JSONObject customeDesign = request.getJSONObject(ServerConstants.CUSTOME_DESIGN);
        String cAppId = customeDesign.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String cScreenId = customeDesign.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
        String cLayOutId = customeDesign.getString(ServerConstants.LAYOUT_ID);
        String cDesignId = customeDesign.getString(ServerConstants.DESIGN_ID);

        TbAsczScreenLayoutsPK tbAsczScreenLayoutsPK = new TbAsczScreenLayoutsPK(cAppId, cScreenId, cLayOutId);
        TbAsczScreenLayouts tbAsczScreenLayouts = new TbAsczScreenLayouts();
        tbAsczScreenLayouts.setId(tbAsczScreenLayoutsPK);
        tbAsczScreenLayouts.setDefaultTemplate(cDesignId);
        tbAsczScreenLayouts.setAuthStatus("U");
        tbAsczScreenLayouts.setCheckerId(null);
        tbAsczScreenLayouts.setMakerId(pMessage.getHeader().getUserId());
        tbAsczScreenLayouts.setMakerTs(new Date());
        tbAsczScreenLayouts.setVersionNo(1);
        repository.save(tbAsczScreenLayouts);

        // delete existing design receiver
        objectsRepository.deleteDesignReceiverByAppIdScreenIdLayoutIdTemplateId(cAppId, cScreenId, cLayOutId, cDesignId);

        JSONArray designReceivers = request.getJSONArray(ServerConstants.DESIGN_RECEIVERS);
        List<TbAsczTemplateObjects> tbAsczTemplateObjects = new ArrayList<>();
        for (int i = 0; i < designReceivers.length(); i++) {
            String lAppId = designReceivers.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String lScreenId = designReceivers.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
            String lLayOutId = designReceivers.getJSONObject(i).getString(ServerConstants.LAYOUT_ID);
            String lDesignId = designReceivers.getJSONObject(i).getString(ServerConstants.DESIGN_ID);
            String lParentId = designReceivers.getJSONObject(i).getString(ServerConstants.PARENT_ID);
            String lChildId = designReceivers.getJSONObject(i).getString(ServerConstants.CHILD_ID);
            String lChildSeq = designReceivers.getJSONObject(i).getString(ServerConstants.CHILD_SEQ);
            TbAsczTemplateObjectsPK tbAsczTemplateObjectsPK = new TbAsczTemplateObjectsPK(lAppId, lScreenId, lLayOutId, lDesignId, lParentId, lChildId);
            TbAsczTemplateObjects templateObjects = new TbAsczTemplateObjects();
            templateObjects.setId(tbAsczTemplateObjectsPK);
            templateObjects.setChildSeq(Integer.parseInt(lChildSeq));
            tbAsczTemplateObjects.add(templateObjects);
        }
        objectsRepository.saveAll(tbAsczTemplateObjects);
        JSONObject response = new JSONObject();
        response.put(ServerConstants.APPZILLON_ROOT_SAVE_CUSTOMIZATION_DATA_RESPONSE, getCustomizerDetails(cAppId, cScreenId, ServerConstants.YES, cLayOutId, cDesignId));
        pMessage.getResponseObject().setResponseJson(response);
    }

    public void queryDeviceGroups(Message pMessage) {
        JSONArray lResponse = new JSONArray();
        try {
            JSONObject jsonObject = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_DEVICEGRPQUERY_REQUEST);

            String appId = jsonObject.getString("appId");
            List<Object[]> lResp = null;
            lResp = tbAsmiScreensLayoutRepository.findDeviceGroupsByAppId(appId);
            if (!lResp.isEmpty()) {

                for (Object[] obj : lResp) {
                    JSONObject json = new JSONObject();
                    json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, obj[0]);
                    json.put(ServerConstants.DEVICE_DESC, obj[1]);
                    json.put(ServerConstants.OS, obj[2]);
                    json.put(ServerConstants.HEIGHT, obj[3]);
                    json.put(ServerConstants.WIDTH, obj[4]);
                    json.put(ServerConstants.ORIENTATION, obj[5]);
                    lResponse.put(json);
                }

                JSONObject deviceGrps = new JSONObject();
                deviceGrps.put(ServerConstants.DEVICE_GROUPS, lResponse);
                JSONObject pResp = new JSONObject();
                pResp.put(ServerConstants.APPZILLON_ROOT_DEVICEGRPQUERY_RESPONSE, deviceGrps);
                pMessage.getResponseObject().setResponseJson(pResp);

            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error("{} No record found", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
        } catch (JSONException e) {
            LOG.error("{} {}", ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }

    }

    public void getListOfScreens(Message pMessage) {
        // TODO document why this method is empty
    }

    public void getCustomizerServiceDetails(Message pMessage) {
        JSONObject request = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CUSTOMIZER_DETAILS_REQUEST);
        String appId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String screenId = request.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
        String customizer = request.getString(ServerConstants.CUSTOMIZER);
        String layoutId = request.getString(ServerConstants.LAYOUT_ID);
        String templateId = request.has(ServerConstants.TEMPLATE_ID) ? request.getString(ServerConstants.TEMPLATE_ID) : null;

        pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_GET_CUSTOMIZER_DETAILS_RESPONSE, getCustomizerDetails(appId, screenId, customizer, layoutId, templateId)));
    }

    private JSONObject getCustomizerDetails(String appId, String screenId, String customizer, String layoutId, String templateId) {
        JSONObject lResponse = new JSONObject();
        TbAsczScreenLayoutsPK tbAsczScreenLayoutsPK = new TbAsczScreenLayoutsPK(appId, screenId, layoutId);
        Optional<TbAsczScreenLayouts> tbAsczScreenLayouts = repository.findById(tbAsczScreenLayoutsPK);
        if (Utils.isNullOrEmpty(templateId)) {
            templateId = tbAsczScreenLayouts.isPresent() ? tbAsczScreenLayouts.get().getDefaultTemplate() : ServerConstants.PERCENT;
        }
        if (tbAsczScreenLayouts.isPresent()) {
            lResponse.put(ServerConstants.ISMODIFIED, true);
            lResponse.put(ServerConstants.TEMLT, templateId);
        } else {
            lResponse.put(ServerConstants.ISMODIFIED, false);
        }

        List<Object[]> objects = objectsRepository.findTemplateObjectOrderByParentIdAndChildSeq(appId, screenId, layoutId, templateId);
        JSONObject containers = new JSONObject();
        Iterator<Object[]> iterator = objects.iterator();
        while (iterator.hasNext()) {
            Object[] objects1 = iterator.next();
            String parentId = (String) objects1[0];
            String childSeq = (String) objects1[1];
            if (containers.has(parentId)) {
                ((ArrayList<String>) containers.get(parentId)).add(childSeq);
            } else {
                ArrayList<String> strings = new ArrayList<>();
                strings.add(childSeq);
                containers.put(parentId, strings);
            }
        }

        Iterator<String> jsonKeys = containers.keys();
        JSONArray lContainers = new JSONArray();
        while (jsonKeys.hasNext()) {
            String key = jsonKeys.next();
            JSONObject json = new JSONObject();
            json.put(ServerConstants.ID, key);
            json.put(ServerConstants.SEQUENCE, containers.get(key));
            lContainers.put(json);
        }
        lResponse.put(ServerConstants.CONTAINERS, lContainers);

        if (ServerConstants.YES.equalsIgnoreCase(customizer)) {
            createMicroAppsList(lResponse, appId, screenId);
        }
        return lResponse;

    }

    private void createMicroAppsList(JSONObject lResponse, String appId, String screenId) {

        List<Object[]> microApps;
        microApps = tbAsmiScreensLayoutRepository.findMicroApps(appId, screenId);
        ArrayList<String> microAppList = new ArrayList<>();
        for (Object[] microApp : microApps) {
            microAppList.add((String) microApp[0]);

        }
        lResponse.put(ServerConstants.MICROAPPS, microAppList);

        List<Object[]> widgets;
        widgets = tbAsmiScreensLayoutRepository.findWidgets(appId, screenId);
        ArrayList<String> widgetsList = new ArrayList<>();
        for (Object[] widget : widgets) {
            widgetsList.add((String) widget[0]);

        }
        lResponse.put(ServerConstants.CALLFORMS, widgetsList);

        List<Object[]> navigators;
        navigators = tbAsmiScreensLayoutRepository.findNavigators(appId, screenId);
        ArrayList<String> navigatorsList = new ArrayList<>();
        for (Object[] navigator : navigators) {
            navigatorsList.add((String) navigator[0]);

        }
        lResponse.put(ServerConstants.NAVIGATORS, navigatorsList);
    }
}
