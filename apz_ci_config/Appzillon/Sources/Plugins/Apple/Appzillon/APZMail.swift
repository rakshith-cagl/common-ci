import Foundation
import UIKit
import MessageUI

class APZMail: APZPlugin, MFMailComposeViewControllerDelegate {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var filePathExists: Bool = false
    // MARK: Plugin Life Cycle Method
    override init(plugin webView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if !jsonDict.isEmpty {
            if MFMailComposeViewController.canSendMail() {
                showMailWindow(jsonDict: jsonDict)
            } else {
                callBack(resultKeys: [StringConstants.Generic.errorCode],
                         resultValues: [StringConstants.Mail.clientError],
                         status: false)
                APZLogger.log(logLvl: "E", message: "APZMail--Device Unable to Send Mail")
            }
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZMail--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Show Mail Methods
    private func showMailWindow(jsonDict: [AnyHashable: Any]) {
        let message = jsonDict[StringConstants.Mail.subject] as? String ?? StringConstants.Generic.emptyString
        let toRecipients = jsonDict[StringConstants.Mail.recipientId] as? String ?? StringConstants.Generic.emptyString
        let body = jsonDict[StringConstants.Mail.body] as? String ?? StringConstants.Generic.emptyString
        
        
        var emailCcList: [String] = []
        if let ccListStr = jsonDict[StringConstants.Mail.ccIdList] as? NSString {
            emailCcList = ccListStr.components(separatedBy: StringConstants.Generic.comma)
        }
        filePathExists = jsonDict.keys.contains(StringConstants.Mail.filePaths)
        let mailVC = getMailViewController(subject: message,
                                           toRecipients: [toRecipients],
                                           ccRecipient: emailCcList,
                                           messageBody: body)
        mailVC.modalPresentationStyle = UIModalPresentationStyle.formSheet
        if filePathExists {
            attachAndShowMail(jsonDict: jsonDict, mailVC: mailVC)
        } else {
            // When there is no attachements
            self.viewController?.present(mailVC, animated: true, completion: nil)
        }
    }
    private func attachAndShowMail(jsonDict: [AnyHashable: Any], mailVC: MFMailComposeViewController) {
        let maxSize = jsonDict[StringConstants.Mail.maxAttachmentSize]
            as? String ?? StringConstants.Mail.defaultMaxAttachmentSize
        let maxIntFileSize = (Int(maxSize) ?? 0) * 1024 * 1024
        let fileManager = FileManager.default
        var fileSizeCount = UInt64(0)
        if let files = jsonDict[StringConstants.Mail.filePaths] as? [String] {
            for filePath in files {
                if fileManager.fileExists(atPath: filePath) {
                    fileSizeCount += getFileSize(filePath: filePath)
                    if fileSizeCount > maxIntFileSize {
                        callBack(resultKeys: [StringConstants.Generic.errorCode],
                                 resultValues: [StringConstants.Mail.largeFileErrorCode],
                                 status: false)
                        APZLogger.log(logLvl: "E", message: "APZMail--FileSize is greater than the allowed limit")
                        break
                    } else {
                        if let fileData = NSData(contentsOfFile: filePath) {
                            mailVC.addAttachmentData(fileData as Data,
                                                     mimeType: self.mimeTypeForFile(path: filePath),
                                                     fileName: filePath)
                            self.viewController?.present(mailVC, animated: true, completion: nil)
                        } else {
                            callBack(resultKeys: [StringConstants.Generic.errorCode],
                                     resultValues: [StringConstants.Generic.fileNotFoundCode],
                                     status: false)
                            APZLogger.log(logLvl: "E", message: "APZMail--File Data Not Found")
                            break
                        }
                    }
                } else {
                    callBack(resultKeys: [StringConstants.Generic.errorCode],
                             resultValues: [StringConstants.Generic.fileNotFoundCode],
                             status: false)
                    APZLogger.log(logLvl: "E", message: "APZMail--invalid file location")
                    break
                }
            }
        }

    }
    private func getMailViewController(subject: String, toRecipients: [String],
                                       ccRecipient: [String], messageBody: String ) -> MFMailComposeViewController {
        let mailer = MFMailComposeViewController()
        mailer.mailComposeDelegate = self
        mailer.setSubject(subject)
        mailer.setToRecipients(toRecipients)
        mailer.setCcRecipients(ccRecipient)
        mailer.setMessageBody(messageBody, isHTML: false)
        return mailer
    }
    // MARK: Mail Delegate Methods
    func mailComposeController(_ controller: MFMailComposeViewController,
                               didFinishWith result: MFMailComposeResult,
                               error: Error?) {
        controller.dismiss(animated: true, completion: nil)
        switch result {
        case MFMailComposeResult.sent:
            callBack(resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.Mail.sent], status: true)
            APZLogger.log(logLvl: "I", message: "APZMail--Mail Sent")
        case MFMailComposeResult.saved:
            callBack(resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.Mail.saved], status: true)
            APZLogger.log(logLvl: "I", message: "APZMail--Mail Saved")
        case MFMailComposeResult.cancelled:
            callBack(resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.Generic.cancel],
                     status: true)
            APZLogger.log(logLvl: "I", message: "APZMail--Mail Cancelled")
        case MFMailComposeResult.failed:
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Mail.clientError],
                     status: false)
            let errMsg = error?.localizedDescription ?? StringConstants.Generic.emptyString
            APZLogger.log(logLvl: "E", message: String(format: "APZMail--%@", errMsg))
        default:
            break
        }
    }
    private func getFileSize(filePath: String) -> UInt64 {
        var fileSize: UInt64 = 0
        do {
            let attr = try FileManager.default.attributesOfItem(atPath: filePath)
            let dict = attr as NSDictionary
            fileSize = dict.fileSize()
            return fileSize
        } catch {
            print("Error: \(error)")
            return fileSize
        }
    }
    private func mimeTypeForFile(path: String) -> String {
        if let url = URL(string: path) {
            let pathExtension = url.pathExtension
            if let uti = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension,
                                                               pathExtension as NSString, nil)?.takeRetainedValue(),
               let mimetype = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)?.takeRetainedValue() {
                return mimetype as String
            }
        }
        return "application/octet-stream"
    }
    // MARK: CallBack Method
    func callBack(resultKeys: [String], resultValues: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
