package com.iexceed.plugins.autocapturedocument;
import android.util.Log;

import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mTextToDetect;

public class TextValidator {
     private static String TAG="TextValidator";
    private static String fireLastLine;
    private static String fireFirstLine;
    public static void setModifiedPassportLine(String lastLine){
        fireLastLine = lastLine;
    }
    public static String getModifiedPassportLine(){
        return fireLastLine;
    }
    public static void setModifiedTD1Line(String td1Line){
        fireFirstLine = td1Line;
    }
    public static String getModifiedTD1Line(){
        return fireFirstLine;
    }
    public static boolean validateDetectedText(String detectedText, String documentType) {
        boolean bool = false;
          Log.i(TAG, "DetectedText--- " + detectedText);

        try {
            if(verifyCard(detectedText)) {
                setModifiedPassportLine("");
                setModifiedTD1Line("");
                if (documentType.equalsIgnoreCase("passport")|| documentType.equalsIgnoreCase("td3")) {
                    bool = verifyPassport(detectedText);
                } else if (documentType.equalsIgnoreCase("omanCard") || documentType.equalsIgnoreCase("td1")) {
                    bool = verifyTD1(detectedText);
                } else {
                    bool = true;
                }
            }
        } catch (Exception e) {
            bool = false;
        }
        //  Log.i(TAG, "validateDetectedText: after" + bool);
        return bool;
    }
    private static boolean searchText(String textToSearch, String key) {
        return textToSearch.toUpperCase().contains(key.toUpperCase());
    }
    private static boolean verifyCard(String detectedText){
        boolean bool = false;
        try {
            if (!detectedText.equalsIgnoreCase("")) {
                if (ApzAutoCapturePlugin.mTextTypeToDetect.equalsIgnoreCase("All")) {
                    if (mTextToDetect.length() == 0) {
                        bool = false;
                        return bool;
                    }
                    for (int i = 0; i < mTextToDetect.length(); i++) {
                        String key = mTextToDetect.getString(i);
                        if (key.startsWith("$$")) {
                            if (findLastLine(detectedText, key)) {
                                bool = true;
                            } else {
                                bool = false;
                                return bool;
                            }
                        } else {
                            if (searchText(detectedText, key)) {
                                bool = true;
                            } else {
                                bool = false;
                                return bool;
                            }
                        }
                    }
                } else if (ApzAutoCapturePlugin.mTextTypeToDetect.equalsIgnoreCase("any")) {
                    if (mTextToDetect.length() == 0) {
                        mTextToDetect.put(0, " ");
                    }
                    for (int i = 0; i < mTextToDetect.length(); i++) {
                        String key = mTextToDetect.getString(i);
                        if (key.startsWith("$$")) {
                            if (findLastLine(detectedText, key)) {
                                bool = true;
                                return bool;
                            }
                        } else {
                            if (searchText(detectedText, key)) {
                                bool = true;
                                return bool;
                            }
                        }
                    }

                }
            }
        }catch(Exception e){
            bool = false;
        }
        return bool;
    }
    private static boolean verifyTD1(String textToSearch){
        boolean bool = true;
        try {
            String lastThreeLines= getLastNLines(textToSearch,3);
            Log.d(TAG,"verifyIdCard:-last three lines "+ lastThreeLines);
            String[] lastThreeLinesList = lastThreeLines.split("\n");
            Log.d(TAG,"verifyIdCard:-last three lines list  "+ lastThreeLinesList);
            if (lastThreeLinesList.length ==3) {
                String firstLine = lastThreeLinesList[0];
                String secondLine = lastThreeLinesList[1];
                String thirdLine = lastThreeLinesList[2];

                thirdLine = thirdLine.replaceAll("\\s", "");
                secondLine = secondLine.replaceAll("\\s", "");
                firstLine = firstLine.replaceAll("\\s", "");
                Log.d(TAG, "verifyIdCard:-thirdLine " + thirdLine);
                Log.d(TAG, "verifyIdCard:-secondLine " + secondLine);
                Log.d(TAG, "verifyIdCard:-firstLine " + firstLine);

                //space and char count check using regex
                if (thirdLine.matches("^[\\SA-Z<]{26,}$") &&
                        secondLine.matches("^[\\SA-Z0-9<]{26,}$") &&
                        firstLine.matches("^[\\SA-Z0-9<]{26,}$") &&
                        firstLine.matches("^[\\SA-Z]{5,}$") &&
                        secondLine.matches("^[\\S0-9]{5,}$")) {
                    Log.d(TAG, "verifyIdCard:-Passed the regex");
                    if (validateCheckSum(firstLine, 5, 13, 14)){
                        String oldLine= firstLine.substring(5,13);
                        String newLine= oldLine.toUpperCase().replace("O","0");
                        firstLine= firstLine.replace(oldLine,newLine);
                    }
                    setModifiedTD1Line(firstLine);
                    if (validateCheckSum(firstLine, 5, 13, 14) ){
                        if(validateCheckSum(secondLine, 0, 5, 6) &&
                                validateCheckSum(secondLine, 8, 13, 14)) {
                            Log.d(TAG, "verifyIdCard:-Success");
                            bool = true;
                        } else {
                            Log.d(TAG, "verifyIdCard:-Failure");
                            bool = false;
                        }
                    }else{
                        bool=false;
                    }
                } else {
                    Log.d(TAG, "verifyIdCard:-Failure");
                    bool = false;
                }
            }else{
                Log.d(TAG, "verifyIdCard:-Failure");
                bool = false;
            }
        }catch(Exception e){
            bool=false;
        }
        return bool;
    }
    private static boolean verifyPassport(String textToSearch){
        boolean bool = true;
        try {
            String lastTwoLines= getLastNLines(textToSearch,2);
            String lastBeforeLine = lastTwoLines.substring(0, lastTwoLines.lastIndexOf("\n"));
            String lastLine = lastTwoLines.substring(lastTwoLines.lastIndexOf("\n")).substring(1);

            //  Log.i(TAG, "verifyPassport: last two lines -----"+lastTwoLines);
            //space and char count check using regex
            if (lastBeforeLine.matches("^[\\S<]{40,}$") && lastLine.matches("^[\\sa-zA-Z0-9<]{40,}$")) {
                lastLine = lastLine.replaceAll("\\s", "");
                if (lastLine.matches("[a-zA-Z0-9<]{40,}$")) {
                    Log.i(TAG, "verifyPassport: text lastline"+lastLine);
                    if (!(validateCheckSum(lastLine, 0, 8, 9))) {
                       String oldLine= lastLine.substring(1,9);
                       String newLine= oldLine.toUpperCase().replace("O","0");
                       lastLine= lastLine.replace(oldLine,newLine);
                     //  validateCheckSum(lastLine,0,8,9);
                    }
                    setModifiedPassportLine(lastLine);
                    if ((validateCheckSum(lastLine, 0, 8, 9))) {
                        if ((validateCheckSum(lastLine, 13, 18, 19)) && (validateCheckSum(lastLine, 21, 26, 27))) {
                            bool = true;
                        } else {
                            bool = false;
                        }
                    }else{
                        bool=false;
                    }
                } else {
                    bool = false;
                }

            } else {
                bool = false;
            }
        }catch(Exception e){
            bool=false;
        }
        return bool;
    }
    private static boolean validateCheckSum(String lastLine,int startIndex,int endIndex,int checkCode){
        boolean bool=true;
        int sum = 0, weight = 0, multiplier = 0, modValue, checkDigit,modI=0;
        String lastElement=  lastLine;
        for (int i=startIndex; i <=(endIndex); i++) {
            modValue = modI%3;
            if (modValue == 0)
                weight = 7;
            else if (modValue == 1)
                weight = 3;
            else if (modValue == 2)
                weight = 1;

            if(lastElement.charAt(i)=='<'){
                multiplier=0;
            }else {
                if (Character.isDigit(lastElement.charAt(i))) {
                    multiplier = (int) lastElement.charAt(i) - '0';
                } else if (Character.isUpperCase(lastElement.charAt(i))) {
                    multiplier = lastElement.codePointAt(i) - 55; //a means 10, b means 11.. like that we have to go.
                } else {
                    bool = false;
                    break;
                }
            }

            sum += (multiplier * weight);
            modI++;
        }

        if (bool) {
            modValue = sum % 10;
            checkDigit = lastLine.charAt(checkCode)-'0'; // just convert the value to digit
            if (checkDigit != modValue) {
                bool = false;
            }
        }

        return bool;
    }
    private static String getLastNLines(String wholeText,int N){
        String [] splitText=wholeText.split("\n");
        String lastLineText= splitText[splitText.length-N];
        return wholeText.substring(wholeText.indexOf(lastLineText));
    }
    private static boolean findLastLine(String textToSearch, String key) {
        boolean matcher = false;
        try {
            String lastLine = textToSearch.substring(textToSearch.lastIndexOf("\n"));
            matcher = lastLine.substring(2).matches(key.substring(2));
            // Log.i(TAG, "findLastLine: " + lastLine + " matches " + lastLine.matches(key.substring(2)) + "lastline len" + lastLine.length());
        }catch(Exception e){
          //  failureCallback("Invalid regex","");
          //  lActivity.finish();
        }
        return matcher;
    }
}
