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
import com.iexceed.appzillon.utils.PDFMetaDataManager;
import com.iexceed.appzillon.utils.ServerConstants;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static com.iexceed.appzillon.utils.Constants.FILE_NAME;
import static com.iexceed.appzillon.utils.Constants.TEMP;
import static org.eclipse.birt.report.engine.api.IRenderOption.OUTPUT_FORMAT_HTML;
import static org.eclipse.birt.report.engine.api.IRenderOption.OUTPUT_FORMAT_PDF;

/**
 * @author Ripu
 */
public class BirtService implements IReportServiceBean {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, BirtService.class.getName());
    private static String cPassword = null;
    private static boolean birtEngineIntialized = false;
    private static ReportEngine re = null;
    private ReportDetails reportDtls = null;

    @Autowired
    private FileService fileService;

    private static void intializeBirtEngine() {
        EngineConfig config = null;

        try {
            if (!birtEngineIntialized) {
                LOG.debug("{} BirtEngine is not initialized.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                config = new EngineConfig();
                RegistryProviderFactory.releaseDefault();
                Platform.startup(config);
                re = new ReportEngine(config);
                birtEngineIntialized = true;
            } else {
                LOG.debug("{} BirtEngine is initialized.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            }
        } catch (Exception e) {
            LOG.error("{} BirtEngine initialization is failed {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        }
    }

    private static byte[] bytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            LOG.debug("{} Sorry! Your given file is too large.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            /*J2EE Bad Practices: Use of System.exit() (CWE ID 382)*/
            //System.exit(0);
            /*Fix*/
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
        intializeBirtEngine();
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

            if (ServerConstants.YES.equalsIgnoreCase(reportDtls.getCredRequired()) && (ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(fileType)
                    || ServerConstants.REPORT_XLSX.equalsIgnoreCase(fileType))) {
                finalResponseJson = reportResponse(pMessage, reportResponse);
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

    private JSONObject reportResponse(Message pMessage, String reportResponse) {
        JSONObject finalResponseJson = null;
        String reportStatus = null;
        String mailStatus = null;
        reportResponse = this.generateEncodedReport(pMessage);
        JSONObject reportResponseJson = new JSONObject(reportResponse);
        if (reportResponseJson.has(ServerConstants.MESSAGE_HEADER_STATUS)) {
            reportStatus = (String) reportResponseJson.get(ServerConstants.MESSAGE_HEADER_STATUS);
            if (ServerConstants.SUCCESS.equalsIgnoreCase(reportStatus)) {
                String linterfaceId = pMessage.getHeader().getInterfaceId();
                mailStatus = this.sendMailtoUser(pMessage);
                pMessage.getHeader().setInterfaceId(linterfaceId);
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
        return finalResponseJson;
    }

    @Override
    public void validateRequest(Message pMessage) {
        LOG.debug("{} Validate Request and payLoad : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        try {
            JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
            JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
            JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
            String fileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);
            JSONObject reportParam = (JSONObject) reportData.get(ServerConstants.REPORT_PARAMS);
            LOG.debug("{} report parameter : {}, fileType is : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, reportParam, fileType);
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

    @SuppressWarnings("unchecked")
    @Override
    public String generateEncodedReport(Message pMessage) {
        LOG.debug("{} Generate Encoded Report and Request {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
        JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
        String lFileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);
        JSONObject reportParam = (JSONObject) reportData.get(ServerConstants.REPORT_PARAMS);
        Map<String, Object> parameters = JSONUtils.buildParamMap(reportParam);
        String birtDesignfile = pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + ".rptdesign";
        String destinationfilePath = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
        LOG.debug("{} destination file Path :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, destinationfilePath);
        //Changes made by Samy on 27/07/2017 for Bug-18302
        if (destinationfilePath != null && destinationfilePath.startsWith("${sys:")) {
            String destinationFilePathSysVar = destinationfilePath.replace("${sys:", "").replace("}", "");
            LOG.debug("{} Destination file Path System variable :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, destinationFilePathSysVar);
            destinationfilePath = System.getProperty(destinationFilePathSysVar);
            LOG.debug("{} destination file Path From System property :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, destinationfilePath);
        }

        destinationfilePath = Utils.filePathCheck(destinationfilePath);
        LOG.debug("{} Destination file Path after check :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, destinationfilePath);
        String lFileExt = "";
        JSONObject responseJson = new JSONObject();
        String reportName = getReportName(reportDetails);
        Connection connection = DBUtils.getConnectionFromDataSource(reportDtls.getDataSource());

        IRunAndRenderTask runAndRender = null;
        try {
            IReportRunnable reportRunnable = null;
            LOG.debug("Going to Load the design file from : {}", (destinationfilePath + birtDesignfile));
            reportRunnable = re.openReportDesign(destinationfilePath + birtDesignfile);
            runAndRender = re.createRunAndRenderTask(reportRunnable);
            runAndRender.setParameterValues(parameters);
            RenderOption options = new RenderOption();
            LOG.info("Report Type is : {}", lFileType);
            lFileExt = getFileExt(lFileType, destinationfilePath, lFileExt, reportName, options);
            runAndRender.setRenderOption(options);
            runAndRender.getAppContext().put("OdaJDBCDriverPassInConnection", connection);
            // setting start time for external service
            Utils.setExtTime(pMessage, "S");
            runAndRender.run();
            // setting end time for external service
            Utils.setExtTime(pMessage, "E");

            String fileName = destinationfilePath + reportName + lFileExt;
            LOG.debug("{} File Name :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileName);

            if (ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType)) {
                PDFMetaDataManager.insertMetadata(fileName);
            }

            if (ServerConstants.YES.equalsIgnoreCase(reportDtls.getCredRequired())) {
                fileName = getFileName(lFileType, destinationfilePath, reportName, fileName);
                deleteTempFileForPwdProtect(fileName);
            }

            File finalGeneratedReportFile = fileService.getFile(fileName);
            LOG.debug("{} Exported.....:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileName);
            byte[] byteFile = bytesFromFile(finalGeneratedReportFile);
            String base64 = Base64.encodeBase64String(byteFile);
            responseJson.put(ServerConstants.REPORT_FILE, base64);
            responseJson.put(ServerConstants.REPORT_FILENAME, reportName + lFileExt);

            deleteFile(fileName);

            responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        } catch (EngineException engineException) {
            LOG.error("{} EngineException :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, engineException);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode("APZ-FM-EX-034");
            exsrvcallexp.setMessage(engineException.getMessage());
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (DocumentException docExp) {
            LOG.error("{} DocumentException :: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, docExp);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
            exsrvcallexp.setMessage(docExp.getMessage());
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (IOException e) {
            LOG.error("{} IOException : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
            exsrvcallexp.setMessage(e.getMessage());
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            LOG.info("Shutting Down Report Engine");
            if (runAndRender != null)
                runAndRender.close();
            DBUtils.closeDbConnection(connection);
        }
        return responseJson.toString();
    }

    private void deleteFile(String fileName) {
        //changes starts here to delete temp file
        LOG.debug("Deleting temp file");
        if (!Utils.deleteFile(fileName)) {
            LOG.debug("{} file : {}  couldn't get deleted, due to above reasons", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileName);
        }
        //changes end here to delete temp file
    }

    private String getFileName(String lFileType, String destinationfilePath, String reportName, String fileName) throws IOException, DocumentException {
        if (ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType)) {
            LOG.debug("PDF file is password protected.. So going to apply password in pdf.");
            String passwordProtectedPdfLocation = destinationfilePath + reportName + TEMP + ServerConstants.REPORT_PDF_FILE_EXT;
            PdfReader reader = new PdfReader(fileName); // reading generated pdf file
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(passwordProtectedPdfLocation));
            stamper.setEncryption(cPassword.getBytes(), cPassword.getBytes(),
                    PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
            stamper.close();
            reader.close();
            fileName = passwordProtectedPdfLocation;
        } else if (ServerConstants.REPORT_XLSX.equalsIgnoreCase(lFileType)) {
            LOG.debug("XLSX file is password protected.. So going to apply password in XLSX.");
            fileName = getPasswordProtectedXLSXFile(fileName, cPassword);
        }
        return fileName;
    }

    private String getFileExt(String lFileType, String destinationfilePath, String lFileExt, String reportName, RenderOption options) {
        if (ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType)) {
            lFileExt = ServerConstants.REPORT_PDF_FILE_EXT;
            options.setOutputFormat(OUTPUT_FORMAT_PDF);
            options.setOutputFileName(destinationfilePath + reportName + lFileExt);
        } else if (ServerConstants.REPORT_XLS.equalsIgnoreCase(lFileType)) {
            lFileExt = ServerConstants.REPORT_XLS_EXT;
            options.setOutputFormat(ServerConstants.REPORT_XLS);
            options.setOutputFileName(destinationfilePath + reportName + lFileExt);
        } else if (ServerConstants.REPORT_XLSX.equalsIgnoreCase(lFileType)) {
            lFileExt = ServerConstants.REPORT_XLSX_EXT;
            options.setEmitterID("uk.co.spudsoft.birt.emitters.excel.XlsxEmitter");
            options.setOutputFileName(destinationfilePath + reportName + lFileExt);
        } else if (ServerConstants.REPORT_HTML.equalsIgnoreCase(lFileType)) {
            lFileExt = ServerConstants.REPORT_HTML_EXT;
            options.setOutputFormat(OUTPUT_FORMAT_HTML);
            options.setOutputFileName(destinationfilePath + reportName + lFileExt);
        }
        return lFileExt;
    }

    private String getReportName(JSONObject reportDetails) {
        String reportName = "";
        if (reportDetails.has(FILE_NAME) && !reportDetails.getString(FILE_NAME).isEmpty()) {
            reportName = reportDetails.getString(FILE_NAME);
        } else {
            Date date = new Date();
            reportName = Long.toString(date.getTime());
        }
        return reportName;
    }

    private void deleteTempFileForPwdProtect(String fileName) {
        //changes starts here to delete temp pasword protected file
        LOG.debug("deleting temp pasword protected file");
        if (fileName.contains(TEMP)) {
            LOG.debug("File name found with _temp");
            fileName = fileName.replace(TEMP, "");
        }
        if (!Utils.deleteFile(fileName)) {
            LOG.debug("{} file : {}  couldn't get deleted, due to above reasons", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fileName);
        }
        //changes ends here to delete temp pasword protected file
    }

    @Override
    public String generatePassword(Message pMessage, SpringCamelContext pContext) {
        LOG.debug("{} Inside generatePassword()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lUserID = pMessage.getHeader().getUserId();
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

    @Override
    public String genreratePDFReport(Message pMessage) {
        return null;
    }

    @Override
    public String generateExcelReport(Message pMessage) {
        return null;
    }

    // New changes for bugId - 34002
    private String getPasswordProtectedXLSXFile(String pFileName, String pPasswrd) {
        LOG.debug("{} inside getPasswordProtectedXLSXFile.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String outputFile = getEncXLSXTempFileName(pFileName);

        OPCPackage opc = null;
        OutputStream os = null;
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             POIFSFileSystem poiFileSystem = new POIFSFileSystem();) {
            EncryptionInfo encryptionInfo = new EncryptionInfo(EncryptionMode.standard);
            Encryptor enc = encryptionInfo.getEncryptor();
            enc.confirmPassword(pPasswrd);
            opc = OPCPackage.open(fileService.getFile(pFileName), PackageAccess.READ_WRITE);
            os = enc.getDataStream(poiFileSystem);
            opc.save(os);
            poiFileSystem.writeFilesystem(fos);
            LOG.debug("{} Password Protected XLSX File created at : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, outputFile);
        } catch (InvalidFormatException e) {
            LOG.error("{} InvalidFormatException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        } catch (IOException e) {
            LOG.error("{} IOException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        } catch (GeneralSecurityException e) {
            LOG.error("{} GeneralSecurityException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        } finally {
            try {
                if (opc != null) {
                    opc.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LOG.error("{} IOException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            }
        }
        return outputFile;
    }

    private String getEncXLSXTempFileName(String pFileName) {
        if (pFileName.contains(".xlsx")) {
            pFileName = pFileName.replaceAll(".xlsx", "_temp.xlsx");
        }
        return pFileName;
    }
    // New changes end for bugId - 34002
}
