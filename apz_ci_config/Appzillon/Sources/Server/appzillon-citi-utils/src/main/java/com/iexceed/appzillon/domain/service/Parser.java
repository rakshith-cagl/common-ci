package com.iexceed.appzillon.domain.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScr;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScrPK;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiCnvUIScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.Response;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.citi.dataobj.*;
import com.iexceed.citi.products.GenDesignDef;
import com.iexceed.citi.utils.DesignUtils;
import com.iexceed.intf.json.IntfDefinition;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.utils.Constants.*;

@Named("Parser")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class Parser {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getDomainLogger(ServerConstants.LOGGER_PREFIX_CITI, Parser.class.toString());
    ///// Map for holding the sub products which were received during enriching,
    ///// Linked hash map for ensuring the order of insertion
    public static Map<String, ObjectNode> productsMapper = new LinkedHashMap<String, ObjectNode>();
    ///// Map with unique id made of product and name of object.
    public static HashMap<String, ScrElementData> nameMapper = new HashMap<>();
    public static HashMap<String, ScrElementData> origIdMapper = new HashMap<>();
    ///// Mapping decoration rule for global objects dependencies
    public static Map<String, String> decorationMappings = new LinkedHashMap<String, String>();
    public static ArrayList<Parser> screenParsers = new ArrayList<Parser>();
    public static ObjectNode sharedRelatedProductsMap = JsonNodeFactory.instance.objectNode();
    public static String prodShortName = "";
    static StringBuilder temp = new StringBuilder();
    public ArrayList<String> relatedProducts = new ArrayList<String>();
    public HashMap<String, ObjectNode> nodesMap = new HashMap<String, ObjectNode>();
    public DesignData configObj = null;
    public String originalProductId = "", uniqueProductId = "", parentProductId = "", relationshipId = "", scrId = "";
    public String appId = "";
    public boolean relationExists = false;
    public String parentTitle = "N";// for standalone run change to parentTitle="Y"
    public String userId = "";
    public String productName = "";
    public String screenParentId = "";
    @Inject
    TbAsmiCnvUIScrRepository cAsmiCnvUIScrRepository;
    @Inject
    TbAsmiIntfMasterRepository iAsmiIntfMasterRepository;
    ObjectNode relatedProductsMap = JsonNodeFactory.instance.objectNode();
    IntfDefinition objIntfDefinition = new IntfDefinition();
    private ParserUtils objParserUtils = new ParserUtils();

    private static void cleanUpRef() {
        productsMapper.clear();
        nameMapper.clear();
        origIdMapper.clear();
        decorationMappings.clear();
        screenParsers.clear();
    }

    ///// Entry point for Reading Product JSON files
    public List<ScreenResponseJson> readProductsJson(JSONObject objRequestJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ScreenResponseJson objScreenResponseJson = null;
        String template = null;
        String firstProductId = null;
        try {

            JSONArray objRequestArray = objRequestJson.getJSONArray("productdef");
            ObjectNode actualObj = (ObjectNode) mapper.readTree(objRequestJson.toString());
            for (int index = 0; index < objRequestArray.length(); index++) {
                JSONObject object = objRequestArray.getJSONObject(index);
                originalProductId = object.getString("productId");
                uniqueProductId = Utils.removeSpecialChars(originalProductId);
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + INSIDE_READ_PRODUCT_JSON);

                JsonNode objJsonNode = actualObj.get("productdef").get(index).get("proddef");
                ObjectNode objNode = (ObjectNode) mapper.readTree(objJsonNode.asText());

                productsMapper.put(uniqueProductId, objNode);
            }
            Map.Entry<String, ObjectNode> key = productsMapper.entrySet().iterator().next();
            firstProductId = key.getKey();
            LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + INSIDE_READ_PRODUCT_JSON);
            TbAsmiCnvUIScr CnvUIScrtemplate = getTemplate(appId, firstProductId);
            template = CnvUIScrtemplate.getTemplate();
            if (!Utils.isNull(template)) {
                configObj = getTemplateObj(template);
            }
            if (!Utils.isNull(CnvUIScrtemplate.getScreenLayout())) {
                configObj.scripts = getScreenScripts(CnvUIScrtemplate.getScreenLayout());
            }
        } catch (Exception e) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        try {
            ObjectNode actualObj = productsMapper.get(firstProductId);
            Utils.getPName(actualObj);
            if (Utils.isNull(Parser.prodShortName)) {
                Utils.getProductShortName(actualObj);
            }

            parseProductJson(actualObj, "", "");
            updateConfigObj(actualObj, "", "", "", null, null);
            Utils.populateGroups(configObj, scrId);
            for (Parser scrParser : screenParsers) {
                ObjectNode scrDef = new GenDesignDef().gen(scrParser.configObj, scrParser.uniqueProductId,
                        scrParser.appId, scrParser.scrId);
                objScreenResponseJson = new ScreenResponseJson();
                objScreenResponseJson.setScreenDesignJson(mapper.writeValueAsString(scrParser.configObj));
                objScreenResponseJson.setIntfJson(mapper.writeValueAsString(scrParser.objIntfDefinition));
                objScreenResponseJson.setScreenDefJson(mapper.writeValueAsString(scrDef));
                objScreenResponseJson.setProductId(scrParser.screenParentId);
                persistProductJson(objScreenResponseJson);
                objParserUtils.objScreenResponseJson.add(objScreenResponseJson);
            }
            ObjectNode scrDef = new GenDesignDef().gen(configObj, firstProductId, appId, scrId);
            objScreenResponseJson = new ScreenResponseJson();
            objScreenResponseJson.setScreenDesignJson(mapper.writeValueAsString(configObj));
            objScreenResponseJson.setIntfJson(mapper.writeValueAsString(objIntfDefinition));
            objScreenResponseJson.setScreenDefJson(mapper.writeValueAsString(scrDef));
            objScreenResponseJson.setProductId(uniqueProductId);
            objScreenResponseJson.setTempalate(template);
            persistProductJson(objScreenResponseJson);
            objParserUtils.objScreenResponseJson.add(objScreenResponseJson);
            LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside ScreenResponseJson ");

        } catch (Exception e) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return objParserUtils.objScreenResponseJson;
    }

    private ArrayNode getScreenScripts(String screenLayout) {
        ArrayNode scriptsArray = null;
        try {
            ObjectMapper m = new ObjectMapper();
            JsonNode screenLayoutnode = m.readTree(screenLayout);
            if (screenLayoutnode.has("scripts")) {
                scriptsArray = (ArrayNode) screenLayoutnode.get("scripts");
            }
        } catch (JsonProcessingException jpe) {
            LOG.error(ServerConstants.LOGGER_PREFIX_CITI + jpe.getLocalizedMessage());
        } catch (IOException ioe) {
            LOG.error(ServerConstants.LOGGER_PREFIX_CITI + ioe.getLocalizedMessage());
        }
        return scriptsArray;
    }

    private void cleanUpStaticRef() {
        relatedProducts.clear();
        nodesMap.clear();
        objParserUtils = new ParserUtils();
        cleanUpRef();

    }

    public TbAsmiCnvUIScr getTemplate(String appId, String productId) throws Exception {
        String template = "";
        String scrlayout = "";
        TbAsmiCnvUIScr responseobjScreenJson = new TbAsmiCnvUIScr();
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "TEMPLATE:: Getting Template for " + productId);
        if (cAsmiCnvUIScrRepository != null) {
            TbAsmiCnvUIScr objScreenJson = cAsmiCnvUIScrRepository.getScreenDetailsOnVersion(appId, productId + "_Scr");
            if (objScreenJson != null) {
                template = objScreenJson.getTemplate();
                scrlayout = objScreenJson.getScreenLayout();
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "TEMPLATE::" + objScreenJson.getTemplate());
            } else {
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "Fetching DEFAULT TEMPLATE ");
                template = Utils.getDefaultTemplate();
                scrlayout = Utils.getDefaultLayoutDef(productId + "_Scr");
            }
        } else {
            template = Utils.getFileContent(productId + "_Template.json");
            scrlayout = Utils.getDefaultLayoutDef(productId + "_Scr");
        }
        if (Utils.isNull(template)) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "Fetching DEFAULT TEMPLATE ");
            template = Utils.getDefaultTemplate();
            scrlayout = Utils.getDefaultLayoutDef(productId + "_Scr");
        }
        responseobjScreenJson.setTemplate(template);
        responseobjScreenJson.setScreenLayout(scrlayout);
        return responseobjScreenJson;
    }

    public void parseCitiProjectJson(Message pMessage) {
        Header header = pMessage.getHeader();
        this.appId = header.getAppId();
        this.userId = header.getUserId();
        String titleValue = header.getParentTitle();
        this.parentTitle = Utils.isNull(titleValue) ? "N" : titleValue;
        JSONObject objRequestJson = pMessage.getRequestObject().getRequestJson();
        Response responseObject = Response.getInstance();
        JSONObject responseProdJson = new JSONObject();
        List<ScreenResponseJson> objScreenResponseJson;
        try {
            objScreenResponseJson = readProductsJson(objRequestJson);

            for (ScreenResponseJson screenResponseJson : objScreenResponseJson) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(DesignUtils.SCREEN_DEF, screenResponseJson.getScreenDefJson());// screenDesignDef
                jsonObject.put(DesignUtils.SCREEN_DESIGN, screenResponseJson.getScreenDesignJson());// ScreenDesign
                jsonObject.put(DesignUtils.LAYOUT_DEF, screenResponseJson.getLayoutDef());
                jsonObject.put(DesignUtils.INTERFACE_DEF, screenResponseJson.getIntfJson());// json intf
                responseProdJson.put(screenResponseJson.getProductId() + "_Scr", jsonObject);
            }
        } catch (Exception e) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        responseObject.setResponseJson(responseProdJson);
        pMessage.setResponseObject(responseObject);
        cleanUpStaticRef();

    }

    public void persistProductJson(ScreenResponseJson objScreenResponseJson) throws JsonProcessingException {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside persistProductJson() ");
        if (cAsmiCnvUIScrRepository != null) {
            try {
                TbAsmiCnvUIScr objTbAsmiCnvUIScr = cAsmiCnvUIScrRepository.getScreenDetailsOnVersion(appId,
                        objScreenResponseJson.getProductId() + "_Scr");
                if (objTbAsmiCnvUIScr == null) {
                    objTbAsmiCnvUIScr = new TbAsmiCnvUIScr();
                    objTbAsmiCnvUIScr.setCreateUserId(appId);
                    objTbAsmiCnvUIScr.setVersionNo(1);
                    objTbAsmiCnvUIScr.setScreenDesc("New Screen");
                    String layoutdef = Utils.getDefaultLayoutDef(objScreenResponseJson.getProductId());
                    objTbAsmiCnvUIScr.setScreenLayout(layoutdef);
                    objScreenResponseJson.setLayoutDef(layoutdef);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + " Creating record for Screen "
                            + objScreenResponseJson.getProductId());
                } else {
                    objScreenResponseJson.setLayoutDef(objTbAsmiCnvUIScr.getScreenLayout());
                }
                TbAsmiCnvUIScrPK objTbAsmiCnvUIScrPK = new TbAsmiCnvUIScrPK();
                objTbAsmiCnvUIScrPK.setAppId(appId);
                objTbAsmiCnvUIScrPK.setScreenId(objScreenResponseJson.getProductId() + "_Scr");
                objTbAsmiCnvUIScr.setId(objTbAsmiCnvUIScrPK);
                objTbAsmiCnvUIScr.setScreenDef(objScreenResponseJson.getScreenDefJson());
                objTbAsmiCnvUIScr.setScreenDesign(objScreenResponseJson.getScreenDesignJson());
                objTbAsmiCnvUIScr.setScreenHtml("");
                objTbAsmiCnvUIScr.setCreateTs(new Timestamp(System.currentTimeMillis()));
                cAsmiCnvUIScrRepository.save(objTbAsmiCnvUIScr);
                TbAsmiIntfMaster objTbAsmiIntfMaster = new TbAsmiIntfMaster();
                objTbAsmiIntfMaster.setInterfaceDef(objScreenResponseJson.getIntfJson());
                objTbAsmiIntfMaster.setCaptchaReq(ServerConstants.NO);
                objTbAsmiIntfMaster.setCategory(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
                objTbAsmiIntfMaster.setType(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
                objTbAsmiIntfMaster.setDgTxnLogReq(ServerConstants.YES);
                objTbAsmiIntfMaster.setCreateUserId(userId);
                objTbAsmiIntfMaster.setCreateTs(new Timestamp(System.currentTimeMillis()));
                objTbAsmiIntfMaster.setDescription(appId + "__" + objScreenResponseJson.getProductId() + INTF);
                TbAsmiIntfMasterPK objTbAsmiIntfMasterPK = new TbAsmiIntfMasterPK();
                objTbAsmiIntfMasterPK.setInterfaceId(appId + "__" + objScreenResponseJson.getProductId() + INTF);
                objTbAsmiIntfMasterPK.setAppId(appId);
                objTbAsmiIntfMaster.setTbAsmiIntfMasterPK(objTbAsmiIntfMasterPK);
                iAsmiIntfMasterRepository.save(objTbAsmiIntfMaster);
            } catch (Exception e) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } else {
            String prodId = objScreenResponseJson.getProductId();
            temp.append(prodId + " DesignDef: " + objScreenResponseJson.getScreenDesignJson() + "\n");
            temp.append(prodId + " InterfaceDef: " + objScreenResponseJson.getIntfJson() + "\n");
            temp.append(prodId + " ScrDef: " + objScreenResponseJson.getScreenDefJson() + "\n");
            temp.append(
                    prodId + " LayoutDef: " + Utils.getDefaultLayoutDef(objScreenResponseJson.getProductId()) + "\n");
        }
        LOG.debug("Product Json Persisted in interface and screen ");
    }

    public DesignData getTemplateObj(String fileName) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        byte[] jsonData = fileName.getBytes();
        DesignData designData = mapper.readValue(jsonData, DesignData.class);
        enrichLoadedDataObject(designData);
        for (int i = 0; i < designData.getChilds().size(); i++) {
            PortionData lportiondata = (PortionData) designData.getChilds().get(i);
            if (lportiondata.widgettype.equals(DesignUtils.HEADER)) {
                designData.header = lportiondata;
                Utils.populatePortions(designData, designData.header);
            } else if (lportiondata.widgettype.equals(DesignUtils.BODY)) {
                designData.body = lportiondata;
                Utils.populatePortions(designData, designData.body);
            } else if (lportiondata.widgettype.equals(DesignUtils.SIDEBAR)) {
                designData.sidebar = lportiondata;
                Utils.populatePortions(designData, designData.sidebar);
            } else if (lportiondata.widgettype.equals(DesignUtils.FOOTER)) {
                designData.footer = lportiondata;
                Utils.populatePortions(designData, designData.footer);
            }
        }
        return designData;
    }

    public boolean enrichLoadedDataObject(DataObject pdataobj) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside enrichLoadedDataObject()");
        boolean lres = true;
        int lnoofchilds = pdataobj.getChilds().size();
        if (lnoofchilds > 0) {
            for (int i = 0; i < lnoofchilds; i++) {
                DataObject lchild = pdataobj.getChilds().get(i);
                lchild.parent = pdataobj;
                pdataobj.childsMap.put(lchild.name, lchild);
                lres = lchild.add();
                enrichLoadedDataObject(lchild);
            }
        }
        return lres;
    }

    void parseProductJson(ObjectNode response, String parentProductId, String relationshipId) throws Exception {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside enrichMap()");
        if (response.has(DesignUtils.LIST_OF_OBJECT)) {
            ObjectNode listOfObject = (ObjectNode) response.get(DesignUtils.LIST_OF_OBJECT);

            if (listOfObject.has(DesignUtils.TYPE_OBJECT)) {
                ObjectNode object = (ObjectNode) listOfObject.get(DesignUtils.TYPE_OBJECT);
                originalProductId = object.get(DesignUtils.ID).asText();
                parentProductId = Utils.removeSpecialChars(parentProductId);
                if (!parentProductId.isEmpty() && !relationshipId.isEmpty()) {
                    uniqueProductId = parentProductId + "_" + relationshipId + "_"
                            + Utils.removeSpecialChars(originalProductId);
                } else {
                    uniqueProductId = Utils.removeSpecialChars(originalProductId);
                }
                scrId = uniqueProductId + "_Scr";
                Utils.createDecorationsMap(object, "ObjectDecoration");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode attrs = object.get(DesignUtils.ATTRIBUTE);
                if (attrs != null) {
                    getProductByAttribute(object, mapper, attrs);
                }
                JsonNode relationshipNode = (JsonNode) object.get(DesignUtils.RELATIONSHIP);
                if (relationshipNode != null) {
                    getProductByRelationShip(object, mapper, relationshipNode);
                }
            }
        }
    }

    private void getProductByRelationShip(ObjectNode object, ObjectMapper mapper, JsonNode relationshipNode)
            throws Exception {
        String parentProdId = Utils.removeSpecialChars(originalProductId);
        object.put("min" + DesignUtils.RELATIONSHIP_DECORATION, DesignUtils.SEQUENCEUPPERLIMIT);
        object.put("max" + DesignUtils.RELATIONSHIP_DECORATION, 0);
        if (relationshipNode.isArray()) {
            List<ObjectNode> relation = mapper.readValue(relationshipNode.toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, ObjectNode.class));
            Utils.sortObjects(relation, DesignUtils.RELATIONSHIP_DECORATION, object);
            object.put(DesignUtils.RELATIONSHIP, mapper.valueToTree(relation));
            relationshipNode = (JsonNode) object.get(DesignUtils.RELATIONSHIP);
            for (int r = 0; r < relationshipNode.size(); r++) {
                parseRelationshipJson((ObjectNode) relationshipNode.get(r), parentProdId, object);
            }
        } else if (relationshipNode.isObject()) {
            parseRelationshipJson((ObjectNode) relationshipNode, parentProdId, object);
            int seqNum = getUISeqValue((ObjectNode) relationshipNode, "");
            object.put("min" + DesignUtils.ATTRIBUTE_DECORATION, seqNum);
            object.put("max" + DesignUtils.ATTRIBUTE_DECORATION, seqNum);
        }
    }

    private void getProductByAttribute(ObjectNode object, ObjectMapper mapper, JsonNode attrs) throws IOException {
        object.put("min" + DesignUtils.ATTRIBUTE_DECORATION, DesignUtils.SEQUENCEUPPERLIMIT);
        object.put("max" + DesignUtils.ATTRIBUTE_DECORATION, 0);
        if (attrs.isArray()) {
            List<ObjectNode> attr = mapper.readValue(attrs.toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, ObjectNode.class));
            Utils.sortObjects(attr, DesignUtils.ATTRIBUTE_DECORATION, object);
            for (int d = 0; d < attr.size(); d++) {
                Utils.createDecorationsMap(attr.get(d), DesignUtils.ATTRIBUTE_DECORATION);
            }
            object.put(DesignUtils.ATTRIBUTE, mapper.valueToTree(attr));
        } else if (attrs.isObject()) {
            Utils.createDecorationsMap((ObjectNode) attrs, DesignUtils.ATTRIBUTE_DECORATION);
            int seqNum = getUISeqValue((ObjectNode) attrs, "");
            object.put("min" + DesignUtils.ATTRIBUTE_DECORATION, seqNum);
            object.put("max" + DesignUtils.ATTRIBUTE_DECORATION, seqNum);
        }
    }

    void parseRelationshipJson(ObjectNode relationship, String parentProductId, ObjectNode object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Utils.createDecorationsMap(relationship, DesignUtils.RELATIONSHIP_DECORATION);
        JsonNode relationsDomain = relationship.get(DesignUtils.RELATIONSHIP_DOMAIN);
        String realtionShipId = Utils.removeSpecialChars(relationship.get(DesignUtils.ID).asText());
        if (relationsDomain != null) {
            if (relationsDomain.isArray()) {
                List<ObjectNode> attr = mapper.readValue(relationsDomain.toString(),
                        mapper.getTypeFactory().constructCollectionType(List.class, ObjectNode.class));
                Utils.sortObjects(attr, DesignUtils.RELATIONSHIP_DOMAIN_DECORATION, object);
                for (int d = 0; d < attr.size(); d++) {
                    Utils.createDecorationsMap(attr.get(d), DesignUtils.RELATIONSHIP_DOMAIN_DECORATION);
                }
                relationship.put(DesignUtils.RELATIONSHIP_DOMAIN, mapper.valueToTree(attr));
            } else if (relationsDomain.isObject()) {
                Utils.createDecorationsMap((ObjectNode) relationsDomain, DesignUtils.RELATIONSHIP_DOMAIN_DECORATION);
            }
            JsonNode relationshipDomain = relationship.get(DesignUtils.RELATIONSHIP_DOMAIN);
            if (relationshipDomain.isArray()) {
                ArrayNode relateDomains = (ArrayNode) relationshipDomain;
                for (int r = 0; r < relateDomains.size(); r++) {
                    ObjectNode relateDomain = (ObjectNode) relateDomains.get(r);
                    parseRelationshipDomain(relateDomain, parentProductId, realtionShipId);
                }
            } else if (relationshipDomain.isObject()) {
                parseRelationshipDomain((ObjectNode) relationshipDomain, parentProductId, realtionShipId);
            }
        }
    }

    void parseRelationshipDomain(ObjectNode relationshipDomain, String parentProductId, String realtionShipId)
            throws Exception {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside processProductsRelationship()");
        String subProductID = Utils.removeSpecialChars(relationshipDomain.get(DesignUtils.ID).asText());
        ///// Further server call for sub products Json should be here with recursive
        ///// call to this enrichMap function.
        if (productsMapper.containsKey(subProductID)) {
            try {
                ObjectNode actualObj = productsMapper.get(subProductID);
                parseProductJson(actualObj, parentProductId, realtionShipId);

            } catch (Exception e) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(e.getMessage());
                dexp.setCode(DomainException.Code.APZ_DM_000.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        }
    }

    void updateConfigObj(ObjectNode response, String group, String parentProductID, String relationshipId,
                         Parser parent, ObjectNode parentRelationship) throws Exception {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + INSIDE_READ_PRODUCT_JSON);
        if (response.has(DesignUtils.LIST_OF_OBJECT)) {
            ObjectNode listOfObject = (ObjectNode) response.get(DesignUtils.LIST_OF_OBJECT);
            boolean attrRelExists = false;
            if (listOfObject.has(DesignUtils.TYPE_OBJECT)) {
                ObjectNode object = (ObjectNode) listOfObject.get(DesignUtils.TYPE_OBJECT);
                originalProductId = object.get(DesignUtils.ID).asText();
                parentProductId = Utils.removeSpecialChars(parentProductID);
                if (!parentProductID.isEmpty() && !relationshipId.isEmpty()) {
                    uniqueProductId = parentProductID + "_" + relationshipId + "_"
                            + Utils.removeSpecialChars(originalProductId);
                } else {
                    uniqueProductId = Utils.removeSpecialChars(originalProductId);
                }
                relatedProducts.add(uniqueProductId);
                scrId = uniqueProductId + "_Scr";
                JsonNode objDecoration = object.get("ObjectDecoration");
                String attrsGrp = group;
                JsonNode grpRule = null;
                attrsGrp = getString(object, attrsGrp, grpRule);
                configObj.prodName = getName(object);
                configObj.prodId = originalProductId;
                if (parent != null) {
                    ObjectNode curProdMap = JsonNodeFactory.instance.objectNode();
                    curProdMap.put("Name", uniqueProductId);
                    relatedProductsMap.put(configObj.prodName, curProdMap);
                    relatedProductsMap.put(originalProductId, curProdMap);
                }
                objIntfDefinition.setName(appId + "__" + uniqueProductId + INTF);
                objIntfDefinition.setType("CUSTOM");
                objIntfDefinition.setDateFormat(DesignUtils.citiDateFormat);
                if (DesignUtils.citiDateTimeFormat != null && !DesignUtils.citiDateTimeFormat.isEmpty()) {
                    objIntfDefinition.setDateTimeFormat(DesignUtils.citiDateTimeFormat);
                    String timeFormat = DesignUtils.citiDateTimeFormat.split(" ")[1];
                    objIntfDefinition.setTimeFormat(timeFormat);
                }
                objIntfDefinition.setOffline("N");
                objIntfDefinition.setAmountMask("");
                objIntfDefinition.setDateFormat("");
                objIntfDefinition.setDateTimeFormat("");
                objIntfDefinition.setTimeFormat("");
                objIntfDefinition.setSession("N");
                objIntfDefinition.setCorrectReq("N");
                objIntfDefinition.setNoOfResNodes(1);
                objIntfDefinition.setNoOFaultNodes(1);
                objIntfDefinition.setCorrectReq("Y");
                objIntfDefinition.setCorrectRes("Y");
                JsonNode attrsNode = object.get(DesignUtils.ATTRIBUTE);
                String cntrId = Utils.removeSpecialChars(originalProductId);
                String relcntrId = cntrId;
                JsonNode relationshipNode = object.get(DesignUtils.RELATIONSHIP);
                productName = Utils.getTitle(object);
                if (attrsNode != null) {
                    int attrSequenceNo = 0;
                    int realtionUISeqNum = 0;
                    int tempAttrSeq = 0;
                    int checkDiff = 0;
                    boolean relationexists = false;
                    int attrMinSeq = object.get("min" + DesignUtils.ATTRIBUTE_DECORATION).asInt();
                    if (relationshipNode != null) {
                        int relMinSeq = object.get("min" + DesignUtils.RELATIONSHIP_DECORATION).asInt();
                        if (relMinSeq < attrMinSeq) {
                            attrRelExists = true;
                            updateRelations(group, object, relationshipNode, attrSequenceNo, tempAttrSeq, attrMinSeq);
                        }
                    }
                    computeScrElementData(group, object, attrsGrp, attrsNode, cntrId, relcntrId, relationshipNode,
                            tempAttrSeq, relationexists);
                }
                if (relationshipNode != null) {
                    updateRelationsByNode(group, object, relationshipNode);
                }
                Integer[] noOfNodesList = relationExists ? new Integer[]{0, 0, 0, 0} : new Integer[]{0, 0, 0};
                String[] nodeName = relationExists
                        ? new String[]{appId + "__" + uniqueProductId + INTF_REQ, uniqueProductId + "_Rel",
                        appId + "__" + uniqueProductId + "_Intf_Res",
                        appId + "__" + uniqueProductId + "_Intf_Flt"}
                        : new String[]{appId + "__" + uniqueProductId + INTF_REQ,
                        appId + "__" + uniqueProductId + "_Intf_Res",
                        appId + "__" + uniqueProductId + "_Intf_Flt"};
                objIntfDefinition.setNodes(nodeName);
                objIntfDefinition.setnExtName(nodeName);
                objIntfDefinition.setnMrParent(nodeName);
                objIntfDefinition.setNoOfReqNodes(relationExists ? 2 : 1);

                String[] nodeParent = relationExists
                        ? new String[]{"", appId + "__" + uniqueProductId + INTF_REQ, "", ""}
                        : new String[]{"", "", ""};
                objIntfDefinition.setnParent(nodeParent);
                objIntfDefinition.setnParents(nodeParent);
                String[] nodeChilds = relationExists ? new String[]{uniqueProductId + "_Rel", "", "", ""}
                        : new String[]{"", "", ""};
                objIntfDefinition.setnChilds(nodeChilds);

                noOfNodesList[0] = objParserUtils.elms.size();
                if (!objParserUtils.relElms.isEmpty()) {
                    forMobjParserUtils(noOfNodesList);
                }
                forMobjIntfDefinition(noOfNodesList);
                String[] nodeMultRec = relationExists ? new String[]{"N", "N", "N", "N"}
                        : new String[]{"N", "N", "N"};
                String[] rel = relationExists ? new String[]{"1:1", "1:1", "1:1", "1:1"}
                        : new String[]{"1:1", "1:1", "1:1"};
                if (nodesMap.containsKey(nodeName[0])) {
                    ObjectNode nodeObj = nodesMap.get(nodeName[0]);
                    String nodeType = nodeObj.get(NODE_TYPE).asText();
                    if (nodeType.equals("1:N")) {
                        nodeMultRec[0] = "Y";
                        rel[0] = "1:N";
                    }
                }
                objIntfDefinition.setnMultiRec(nodeMultRec);
                objIntfDefinition.setnRelType(rel);
                for (int e = 0; e < configObj.elms.size(); e++) {
                    ScrElementData elmObj = configObj.elms.get(e);
                    updateElmDecorations(elmObj, objDecoration);
                }
                ScrContainerData cntr = configObj.containersMap.get(cntrId);
                updateContainerInfo(cntr, object, attrRelExists, parentRelationship);

                for (int e = 0; e < configObj.containers.size(); e++) {
                    ScrContainerData cntrObj = configObj.containers.get(e);
                    updateElmDecorations(cntrObj, objDecoration);
                }
                applyObjectDecoration(objDecoration);
            }
        }
    }

    private String getString(ObjectNode object, String attrsGrp, JsonNode grpRule) {
        if (object.has("DXP_" + Parser.prodShortName + BASIC_RULE_SET)) {
            grpRule = object.get("DXP_" + Parser.prodShortName + BASIC_RULE_SET);
        } else if (object.has("DX_" + Parser.prodShortName + BASIC_RULE_SET)) {
            grpRule = object.get("DX_" + Parser.prodShortName + BASIC_RULE_SET);
        }
        if (grpRule != null) {
            attrsGrp = grpRule.get(VALUE).asText().split("\\|")[0];
        }
        return attrsGrp;
    }

    private void computeScrElementData(String group, ObjectNode object, String attrsGrp, JsonNode attrsNode,
                                       String cntrId, String relcntrId, JsonNode relationshipNode, int tempAttrSeq, boolean relationexists)
            throws Exception {
        int attrSequenceNo;
        int realtionUISeqNum;
        int checkDiff;
        if (attrsNode.isArray()) {
            ArrayNode attrs = (ArrayNode) attrsNode;
            String sequenceCounter = "";
            for (int a = 0; a < attrs.size(); a++) {
                ObjectNode attr = (ObjectNode) attrs.get(a);
                String origId = attr.get(ORIG_ID).asText();
                attrSequenceNo = getUISeqValue(attr, DesignUtils.ATTRIBUTE_DECORATION);
                if (sequenceCounter != null) {
                    checkDiff = attrSequenceNo - tempAttrSeq;
                    if (checkDiff > 1 && relationshipNode != null) {
                        if (relationshipNode.isArray()) {
                            ArrayNode relationships = (ArrayNode) relationshipNode;
                            for (int r = 0; r < relationships.size(); r++) {
                                ObjectNode relationship = (ObjectNode) relationships.get(r);
                                realtionUISeqNum = getUISeqValue(relationship, DesignUtils.RELATIONSHIP_DECORATION);
                                if (realtionUISeqNum > attrSequenceNo) {
                                    break;
                                }
                                if (realtionUISeqNum > tempAttrSeq && realtionUISeqNum < attrSequenceNo) {
                                    relationexists = true;
                                    sequenceCounter = Integer.toString(realtionUISeqNum);
                                    updateRelationsObj(relationship, group);
                                }
                            }
                        } else if (relationshipNode.isObject()) {
                            ObjectNode relationship = (ObjectNode) object.get(DesignUtils.RELATIONSHIP);
                            realtionUISeqNum = getUISeqValue(relationship, DesignUtils.RELATIONSHIP_DECORATION);
                            if (realtionUISeqNum > tempAttrSeq && realtionUISeqNum < attrSequenceNo) {
                                relationexists = true;
                                updateRelationsObj(relationship, group);
                            }
                        }
                    }
                    if (attrSequenceNo != 0) {
                        tempAttrSeq = attrSequenceNo;
                    }
                    origId = Utils.removeSpecialChars(origId);
                    ScrElementData elmObj = configObj.elms_map.get(origId);
                    if (elmObj != null) {
                        updateElmObj(elmObj, attr, DesignUtils.ATTRIBUTE, attrsGrp);
                    } else {
                        if (relationexists) {
                            relcntrId = cntrId + "_" + attrSequenceNo;
                            elmObj = Utils.getNewElement(attr, relcntrId, this, DesignUtils.ATTRIBUTE, attrsGrp);
                            relationexists = false;
                        } else {
                            elmObj = Utils.getNewElement(attr, relcntrId, this, DesignUtils.ATTRIBUTE, attrsGrp);
                        }
                    }
                    if (attr.has(ATTRIBUTE_REJECTED_VALUE)) {
                        elmObj.attributeRejectedValue = (ObjectNode) attr.get(ATTRIBUTE_REJECTED_VALUE);
                    }
                }
            }
        } else if (attrsNode.isObject()) {
            getElmObj(attrsGrp, (ObjectNode) attrsNode, cntrId);
        }
    }

    private void forMobjIntfDefinition(Integer[] noOfNodesList) {
        int strSize = objParserUtils.elms.size();
        objIntfDefinition.setElms(objParserUtils.elms.toArray(new String[strSize]));
        objIntfDefinition.seteExtName(objParserUtils.eExtName.toArray(new String[strSize]));
        objIntfDefinition.seteDataType(objParserUtils.eDataType.toArray(new String[strSize]));
        objIntfDefinition.seteMinVal(objParserUtils.eMinVal.toArray(new String[strSize]));
        objIntfDefinition.seteMaxVal(objParserUtils.eMaxVal.toArray(new String[strSize]));
        objIntfDefinition.seteMinLen(objParserUtils.eMinLen.toArray(new String[strSize]));
        objIntfDefinition.seteMaxLen(objParserUtils.eMaxLen.toArray(new String[strSize]));
        objIntfDefinition.seteMaxDec(objParserUtils.eMaxDec.toArray(new String[strSize]));
        objIntfDefinition.seteLenType(objParserUtils.eLenType.toArray(new String[strSize]));
        objIntfDefinition.seteArr(objParserUtils.eArr.toArray(new String[strSize]));
        objIntfDefinition.setePattern(objParserUtils.ePattern.toArray(new String[strSize]));
        objIntfDefinition.seteMand(objParserUtils.eMand.toArray(new String[strSize]));
        objIntfDefinition.seteRelNode(objParserUtils.eRelNode.toArray(new String[strSize]));
        objIntfDefinition.seteRelElm(objParserUtils.eRelElm.toArray(new String[strSize]));
        objIntfDefinition.setNoOfNodeElms(noOfNodesList);
    }

    private void forMobjParserUtils(Integer[] noOfNodesList) {
        noOfNodesList[1] = objParserUtils.relElms.size();
        objParserUtils.elms.addAll(objParserUtils.elms.size(), objParserUtils.relElms);
        objParserUtils.eExtName.addAll(objParserUtils.releExtName);
        objParserUtils.eDataType.addAll(objParserUtils.releDataType);
        objParserUtils.eMinVal.addAll(objParserUtils.releMinVal);
        objParserUtils.eMaxVal.addAll(objParserUtils.releMaxVal);
        objParserUtils.eMinLen.addAll(objParserUtils.releMinLen);
        objParserUtils.eMaxLen.addAll(objParserUtils.releMaxLen);
        objParserUtils.eMaxDec.addAll(objParserUtils.releMaxDec);
        objParserUtils.eLenType.addAll(objParserUtils.releLenType);
        objParserUtils.eArr.addAll(objParserUtils.releArr);
        objParserUtils.ePattern.addAll(objParserUtils.relePattern);
        objParserUtils.eMand.addAll(objParserUtils.releMand);
        objParserUtils.eRelNode.addAll(objParserUtils.releRelNode);
        objParserUtils.eRelElm.addAll(objParserUtils.releRelElm);
    }

    private void updateRelationsByNode(String group, ObjectNode object, JsonNode relationshipNode) throws Exception {
        if (relationshipNode.isArray()) {
            ArrayNode relationships = (ArrayNode) relationshipNode;
            for (int r = 0; r < relationships.size(); r++) {
                ObjectNode relationship = (ObjectNode) relationships.get(r);
                updateRelationsObj(relationship, group);
            }
        } else if (relationshipNode.isObject()) {
            ObjectNode relationship = (ObjectNode) object.get(DesignUtils.RELATIONSHIP);
            updateRelationsObj(relationship, group);
        }
    }

    private void getElmObj(String attrsGrp, ObjectNode attrsNode, String cntrId) {
        ObjectNode attr = attrsNode;
        String origId = attr.get(ORIG_ID).asText();
        origId = Utils.removeSpecialChars(origId);
        ScrElementData elmObj = configObj.elms_map.get(origId);
        if (elmObj != null) {
            updateElmObj(elmObj, attr, DesignUtils.ATTRIBUTE, attrsGrp);
        } else {
            elmObj = Utils.getNewElement(attr, cntrId, this, DesignUtils.ATTRIBUTE, attrsGrp);
        }
        if (attr.has(ATTRIBUTE_REJECTED_VALUE)) {
            elmObj.attributeRejectedValue = (ObjectNode) attr.get(ATTRIBUTE_REJECTED_VALUE);
        }
    }

    private void updateRelations(String group, ObjectNode object, JsonNode relationshipNode, int attrSequenceNo,
                                 int tempAttrSeq, int attrMinSeq) throws Exception {
        int realtionUISeqNum;
        if (relationshipNode.isArray()) {
            ArrayNode relationships = (ArrayNode) relationshipNode;
            for (int r = 0; r < relationships.size(); r++) {
                ObjectNode relationship = (ObjectNode) relationships.get(r);
                realtionUISeqNum = getUISeqValue(relationship, DesignUtils.RELATIONSHIP_DECORATION);
                if (realtionUISeqNum > attrMinSeq) {
                    break;
                }
                if (realtionUISeqNum < attrMinSeq) {
                    updateRelationsObj(relationship, group);
                }
            }
        } else if (relationshipNode.isObject()) {
            ObjectNode relationship = (ObjectNode) object.get(DesignUtils.RELATIONSHIP);
            realtionUISeqNum = getUISeqValue(relationship, DesignUtils.RELATIONSHIP_DECORATION);
            if (realtionUISeqNum > tempAttrSeq && realtionUISeqNum < attrSequenceNo) {
                updateRelationsObj(relationship, group);
            }
        }
    }

    private void applyObjectDecoration(JsonNode objDecoration) {
        if (objDecoration != null) {
            if (objDecoration.isArray()) {
                getUpdateObjectDecoration(objDecoration);
            } else if (objDecoration.isObject()) {
                ObjectNode decoration = (ObjectNode) objDecoration;
                String objDecorName = objDecoration.get("Name").asText();
                String objDecorValue = decoration.get(VALUE).asText();
                if (objDecorName.contains("DXP_" + Parser.prodShortName + LINKED_ITEM)
                        || objDecorName.contains("DX_" + Parser.prodShortName + LINKED_ITEM)
                        || objDecorName.equals("DXP_" + Parser.prodShortName + "_API")
                        || objDecorName.equals("DX_" + Parser.prodShortName + "_API")
                        || objDecorName.equals("DXP_" + Parser.prodShortName + API_OUTPUT_MAP)
                        || objDecorName.equals("DX_" + Parser.prodShortName + API_OUTPUT_MAP)) {
                    objDecorValue = updateObjectDecoration(objDecorName, objDecorValue);
                    decoration.put(VALUE, objDecorValue);
                }
                configObj.decorations = JsonNodeFactory.instance.arrayNode().add(objDecoration);
            }
        }
    }

    private void getUpdateObjectDecoration(JsonNode objDecoration) {
        for (int r = 0; r < objDecoration.size(); r++) {
            ObjectNode relateDomain = (ObjectNode) objDecoration.get(r);
            String objDecorName = relateDomain.get("Name").asText();
            String objDecorValue = relateDomain.get(VALUE).asText();
            if (objDecorName.contains("DXP_" + Parser.prodShortName + LINKED_ITEM)
                    || objDecorName.contains("DX_" + Parser.prodShortName + LINKED_ITEM)
                    || objDecorName.equals("DXP_" + Parser.prodShortName + "_API")
                    || objDecorName.equals("DX_" + Parser.prodShortName + "_API")
                    || objDecorName.equals("DXP_" + Parser.prodShortName + API_OUTPUT_MAP)
                    || objDecorName.equals("DX_" + Parser.prodShortName + API_OUTPUT_MAP)) {
                objDecorValue = updateObjectDecoration(objDecorName, objDecorValue);
                relateDomain.put(VALUE, objDecorValue);
            }
        }
        configObj.decorations = (ArrayNode) objDecoration;
    }

    private void updateContainerInfo(ScrContainerData cntr, ObjectNode object, boolean attrRelExists,
                                     ObjectNode parentRelationship) {
        if (cntr != null) {
            if (Utils.isNull(cntr.title)) {
                //// Is if required? for what condition?
                cntr.title = attrRelExists ? "" : Utils.getTitle(object);
            }
            if (Utils.isNull(cntr.externalname)) {
                cntr.externalname = getName(object);
            }
            if (object.has("DXP_" + Parser.prodShortName + UI_DESCP)) {
                cntr.uidescription = object.get("DXP_" + Parser.prodShortName + UI_DESCP).get(VALUE).asText();
            } else if (object.has("DX_" + Parser.prodShortName + UI_DESCP)) {
                cntr.uidescription = object.get("DX_" + Parser.prodShortName + UI_DESCP).get(VALUE).asText();
            }
            computeScrContainerDate(cntr, object, parentRelationship);
        }

    }

    private void computeScrContainerDate(ScrContainerData cntr, ObjectNode object, ObjectNode parentRelationship) {
        if (Utils.isNull(cntr.displayname) && object.has(DISPLAY_NAME)) {
            cntr.displayname = object.get(DISPLAY_NAME).asText();
        }
        if (cntr.widgettype.equals(TABLE) && parentRelationship != null) {
            computeScrContainerDateFilterValues(cntr, parentRelationship);
        }
    }

    private void computeScrContainerDateFilterValues(ScrContainerData cntr, ObjectNode parentRelationship) {
        if (parentRelationship.has("DXP_" + Parser.prodShortName + FILTER)) {
            String[] filterVal = parentRelationship.get("DXP_" + Parser.prodShortName + FILTER).get(VALUE).asText()
                    .split("\\|");
            cntr.filtercolumns = (filterVal.length > 0 && filterVal[0] != null) ? filterVal[0] : cntr.filtercolumns;
            cntr.filteronline = (filterVal.length > 1 && filterVal[1] != null) ? filterVal[1] : cntr.filteronline;
        } else if (parentRelationship.has("DX_" + Parser.prodShortName + FILTER)
                && !parentRelationship.has("DXP_" + Parser.prodShortName + FILTER)) {
            String[] filterVal = parentRelationship.get("DX_" + Parser.prodShortName + FILTER).get(VALUE).asText()
                    .split("\\|");
            cntr.filtercolumns = (filterVal.length > 0 && filterVal[0] != null) ? filterVal[0] : cntr.filtercolumns;
            cntr.filteronline = (filterVal.length > 1 && filterVal[1] != null) ? filterVal[1] : cntr.filteronline;
        }
    }

    private int getUISeqValue(ObjectNode attr, String attributeDecoration) {
        int uiSeq = 0;
        JsonNode dxpSeq = attr.get("DXP_" + Parser.prodShortName + UI_SEQ);
        JsonNode dxSeq = attr.get("DX_" + Parser.prodShortName + UI_SEQ);
        if (dxpSeq != null) {
            uiSeq = Integer.parseInt(dxpSeq.get(VALUE).asText());
        } else if (dxSeq != null) {
            uiSeq = Integer.parseInt(dxSeq.get(VALUE).asText());
        }
        return uiSeq;
    }

    public void updateElmObj(ScrElementData elmObj, ObjectNode attr, String subKey, String group) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside updateElmObj()");
        String attrFinalName = Utils.removeSpecialChars(attr.get(ORIG_ID).asText());
        String extName = attr.get("Name").asText();
        String origId = attr.get(ORIG_ID).asText();
        String localType = "String";
        String elmMaxStrLength = "";
        String id = "";
        if (attr.get("LocalType") != null) {
            localType = attr.get("LocalType").asText();
        }

        id = attr.has("Id") ? attr.get("Id").asText() : "";
        String elmDataType = "STRING";
        String elmMinVal = "";
        String elmMaxVal = "";
        String elmFmt = "";
        String extDatatype = "";
        String ipsPlaceHolder = "";
        String elmSym = "";
        String elmMaxLen = "";
        if (localType.equals("String") || localType.equals("Text")) {
            elmDataType = "STRING";
        } else if (localType.equals("Number") || localType.equals("Decimal") || localType.equals("Double")) { //// Removed
            //// integer
            //// from
            //// else
            //// if
            elmDataType = NUMBER;
        } else if (localType.equals("Integer")) { //// Adding else if condition for setting integer datatype
            elmDataType = INTEGER;
        } else if (localType.equals(DATE_TIME)) {
            elmDataType = "DATE";
        } else if (localType.equals("Date")) {
            elmDataType = "DATE";
        }
        if (Utils.isNull(elmObj.datatype)) {
            elmObj.datatype = elmDataType;
        }
        if (elmDataType.equals(NUMBER) || elmDataType.equals(INTEGER)) {
            if (attr.has("DXP_" + Parser.prodShortName + "_MIN")) {
                elmMinVal = attr.get("DXP_" + Parser.prodShortName + "_MIN").get(VALUE).asText();
            }
            if (attr.has("DXP_" + Parser.prodShortName + "_MAX")) {
                elmMaxVal = attr.get("DXP_" + Parser.prodShortName + "_MAX").get(VALUE).asText();
            }

            // runtime addition
            elmObj.runtimeamountformat = "N";
            elmObj.donotformat = "N";
            if (attr.has("DXP_" + Parser.prodShortName + "_FMT")) {
                elmFmt = attr.get("DXP_" + Parser.prodShortName + "_FMT").get(VALUE).asText();
                if (elmFmt.equals("N")) {
                    elmObj.runtimeamountformat = "Y";
                    elmObj.donotformat = "Y";
                }

            }
            if (attr.has("DXP_" + Parser.prodShortName + "_SYM")) {
                elmSym = attr.get("DXP_" + Parser.prodShortName + "_SYM").get(VALUE).asText();
                String[] symbolPst = elmSym.split("\\|");
                if (symbolPst.length > 0 && symbolPst[0] != null) {
                    elmObj.symbol = "Y";
                    elmObj.symbolvalue = symbolPst[0];
                }
                if (symbolPst.length > 1 && symbolPst[1] != null) {
                    elmObj.iconposition = symbolPst[1].toUpperCase();
                }
            }
            if (attr.has("DXP_" + Parser.prodShortName + "_ExternalType")) {
                extDatatype = attr.get("DXP_" + Parser.prodShortName + "_ExternalType").get(VALUE).asText();
                elmDataType = extDatatype.equalsIgnoreCase(INTEGER) ? INTEGER : NUMBER;
            }
        }
        int index = objParserUtils.elms.size();
        elmObj.interfacename = Utils.removeSpecialChars(uniqueProductId + INTF);
        elmObj.datamodeltype = "REQUESTDATAMODEL";
        elmObj.nodename = Utils.removeSpecialChars(uniqueProductId + INTF_REQ);
        elmObj.closeonselect = "Y";
        if ("RelationshipDomain".equals(subKey)) {
            index = objParserUtils.relElms.size();
            objParserUtils.relElms.add(index, attrFinalName);
            objParserUtils.releExtName.add(index, extName);
            objParserUtils.releDataType.add(index, elmDataType);
            objParserUtils.releMinVal.add(index, elmMinVal);
            objParserUtils.releMaxVal.add(index, elmMaxVal);
            objParserUtils.releMinLen.add(index, "");
            objParserUtils.releMaxDec.add(index, "");
            objParserUtils.releLenType.add(index, "F");
            objParserUtils.releArr.add(index, "");
            objParserUtils.relePattern.add(index, "");
            objParserUtils.releRelNode.add(index, "");
            objParserUtils.releRelElm.add(index, "");
            objParserUtils.releMand.add(index, "N");
            elmObj.nodename = uniqueProductId + "_Rel";
        }
        if (DesignUtils.ATTRIBUTE.equals(subKey)) {
            objParserUtils.elms.add(index, attrFinalName);
            objParserUtils.eExtName.add(index, extName);
            objParserUtils.eDataType.add(index, elmDataType);
            objParserUtils.eMinVal.add(index, elmMinVal);
            objParserUtils.eMaxVal.add(index, elmMaxVal);
            objParserUtils.eMinLen.add(index, "");
            objParserUtils.eMaxDec.add(index, "");
            objParserUtils.eLenType.add(index, "F");
            objParserUtils.eArr.add(index, "");
            objParserUtils.ePattern.add(index, "");
            objParserUtils.eRelNode.add(index, "");
            objParserUtils.eRelElm.add(index, "");
            objParserUtils.eMand.add(index, "N");
        }
        elmObj.elementname = Utils.removeSpecialChars(origId);

        elmObj.externalid = Utils.removeSpecialChars(id);

        elmObj.name = Utils.getElementId(elmObj, appId, scrId, -1);
        ScrContainerData cntrData = updateNodeInContainer(elmObj, group);
        elmObj.title = Utils.getTitle(attr);
        if (cntrData != null) {
            if (!(cntrData.widgettype.equals(TABLE) || cntrData.widgettype.equals("LIST"))
                    && (DesignUtils.ATTRIBUTE.equals(subKey))) {
                if (parentTitle.equals("Y")) {
                    elmObj.title = parentProductId.isEmpty() ? Utils.getTitle(attr)
                            : productName + " : " + Utils.getTitle(attr);
                } else {
                    elmObj.title = Utils.getTitle(attr);
                }
            }
        }
        if (attr.has(DISPLAY_NAME)) {
            elmObj.displayname = attr.get(DISPLAY_NAME).asText();
        }
        if (Utils.isNull(elmObj.title)) {
            elmObj.labelwidth = "0";
        }

        nameMapper.put(uniqueProductId + "__" + extName, elmObj);
        origIdMapper.put(uniqueProductId + "__" + origId, elmObj);
        elmObj.basicrulesetpresent = "N";
        List<String> validValues = new ArrayList<String>();
        List<String> rejectedValues = new ArrayList<String>();
        if (attr.has(subKey + "Decoration")) {
            JsonNode attrDecorations = attr.get(subKey + "Decoration");
            if (attrDecorations.isArray()) {
                ArrayNode decorations = (ArrayNode) attrDecorations;
                for (int d = 0; d < decorations.size(); d++) {
                    ObjectNode decor = (ObjectNode) decorations.get(d);
                    applyElmDecorations(elmObj, decor, index, validValues, rejectedValues, attr, subKey);
                }
            } else if (attrDecorations.isObject()) {
                ObjectNode decor = (ObjectNode) attrDecorations;
                applyElmDecorations(elmObj, decor, index, validValues, rejectedValues, attr, subKey);
            }
            String decorFunction = "apz.products.processRule(this, event);";
            if (DesignUtils.RELATIONSHIP_DOMAIN.equals(subKey)) {
                String subProdId = attr.get(DesignUtils.ID).asText();
                subProdId = Utils.removeSpecialChars(subProdId);
                String orgProdId = Utils.removeSpecialChars(originalProductId);
//				ObjectNode launchSubProductJson=productsMapper.get(subProdId);				
//				if(launchSubProductJson.has("hasDomain")){
//					if(launchSubProductJson.get("hasDomain").asText().equals("Y")){
                decorFunction = decorFunction + "apz.products.launchSubProduct('" + orgProdId + "_" + relationshipId
                        + "_" + subProdId + "', this, event, '', '" + orgProdId + "_" + relationshipId + "');";
                elmObj.combinedproductid = orgProdId + "_" + relationshipId + "_" + subProdId;
//					}
//				}

            }
            boolean evntRecorded = false;
            boolean searcheventRecorded = false;
            boolean customeventRecorded = false;
            String searchEventFunction = "";
            String searchEventType = "";
            String customSearchType = "";
            String customEventFunction = "";
            String widgetTyp = elmObj.widgettype, eventTyp = "ONCLICK";
            if (widgetTyp.equals(CHECKBOX) || widgetTyp.equals("DROPDOWN") || widgetTyp.equals("RADIO")) {
                eventTyp = "ONCHANGE";
            } else if (widgetTyp.equals(INPUTBOX) || widgetTyp.equals(TEXTAREA) || widgetTyp.equals(INPUTWITHBUTTON)) {
                if (elmObj.externalwidgettype.equals("IPS")) {

                    if (attr.has("DXP_" + Parser.prodShortName + "_PlaceHolder")) {
                        ipsPlaceHolder = attr.get("DXP_" + Parser.prodShortName + "_PlaceHolder").get(VALUE).asText();
                        elmObj.placeholder = ipsPlaceHolder;
                    }

                    searchEventType = "ONKEYUP";
                    searchEventFunction = "apz.products.elasticSearchLov(this, event);";
                    customSearchType = "ONCHANGE";
                    customEventFunction = "apz.products.customSearchFunction(this,event);";
                } else {
                    eventTyp = "ONBLUR";
                }
            }
            if (widgetTyp.equals("FILEBROWSER")) {
                if (elmObj.uploadbuttonrequired.isEmpty()) {
                    elmObj.uploadbuttonrequired = "Y";
                }
                if (elmObj.browsefiletype.isEmpty()) {
                    elmObj.browsefiletype = "SINGLEFILE";
                }
            }
            if (elmObj.events.size() > 0) {
                for (int e = 0; e < elmObj.events.size(); e++) {
                    EventData evnt = elmObj.events.get(e);
                    if (evnt.name.equals(eventTyp)) {
                        evntRecorded = true;
                        String previousEvnt = "";
                        if (!Utils.isNull(evnt.function)) {
                            previousEvnt = evnt.function;
                        }
                        evnt.function = decorFunction + previousEvnt;
                    }
                    if (evnt.name.equals(searchEventType)) {
                        searcheventRecorded = true;
                        String searchPrevEvnt = "";
                        if (!Utils.isNull(evnt.function)) {
                            searchPrevEvnt = evnt.function;
                        }
                        evnt.function = searchEventFunction + searchPrevEvnt;
                    }
                    if (evnt.name.equals(customSearchType)) {
                        customeventRecorded = true;
                        String customsearchPrevEvnt = "";
                        if (!Utils.isNull(evnt.function)) {
                            customsearchPrevEvnt = evnt.function;
                        }
                        evnt.function = customEventFunction + customsearchPrevEvnt;
                    }
                }
            }
            if (!evntRecorded) {
                EventData evnt = new EventData(eventTyp, decorFunction);
                elmObj.events.add(evnt);
            }
            if (!searcheventRecorded) {
                EventData evnt = new EventData(searchEventType, searchEventFunction);
                elmObj.events.add(evnt);
            }
            if (!customeventRecorded) {
                EventData evnt = new EventData(customSearchType, customEventFunction);
                elmObj.events.add(evnt);
            }
        }
        if (elmObj.datatype.equals(NUMBER) || elmObj.datatype.equals(INTEGER)) {
            elmMaxLen = "18";
            elmObj.datatype = elmDataType;
        }
        if ("RelationshipDomain".equals(subKey)) {
            objParserUtils.releMaxLen.add(index, elmMaxLen);
        }
        if (DesignUtils.ATTRIBUTE.equals(subKey)) {
            objParserUtils.eMaxLen.add(index, elmMaxLen);
        }
        if (!(attr.has("DXP_" + Parser.prodShortName + BASIC_RULE_SET)
                || (attr.has("DX_" + Parser.prodShortName + BASIC_RULE_SET)))) {
            elmObj.widgettype = INPUTBOX;
            elmObj.options = "N";
        }
        if (attr.has("DXP_" + Parser.prodShortName + "_MaxLength")
                && (elmObj.widgettype.equals(INPUTBOX) || elmObj.widgettype.equals(TEXTAREA))) {
            elmMaxStrLength = attr.get("DXP_" + Parser.prodShortName + "_MaxLength").get(VALUE).asText();
            elmObj.maxstringlength = elmMaxStrLength;
        }
        ScrElementData titleElmObj = configObj.elms_map.get(elmObj.elementname + "_title");
        if (titleElmObj != null) {
            titleElmObj.options = elmObj.options;
            titleElmObj.mandatory = elmObj.mandatory;
        }
        if (attr.has(subKey + "Domain")) {
            JsonNode attrdomains = attr.get(subKey + "Domain");
            if (attrdomains.isArray()) {
                ArrayNode arryDomain = (ArrayNode) attrdomains;
                if (arryDomain.size() > 0) {
                    elmObj.staticoptions.clear();
                }
                for (int d = 0; d < arryDomain.size(); d++) {
                    ObjectNode domain = (ObjectNode) arryDomain.get(d);
                    prepareStaticOptions(elmObj, domain, validValues, rejectedValues);
                }
            } else if (attrdomains.isObject()) {
                ObjectNode domain = (ObjectNode) attrdomains;
                if (attrdomains != null) {
                    elmObj.staticoptions.clear();
                }
                if (!elmObj.widgettype.equals(CHECKBOX)) {
                    prepareStaticOptions(elmObj, domain, validValues, rejectedValues);
                }
            }
        }

    }

    private void prepareStaticOptions(ScrElementData elmObj, ObjectNode domain, List<String> validValues,
                                      List<String> rejectedValues) {
        ElementStaticOptions option = null;
        option = getElementStaticOptions(domain, validValues, rejectedValues, option);
        if (option != null) {
            if (elmObj.widgettype.equals(CHECKBOX)) {
                if (!(option.description.equals(PLEASE_SELECT) || option.description.equals("Please select"))) {
                    elmObj.staticoptions.add(option);
                }
            } else {
                elmObj.staticoptions.add(option);
            }
        }

    }

    private ElementStaticOptions getElementStaticOptions(ObjectNode domain, List<String> validValues,
                                                         List<String> rejectedValues, ElementStaticOptions option) {
        if (validValues.size() > 0 && validValues.contains(domain.get(LOCAL_VARIABLE).asText())) {
            String decorDesc = domain.get(LOCAL_VARIABLE).asText();
            String decorValue = domain.get(LOCAL_VARIABLE).asText().equalsIgnoreCase(PLEASE_SELECT) ? "PS"
                    : domain.get(LOCAL_VARIABLE).asText();
            option = new ElementStaticOptions(decorValue, decorDesc, ENABLED, "", "");
        } else if (rejectedValues.size() > 0
                && (rejectedValues.size() == 0 || !rejectedValues.contains(domain.get(LOCAL_VARIABLE).asText()))) {
            String decorDesc = domain.get(LOCAL_VARIABLE).asText();
            String decorValue = domain.get(LOCAL_VARIABLE).asText().equalsIgnoreCase(PLEASE_SELECT) ? "PS"
                    : domain.get(LOCAL_VARIABLE).asText();
            option = new ElementStaticOptions(decorValue, decorDesc, ENABLED, "", "");
        }
        return option;
    }

    void updateElmDecorations(Object obj, JsonNode objDecoration) {
        ScrElementData elmObj = null;
        ScrContainerData cntObj = null;
        String type = SCR_ELEMENT_DATA;
        if (obj instanceof ScrElementData) {
            elmObj = (ScrElementData) obj;
        } else if (obj instanceof ScrContainerData) {
            type = SCR_CONTAINER_DATA;
            cntObj = (ScrContainerData) obj;
        }
        if (SCR_ELEMENT_DATA.equals(type) && elmObj != null) {
            for (int d = 0; d < elmObj.decorations.size(); d++) {
                Decoration decorData = elmObj.decorations.get(d);
                updateDecoration(decorData, obj);
            }
        } else if (SCR_CONTAINER_DATA.equals(type) && cntObj != null) {
            computeDecoration(obj, objDecoration, cntObj);
        }
    }

    private void computeDecoration(Object obj, JsonNode objDecoration, ScrContainerData cntObj) {
        if (cntObj.decorations.size() > 0) {
            for (int d = 0; d < cntObj.decorations.size(); d++) {
                Decoration decorData = cntObj.decorations.get(d);
                updateDecoration(decorData, obj);
            }
        } else if (objDecoration != null) {
            applyObjectDecoration(objDecoration);
            if (configObj.decorations.size() > 0) {
                if (configObj.decorations.isArray()) {
                    ArrayNode node = (ArrayNode) configObj.decorations;
                    for (int r = 0; r < node.size(); r++) {
                        ObjectNode objDecorationNode = (ObjectNode) node.get(r);
                        Decoration decoration = new Decoration();
                        decoration.value = objDecorationNode.get(VALUE).asText();
                        decoration.name = objDecorationNode.get("Name").asText();
                        cntObj.decorations.add(decoration);
                    }
                }
            }
        }
    }

    private void updateDecoration(Decoration decorData, Object obj) {
        String decorName = decorData.name;
        String decorValue = decorData.value;
        if (!(decorName.equals("DXP_" + Parser.prodShortName + BASIC_RULE_SET)
                || decorName.equals("DX_" + Parser.prodShortName + BASIC_RULE_SET)
                || decorName.equals("DXP_" + Parser.prodShortName + "_Cardinality")
                || decorName.equals("DX_" + Parser.prodShortName + "_Cardinality")
                || decorName.equals("DXP_" + Parser.prodShortName + "_UIName")
                || decorName.equals("DX_" + Parser.prodShortName + "_UIName")
                || decorName.equals("DXP_" + Parser.prodShortName + UI_SEQ)
                || decorName.equals("DX_" + Parser.prodShortName + UI_SEQ)
                || decorName.equals("DXP_" + Parser.prodShortName + "_DispName")
                || decorName.equals("DX_" + Parser.prodShortName + "_DispName")
                || decorName.equals("DXP_" + Parser.prodShortName + REFER_ID)
                || decorName.equals("DX_" + Parser.prodShortName + REFER_ID)
                || decorName.equals("DXP_" + Parser.prodShortName + BIP_HIPER)
                || decorName.equals("DX_" + Parser.prodShortName + BIP_HIPER)
                || decorName.equals("DXP_" + Parser.prodShortName + RE_APPROVAL)
                || decorName.equals("DX_" + Parser.prodShortName + RE_APPROVAL))) {
            /// Below fix to address sequence number in rule name
            String trimedDecorName = decorName.split(" ")[0];
            if (decorName.equals("DXP_" + Parser.prodShortName + "_Show")
                    || decorName.equals("DX_" + Parser.prodShortName + "_Show")
                    || decorName.equals("DXP_" + Parser.prodShortName + "_Hide")
                    || decorName.equals("DX_" + Parser.prodShortName + "_Hide")) {
                String prefixStr = "";
                if (decorName.startsWith("DX_") && decorValue.contains(":")) {
                    int index = decorValue.indexOf(":");
                    prefixStr = decorValue.substring(0, index);
                    decorValue = decorValue.substring(index);
                }
                decorData.value = prefixStr + Utils.updateStrWithIds(obj, decorName, decorValue, this, false);
            } else if (decorName.equals("DXP_" + Parser.prodShortName + UI_DESCP)
                    || decorName.equals("DX_" + Parser.prodShortName + UI_DESCP)) {
                String prefixStr = "";
                int index = decorValue.indexOf("|");
                if (index > 0) {
                    prefixStr = decorValue.substring(0, index + 1);
                    decorValue = decorValue.substring(index + 1);
                }
                decorData.value = prefixStr + Utils.updateStrWithIds(obj, decorName, decorValue, this, false);
            } else if (trimedDecorName.equals("DXP_" + Parser.prodShortName + "_SetValue")
                    || trimedDecorName.equals("DX_" + Parser.prodShortName + "_SetValue")) {
                boolean ignoreRltdElms = false;
                if (trimedDecorName.startsWith("DXP_")) {
                    ignoreRltdElms = true;
                }
                decorData.value = Utils.updateStrWithIds(obj, decorName, decorValue, this, ignoreRltdElms);
            } else if (trimedDecorName.equals("DXP_" + Parser.prodShortName + "_API")
                    || trimedDecorName.equals("DX_" + Parser.prodShortName + "_API")
                    || trimedDecorName.equals("DXP_" + Parser.prodShortName + API_OUTPUT_MAP)
                    || trimedDecorName.equals("DX_" + Parser.prodShortName + API_OUTPUT_MAP)) {
                boolean ignoreRltdElms = false;
                if (trimedDecorName.startsWith("DXP_")) {
                    ignoreRltdElms = true;
                }
                decorData.value = Utils.updateStrWithIds(obj, decorName, decorValue, this, ignoreRltdElms);
            } else if (decorName.startsWith("DXP_" + Parser.prodShortName + LINKED_ITEM)
                    || decorName.startsWith("DX_" + Parser.prodShortName + LINKED_ITEM)) {
                decorData.value = updateObjectDecoration(decorName, decorValue);
            } else if (decorName.startsWith("DXP_" + Parser.prodShortName)
                    || decorName.startsWith("DX_" + Parser.prodShortName)) {
                decorData.value = Utils.updateStrWithIds(obj, decorName, decorValue, this, false);
            }
        }

    }

    String updateObjectDecoration(String decorName, String decorValue) {
        String[] splitted = Utils.getOperatorsSplitStr(decorValue, decorName);
        int length = splitted.length;
        String trimedDecorName = decorName.split(" ")[0];
        String[] result = new String[length];
        for (int a = 0; a < length; a++) {
            String fragment = splitted[a];
            String trimedFragmnt = fragment.trim();
            if (trimedFragmnt.length() > 2) {
                if (trimedDecorName.endsWith("_API")) {
                    if (a > 0 && splitted[a - 1].trim().endsWith("&")) {
                        result[a] = fragment.replace(trimedFragmnt,
                                Utils.getObjectGlobalVariables(trimedFragmnt, this));
                    } else {
                        result[a] = splitted[a];
                    }
                } else {
                    result[a] = splitted[a];
                }
                if (trimedDecorName.endsWith(LINKED_ITEM)) {
                    if (a > 0 && splitted[a - 1].trim().equals("&")) {
                        result[a] = fragment.replace(trimedFragmnt,
                                Utils.getObjectGlobalVariables(trimedFragmnt, this));
                    } else {
                        result[a] = splitted[a];
                    }
                    if (a == 2) {
                        result[a] = fragment.replace(trimedFragmnt,
                                Utils.getObjectGlobalVariables(trimedFragmnt, this));
                    }
                }
                if (trimedDecorName.contains(API_OUTPUT_MAP)) {
                    if (a > 0 && splitted[a - 1].equals(",")) {
                        result[a] = splitted[a];
                    } else {
                        result[a] = fragment.replace(trimedFragmnt,
                                Utils.getObjectGlobalVariables(trimedFragmnt, this));
                    }
                }

            } else if (trimedFragmnt.equals("&") && result.length > a) {
                String previousStr = splitted[a - 1];
                String nextStr = splitted[a + 1];
                if (splitted[a - 1].endsWith(" ") && splitted[a + 1].startsWith(" ")) {
                    String temp = previousStr + splitted[a] + nextStr;
                    result[a - 1] = fragment.replace(trimedFragmnt, Utils.getObjectGlobalVariables(temp.trim(), this));
                    result[a] = result[a + 1] = "";
                    a++;
                } else {
                    result[a] = splitted[a];
                }
            } else {
                result[a] = splitted[a];
            }
        }
        decorValue = StringUtils.join(result, "");
        return decorValue;
    }

    void applyElmDecorations(Object obj, ObjectNode decor, int index, List<String> validValues,
                             List<String> rejectedValues, ObjectNode attrObj, String subkey) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside applyElmDecorations()");
        String decorName = decor.get("Name").asText();
        String decorValue = decor.get(VALUE).asText();
        String updatedDecorVal = decorValue;
        ScrElementData elmObj = null;
        ScrContainerData cntObj = null;
        String type = SCR_ELEMENT_DATA;
        if (obj instanceof ScrElementData) {
            elmObj = (ScrElementData) obj;
        } else if (obj instanceof ScrContainerData) {
            type = SCR_CONTAINER_DATA;
            cntObj = (ScrContainerData) obj;
        }
        if (SCR_ELEMENT_DATA.equals(type)) {
            if (decorName.equals("DXP_" + Parser.prodShortName + BASIC_RULE_SET)) {
                dxpRuleSetupdate(elmObj, index, validValues, rejectedValues, decorValue, subkey);
                elmObj.basicrulesetpresent = "Y";
            } else if (decorName.equals("DX_" + Parser.prodShortName + BASIC_RULE_SET)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + BASIC_RULE_SET)) {
                    dXRuleSetupdate(elmObj, index, decorValue, subkey);
                    elmObj.basicrulesetpresent = "Y";
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + REFER_ID)) {
                elmObj.referId = decorValue.equals("Y") ? true : false;
            } else if (decorName.equals("DX_" + Parser.prodShortName + REFER_ID)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + REFER_ID)) {
                    elmObj.referId = decorValue.equals("Y") ? true : false;
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + UI_DESCP)) {
                elmObj.uidescription = decorValue;
            } else if (decorName.equals("DX_" + Parser.prodShortName + UI_DESCP)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + UI_DESCP)) {
                    elmObj.uidescription = decorValue;
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + BIP_HIPER)) {
                elmObj.biphier = decorValue;
            } else if (decorName.equals("DX_" + Parser.prodShortName + BIP_HIPER)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + BIP_HIPER)) {
                    elmObj.biphier = decorValue;
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + RE_APPROVAL)) {
                elmObj.reapproval = decorValue;
            } else if (decorName.equals("DX_" + Parser.prodShortName + RE_APPROVAL)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + RE_APPROVAL)) {
                    elmObj.reapproval = decorValue;
                }
            }
            if (decorName.endsWith("_CSSBasicRuleSet")) {// Added on 09-oct-2020
                dxpCSSRuleSetupdate(elmObj, decorValue, decorName);
            }
        } else if (SCR_CONTAINER_DATA.equals(type)) {
            if (decorName.equals("DXP_" + Parser.prodShortName + BASIC_RULE_SET)) {
                dxpRuleSetupdate(cntObj, index, validValues, rejectedValues, decorValue, subkey);

            } else if (decorName.equals("DX_" + Parser.prodShortName + BASIC_RULE_SET)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + BASIC_RULE_SET)) {
                    dXRuleSetupdate(cntObj, index, decorValue, subkey);
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + REFER_ID)) {
                cntObj.referId = decorValue.equals("Y") ? true : false;
            } else if (decorName.equals("DX_" + Parser.prodShortName + REFER_ID)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + REFER_ID)) {
                    cntObj.referId = decorValue.equals("Y") ? true : false;
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + UI_DESCP)) {
                cntObj.uidescription = decorValue;
            } else if (decorName.equals("DX_" + Parser.prodShortName + UI_DESCP)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + UI_DESCP)) {
                    cntObj.uidescription = decorValue;
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + BIP_HIPER)) {
                cntObj.biphier = decorValue;
            } else if (decorName.equals("DX_" + Parser.prodShortName + BIP_HIPER)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + BIP_HIPER)) {
                    cntObj.biphier = decorValue;
                }
            }
            if (decorName.equals("DXP_" + Parser.prodShortName + RE_APPROVAL)) {
                cntObj.reapproval = decorValue;
            } else if (decorName.equals("DX_" + Parser.prodShortName + RE_APPROVAL)) {
                if (!attrObj.has("DXP_" + Parser.prodShortName + RE_APPROVAL)) {
                    cntObj.reapproval = decorValue;
                }
            }
            if (decorName.endsWith("_CSSBasicRuleSet")) {
                dxpCSSRuleSetupdate(cntObj, decorValue, decorName);// Added on 09-oct-2020
            }
        }
        Decoration decorData = new Decoration(decorName, updatedDecorVal);
        if (SCR_ELEMENT_DATA.equals(type)) {
            elmObj.decorations.add(decorData);
        }
        if (SCR_CONTAINER_DATA.equals(type) && cntObj != null) {
            cntObj.decorations.add(decorData);
        }
    }

    /*
     * Written for parsing css grammmar rules on 09-oct-2020
     */
    private void dxpCSSRuleSetupdate(Object obj, String decorValue, String decorName) {
        ScrElementData elmObj = null;
        ScrContainerData cntObj = null;
        String[] decorValArr = decorValue.split("\\|");
        String elementType = decorName.split("_")[2];
        String type = SCR_ELEMENT_DATA;
        if (obj instanceof ScrElementData) {
            elmObj = (ScrElementData) obj;
        } else if (obj instanceof ScrContainerData) {
            type = SCR_CONTAINER_DATA;
            cntObj = (ScrContainerData) obj;
        }
        // Parsing elements
        if (SCR_ELEMENT_DATA.equals(type)) {
            if (elementType.equals("Button")) {
                // 1st Button
                // Variation|Appearance|State|Button-as-icon|Hint|Semantics|Effects|Menu|Button-size|Icon|Icon-position|Icon-size|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.buttonasicon = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.hint = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.semantics = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.effects = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : ""; // not present
                // add
                // effects
                elmObj.menu = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
                elmObj.size = decorValArr.length > 8 && decorValArr[8] != null ? decorValArr[8] : "";
                elmObj.icon = decorValArr.length > 9 && decorValArr[9] != null ? decorValArr[9] : "";
                elmObj.iconposition = decorValArr.length > 10 && decorValArr[10] != null ? decorValArr[10] : "";
                elmObj.iconsize = decorValArr.length > 11 && decorValArr[11] != null ? decorValArr[11] : "";
                elmObj.cssclasses = decorValArr.length > 12 && decorValArr[12] != null ? decorValArr[12] : "";
            } else if (elementType.equals("Checkbox")) {
                // 2nd Checkbox
                // Variation|Appearance|State|Checkbox-type|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.checkboxtype = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.hint = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.cssclasses = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
            } else if (elementType.equals("CheckboxGroup")) {
                // 3rd CheckboxGroup
                // Variation|Appearance|Checkbox-type|Orientation|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.checkboxtype = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.orientation = decorValArr.length > 3 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.hint = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.cssclasses = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
            } else if (elementType.equals("DDN")) {
                // 4th DDN
                // Variation|Appearance|State|dropdown-type|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.dropdowntype = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.hint = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.cssclasses = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
            } else if (elementType.equals("File")) {
                // 5th File
                // Variation|Appearance|State|File-type|Hint|Upload-button-required|Button-click-function|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                // for File-type used browsefiletype
                elmObj.browsefiletype = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.hint = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.uploadbuttonrequired = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.buttonclickevent = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                elmObj.cssclasses = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
            } else if (elementType.equals("Hyperlink")) {
                // 6th Hyperlink
                // Variation|Appearance|State|Icon|Icon-position|Menu|URL|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.icon = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.iconposition = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.menu = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.url = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                elmObj.hint = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
                elmObj.cssclasses = decorValArr.length > 8 && decorValArr[8] != null ? decorValArr[8] : "";
            } else if (elementType.equals("Icon")) {
                // 7th Icon
                // Variation|Appearance|Semantics|Icon|Size|Menu|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.semantics = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.icon = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.size = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.menu = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.hint = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                elmObj.cssclasses = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
            } else if (elementType.equals("Image")) {
                // 8th Image
                // Variation|Appearance|Default-image|Size|Menu|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.defaultimage = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.size = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.menu = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.hint = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.cssclasses = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
            } else if (elementType.equals("Input")) {
                // 9th Input
                // Variation|Appearance|State|Input-type|Password|Email|Symbol|Icon|Icon-alignment|Icon-click-function|Content-alignment|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.inputtype = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.password = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.email = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.symbol = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                elmObj.icon = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
                // for Icon-alignment using iconposition of appzillon
                elmObj.iconposition = decorValArr.length > 8 && decorValArr[8] != null ? decorValArr[8] : "";
                // for Icon-click-function using labeliconfunction
                elmObj.iconevent = decorValArr.length > 9 && decorValArr[9] != null ? decorValArr[9] : "";
                elmObj.contentalignment = decorValArr.length > 10 && decorValArr[10] != null ? decorValArr[10] : "";
                elmObj.hint = decorValArr.length > 11 && decorValArr[11] != null ? decorValArr[11] : "";
                elmObj.cssclasses = decorValArr.length > 12 && decorValArr[12] != null ? decorValArr[12] : "";
            } else if (elementType.equals("InputWithButton")) {
                // Variation|Appearance|Button-title|Password|Email|State
                // |Content-alignment|Button-click-function|Hint|Icon|Icon-position|Button-position|Semantics|CSS-class

                // 10th InputWithButton
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.buttontitle = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.password = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.email = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.state = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.contentalignment = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                // for Icon-click-function using labeliconfunction
                elmObj.iconevent = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
                elmObj.hint = decorValArr.length > 8 && decorValArr[8] != null ? decorValArr[8] : "";
                elmObj.icon = decorValArr.length > 9 && decorValArr[9] != null ? decorValArr[9] : "";
                // for Icon-alignment using iconposition of appzillon
                elmObj.iconposition = decorValArr.length > 10 && decorValArr[10] != null ? decorValArr[10] : "";
                elmObj.buttonposition = decorValArr.length > 11 && decorValArr[11] != null ? decorValArr[11] : "";
                elmObj.semantics = decorValArr.length > 12 && decorValArr[12] != null ? decorValArr[12] : "";
                elmObj.cssclasses = decorValArr.length > 13 && decorValArr[13] != null ? decorValArr[13] : "";
            } else if (elementType.equals("Label")) {
                // 11th Label
                // Variation|Appearance|Semantics|Mandatory|Icon|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.semantics = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.mandatory = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.icon = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.hint = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                elmObj.cssclasses = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
            } else if (elementType.equals("Radio")) {
                // 12th Radio
                // Variation|Appearance|Radio-type|Orientation|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.radiotype = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.orientation = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.hint = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.cssclasses = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
            } else if (elementType.equals("Text")) {
                // 13th Text
                // Variation|Appearance|Element-type|Semantics|Symbol|Symbol-value|Icon|Icon-alignment|Menu|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.elementtype = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.semantics = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.symbol = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.symbolvalue = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.icon = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                // for Icon-alignment using iconposition of appzillon
                elmObj.iconposition = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
                elmObj.menu = decorValArr.length > 8 && decorValArr[8] != null ? decorValArr[8] : "";
                elmObj.hint = decorValArr.length > 9 && decorValArr[9] != null ? decorValArr[9] : "";
                elmObj.cssclasses = decorValArr.length > 10 && decorValArr[10] != null ? decorValArr[10] : "";
            } else if (elementType.equals("Textarea")) {
                // 14th Textarea
                // Variation|Appearance|State|Custom-height|Content-alignment|Hint|CSS-class
                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                elmObj.customheight = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                elmObj.contentalignment = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                elmObj.hint = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.cssclasses = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
            } else if (elementType.equals("Toggle")) {
                // 15th Toggle
                // Variation|Appearance|State|Label1|Label2|Toggle-type|Hint|CSS-class

                elmObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                elmObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                elmObj.state = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                // Label1 and Label2 are not present
                elmObj.switchoption1 = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "Label1";
                elmObj.switchoption2 = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "Label2";
                elmObj.toggleswitchtype = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                elmObj.hint = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                elmObj.cssclasses = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
            }

        } else if (SCR_CONTAINER_DATA.equals(type)) { // Parsing containers

            if (elementType.equals("Form")) {
                // 1st Form
                // Variation|Appearance|State|Orientation|CSS-class
                cntObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                cntObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                cntObj.containerstate = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                cntObj.orientation = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                cntObj.cssclasses = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
            } else if (elementType.equals("List")) {
                // 2nd List
                // Variation|Appearance|State|CSS-class
                cntObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                cntObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                cntObj.containerstate = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                cntObj.cssclasses = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
            } else if (elementType.equals("Navbar")) {
                // 3rd Navbar
                // Variation|Appearance|CSS-class
                cntObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                cntObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                cntObj.cssclasses = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
            } else if (elementType.equals("Table")) {
                // 4th Table
                // Variation|Appearance|State|Native|Table-height|Row-selector|Hover-effect|Sortable|Searchable|Filter-Columns|Animation-class|CSS-class
                cntObj.variation = decorValArr.length > 0 && decorValArr[0] != null ? decorValArr[0] : "";
                cntObj.appearance = decorValArr.length > 1 && decorValArr[1] != null ? decorValArr[1] : "";
                cntObj.containerstate = decorValArr.length > 2 && decorValArr[2] != null ? decorValArr[2] : "";
                cntObj.nativetable = decorValArr.length > 3 && decorValArr[3] != null ? decorValArr[3] : "";
                cntObj.tableheight = decorValArr.length > 4 && decorValArr[4] != null ? decorValArr[4] : "";
                cntObj.rowselectorrequired = decorValArr.length > 5 && decorValArr[5] != null ? decorValArr[5] : "";
                cntObj.hovereffect = decorValArr.length > 6 && decorValArr[6] != null ? decorValArr[6] : "";
                cntObj.sortable = decorValArr.length > 7 && decorValArr[7] != null ? decorValArr[7] : "";
                cntObj.searchable = decorValArr.length > 8 && decorValArr[8] != null ? decorValArr[8] : "";
                cntObj.filtercolumns = decorValArr.length > 9 && decorValArr[9] != null ? decorValArr[9] : "";
                cntObj.animation = decorValArr.length > 10 && decorValArr[10] != null ? decorValArr[10] : "";
                cntObj.cssclasses = decorValArr.length > 11 && decorValArr[11] != null ? decorValArr[11] : "";
            }
        }

    }

    private void dXRuleSetupdate(Object obj, int index, String decorValue, String subkey) {

        ScrElementData elmObj = null;
        ScrContainerData cntObj = null;
        String type = SCR_ELEMENT_DATA;
        if (obj instanceof ScrElementData) {
            elmObj = (ScrElementData) obj;
        } else {
            type = SCR_CONTAINER_DATA;
            cntObj = (ScrContainerData) obj;
        }
        if (SCR_ELEMENT_DATA.equals(type)) {
            getScrElementData(index, decorValue, subkey, elmObj);
        } else if (decorValue.contains(VISIBLE)) {
            String visibilityStrtStr = decorValue.substring(decorValue.indexOf(VISIBLE)).replace(VISIBLE, "");
            String visibility = visibilityStrtStr.substring(visibilityStrtStr.indexOf("=") + 1).trim();
            if (visibility.equals("N")) {
                cntObj.options = "N";
            } else {
                cntObj.options = "Y";
            }
        }
    }

    private void getScrElementData(int index, String decorValue, String subkey, ScrElementData elmObj) {
        if (decorValue.contains("Man") || decorValue.contains("CMan")) {
            elmObj.mandatory = "Y";
            if (DesignUtils.RELATIONSHIP_DOMAIN.equals(subkey)) {
                objParserUtils.releMand.remove(index);
                objParserUtils.releMand.add(index, "Y");
            }
            if (DesignUtils.ATTRIBUTE.equals(subkey)) {
                objParserUtils.eMand.remove(index);
                objParserUtils.eMand.add(index, "Y");
            }
        } else {
            elmObj.mandatory = "N";
        }
        updateScrElementDataVisibiltyDefV(decorValue, elmObj);
        String elementName = decorValue.substring(0, decorValue.indexOf("|"));
        if (Utils.isNull(elmObj.widgettype)) {
            updateWidgetStaticOptions(elmObj, elementName);
        } else if (elmObj.widgettype.equals(INPUTBOX) || elmObj.widgettype.equals(INPUTWITHBUTTON)) {
            elmObj.externalwidgettype = elementName;
        }
    }

    private void updateScrElementDataVisibiltyDefV(String decorValue, ScrElementData elmObj) {
        if (decorValue.contains(DEF_V)) {
            int defValIndex = decorValue.indexOf(DEF_V);
            String defaultValStrtStr = decorValue.substring(defValIndex).replace(DEF_V, "");
            if (defaultValStrtStr.contains("|")) {
                defaultValStrtStr = defaultValStrtStr.substring(0, defaultValStrtStr.indexOf("|"));
            }
            if (!defaultValStrtStr.contains("API_Get")) {
                elmObj.defaultvalue = defaultValStrtStr;
            }
        }
        if (decorValue.contains(VISIBLE)) {
            String visibilityStrtStr = decorValue.substring(decorValue.indexOf(VISIBLE)).replace(VISIBLE, "");
            String visibility = visibilityStrtStr.substring(visibilityStrtStr.indexOf("=") + 1).trim();
            if (visibility.equals("N")) {
                elmObj.options = "N";
            } else {
                elmObj.options = "Y";
            }
        }
    }

    private void updateWidgetStaticOptions(ScrElementData elmObj, String elementName) {
        elmObj.widgettype = Utils.getElementType(elementName);
        elmObj.externalwidgettype = elementName;
        if (elmObj.widgettype.equals(CHECKBOX)) {
            updateElmobjByCheckboxCase(elmObj);
        } else if (elmObj.widgettype.equals("RADIO")) {
            ElementStaticOptions option1 = new ElementStaticOptions("One", "Option1", ENABLED, "", "");
            elmObj.staticoptions.add(option1);
            ElementStaticOptions option2 = new ElementStaticOptions("Two", "Option2", ENABLED, "", "");
            elmObj.staticoptions.add(option2);
        } else if (elmObj.widgettype.equals("TOGGLESWITCH")) {
            updateElmobjByToggleSwitchCase(elmObj);
        } else if (elmObj.widgettype.equals(INPUTBOX)) {
            updateElmobjByInputboxCase(elmObj, elementName);
        } else if (elmObj.widgettype.equals(INPUTWITHBUTTON)) {
            if (elementName.equals("DPF")) {
                if (Utils.isNull(elmObj.icon)) {
                    elmObj.icon = "icon-grid";
                    elmObj.state = "READONLY";
                }
                elmObj.buttonclickevent = "apz.products.createDynamicLOV(this, event);";
            }
        } else if (elmObj.widgettype.equals(TEXTAREA)) {
            elmObj.maxstringlength = "250";
            elmObj.width = "CUSTOM";
        } else if (!elementName.equals("CBX") || !elementName.equals("RadB") || !elementName.equals("Date")
                || !elementName.equals(DATE_TIME) || !elementName.equals("Phone") || !elementName.equals("Btn")) {
            elmObj.maxstringlength = "100";
        }

    }

    private void updateElmobjByCheckboxCase(ScrElementData elmObj) {
        elmObj.labelwidth = "90";
        ElementStaticOptions elmStatOpts = new ElementStaticOptions();
        elmStatOpts.description = "Checked";
        elmStatOpts.value = "y";
        elmObj.staticoptions.add(elmStatOpts);
        elmStatOpts = new ElementStaticOptions();
        elmStatOpts.description = "Unchecked";
        elmStatOpts.value = "n";
        elmObj.staticoptions.add(elmStatOpts);
        elmStatOpts = new ElementStaticOptions();
        elmStatOpts.description = "Indeterminate";
        elmStatOpts.value = "i";
        elmObj.staticoptions.add(elmStatOpts);
    }

    private void updateElmobjByToggleSwitchCase(ScrElementData elmObj) {
        ElementStaticOptions toggleOption1 = new ElementStaticOptions("on", "", "", "", "");
        elmObj.staticoptions.add(toggleOption1);
        ElementStaticOptions toggleOption2 = new ElementStaticOptions("off", "", "", "", "");
        elmObj.staticoptions.add(toggleOption2);
        elmObj.switchoption1 = "Label1";
        elmObj.switchoption2 = "Label2";
    }

    private void updateElmobjByInputboxCase(ScrElementData elmObj, String elementName) {
        switch (elementName) {
            case "MailId":
                elmObj.email = "Y";
                elmObj.maxstringlength = "100";
                break;
            case "Phone":
                // datatype will be overidden with number if Externaltype is mentioned in json
                elmObj.datatype = INTEGER;
                elmObj.runtimeamountformat = "N";
                elmObj.donotformat = "N";
                break;
            case "Num":
                elmObj.datatype = NUMBER;
                elmObj.runtimeamountformat = "N";
                elmObj.donotformat = "N";
                break;
            case "RdO":
                elmObj.state = "READONLY";
                break;
            case "Date":
                elmObj.datatype = "DATE";
                elmObj.display = "BUBBLE";
                break;
            case DATE_TIME:
                elmObj.datatype = DATE_TIME;
                elmObj.display = "BUBBLE";
                break;
            case "FTxtL":
                elmObj.textdecoration = "L";
                elmObj.maxstringlength = "100";
                break;
            case "FTxtU":
                elmObj.textdecoration = "U";
                elmObj.maxstringlength = "100";
                break;
            case "FTxtC":
                elmObj.textdecoration = "C";
                elmObj.maxstringlength = "100";
                break;
            case "URL":
                elmObj.url = "Y";
                elmObj.maxstringlength = "100";
                break;
            case "IPS":
                if (Utils.isNull(elmObj.icon)) {
                    elmObj.icon = "icon-search";
                }
                if (Utils.isNull(elmObj.symbol)) {
                    elmObj.symbol = "N";
                }
                break;
            default:
        }
    }

    private void dxpRuleSetupdate(Object obj, int index, List<String> validValues, List<String> rejectedValues,
                                  String decorValue, String subkey) {
        ScrElementData elmObj = null;
        ScrContainerData cntObj = null;
        String[] decorValArr = decorValue.split("\\|");

        String type = SCR_ELEMENT_DATA;
        if (obj instanceof ScrElementData) {
            elmObj = (ScrElementData) obj;
        } else if (obj instanceof ScrContainerData) {
            type = SCR_CONTAINER_DATA;
            cntObj = (ScrContainerData) obj;
        }
        if (SCR_ELEMENT_DATA.equals(type)) {
            if (decorValArr.length > 1 && decorValArr[1] != null) {
                String mandatVal = decorValArr[1];
                updateReleMandBYRelationshipDomainAndAttribute(index, subkey, elmObj, mandatVal);
            }
            if (decorValArr.length > 4 && decorValArr[4] != null) {

                if (!decorValArr[4].isEmpty())
                    rejectedValues.addAll(Arrays.asList(decorValArr[4].split(",")));
            }
            if (decorValArr.length > 2 && decorValArr[2] != null) {
                if (!decorValArr[2].isEmpty())
                    validValues.addAll(Arrays.asList(decorValArr[2].split(",")));
            }
            if (decorValArr.length > 3 && decorValArr[3] != null) {
                String defaultValStrtStr = decorValArr[3];
                if (defaultValStrtStr.equals("Null")) {
                    defaultValStrtStr = "";
                } else if (!defaultValStrtStr.contains("API_Get")) {
                    elmObj.defaultvalue = defaultValStrtStr;
                }
            }
            if (decorValArr.length > 5 && !decorValArr[5].isEmpty()) {
                String visibility = decorValArr[5];
                if (visibility.equals("N")) {
                    elmObj.options = "N";
                } else {
                    elmObj.options = "Y";
                }
            }
            String elementName = decorValArr[0];
            if (Utils.isNull(elmObj.widgettype)) {
                updateWidgetStaticOptions(elmObj, elementName);
            } else if (elmObj.widgettype.equals(INPUTBOX) || elmObj.widgettype.equals(INPUTWITHBUTTON)) {
                elmObj.externalwidgettype = elementName;
            }
        } else if (SCR_CONTAINER_DATA.equals(type)) {
            if (decorValArr.length > 5 && !decorValArr[5].isEmpty()) {
                String visibility = decorValArr[5];
                if (visibility.equals("N")) {
                    cntObj.options = "N";
                } else {
                    cntObj.options = "Y";
                }
            }
        }
    }

    private void updateReleMandBYRelationshipDomainAndAttribute(int index, String subkey, ScrElementData elmObj,
                                                                String mandatVal) {
        if (mandatVal.equals("Man") || mandatVal.equals("CMan")) {
            elmObj.mandatory = "Y";
            if (DesignUtils.RELATIONSHIP_DOMAIN.equals(subkey)) {
                objParserUtils.releMand.remove(index);
                objParserUtils.releMand.add(index, "Y");
            }
            if (DesignUtils.ATTRIBUTE.equals(subkey)) {
                objParserUtils.eMand.remove(index);
                objParserUtils.eMand.add(index, "Y");
            }
        } else {
            elmObj.mandatory = "N";
        }
    }

    ScrContainerData updateNodeInContainer(ScrElementData elmObj, String group) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside updateNodeInContainer()");
        ScrContainerData cntrData = null;
        DataObject parentObj = elmObj;
        while (parentObj.parent != null && !"CONTAINER".equals(parentObj.parent.type)) {
            parentObj = parentObj.parent;
        }
        if (parentObj != null) {
            cntrData = (ScrContainerData) parentObj.parent;
            ObjectNode nodeObj = JsonNodeFactory.instance.objectNode();
            nodeObj.put("interfacename", elmObj.interfacename);
            nodeObj.put("datamodeltype", elmObj.datamodeltype);
            nodeObj.put("nodename", elmObj.nodename);
            // nodeObj.put("elementname", elmObj.elementname); /// TBC --- elm name required
            // for nodes obj?
            nodeObj.put("nodelevel", 0);
            ObjectNode availNodeObj = nodesMap.get(appId + "__" + elmObj.nodename);
            if (availNodeObj == null) {
                nodeObj.put(NODE_TYPE, "1:1");
            } else {
                nodeObj.put(NODE_TYPE, availNodeObj.get(NODE_TYPE).asText());
            }
            String lnodeid = Utils.getNodeId(nodeObj);
            if (!cntrData.nodesmap.containsKey(lnodeid)) {
                cntrData.nodes.add(nodeObj);
                cntrData.nodesmap.put(lnodeid, nodeObj);
            }
            if (!cntrData.elms.contains(elmObj)) {
                cntrData.elms.add(elmObj);
                cntrData.elmsmap.put(elmObj.name, elmObj);
            }
            String cntrType = "FORM";
            if (!Utils.isNull(group)) {
                if (group.equals("Grid")) {
                    cntrType = TABLE;
                    cntrData.nativetable = "Y";
                    cntrData.rowselectorrequired = "Y";
                } else if (group.equals("List")) {
                    cntrType = "LIST";
                } else if (group.equals("Form") || group.equals("Label")) {
                    cntrType = "FORM";
                }
            }
            if (Utils.isNull(cntrData.widgettype)) {
                cntrData.widgettype = cntrType;
            }
            if (cntrData.widgettype.equals(TABLE) || cntrData.widgettype.equals("LIST")) {
                nodeObj.put(NODE_TYPE, "1:N");
                cntrData.multirec = "Y";
            }
            nodesMap.put(appId + "__" + elmObj.nodename, nodeObj);
        }
        return cntrData;
    }

    void updateRelationsObj(ObjectNode relationship, String group) throws Exception {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside updateRelationsObj()");
        String subProdGroup = "";
        relationshipId = Utils.removeSpecialChars(relationship.get("Id").asText());
        if (relationship.has(DesignUtils.RELATIONSHIP_DOMAIN) && (!relationship.has("relationProcessed"))) {

            JsonNode relationshipDomain = relationship.get(DesignUtils.RELATIONSHIP_DOMAIN);
            boolean processElms = false;
            JsonNode basicRuleObj = null;
            if (relationship.has("DXP_" + Parser.prodShortName + BASIC_RULE_SET)) {
                basicRuleObj = relationship.get("DXP_" + Parser.prodShortName + BASIC_RULE_SET);
            } else if (relationship.has("DX_" + Parser.prodShortName + BASIC_RULE_SET)) {
                basicRuleObj = relationship.get("DX_" + Parser.prodShortName + BASIC_RULE_SET);
            }
            if (basicRuleObj != null) {
                subProdGroup = basicRuleObj.get(VALUE).asText().split("\\|")[0];
            } else if (relationship.has("DX_" + Parser.prodShortName + "_GRPREF")) {
                subProdGroup = relationship.get("DX_" + Parser.prodShortName + "_GRPREF").get(VALUE).asText();
            }
            if (relationshipDomain.isArray()) {
                ArrayNode relateDomains = (ArrayNode) relationshipDomain;
                for (int r = 0; r < relateDomains.size(); r++) {
                    ObjectNode relateDomain = (ObjectNode) relateDomains.get(r);
                    updateRelationsDomainObj(relateDomain, relationship, subProdGroup, processElms);
                }
            } else if (relationshipDomain.isObject()) {
                updateRelationsDomainObj((ObjectNode) relationshipDomain, relationship, subProdGroup, processElms);
            }
            if (relationship.has("DefaultProductId")) {
                String defaultProductId = Utils.removeSpecialChars(relationship.get("DefaultProductId").asText());
                if (!Utils.isNull(defaultProductId)) {
                    String productID = Utils.removeSpecialChars(originalProductId);
                    configObj.defaultProducts.add(productID + "_" + relationshipId + "_" + defaultProductId);
                    String cntrClass = " " + productID + "_" + relationshipId + "_" + defaultProductId + "RelCntr";
                    ScrContainerData cntr = configObj.containersMap.get(relationshipId);
                    if (cntr != null) {
                        cntr.defaultProducts.add(productID + "_" + relationshipId + "_" + defaultProductId);
                        if (!cntr.cssclasses.contains(cntrClass)) {
                            cntr.cssclasses = cntr.cssclasses + cntrClass;
                        }
                        boolean showCntr = false;
                        for (int c = 0; c < cntr.elms.size(); c++) {
                            ScrElementData elmObj = cntr.elms.get(c);
                            if (elmObj.options.equals("Y")) {
                                showCntr = true;
                                break;
                            }
                        }
                        if (!showCntr) {
                            cntr.options = "N";
                        }
                        if (Utils.isNull(cntr.uidescription)) {
                            if (relationship.has("DXP_" + Parser.prodShortName + UI_DESCP)) {
                                cntr.uidescription = relationship.get("DXP_" + Parser.prodShortName + UI_DESCP)
                                        .get(VALUE).asText();
                            } else if (relationship.has("DX_" + Parser.prodShortName + UI_DESCP)) {
                                cntr.uidescription = relationship.get("DX_" + Parser.prodShortName + UI_DESCP)
                                        .get(VALUE).asText();
                            }
                        }
                    }
                }
            }
            ScrContainerData cntr = configObj.containersMap.get(relationshipId);
            if (cntr != null) {
                if (Utils.isNull(cntr.title)) {
                    if (parentTitle.equals("Y")) {
                        cntr.title = parentProductId.isEmpty() ? Utils.getTitle(relationship)
                                : productName + " : " + Utils.getTitle(relationship);
                    } else {
                        cntr.title = Utils.getTitle(relationship);
                    }
                }
                if (Utils.isNull(cntr.externalname)) {
                    cntr.externalname = getName(relationship);
                }
                if (Utils.isNull(cntr.displayname)) {
                    if (relationship.has(DISPLAY_NAME)) {
                        cntr.displayname = relationship.get(DISPLAY_NAME).asText();
                    }
                }
            }
        }
        if (!relationship.has("decorationProcessed")) {
            if (relationship.has(DesignUtils.RELATIONSHIP_DECORATION)) {
                ScrContainerData cntr = configObj.containersMap.get(relationshipId);
                if (cntr != null) {
                    JsonNode cntrDecorations = relationship.get(DesignUtils.RELATIONSHIP_DECORATION);
                    List<String> validValues = new ArrayList<String>();
                    List<String> rejectedValues = new ArrayList<String>();
                    if (cntrDecorations.isArray()) {
                        ArrayNode decorations = (ArrayNode) cntrDecorations;
                        for (int d = 0; d < decorations.size(); d++) {
                            ObjectNode decor = (ObjectNode) decorations.get(d);
                            applyElmDecorations(cntr, decor, 0, validValues, rejectedValues, relationship,
                                    DesignUtils.RELATIONSHIP_DECORATION);
                        }
                    } else if (cntrDecorations.isObject()) {
                        ObjectNode decor = (ObjectNode) cntrDecorations;
                        applyElmDecorations(cntr, decor, 0, validValues, rejectedValues, relationship,
                                DesignUtils.RELATIONSHIP_DECORATION);
                    }
                    relationship.put("decorationProcessed", "Y");
                }

            }
        }
    }

    boolean updateRelationsDomainObj(ObjectNode relationshipDomain, ObjectNode relationship, String subProdGroup,
                                     boolean processElms) {
        String origSubProductID = relationshipDomain.get(DesignUtils.ID).asText();
        String subProductID = Utils.removeSpecialChars(origSubProductID);
        boolean parseSubProd = true;
        relationship.put("relationProcessed", "Y");
        relationExists = true;
        String origId = relationshipDomain.get(ORIG_ID).asText();
        String subProdId = relationshipDomain.get("Id").asText();
        origId = Utils.removeSpecialChars(origId);
        ScrElementData elmObj = configObj.elms_map.get(origId);
        if (elmObj != null) {
            updateElmObj(elmObj, relationshipDomain, DesignUtils.RELATIONSHIP_DOMAIN, "");
        } else {
            elmObj = Utils.getNewElement(relationshipDomain, relationshipId, this, DesignUtils.RELATIONSHIP_DOMAIN, "");
        }
        elmObj.cssclasses = elmObj.cssclasses + " relations" + uniqueProductId + " " + subProdId;
        String parentId = Utils.removeSpecialChars(originalProductId);
        String cntrClass = " " + parentId + "_" + relationshipId + "_" + Utils.removeSpecialChars(subProdId)
                + "RelCntr";
        ScrContainerData cntr = configObj.containersMap.get(relationshipId);
        if (cntr != null) {
            if (!cntr.cssclasses.contains(cntrClass)) {
                cntr.cssclasses = cntr.cssclasses + cntrClass;
            }
            if (relationship.has("DXP_" + Parser.prodShortName + UI_DESCP)) {
                cntr.uidescription = relationship.get("DXP_" + Parser.prodShortName + UI_DESCP).get(VALUE).asText();
            } else if (relationship.has("DX_" + Parser.prodShortName + UI_DESCP)) {
                cntr.uidescription = relationship.get("DX_" + Parser.prodShortName + UI_DESCP).get(VALUE).asText();
            }
        }
        if (productsMapper.containsKey(subProductID)) {
            ObjectNode relativeObj = productsMapper.get(subProductID);
            if (parseSubProd) {
                relativeObj.put("ParsingReq", "Y");
            }
            try {
                if (relativeObj.has("ParsingReq")) {
                    if (!configObj.subProducts.has(parentId + "_" + relationshipId + "_" + subProductID)) {
                        configObj.subProducts.add(parentId + "_" + relationshipId + "_" + subProductID);
                    }
                    Parser p = new Parser();
                    p.appId = appId;
                    p.parentTitle = parentTitle;
                    p.cAsmiCnvUIScrRepository = cAsmiCnvUIScrRepository;
                    p.iAsmiIntfMasterRepository = iAsmiIntfMasterRepository;
                    TbAsmiCnvUIScr productTemplate = getTemplate(appId,
                            parentId + "_" + relationshipId + "_" + subProductID);
                    if (!Utils.isNull(productTemplate.getTemplate())) {
                        p.configObj = getTemplateObj(productTemplate.getTemplate());
                    } else {
                        DomainException lDomainException = DomainException.getDomainExceptionInstance();
                        lDomainException.setMessage(
                                lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                        lDomainException.setCode(DomainException.Code.APZ_DM_008.toString());
                        lDomainException.setPriority("1");
                        throw lDomainException;
                    }
                    if (!Utils.isNull(productTemplate.getScreenLayout())) {
                        p.configObj.scripts = getScreenScripts(productTemplate.getScreenLayout());
                    }
                    p.relatedProducts = new ArrayList<String>(relatedProducts);
                    if (relatedProductsMap.has(configObj.prodName)) {
                        p.relatedProductsMap = (ObjectNode) relatedProductsMap.get(configObj.prodName);
                    } else {
                        p.relatedProductsMap = sharedRelatedProductsMap;
                    }
                    p.enrichLoadedDataObject(p.configObj);
                    p.configObj.combinedParentId = uniqueProductId;
                    p.updateConfigObj(relativeObj, subProdGroup, parentId, relationshipId, p, relationship);
                    p.configObj.rootId = p.configObj.headerId = origSubProductID;
                    p.configObj.parentId = originalProductId;

                    p.configObj.portItemId = relationship.get(DesignUtils.ID).asText();
                    p.configObj.prodItemId = relationshipDomain.get(ORIG_ID).asText();
                    Utils.populateGroups(p.configObj, p.scrId);
                    if (!parentId.isEmpty()) {
                        p.screenParentId = parentId + "_" + relationshipId + "_" + subProductID;
                    }
                    screenParsers.add(p);
                }
            } catch (Exception ex) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(ex.getMessage());
                dexp.setCode(DomainException.Code.APZ_DM_000.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        }
        return true;
    }

    String getName(ObjectNode obj) {
        String name = "";
        name = obj.get("Name").asText();
        //// Process Name for removing space and special char as per apz id standards.
        return name;
    }

}
