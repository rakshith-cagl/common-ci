package com.iexceed.appzillon.jsonutils;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static javax.xml.transform.TransformerFactory.newInstance;

public class JsonToXMLConverter {

    //Added by Sourav
    private static Logger log = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, JsonToXMLConverter.class.getName());

    public static String jsonToXml(String str) {
        log.debug("{} Start JsonToXMLConverter", ServerConstants.LOGGER_PREFIX_RESTFULL);
        InputStream input = null;
        XMLEventReader reader = null;
        XMLEventWriter writer = null;

        input = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));

        StringWriter output = new StringWriter();

        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false)
                .build();
        try {
            reader = new JsonXMLInputFactory(config)
                    .createXMLEventReader(input);
            writer = XMLOutputFactory.newInstance()
                    .createXMLEventWriter(output);
            writer.add(reader);

        } catch (XMLStreamException e) {
            log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "XMLStreamException:", e);

        } finally {
            try {
                output.close();

                if (input != null) {
                    input.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "IOException:", e);

            } catch (XMLStreamException e) {
                log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "XMLStreamException:", e);
            }
        }
        return output.toString();
    }

    /**
     * Changes made by Sourav on 03-03-2014
     * This method will reorder the xml based on the input xslt using transformer
     * (The xslt must contain the ordering of nodes based on the xml schema)
     * pXmlInput is the unordered xml
     *
     * @param pXmlInput
     * @param pXslFilePath
     * @return String
     */

    public String generateFinalXML(String pXmlInput, String pXslFilePath) {
        log.debug("{} Start generateFinalXML", ServerConstants.LOGGER_PREFIX_RESTFULL);
        String respXML = "";
        try {
            InputStream in = new ByteArrayInputStream(pXmlInput.getBytes());
            Source xmlSource = new StreamSource(in);
            Source xslSource = new StreamSource(new File(pXslFilePath));
            TransformerFactory factory = newInstance();
            Transformer transformer = factory.newTransformer(xslSource);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            transformer.transform(xmlSource, new StreamResult(bout));
            bout.close();
            respXML = bout.toString();
            log.debug("{} Response xml : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, respXML);
        } catch (TransformerException ex) {
            log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "TransformerException  ", ex);
        } catch (IOException ex) {
            log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "IOException   ", ex);
        }
        return respXML;
    }
}
