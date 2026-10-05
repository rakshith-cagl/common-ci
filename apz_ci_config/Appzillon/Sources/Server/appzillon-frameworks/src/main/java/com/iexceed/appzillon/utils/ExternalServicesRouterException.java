package com.iexceed.appzillon.utils;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.EnumMap;
import java.util.Map;

public class ExternalServicesRouterException extends AppzillonException {

    private static final Map<EXCEPTION_CODE, String> FRAME_WORK_EXCEPTIONS = new EnumMap<>(EXCEPTION_CODE.class);
    /**
     *
     */
    private static final long serialVersionUID = -8959712983992152202L;
    String code;
    String message;

    private ExternalServicesRouterException() {

        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_000, "JSONException");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_001, "Failed while fetching LOV parameter list");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_002, "Unable to get spring camel context");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_003, "Unable to create producer template from spring camel context");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_004, "Unable to inject bean from bean id");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_005, "Total number of pages is less than the requested page number");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_006, "Failed while bulding response for the page size");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_007, "Failed while writting filter criteria query");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_008, "Failed while appending bind variables to the prepared statement");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_009, "Configured textMessage node not found in JMSRequest");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_010, "Exception occured while getting the response from the Database");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_011, "Exception occured while JMS exchange");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_012, "Email id is not found in request");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_013, "Number of result fields is not equal to the result data types");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_014, "Service mismatched for external service");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_015, "Number of bindvariables columns are not equal with number of bind values");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_016, "Failed while appending bind variables to the query");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_017, "Failed to receive the response from external system, please try again later");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_018, "Failed to receive ISO 8583 sign on response, please try again later");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_019, "Failed while sending ISO 8583 sign on request, please try again later");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_020, "LDAP error ");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_021, "User created successfully but failed while sending mail, please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_022, "User password reseted successfully but failed while sending mail, please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_023, "Failed while sending mail, please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_024, "Unable to find interface details for the requested external service interface");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_025, "Invalid email addresses");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_026, "Invalid Request format");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_027, "Database not connected");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_028, "NamingException. Database is not connected");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_029, "SQLException. Database is not connected");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_030, "JSON Exception");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_031, "File not found exception");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_032, "Report created but failed while mailing password for report");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_033, "reportData not found in appzillonBody");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_034, "Database not connected for report");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_035, "Authentication failed, please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_036, "Failed while sending mail, please check your email address");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_037, "More than one record found");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_038, "No data found");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_039, "Request is null");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_040, "Server error");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_041, "Data doesn't exist");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_042, "Data already exists");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_043, "Database exception");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_044, "Mail details not found");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_045, "Record is already authorized");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_046, "User already exists and active");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_047, "Phone number already registered for SMS or USSD services");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_048, "User not allowed to access this appid");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_049, "AppId can not be empty");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_050, "Password changed successfully but failed while sending mail, please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_051, "User registered successfully but failed while sending mail, please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_052, "Same user is not allowed to modify and authorize");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_053, "User authorised successfully but failed while sending mail, please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_054, "Invalid socket details. Please check IP and PORT");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_055, "Server not found");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_056, "Invalid ISO8583 bitmap");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_057, "Invalid Socket Details.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_058, "Unkown Host.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_059, "Unable to connect / Connection Timeout.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_060, "Failed to receive response / read Timeout.");
    }

    public static ExternalServicesRouterException getExternalServicesRouterExceptionInstance() {
        return new ExternalServicesRouterException();
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrameWorksExceptionMessage(Object key) {
        return FRAME_WORK_EXCEPTIONS.get(key);

    }

    public enum EXCEPTION_CODE {
        APZ_FM_EX_000("APZ_FM_EX_000"),
        APZ_FM_EX_001("APZ_FM_EX_001"), APZ_FM_EX_002("APZ_FM_EX_002"), APZ_FM_EX_003(
                "APZ_FM_EX_003"), APZ_FM_EX_004("APZ_FM_EX_004"), APZ_FM_EX_005(
                "APZ_FM_EX_005"), APZ_FM_EX_006("APZ_FM_EX_006"), APZ_FM_EX_007(
                "APZ_FM_EX_007"), APZ_FM_EX_008("APZ_FM_EX_008"), APZ_FM_EX_009(
                "APZ_FM_EX_009"), APZ_FM_EX_010("APZ_FM_EX_010"), APZ_FM_EX_011(
                "APZ_FM_EX_011"), APZ_FM_EX_012("APZ_FM_EX_012"), APZ_FM_EX_013("APZ_FM_EX_013"),
        APZ_FM_EX_014("APZ_FM_EX_014"), APZ_FM_EX_015("APZ_FM_EX_015"), APZ_FM_EX_016("APZ_FM_EX_016"),
        APZ_FM_EX_017("APZ_FM_EX_017"), APZ_FM_EX_018("APZ_FM_EX_018"), APZ_FM_EX_019("APZ_FM_EX_019"),
        APZ_FM_EX_020("APZ_FM_EX_020"), APZ_FM_EX_021("APZ_FM_EX_021"), APZ_FM_EX_022("APZ_FM_EX_022"),
        APZ_FM_EX_023("APZ_FM_EX_023"), APZ_FM_EX_024("APZ_FM_EX_024"), APZ_FM_EX_025("APZ_FM_EX_025"),
        APZ_FM_EX_026("APZ_FM_EX_026"), APZ_FM_EX_027("APZ_FM_EX_027"), APZ_FM_EX_028("APZ_FM_EX_028"),
        APZ_FM_EX_029("APZ_FM_EX_029"), APZ_FM_EX_030("APZ_FM_EX_030"), APZ_FM_EX_031("APZ_FM_EX_031"),
        APZ_FM_EX_032("APZ_FM_EX_032"), APZ_FM_EX_033("APZ_FM_EX_033"), APZ_FM_EX_034("APZ_FM_EX_034"),
        APZ_FM_EX_035("APZ_FM_EX_035"), APZ_FM_EX_036("APZ_FM_EX_036"), APZ_FM_EX_037("APZ_FM_EX_037"),
        APZ_FM_EX_038("APZ_FM_EX_038"), APZ_FM_EX_039("APZ_FM_EX_039"), APZ_FM_EX_040("APZ_FM_EX_040"),
        APZ_FM_EX_041("APZ_FM_EX_041"), APZ_FM_EX_042("APZ_FM_EX_042"), APZ_FM_EX_043("APZ_FM_EX_043"),
        APZ_FM_EX_044("APZ_FM_EX_044"), APZ_FM_EX_045("APZ_FM_EX_045"), APZ_FM_EX_046("APZ_FM_EX_046"), APZ_FM_EX_047("APZ_FM_EX_047"),
        APZ_FM_EX_048("APZ_FM_EX_048"), APZ_FM_EX_049("APZ_FM_EX_049"), APZ_FM_EX_050("APZ_FM_EX_050"),
        APZ_FM_EX_051("APZ_FM_EX_051"), APZ_FM_EX_052("APZ_FM_EX_052"), APZ_FM_EX_053("APZ_FM_EX_053"),
        APZ_FM_EX_054("APZ_FM_EX_054"), APZ_FM_EX_055("APZ_FM_EX_055"), APZ_FM_EX_056("APZ_FM_EX_056"),
        APZ_FM_EX_057("APZ_FM_EX_057"), APZ_FM_EX_058("APZ_FM_EX_058"),
        APZ_FM_EX_059("APZ_FM_EX_059"), APZ_FM_EX_060("APZ_FM_EX_060");
        String exCode;

        private EXCEPTION_CODE(String exCode) {
            this.exCode = exCode;
        }

        @Override
        public String toString() {
            return exCode.replace('_', '-');
        }
    }
}
