package com.iexceed.appzillon.domain.exception;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.EnumMap;
import java.util.Map;

public class DomainException extends AppzillonException {

    private static final long serialVersionUID = 1L;

    private static final Map<Code, String> eDomain = new EnumMap<>(Code.class);

    static {
        eDomain.put(Code.APZ_DM_000, "JSONException");
        eDomain.put(Code.APZ_DM_001, "User does not exist in database");
        eDomain.put(Code.APZ_DM_002, "No devices and groups are found for this application");
        eDomain.put(Code.APZ_DM_003, "File entry is not found in the dataBase");
        eDomain.put(Code.APZ_DM_004, "User is not authorised to view the dashboard of workflow");
        eDomain.put(Code.APZ_DM_005, "IllegalAccessException");
        eDomain.put(Code.APZ_DM_006, "IllegalArgumentException");
        eDomain.put(Code.APZ_DM_007, "Primary key columns are not found");
        eDomain.put(Code.APZ_DM_008, "No record found");
        eDomain.put(Code.APZ_DM_009, "Column value can not be null");
        eDomain.put(Code.APZ_DM_010, "Did not find the record in the table for corresponding primary key");
        eDomain.put(Code.APZ_DM_011, "N/A");
        eDomain.put(Code.APZ_DM_012, "This user does not exist, so no role exists for this user");
        eDomain.put(Code.APZ_DM_013, "User account is locked. Please try after some time");
        eDomain.put(Code.APZ_DM_014, "InvocationTargetException");
        eDomain.put(Code.APZ_DM_015, "Record already exists");
        eDomain.put(Code.APZ_DM_016, "No record for corresponding userid and appid");
        eDomain.put(Code.APZ_DM_017, "N/A");
        eDomain.put(Code.APZ_DM_018, "No role exists for this user");
        eDomain.put(Code.APZ_DM_019, "No record is available in the security parameter table for corresponding appid");
        eDomain.put(Code.APZ_DM_020, "N/A");
        eDomain.put(Code.APZ_DM_021, "Record already exists in rolemaster. Do you want to update screens, interfces and then go to update section?");
        eDomain.put(Code.APZ_DM_022, "Workflow sequence is not there in sequence generator table");
        eDomain.put(Code.APZ_DM_023, "Header's interface details are not found in interface master");
        eDomain.put(Code.APZ_DM_024, "Interface details are already found in interface master");
        eDomain.put(Code.APZ_DM_025, "Interface details doesn't not exist");
        eDomain.put(Code.APZ_DM_026, "Interface details doesn't not exist and can not be deleted");
        eDomain.put(Code.APZ_DM_027, "No device found with this applicationid in device tables");
        eDomain.put(Code.APZ_DM_028, "File not found at uploaded location");
        eDomain.put(Code.APZ_DM_029, "Device not found for userid");
        eDomain.put(Code.APZ_DM_030, "No record exists in security parameter table for corresponding appid");
        eDomain.put(Code.APZ_DM_032, "Failed to change password. Please check your username and password");
        eDomain.put(Code.APZ_DM_033, "Password is invalid. Please check your password.");
        eDomain.put(Code.APZ_DM_034, "Please choose different password, this is present in previous passwords");
        eDomain.put(Code.APZ_DM_035, "User already exists");
        eDomain.put(Code.APZ_DM_036, "Security parameters not found for the given appid");
        eDomain.put(Code.APZ_DM_037, "No devices are mapped");
        eDomain.put(Code.APZ_DM_038, "Please select a device to group");
        eDomain.put(Code.APZ_DM_039, "Device is already registered");
        eDomain.put(Code.APZ_DM_040, "User device is not registered");
        eDomain.put(Code.APZ_DM_041, "User mobile number is not registered");
        eDomain.put(Code.APZ_DM_042, "Invalid input. Number expected but got String");
        eDomain.put(Code.APZ_DM_043, "Password should not be blank");
        eDomain.put(Code.APZ_DM_044, "Security parameter details are not found for the appId -");
        eDomain.put(Code.APZ_DM_045, "User not authenticated");
        eDomain.put(Code.APZ_DM_046, "Invalid user credentials/User session is expired/Incorrect otp");
        eDomain.put(Code.APZ_DM_047, "Otp has expired");
        eDomain.put(Code.APZ_DM_048, "Otp Details doesn't exist");
        eDomain.put(Code.APZ_DM_049, "'?' can only be specfied for day-of-month or day-of-week");
        eDomain.put(Code.APZ_DM_050, "Unable to store job, because one already exists with this identification");
        eDomain.put(Code.APZ_DM_051, "Scheduler has stopped already");
        eDomain.put(Code.APZ_DM_052, "Job is already deleted");
        eDomain.put(Code.APZ_DM_053, "User is pending for authorization. Please contact administrator");
        eDomain.put(Code.APZ_DM_054, "User is either locked or inactive. Please contact administrator");
        eDomain.put(Code.APZ_DM_055, "InterfaceId from the header is not authorized for user");
        eDomain.put(Code.APZ_DM_056, "No screen authorized for this userid");
        eDomain.put(Code.APZ_DM_057, "No interface authorized for this userid");
        eDomain.put(Code.APZ_DM_058, "No screen, interface and control mapped to the user");
        eDomain.put(Code.APZ_DM_059, "User otp resend attempt is locked. Please wait till timeout");
        eDomain.put(Code.APZ_DM_060, "Attempts to validate otp has reached maximum tries and otp expired .Please request new otp");
        eDomain.put(Code.APZ_DM_061, "Otp is expired due to numerous attempts of resend otp. Please request new otp");
        eDomain.put(Code.APZ_DM_062, "Otp resend feature is not enabled. Change the security params to access");
        eDomain.put(Code.APZ_DM_063, "Otp is already processed");
        eDomain.put(Code.APZ_DM_064, "Password is not allowed to set by user");
        eDomain.put(Code.APZ_DM_065, "Same user is not allowed to modify and authorize");
        eDomain.put(Code.APZ_DM_066, "User is already authorized");
        eDomain.put(Code.APZ_DM_067, "Allowed appid is not present in master table");
        eDomain.put(Code.APZ_DM_068, "No transaction found with the transaction ref no value");
        eDomain.put(Code.APZ_DM_069, "No key/value pair exists");
        eDomain.put(Code.APZ_DM_070, "Conversational UI Service failed");
        eDomain.put(Code.APZ_DM_071, "Either request is invalid or processed already");
        eDomain.put(Code.APZ_DM_072, "Invalid server nonce or session token");
        eDomain.put(Code.APZ_DM_073, "Server nonce is expired");
        eDomain.put(Code.APZ_DM_074, "Username/Password is invalid. Please check");
        eDomain.put(Code.APZ_DM_075, "Invalid access token");
        eDomain.put(Code.APZ_DM_076, "Access token is expired");
        eDomain.put(Code.APZ_DM_077, "Cookie is expired");
        eDomain.put(Code.APZ_DM_079, "Invalid user cookie");
        eDomain.put(Code.APZ_DM_080, "Otp regeneration count exceeded/ already validated");
        eDomain.put(Code.APZ_DM_081, "Otp regeneration is not enabled");
        eDomain.put(Code.APZ_DM_082, "Provide clientnonce in appzillonHeader");
        eDomain.put(Code.APZ_DM_083, "Otp validation count has exceeded");
        eDomain.put(Code.APZ_DM_084, "UnsupportedEncodingException");
        eDomain.put(Code.APZ_DM_085, "File path is tampered.");
        eDomain.put(Code.APZ_APP_SIGN_FAULT, "Signature does not match");
    }

