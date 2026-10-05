package com.iexceed.appzillon.iface;

import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.json.XML;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import java.io.StringWriter;

import static com.iexceed.appzillon.utils.Constants.LOG_EXCEPTION;

public abstract class ExternalServicesRouter implements IExternalServiceRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    ExternalServicesRouter.class.toString());

    public static Object injectBeanFromSpringContext(String beanId,
                                                     SpringCamelContext context) throws ExternalServicesRouterException {
        Object bean = null;
        try {

            bean = context.getApplicationContext().getBean(beanId);

        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_004.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_004));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;

        }
        return bean;
    }

    public static SpringCamelContext getCamelContext()
            throws ExternalServicesRouterException {
        SpringCamelContext context = null;
        try {
            context = FrameworksStartup.getInstance().getCamelContext();
        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_002.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_002));
            exsrvcallexp.setPriority("1");

            throw exsrvcallexp;
        }
        return context;
    }

    public static ProducerTemplate createProducerTemplate()
            throws ExternalServicesRouterException {
        ProducerTemplate producer = null;
        try {

            producer = FrameworksStartup.getInstance().getProducerTemplate();
            LOG.debug("**** createProducerTemplate - producer: {}", producer);
        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_003.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_003));
            exsrvcallexp.setPriority("1");

            throw exsrvcallexp;
        }
        return producer;
    }

    public static Object getUnMarshalled(String pInputXMLStr,
                                         String pQualifiedClassName) {
        Object lRespObject = null;

        try {

            @SuppressWarnings("rawtypes")
            Class lc = Class.forName(pQualifiedClassName);

            SAXParserFactory spf = getSaxFactory();
            SAXParser parser = spf.newSAXParser();
            //Improper Restriction of XML External Entity Reference (CWE ID 611)
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            Source xmlSource = new SAXSource(parser.getXMLReader(),
                    new InputSource(new StringReader(pInputXMLStr)));
            JAXBContext lJaxbContext = JAXBContext.newInstance(lc);

            Unmarshaller lJaxbUnmarshaller = lJaxbContext.createUnmarshaller();
            lRespObject = lJaxbUnmarshaller.unmarshal(xmlSource);
            //StringReader lInStringReader = new StringReader(pInputXMLStr);
            //lRespObject = lJaxbUnmarshaller.unmarshal(lInStringReader);
        } catch (ClassNotFoundException pCnfex) {

            LOG.error("ClassNotFoundException : ", pCnfex);
        } catch (JAXBException pJaxex) {

            LOG.error("JAXBException : ", pJaxex);
        } catch (SAXException | ParserConfigurationException e) {
            LOG.error("Exception occurred while processing: ", e);
        }

        return lRespObject;
    }

    private static SAXParserFactory getSaxFactory() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //Disable XXE
            //Veracode fix: Improper Restriction of XML External Entity Reference (CWE ID 611)
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception e) {
            LOG.error("Feature is not supported by XML processor: ", e.getMessage());
        }
        return spf;
    }

    public static String getMarshalled(Object pResponseObj, String pQualifiedClassName) {

        String lResponse = null;
        try {

            @SuppressWarnings("rawtypes")
            Class lc = Class.forName(pQualifiedClassName);

            JAXBContext lJaxbContext = JAXBContext.newInstance(lc);

            StringWriter stringWriter = new StringWriter();

            Marshaller lJaxbmarshaller = lJaxbContext.createMarshaller();

            lJaxbmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            lJaxbmarshaller.marshal(pResponseObj, stringWriter);

            lResponse = stringWriter.toString();
        } catch (JAXBException pJaxex) {

            LOG.error("JAXBException : ", pJaxex);
        } catch (ClassNotFoundException pCnfex) {

            LOG.error("ClassNotFoundException : ", pCnfex);
        }

        return lResponse;
    }

    public static String getJSONtoXML(String inputJSON) {
        String lxML = null;
        try {

            JSONObject jSONObject = new JSONObject(inputJSON);
            LOG.debug("getJSONtoXML.Input JSON: {}", inputJSON);
            lxML = XML.toString(jSONObject);
            LOG.debug("getJSONtoXML.After converting JSON to XML: {}", lxML);

        } catch (JSONException ex) {
            LOG.error(LOG_EXCEPTION, ex);
        }
        return lxML;
    }

    public static String getXMLToJSON(String inputXML) {
        String ljSON = null;
        try {

            //com.iexceed.appzillon.json.JSONObject ljSONObj = com.iexceed.appzillon.json.XML.toJSONObject(inputXML);
            com.iexceed.appzillon.json.JSONObject ljSONObj = com.iexceed.appzillon.json.XML.toJSONObject(inputXML, true);
            ljSON = ljSONObj.toString();

        } catch (com.iexceed.appzillon.json.JSONException ex) {
            LOG.error(LOG_EXCEPTION, ex);
        }
        return ljSON;
    }

    public void injectBeanfromCamelContext(String beanId)
            throws ExternalServicesRouterException {

    }
}
