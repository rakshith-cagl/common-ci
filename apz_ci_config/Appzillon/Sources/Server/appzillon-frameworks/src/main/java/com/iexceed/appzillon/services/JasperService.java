/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.ReportDetails;
import com.iexceed.appzillon.dbutils.DBUtils;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.service.FileService;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IReportServiceBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.NamingException;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static com.iexceed.appzillon.utils.Constants.FILE_NAME;
import static net.sf.jasperreports.engine.JRExporterParameter.*;
import static net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter.*;

/**
 * @author ripu
 */
public class JasperService implements IReportServiceBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
            ServerConstants.LOGGER_FRAMEWORKS, JasperService.class.getName());
    private static String cPassword = null;
    private ReportDetails reportDtls = null;

    @Autowired
    private FileService fileService;

    public static byte[] bytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            LOG.error("{} Sorry! Your given file is too large.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            ExternalServicesRouterException externalServicesRouterException = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            externalServicesRouterException.setMessage(externalServicesRouterException.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_040));
            externalServicesRouterException.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_040.toString());
            externalServicesRouterException.setPriority("1");
            throw externalServicesRouterException;
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        try {
            while (offset < bytes.length && (numRead = is.read(bytes,
                    offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }

    @Override
    public Object callService(Message pMessage, SpringCamelContext pContext) {

        LOG.debug("{} inside callService()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String reportResponse = "";
        JSONObject finalResponseJson = null;
        JSONObject reqJson = null;
        JSONObject reportData = null;
        JSONObject reportDetail = null;
        String fileType = null;
        this.getReportDetails(pMessage, pContext);

        if (ServerConstants.YES.equalsIgnoreCase(reportDtls.getCredRequired())) {
            cPassword = generatePassword(pMessage, pContext);
        }

        try {
            this.validateRequest(pMessage);

            reqJson = pMessage.getRequestObject().getRequestJson();
            reportData = (JSONObject) reqJson.get(ServerConstants.REPORT_DATA);
            reportDetail = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
            fileType = (String) reportDetail.get(ServerConstants.REPORT_FILE_TYPE);

            LOG.debug("{} Password Required : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, reportDtls.getCredRequired());

            if (ServerConstants.YES.equalsIgnoreCase(reportDtls.getCredRequired()) && ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(fileType)) {
                String reportStatus = null;
                String mailStatus = null;
                reportResponse = this.generateEncodedReport(pMessage);
                JSONObject reportResponseJson = new JSONObject(reportResponse);
                if (reportResponseJson.has(ServerConstants.MESSAGE_HEADER_STATUS)) {
                    reportStatus = (String) reportResponseJson.get(ServerConstants.MESSAGE_HEADER_STATUS);
                    if (ServerConstants.SUCCESS.equalsIgnoreCase(reportStatus)) {
                        mailStatus = this.sendMailtoUser(pMessage);
                        if (ServerConstants.SUCCESS.equalsIgnoreCase(mailStatus)) {
                            finalResponseJson = new JSONObject(reportResponse);
                        } else {
                            pMessage.getHeader().setStatus(false);
                            Error error = Error.getInstance();
                            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                            error.setErrorCode(EXCEPTION_CODE.APZ_FM_EX_032.toString());
                            error.setErrorDesc(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_032));
                            pMessage.getErrors().add(error);
                        }
                    } else {
                        finalResponseJson = new JSONObject(reportResponse);
                    }
                }
            } else if (ServerConstants.NO.equalsIgnoreCase(reportDtls.getCredRequired())) { // cpasswordRequired
                finalResponseJson = new JSONObject(this.generateEncodedReport(pMessage));
            } else {
                LOG.error("{} Password Protected {} Report can not be generated.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileType);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_027.toString());
                exsrvcallexp.setMessage("Password protected " + fileType + " report cannot be generated.");
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }
        } catch (JSONException exp) {
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + ServerConstants.JSON_EXCEPTION, exp);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }

        return finalResponseJson;
    }

    @Override
    public void validateRequest(Message pMessage) {
        LOG.debug("{} inside validateRequest()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        LOG.debug("{} payLoad :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        try {
            JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
            JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
            JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
            String fileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);
            JSONObject reportParam = (JSONObject) reportData.get(ServerConstants.REPORT_PARAMS);
            LOG.debug("{} report parameter : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, reportParam);

            if (Utils.isNullOrEmpty(fileType)) {
                LOG.error("{} fileType is null..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }
            if (!ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(fileType) && !ServerConstants.REPORT_XLS.equalsIgnoreCase(fileType)
                    && !ServerConstants.REPORT_XLSX.equalsIgnoreCase(fileType) && !ServerConstants.REPORT_HTML.equalsIgnoreCase(fileType)) {
                LOG.error("{} fileType is : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileType);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
                exsrvcallexp.setMessage("Report can not be generated of " + fileType + " type.");
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }
        } catch (JSONException jsonExp) {
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jsonExp);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
    }

    @Override
    public String generateEncodedReport(Message pMessage) {
        LOG.debug("{} inside generateEncodedReport()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject responseJson = null;
        String response = "";
        try {
            JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
            JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
            String lFileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);

            if (ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType) || ServerConstants.REPORT_HTML.equalsIgnoreCase(lFileType)) {
                response = genreratePDFReport(pMessage);
            } else if (ServerConstants.REPORT_XLS.equalsIgnoreCase(lFileType) || ServerConstants.REPORT_XLSX.equalsIgnoreCase(lFileType)) {
                response = generateExcelReport(pMessage);
            }
            responseJson = new JSONObject(response);
        } catch (JSONException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_030.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_030));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return responseJson.toString();
    }

    @Override
    public String genreratePDFReport(Message pMessage) {
        LOG.debug("{} inside genreratePDFReport()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        JSONObject reqJson = pMessage.getRequestObject().getRequestJson();
        JSONObject reportData = reqJson.getJSONObject(ServerConstants.REPORT_DATA);
        JSONObject reportDetail = reportData.getJSONObject(ServerConstants.REPORT_DETAILS);
        String lFileType = reportDetail.getString(ServerConstants.REPORT_FILE_TYPE);
        String reportName = "";
        if (reportDetail.has(FILE_NAME) && !reportDetail.getString(FILE_NAME).isEmpty()) {
            reportName = reportDetail.getString(FILE_NAME);
        } else {
            Date date = new java.util.Date();
            reportName = Long.toString(date.getTime());
        }
        String lFileExt = "";
        String destinationfilePath = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
        LOG.debug("{} destinationfilePath :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, destinationfilePath);

        String base64 = null;
        JSONObject responseJson = new JSONObject();
        Connection lConnection = null;
        try {
            //List<JasperPrint> list = new ArrayList<JasperPrint>();
            lConnection = DBUtils.getConnectionFromDataSource(reportDtls.getDataSource());
            LOG.debug("{} Taking Connection From Data source...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

            //list.add(this.loadJasperFile(pMessage, lConnection));
            //JRPdfExporter exporter = new JRPdfExporter();
            LOG.debug("{} File Type : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lFileType);
            JRExporter exporter = null;
            if (ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType)) {
                lFileExt = ServerConstants.REPORT_PDF_FILE_EXT;
                exporter = new JRPdfExporter();
            } else if (ServerConstants.REPORT_HTML.equalsIgnoreCase(lFileType)) {
                lFileExt = ServerConstants.REPORT_HTML_EXT;
                exporter = new HtmlExporter();
            }
            if (exporter == null) {
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_031.toString());
                exsrvcallexp.setMessage("Invalid file type");
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }
            String pdfGeneratedLocation = destinationfilePath + reportName + lFileExt;
            exporter.setParameter(JASPER_PRINT, this.loadJasperFile(pMessage, lConnection));
            exporter.setParameter(OUTPUT_FILE, fileService.getFile(pdfGeneratedLocation));

            if (ServerConstants.YES.equalsIgnoreCase(reportDtls.getCredRequired())) {
                exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, true);
                exporter.setParameter(JRPdfExporterParameter.USER_PASSWORD, cPassword);
            }
            Utils.setExtTime(pMessage, "S");
            exporter.exportReport();
            Utils.setExtTime(pMessage, "E");
            LOG.debug("{} Report file generated at :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pdfGeneratedLocation);
            if ("ENCODED".equalsIgnoreCase(reportDtls.getReportType())) {
                File pdfReport = fileService.getFile(pdfGeneratedLocation);
                byte[] byteFile = (byte[]) bytesFromFile(pdfReport);
                base64 = Base64.encodeBase64String(byteFile);

                responseJson.put(ServerConstants.REPORT_FILE, base64);
                responseJson.put(ServerConstants.REPORT_FILENAME, reportName + lFileExt);

                if (pdfReport.delete()) {
                    LOG.debug("{} File deleted :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pdfGeneratedLocation);
                }

                responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            } else if ("URL".equalsIgnoreCase(reportDtls.getReportType())) {
                responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
                responseJson.put("filePath", pdfGeneratedLocation);
            }
        } catch (JRException jrExp) {
            LOG.error("{} PDF JRException : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jrExp);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_031.toString());
            exsrvcallexp.setMessage(jrExp.getMessage());
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (IOException ioExp) {
            LOG.error("{} PDF IOException :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ioExp);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
            exsrvcallexp.setMessage(ioExp.getMessage());
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (NamingException e) {
            LOG.error("{} NamingException :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_028.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_028));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (SQLException e) {
            LOG.error("{} SQLException :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_029.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_029));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            DBUtils.closeDbConnection(lConnection);
        }
        return responseJson.toString();
    }

    @Override
    public String generateExcelReport(Message pMessage) {
        LOG.debug("{} inside generateExcelReport()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        LOG.debug("{} Request : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
        JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
        String lFileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);
        String destinationfilePath = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
        LOG.debug("{} destination file Path :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, destinationfilePath);

        String lFileExt = "";
        JSONObject responseJson = new JSONObject();
        String reportName = "";
        if (reportDetails.has(FILE_NAME) && !reportDetails.getString(FILE_NAME).isEmpty()) {
            reportName = reportDetails.getString(FILE_NAME);
        } else {
            Date date = new java.util.Date();
            reportName = Long.toString(date.getTime());
        }
        Connection lConnection = null;
        String fileName = destinationfilePath + reportName + lFileExt;
        LOG.debug("{} File Name :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileName);
        File xlsReport = fileService.getFile(fileName);

        try (OutputStream outputfile = new FileOutputStream(xlsReport)) {
            JRExporter exporter = null;
            if (ServerConstants.REPORT_XLS.equalsIgnoreCase(lFileType)) {
                lFileExt = ServerConstants.REPORT_XLS_EXT;
                exporter = new JRXlsExporter();
            } else if (ServerConstants.REPORT_XLSX.equalsIgnoreCase(lFileType)) {
                lFileExt = ServerConstants.REPORT_XLSX_EXT;
                exporter = new JRXlsxExporter();
            }

            if (exporter == null) {
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_031.toString());
                exsrvcallexp.setMessage("Invalid file type");
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }

            lConnection = DBUtils.getConnectionFromDataSource(reportDtls.getDataSource());
            LOG.debug("{} Taking Connection From Data source...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

            exporter.setParameter(JASPER_PRINT, this.loadJasperFile(pMessage, lConnection));
            exporter.setParameter(OUTPUT_STREAM, outputfile);
            exporter.setParameter(IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporter.setParameter(IS_DETECT_CELL_TYPE, Boolean.TRUE);
            exporter.setParameter(IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporter.setParameter(IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);

            exporter.exportReport();
            LOG.debug("{} Exported.....:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileName);
            outputfile.close();


            byte[] byteFile = (byte[]) bytesFromFile(xlsReport);
            String base64 = Base64.encodeBase64String(byteFile);
            responseJson.put(ServerConstants.REPORT_FILE, base64);
            responseJson.put(ServerConstants.REPORT_FILENAME, reportName + lFileExt);

            if (xlsReport.delete()) {
                LOG.debug("{} File deleted :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileName);
            }
            responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        } catch (JRException jrExp) {
            LOG.error("{} Excel JRException : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jrExp);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_031.toString());
            exsrvcallexp.setMessage(jrExp.getMessage());
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (IOException e) {
            LOG.error("{} Excel IOException : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
            exsrvcallexp.setMessage(e.getMessage());
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (NamingException e) {
            LOG.error("{} Naming Exception :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_028.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_028));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (SQLException e) {
            LOG.error("{} SQL Exception :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_029.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_029));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            DBUtils.closeDbConnection(lConnection);
        }
        return responseJson.toString();
    }

    @Override
    public String generatePassword(Message pMessage, SpringCamelContext pContext) {
        LOG.debug("{} Inside generatePassword()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lUserID = pMessage.getHeader().getUserId();
        LOG.debug("{} user ID : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lUserID);
        Calendar calendar = Calendar.getInstance();
        return lUserID + calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND);
    }

    private String sendMailtoUser(Message pMessage) {
        LOG.debug("{} Inside sendMailtoUser..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String status = null;
        String emailId = null;
        JSONObject responseJson = null;

        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        pMessage.getHeader().setInterfaceId(ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ);

        try {
            DomainStartup.getInstance().processRequest(pMessage);
            responseJson = pMessage.getResponseObject().getResponseJson();
            emailId = (String) responseJson.get(ServerConstants.APPZILLON_ROOT_EMAILID);

            LOG.debug("{} email id :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, emailId);

            pMessage.getHeader().setInterfaceId("appzillonMailRequest");
            pMessage.getIntfDtls().setType("MAIL");

            Properties propfile = new Properties();
            String lFileName = Utils.getPdfReportFileName(pMessage.getHeader().getAppId());
            LOG.debug("{} PDF Report - SendMail mail template file name - l_fileName: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lFileName);
            try (InputStream isr = PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName)) {
                propfile.load(isr);
            }
            String templateBody = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
            templateBody = templateBody.replace("$password", cPassword);

            String templateSubject = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);

            String body = "{'appzillonMailRequest':{'emailid':'" + emailId + "', 'body':'" + templateBody + "', 'subject':'" + templateSubject + "'}}";
            JSONObject jsonBody = new JSONObject(body);
            pMessage.getRequestObject().setRequestJson(jsonBody);

            FrameworksStartup.getInstance().processRequest(pMessage);

            status = "success";
        } catch (ExternalServicesRouterException exp) {
            status = "Fail";
            LOG.error("{} ExternalServicesRouterException -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, Utils.getStackTrace(exp));
        } catch (JSONException exp) {
            status = "Fail";
            LOG.error("{} JSONException -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, Utils.getStackTrace(exp));
        } catch (IOException exp) {
            status = "Fail";
            LOG.error("{} IOException -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, Utils.getStackTrace(exp));
        } catch (ClassNotFoundException exp) {
            status = "Fail";
            LOG.error("{} ClassNotFoundException -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, Utils.getStackTrace(exp));
        } catch (InvalidPayloadException exp) {
            status = "Fail";
            LOG.error("{} InvalidPayloadException -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, Utils.getStackTrace(exp));
        }
        LOG.debug("{} mail sending status :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, status);
        return status;
    }

    private void getReportDetails(Message pMessage, SpringCamelContext pContext) {
        reportDtls = (ReportDetails) ExternalServicesRouter.injectBeanFromSpringContext(pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId(), pContext);
    }

    private JasperPrint loadJasperFile(Message pMessage, Connection connection) throws IOException, JRException, NamingException, SQLException {
        LOG.debug("{} inside loadJasperFile()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String jrxmlSourceFileName = pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + ServerConstants.REPORT_JRXML_FILE_EXT;
        String jasperSourceFileName = pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + ServerConstants.REPORT_JASPER_FILE_EXT;

        String sourceFileLocation = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
        LOG.debug("{} sourceFileLocation : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, sourceFileLocation);
        File file = fileService.getFile(sourceFileLocation + jasperSourceFileName);

        if (!file.exists()) {
            LOG.debug("{} Loading Report Designs", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            InputStream input = new FileInputStream(new File(sourceFileLocation + jrxmlSourceFileName));
            JasperDesign jasperDesign = JRXmlLoader.load(input);
            LOG.debug("{} Compiling Report Designs", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            JasperCompileManager.compileReportToFile(jasperDesign, sourceFileLocation + jasperSourceFileName);
        }
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
        JSONObject reportParam = (JSONObject) reportData.get(ServerConstants.REPORT_PARAMS);
        Map<String, Object> parameters = JSONUtils.buildParamMap(reportParam);


        return JasperFillManager.fillReport(sourceFileLocation + jasperSourceFileName, parameters, connection);
    }
}
