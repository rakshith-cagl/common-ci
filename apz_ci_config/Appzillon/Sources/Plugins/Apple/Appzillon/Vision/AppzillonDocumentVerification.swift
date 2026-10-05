//
//  AppzillonDocumentVerification.swift
//  Appzillon
//
//  Created by Manu Gowda N R on 6/9/20.
//
// swiftlint:disable all
import Foundation
import Firebase

class AppzillonDocumentVerification: NSObject {
    var appzillonAlphaNumericDict: [String: Int] = [:]
    // Passport checksum reverify
    var passportChecksumReverify: Bool = false
    var passportNewResultText: String = ""
    var passportNewOcrTextArray: [[String: Any]] = []
    // TD1 checksum reverify
    var td1ChecksumReverify: Bool = false
    var td1NewResultText: String = ""
    var td1NewOcrTextArray: [[String: Any]] = []
    public override init() {
        super.init()
        initilizeFirebase()
        createAlphaNumericDictionary()
        self.passportChecksumReverify = false
        self.passportNewResultText = ""
        self.td1ChecksumReverify = false
        self.td1NewResultText = ""
        self.td1NewOcrTextArray = []
        self.passportNewOcrTextArray = []
    }
    private func initilizeFirebase() {
        // Configure firebase
        if FirebaseApp.app() == nil,
           let filePath = Bundle.main.path(forResource: "GoogleServiceInfo", ofType: "plist") {
            guard let firebaseOptions = FirebaseOptions(contentsOfFile: filePath) else { return  }
            FirebaseApp.configure(options: firebaseOptions)
        }
    }
    // MARK: Appzillon Customizations ----- Verify document
    func verifyDocument(requestJson: [String: Any], myRequiredImage: UIImage,
                        completionHandler: @escaping(_ FirebaseResult: [String: Any]) -> Void) {
        var textToDetect: [String: Any] = ["": ""]
        if let text = requestJson["textToDetect"] as? [String: Any] {
            textToDetect = text
        }
        var firebaseResult: [String: Any] = [:]
        firebaseResult["validation"] = false
        // Firebase start
        DispatchQueue.global(qos: .background).async {
            let vision = Vision.vision()
            let textRecognizer = vision.onDeviceTextRecognizer()
            let visionImage = VisionImage(image: myRequiredImage)
            textRecognizer.process(visionImage) { result, error in
                guard error == nil, let result = result else {
                    firebaseResult["validation"] = false
                    completionHandler(firebaseResult)
                    return
                }
                let resultText = result.text
                var documentType = ""
                if let docType = requestJson["documentType"] {
                    documentType = (docType as? String ?? StringConstants.Generic.emptyString)
                    documentType = documentType.uppercased()
                }
                var validation = true
                if  textToDetect.count == 2 || textToDetect.keys.count == 2 {
                    if let checkArray: [String] = textToDetect["text"] as? [String] {
                    let checkFor: String = textToDetect["type"] as? String ?? StringConstants.Generic.emptyString
                    if checkFor == "all"{
                        validation = true
                        for check in checkArray {
                            if check.hasPrefix("$$") {
                                let linesArray = resultText.split { $0.isNewline }
                                let myLastLine = linesArray.last
                                let myCheckString = check.dropFirst(2)
                                let range = NSRange(location: 0, length: myLastLine!.utf16.count)
                                if let regex = try? NSRegularExpression(pattern: String(myCheckString)) {
                                    let checkResult = regex.firstMatch(in: String(myLastLine!),
                                                                       options: [],
                                                                       range: range) != nil
                                    if !checkResult {
                                    validation = false
                                }
                              }
                            } else {
                                if !check.isEmpty {
                                    if !resultText.contains(check) {
                                        validation = false
                                    } else {
                                        print("check exists = \(check)")
                                    }
                                } else {
                                    validation = true
                                }
                            }
                        }
                    } else {
                        validation = false
                        for check in checkArray {
                            if check.hasPrefix("$$") {
                                let linesArray = resultText.split { $0.isNewline }
                                let myLastLine = linesArray.last
                                let myCheckString = check.dropFirst(2)
                                let range = NSRange(location: 0, length: myLastLine!.utf16.count)
                                if let regex = try? NSRegularExpression(pattern: String(myCheckString)) {
                                    let checkResult = regex.firstMatch(
                                        in: String(myLastLine!), options: [], range: range) != nil
                                    if checkResult {
                                        validation = true
                                    }
                                }
                            } else {
                                if !check.isEmpty {
                                    if resultText.contains(check) {
                                        validation = true
                                    } else {
                                        print("check not exists = \(check)")
                                    }
                                } else {
                                    validation = true
                                }
                            }
                        }
                    }
                }
            }
                var ocrTextArray: [[String: Any]] = []
                if validation {
                    for block in result.blocks {
                        var blockDict: [String: Any] = [:]
                        let blockText = block.text
                        blockDict["text"] = blockText
                        var arrayOfPointsDict: [Any] = []
                        let blockCornerPoints = block.cornerPoints
                        for cornerPoint in blockCornerPoints! {
                            var pointDict: [String: Any] = [:]
                            let point: CGPoint = cornerPoint.cgPointValue
                            pointDict["x"] = Int(point.x)
                            pointDict["y"] = Int(point.y)
                            arrayOfPointsDict.append(pointDict)
                        }
                        blockDict["bounds"] = arrayOfPointsDict
                        ocrTextArray.append(blockDict)
                    }
                    if documentType == "OMANCARD" || documentType == "TD1" {
                        let td1VerificationFlag  = self.td1Verification(resultText: resultText,
                                                                        resultOcrArray: ocrTextArray)
                        print("td1VerificationFlag = \(td1VerificationFlag)")
                        if td1VerificationFlag {
                            if self.td1ChecksumReverify {
                                firebaseResult["ocrWholeText"] = self.td1NewResultText
                                firebaseResult["ocrText"] = self.td1NewOcrTextArray
                            } else {
                                firebaseResult["ocrWholeText"] = resultText
                                firebaseResult["ocrText"] = ocrTextArray
                            }
                            firebaseResult["validation"] = true
                            print("td1ChecksumReverify = \(self.td1ChecksumReverify)")
                            self.td1ChecksumReverify = false
                            completionHandler(firebaseResult)
                        } else {
                            firebaseResult["validation"] = false
                            completionHandler(firebaseResult)
                        }
                    } else if documentType == "PASSPORT" || documentType == "TD3" {
                        let passportVerificationFlag = self.passportVerification(resultText:
                                                                                    resultText,
                                                                                 resultOcrArray: ocrTextArray)
                        print("passportVerificationFlag = \(passportVerificationFlag)")
                        if passportVerificationFlag {
                            if self.passportChecksumReverify {
                                firebaseResult["ocrWholeText"] = self.passportNewResultText
                                firebaseResult["ocrText"] = self.passportNewOcrTextArray
                            } else {
                                firebaseResult["ocrWholeText"] = resultText
                                firebaseResult["ocrText"] = ocrTextArray
                            }
                            firebaseResult["validation"] = true
                            print("passportChecksumReverify = \(self.passportChecksumReverify)")
                            self.passportChecksumReverify = false
                            completionHandler(firebaseResult)
                        } else {
                            firebaseResult["validation"] = false
                            completionHandler(firebaseResult)
                        }
                    } else {
                        firebaseResult["ocrText"] = ocrTextArray
                        firebaseResult["ocrWholeText"] = resultText
                        firebaseResult["validation"] = true
                        completionHandler(firebaseResult)
                    }
                } else {
                    firebaseResult["validation"] = false
                    completionHandler(firebaseResult)
                }
            }
        }
    }
    // MARK: Appzillon Customizations ----- Passport and TD3 verification
    func passportVerification(resultText: String, resultOcrArray: [[String: Any]]) -> Bool {
        let resultValue = resultText as String?
        var resultArray: [String] = []
        resultValue?.enumerateLines { line, _ in
            resultArray.append(line)
        }
        guard var lastElement = resultArray.last else {
            return false
        }
        let mylastElementToCompare = lastElement
        let secondElement = resultArray.dropLast()
        guard let secondLastElement = secondElement.last else {
            return false
        }
        var flag = true
        if lastElement.contains("<") && secondLastElement.contains("<")
            && String(lastElement).count > 40
            && String(secondLastElement).count > 40 {
            if secondLastElement.contains(" ") {
                flag = false
            }
            if flag {
                if lastElement.contains(" ") {
                    lastElement = lastElement.filter {!$0.isWhitespace}
                    if lastElement.count <= 40 {
                        flag = false
                    }
                }
            }
            if flag {
                flag = self.appzillonChecksumVerification(lastElementString: lastElement,
                                                          firstIndex: 0, lastIndex: 8,
                                                          ckeckIndex: 9)
                if !flag {
                    lastElement = lastElement.uppercased()
                    let index = lastElement.index(lastElement.startIndex, offsetBy: 9)
                    var mySubstring = lastElement[..<index]
                    mySubstring.remove(at: mySubstring.startIndex)
                    if mySubstring.contains("O") {
                        let newString = mySubstring.replacingOccurrences(of: "O",
                                                                         with: "0",
                                                                         options: .literal,
                                                                         range: nil)
                        lastElement = lastElement.replacingOccurrences(of: mySubstring,
                                                                       with: newString,
                                                                       options: .literal,
                                                                       range: nil)
                        flag = self.appzillonChecksumVerification(lastElementString:
                                                                    lastElement,
                                                                  firstIndex: 0,
                                                                  lastIndex: 8,
                                                                  ckeckIndex: 9)
                        self.passportChecksumReverify = true
                        self.passportNewResultText = resultText.replacingOccurrences(of: mylastElementToCompare,
                                                                                     with: lastElement,
                                                                                     options: .literal, range: nil)
                        var newOcrArray: [[String: Any]] = []
                        for blockDict in resultOcrArray {
                            var newBlockDict: [String: Any] = blockDict
                            var myStr = newBlockDict["text"] as? String ?? StringConstants.Generic.emptyString
                            if myStr.contains(mylastElementToCompare) {
                                myStr = myStr.replacingOccurrences(of: mylastElementToCompare,
                                                                   with: lastElement,
                                                                   options: .literal, range: nil)
                                newBlockDict["text"] = myStr
                            }
                            newOcrArray.append(newBlockDict)
                        }
                        self.passportNewOcrTextArray = newOcrArray
                    }
                }
                if flag {
                    flag = self.appzillonChecksumVerification(lastElementString: lastElement,
                                                              firstIndex: 13,
                                                              lastIndex: 18,
                                                              ckeckIndex: 19)
                }
                if flag {
                    flag = self.appzillonChecksumVerification(lastElementString:
                                                                lastElement,
                                                              firstIndex: 21,
                                                              lastIndex: 26,
                                                              ckeckIndex: 27)
                }
                if flag {
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        } else {
            return false
        }
    }
    // MARK: Appzillon Customizations ----- OmanCard and TD1 verification
    func td1Verification(resultText: String, resultOcrArray: [[String: Any]]) -> Bool {
        let resultValue = resultText as String?
        var resultArray: [String] = []
        resultValue?.enumerateLines { line, _ in
            resultArray.append(line)
        }
        guard var lastElement = resultArray.last else {
            return false
        }
        let secondElement = resultArray.dropLast()
        guard var secondLastElement = secondElement.last else {
            return false
        }
        let thirdElement = secondElement.dropLast()
        guard var thirdLastElement = thirdElement.last else {
            return false
        }
        let myThirdLastElementToCompare = thirdLastElement
        var flag = true
        if lastElement.contains("<") && secondLastElement.contains("<") &&
            thirdLastElement.contains("<") && String(lastElement).count > 25 &&
            String(secondLastElement).count > 25 && String(thirdLastElement).count > 25 {
            if lastElement.contains(" ") {
                lastElement = lastElement.filter {!$0.isWhitespace}
                if lastElement.count <= 25 {
                    flag = false
                }
            }
            if flag {
                if secondLastElement.contains(" ") {
                    secondLastElement = secondLastElement.filter {!$0.isWhitespace}
                    if secondLastElement.count <= 25 {
                        flag = false
                    }
                }
            }
            if flag {
                if thirdLastElement.contains(" ") {
                    thirdLastElement = thirdLastElement.filter {!$0.isWhitespace}
                    if thirdLastElement.count <= 25 {
                        flag = false
                    }
                }
            }
            if flag {
                flag = self.appzillonChecksumVerification(lastElementString: thirdLastElement,
                                                          firstIndex: 5,
                                                          lastIndex: 13,
                                                          ckeckIndex: 14)
                if !flag {
                    thirdLastElement = thirdLastElement.uppercased()
                    let index = thirdLastElement.index(thirdLastElement.startIndex, offsetBy: 14)
                    var mySubstring = thirdLastElement[..<index]
                    mySubstring = mySubstring.dropFirst(5)
                    if mySubstring.contains("O") {
                        let newString = mySubstring.replacingOccurrences(of: "O",
                                                                         with: "0",
                                                                         options: .literal,
                                                                         range: nil)
                        thirdLastElement = thirdLastElement.replacingOccurrences(of: mySubstring,
                                                                                 with: newString,
                                                                                 options: .literal,
                                                                                 range: nil)
                        flag = self.appzillonChecksumVerification(lastElementString:
                                                                    thirdLastElement,
                                                                  firstIndex: 5,
                                                                  lastIndex: 13,
                                                                  ckeckIndex: 14)
                        self.td1ChecksumReverify = true
                        self.td1NewResultText = resultText.replacingOccurrences(of:
                                                                                    myThirdLastElementToCompare,
                                                                                with: thirdLastElement,
                                                                                options: .literal, range: nil)
                        var newOcrArray: [[String: Any]] = []
                        for blockDict in resultOcrArray {
                            var newBlockDict: [String: Any] = blockDict
                            var myStr = newBlockDict["text"] as? String ?? StringConstants.Generic.emptyString
                            if myStr.contains(myThirdLastElementToCompare) {
                                myStr = myStr.replacingOccurrences(of:
                                                                    myThirdLastElementToCompare,
                                                                   with: thirdLastElement,
                                                                   options: .literal, range: nil)
                                newBlockDict["text"] = myStr
                            }
                            newOcrArray.append(newBlockDict)
                        }
                        self.td1NewOcrTextArray = newOcrArray
                    }
                }
                if flag {
                    flag = self.appzillonChecksumVerification(lastElementString:
                                                                secondLastElement,
                                                              firstIndex: 0,
                                                              lastIndex: 5,
                                                              ckeckIndex: 6)
                }
                if flag {
                    flag = self.appzillonChecksumVerification(lastElementString:
                                                                secondLastElement,
                                                              firstIndex: 8,
                                                              lastIndex: 13,
                                                              ckeckIndex: 14)
                }
                if flag {
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        } else {
            return false
        }
    }
    // MARK: Appzillon Customizations ----- Appzillon checksum verification
    func appzillonChecksumVerification(lastElementString: String,
                                       firstIndex: Int,
                                       lastIndex: Int,
                                       ckeckIndex: Int) -> Bool {
        var checksumFlag = true
        var sum = 0
        var weight = 0
        var multiplier = 0
        var modValue = 0
        var checkDigit = 0
        var value = 0
        for myIndex in firstIndex...lastIndex {
            modValue = value%3
            if modValue == 0 {
                weight = 7
            } else if modValue == 1 {
                weight = 3
            } else if modValue == 2 {
                weight = 1
            }
            let particularChar = lastElementString[myIndex]
            if (self.appzillonAlphaNumericDict[particularChar]) != nil {
                multiplier = self.appzillonAlphaNumericDict[particularChar]!
            } else {
                checksumFlag = false
                break
            }
            sum += (multiplier * weight)
            value += 1
        }
        if checksumFlag {
            modValue = sum % 10
            let myCheckDigit = lastElementString[ckeckIndex]
            if let checkNumber = Int(myCheckDigit) {
                print("checkNumber = \(checkNumber)")
                checkDigit = checkNumber
            } else {
                print("notNumber")
                checksumFlag = false
                return checksumFlag
            }
            if checkDigit != modValue {
                checksumFlag = false
            }
        }
        return checksumFlag
    }
    func createAlphaNumericDictionary() {
        appzillonAlphaNumericDict["0"] = 0
        appzillonAlphaNumericDict["1"] = 1
        appzillonAlphaNumericDict["2"] = 2
        appzillonAlphaNumericDict["3"] = 3
        appzillonAlphaNumericDict["4"] = 4
        appzillonAlphaNumericDict["5"] = 5
        appzillonAlphaNumericDict["6"] = 6
        appzillonAlphaNumericDict["7"] = 7
        appzillonAlphaNumericDict["8"] = 8
        appzillonAlphaNumericDict["9"] = 9
        appzillonAlphaNumericDict["A"] = 10
        appzillonAlphaNumericDict["B"] = 11
        appzillonAlphaNumericDict["C"] = 12
        appzillonAlphaNumericDict["D"] = 13
        appzillonAlphaNumericDict["E"] = 14
        appzillonAlphaNumericDict["F"] = 15
        appzillonAlphaNumericDict["G"] = 16
        appzillonAlphaNumericDict["H"] = 17
        appzillonAlphaNumericDict["I"] = 18
        appzillonAlphaNumericDict["J"] = 19
        appzillonAlphaNumericDict["K"] = 20
        appzillonAlphaNumericDict["L"] = 21
        appzillonAlphaNumericDict["M"] = 22
        appzillonAlphaNumericDict["N"] = 23
        appzillonAlphaNumericDict["O"] = 24
        appzillonAlphaNumericDict["P"] = 25
        appzillonAlphaNumericDict["Q"] = 26
        appzillonAlphaNumericDict["R"] = 27
        appzillonAlphaNumericDict["S"] = 28
        appzillonAlphaNumericDict["T"] = 29
        appzillonAlphaNumericDict["U"] = 30
        appzillonAlphaNumericDict["V"] = 31
        appzillonAlphaNumericDict["W"] = 32
        appzillonAlphaNumericDict["X"] = 33
        appzillonAlphaNumericDict["Y"] = 34
        appzillonAlphaNumericDict["Z"] = 35
        appzillonAlphaNumericDict["<"] = 0
    }
}

extension String {
    subscript(value: Int) -> String {
        return String(self[index(startIndex, offsetBy: value)])
    }
}
// swiftlint:enable all
