package com.iexceed.plugins.autocapturedocument

import android.util.Log

object TextValidator {
    private val TAG: String = "TextValidator"
    var modifiedPassportLine: String? = null
    var modifiedTD1Line: String? = null
    @JvmStatic
    fun validateDetectedText(detectedText: String, documentType: String): Boolean {
        var bool: Boolean = false
        Log.i(TAG, "DetectedText--- " + detectedText)
        try {
            if (verifyCard(detectedText)) {
                modifiedPassportLine = ""
                modifiedTD1Line = ""
                if (documentType.equals("passport", ignoreCase = true) || documentType.equals(
                        "td3",
                        ignoreCase = true
                    )
                ) {
                    bool = verifyPassport(detectedText)
                } else if (documentType.equals(
                        "omanCard",
                        ignoreCase = true
                    ) || documentType.equals("td1", ignoreCase = true)
                ) {
                    bool = verifyTD1(detectedText)
                } else {
                    bool = true
                }
            }
        } catch (e: Exception) {
            bool = false
        }
        return bool
    }

    private fun searchText(textToSearch: String, key: String): Boolean {
        return textToSearch.toUpperCase().contains(key.toUpperCase())
    }

    private fun verifyCard(detectedText: String): Boolean {
        var bool: Boolean = false
        try {
            if (!detectedText.equals("", ignoreCase = true)) {
                if (ApzAutoCapturePlugin.mTextTypeToDetect.equals("All", ignoreCase = true)) {
                    detectTextTypeAll(bool, detectedText)
                } else if (ApzAutoCapturePlugin.mTextTypeToDetect.equals(
                        "any",
                        ignoreCase = true
                    )
                ) {
                    bool = detectTextTypeAny(detectedText)
                }
            }
        } catch (e: Exception) {
            bool = false
        }
        return bool
    }

    private fun detectTextTypeAll(bool: Boolean, detectedText: String): Boolean {
        var bool1 = bool
        if (ApzAutoCapturePlugin.mTextToDetect!!.length() == 0) {
            bool1 = false
            return bool1
        }
        for (i in 0 until ApzAutoCapturePlugin.mTextToDetect!!.length()) {
            val key: String = ApzAutoCapturePlugin.mTextToDetect!!.getString(i)
            if (key.startsWith("$$")) {
                if (findLastLine(detectedText, key)) {
                    bool1 = true
                } else {
                    bool1 = false
                    return bool1
                }
            } else {
                if (searchText(detectedText, key)) {
                    bool1 = true
                } else {
                    bool1 = false
                    return bool1
                }
            }
        }
        return bool1
    }

    private fun detectTextTypeAny(detectedText: String): Boolean {
        var bool1 = false
        if (ApzAutoCapturePlugin.mTextToDetect!!.length() == 0) {
            ApzAutoCapturePlugin.mTextToDetect!!.put(0, " ")
        }
        for (i in 0 until ApzAutoCapturePlugin.mTextToDetect!!.length()) {
            val key: String = ApzAutoCapturePlugin.mTextToDetect!!.getString(i)
            if (key.startsWith("$$")) {
                if (findLastLine(detectedText, key)) {
                    bool1 = true
                    return bool1
                }
            } else {
                if (searchText(detectedText, key)) {
                    bool1 = true
                    return bool1
                }
            }
        }
        return bool1
    }

    private fun verifyTD1(textToSearch: String): Boolean {
        var bool = true
        try {
            val lastThreeLines: String = getLastNLines(textToSearch, 3)
            val lastThreeLinesList: Array<String> = lastThreeLines.split("\n").toTypedArray()
            if (lastThreeLinesList.size == 3) {
                var firstLine: String = lastThreeLinesList.get(0)
                var secondLine: String = lastThreeLinesList.get(1)
                var thirdLine: String = lastThreeLinesList.get(2)
                thirdLine = thirdLine.replace("\\s".toRegex(), "")
                secondLine = secondLine.replace("\\s".toRegex(), "")
                firstLine = firstLine.replace("\\s".toRegex(), "")

                //space and char count check using regex
                bool = checkSpaceAndCharCount(thirdLine, secondLine, firstLine, bool)
            } else {
                bool = false
            }
        } catch (e: Exception) {
            bool = false
        }
        return bool
    }