    String code;
    String message;

    private DomainException() {

    }

    public static DomainException getDomainExceptionInstance() {
        return new DomainException();
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

    public String getDomainExceptionMessage(Object key) {
        return eDomain.get(key);

    }

    public enum Code {

        APZ_DM_000, APZ_DM_001, APZ_DM_002, APZ_DM_003, APZ_DM_004, APZ_DM_005, APZ_DM_006, APZ_DM_007, APZ_DM_008, APZ_DM_009,
        APZ_DM_010, APZ_DM_011, APZ_DM_012, APZ_DM_013, APZ_DM_014, APZ_DM_015, APZ_DM_016, APZ_DM_017, APZ_DM_018, APZ_DM_019,
        APZ_DM_020, APZ_DM_021, APZ_DM_022, APZ_DM_023, APZ_DM_024, APZ_DM_025, APZ_DM_026, APZ_DM_027, APZ_DM_028, APZ_DM_029,
        APZ_DM_030, APZ_DM_031, APZ_DM_032, APZ_DM_033, APZ_DM_034, APZ_DM_035, APZ_DM_036, APZ_DM_037, APZ_DM_038, APZ_DM_039,
        APZ_DM_040, APZ_DM_041, APZ_DM_042, APZ_DM_043, APZ_DM_044, APZ_DM_045, APZ_DM_046, APZ_DM_047, APZ_DM_048, APZ_DM_049,
        APZ_DM_050, APZ_DM_051, APZ_DM_052, APZ_DM_053, APZ_DM_054, APZ_DM_055, APZ_DM_056, APZ_DM_057, APZ_DM_058, APZ_DM_059,
        APZ_DM_060, APZ_DM_061, APZ_DM_062, APZ_DM_063, APZ_DM_064, APZ_DM_065, APZ_DM_066, APZ_DM_067, APZ_DM_068, APZ_DM_069,
        APZ_DM_070, APZ_DM_071, APZ_DM_072, APZ_DM_073, APZ_DM_074, APZ_DM_075, APZ_DM_076, APZ_DM_077, APZ_DM_079,
        APZ_DM_080, APZ_DM_081, APZ_DM_082, APZ_DM_083, APZ_DM_084, APZ_DM_085, APZ_APP_SIGN_FAULT;


        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }
}