    private fun checkSpaceAndCharCount(
        thirdLine: String,
        secondLine: String,
        firstLine: String,
        bool: Boolean
    ): Boolean {
        var firstLine1 = firstLine
        var bool1 = bool
        if (thirdLine.matches("^[\\SA-Z<]{26,}$".toRegex()) &&
            secondLine.matches("^[\\SA-Z0-9<]{26,}$".toRegex()) &&
            firstLine1.matches("^[\\SA-Z0-9<]{26,}$".toRegex()) &&
            firstLine1.matches("^[\\SA-Z]{5,}$".toRegex()) &&
            secondLine.matches("^[\\S0-9]{5,}$".toRegex())
        ) {
            Log.d(TAG, "verifyIdCard:-Passed the regex")
            if (validateCheckSum(firstLine1, 5, 13, 14)) {
                val oldLine: String = firstLine1.substring(5, 13)
                val newLine: String = oldLine.toUpperCase().replace("O", "0")
                firstLine1 = firstLine1.replace(oldLine, newLine)
            }
            modifiedTD1Line = firstLine1
            if (validateCheckSum(firstLine1, 5, 13, 14)) {
                if (validateCheckSum(secondLine, 0, 5, 6) &&
                    validateCheckSum(secondLine, 8, 13, 14)
                ) {
                    Log.d(TAG, "verifyIdCard:-Success")
                    bool1 = true
                } else {
                    bool1 = false
                }
            } else {
                bool1 = false
            }
        } else {
            bool1 = false
        }
        return bool1
    }

    private fun verifyPassport(textToSearch: String): Boolean {
        var bool: Boolean = true
        try {
            val lastTwoLines: String = getLastNLines(textToSearch, 2)
            val lastBeforeLine: String = lastTwoLines.substring(0, lastTwoLines.lastIndexOf("\n"))
            var lastLine: String =
                lastTwoLines.substring(lastTwoLines.lastIndexOf("\n")).substring(1)

            if (lastBeforeLine.matches("^[\\S<]{40,}$".toRegex()) && lastLine.matches("^[\\sa-zA-Z0-9<]{40,}$".toRegex())) {
                lastLine = lastLine.replace("\\s".toRegex(), "")
                if (lastLine.matches("[a-zA-Z0-9<]{40,}$".toRegex())) {
                    Log.i(TAG, "verifyPassport: text lastline$lastLine")
                    if (!validateCheckSum(lastLine, 0, 8, 9)) {
                        val oldLine = lastLine.substring(1, 9)
                        val newLine = oldLine.toUpperCase().replace("O", "0")
                        lastLine = lastLine.replace(oldLine, newLine)
                    }
                    modifiedPassportLine = lastLine
                    bool = if ((validateCheckSum(lastLine, 0, 8, 9))) {
                        (validateCheckSum(lastLine, 13, 18, 19)) && (validateCheckSum(
                                lastLine,
                                21,
                                26,
                                27
                            ))
                    } else {
                        false
                    }
                } else {
                    bool = false
                }
            } else {
                bool = false
            }
        } catch (e: Exception) {
            bool = false
        }
        return bool
    }

    private fun validateCheckSum(
        lastLine: String,
        startIndex: Int,
        endIndex: Int,
        checkCode: Int
    ): Boolean {
        var bool = true
        var sum = 0
        var weight = 0
        var multiplier: Int
        var modValue: Int
        val checkDigit: Int
        var modI = 0
        val lastElement: String = lastLine
        for (i in startIndex..(endIndex)) {
            modValue = modI % 3
            when (modValue) {
                0 -> weight = 7
                1 -> weight = 3
                2 -> weight = 1
            }
            if (lastElement.get(i) == '<') {
                multiplier = 0
            } else {
                if (Character.isDigit(lastElement.get(i))) {
                    multiplier = lastElement.get(i).toInt() - '0'.toInt()
                } else if (Character.isUpperCase(lastElement.get(i))) {
                    multiplier =
                        lastElement.codePointAt(i) - 55 //a means 10, b means 11.. like that we have to go.
                } else {
                    bool = false
                    break
                }
            }
            sum += (multiplier * weight)
            modI++
        }
        if (bool) {
            modValue = sum % 10
            checkDigit = lastLine.get(checkCode) - '0' // just convert the value to digit
            if (checkDigit != modValue) {
                bool = false
            }
        }
        return bool
    }

    private fun getLastNLines(wholeText: String, n: Int): String {
        val splitText: Array<String> = wholeText.split("\n").toTypedArray()
        val lastLineText: String = splitText.get(splitText.size - n)
        return wholeText.substring(wholeText.indexOf(lastLineText))
    }

    private fun findLastLine(textToSearch: String, key: String): Boolean {
        var matcher = false
        try {
            val lastLine: String = textToSearch.substring(textToSearch.lastIndexOf("\n"))
            matcher = lastLine.substring(2).matches(key.substring(2).toRegex())
        } catch (e: Exception) {
            //handle exception
        }
        return matcher
    }
}