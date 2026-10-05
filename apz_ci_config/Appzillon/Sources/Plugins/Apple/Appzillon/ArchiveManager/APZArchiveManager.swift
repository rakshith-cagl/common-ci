//
//  ApzArchiveManager.swift
//  Appzillon
//
//  Created by Bhavya V on 26/04/21.
//

import Foundation

@objc public enum ArchiveStatus: Int, RawRepresentable {
    case none
    case success
    case failure
    case failureWithStorageError
    case failureSrcFileNotExist
}

public class ArchiveResult: NSObject {
    @objc var path: String = ""
    @objc var status: ArchiveStatus = .none
    
    init(path: String, status: ArchiveStatus) {
        self.path  = path
        self.status = status
    }
}
public class APZArchiveManager: NSObject {
    let EMPTY_STRING = ""
    
    @objc func archive(jsonDict: NSDictionary, appString: String) -> ArchiveResult {
        var isDir : ObjCBool = false
        if var sourcePath = jsonDict["srcFilePath"] as? String,
           FileManager.default.fileExists(atPath: sourcePath, isDirectory: &isDir),
           let destFolderName = jsonDict["destFilePath"] as? String {
            if isDir.boolValue {
                sourcePath = createDummyFileIfEmptyDirectory(srcPath: sourcePath)
            }
            let sourceURL = URL(fileURLWithPath: sourcePath).absoluteURL
            let fileManager = FileManager()
            do {
                let zipPath = zipFilePath(appString: appString, destFolder: destFolderName)
                let zipPathWithoutFilePrefix = zipPath.path
                deleteIfAlreadyExists(fileMngr: fileManager, destPath: zipPathWithoutFilePrefix)
                try fileManager.zipItem(at: sourceURL,
                                        to: zipPath,
                                        shouldKeepParent: true,
                                        compressionMethod: .deflate,
                                        progress: nil)
                return ArchiveResult(path: zipPathWithoutFilePrefix, status: .success)
            } catch {
                print("Creation of ZIP archive failed with error:\(error)")
                return ArchiveResult(path: EMPTY_STRING, status: .failureWithStorageError)
            }
        }
        return ArchiveResult(path: EMPTY_STRING, status: .failureSrcFileNotExist)
    }
    
    @objc func unArchive(jsonDict: NSDictionary, appString: String) -> ArchiveResult {
        if let sourcePath = jsonDict["srcFilePath"] as? String,
           FileManager.default.fileExists(atPath: sourcePath),
           let destFolderName = jsonDict["destFilePath"] as? String {
            do {
                let sourceURL = URL(fileURLWithPath: sourcePath).absoluteURL
                let fileManager = FileManager()
                let unzipPath = unzipFilePath(appString: appString, destFolder: destFolderName)
                try fileManager.unzipItem(at: sourceURL, to: unzipPath)
                let unzipPathWithoutFilePrefix = unzipPath.path
                return ArchiveResult(path: unzipPathWithoutFilePrefix, status: .success)
            }
            catch {
                print("Creation of ZIP archive failed with error:\(error)")
                return ArchiveResult(path: EMPTY_STRING, status: .failureWithStorageError)
            }
        }
        return ArchiveResult(path: EMPTY_STRING, status: .failureSrcFileNotExist)
    }
    
    // MARK: Utility Methods
    private func documentDirectoryPath() -> URL {
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = urls[0]
        return documentsDirectory
    }
    private func zipFilePath(appString: String, destFolder: String) -> URL {
        var documentPath = documentDirectoryPath()
        documentPath.appendPathComponent("Assets/apps/\(appString)/\(destFolder).zip")
        return documentPath
    }
    private func unzipFilePath(appString: String, destFolder: String) -> URL {
        var documentPath = documentDirectoryPath()
        documentPath.appendPathComponent("Assets/apps/\(appString)/\(destFolder)")
        return documentPath
    }
    private func deleteIfAlreadyExists(fileMngr: FileManager,destPath: String){
        do{
            if(fileMngr.fileExists(atPath: destPath)){
                try fileMngr.removeItem(atPath: destPath)
                print("IsFileExist: \(fileMngr.fileExists(atPath: destPath))")
            }
        }
        catch {
            print("Error during deleting file at existing path: \(error)")
        }
    }
    private func createDummyFileIfEmptyDirectory(srcPath: String) -> String {
        //It is a directory so check for empty content
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(atPath: srcPath)
            if directoryContents.isEmpty {
                //create a empty dummy file
                let url = URL(fileURLWithPath: srcPath).appendingPathComponent("apzzipdummy")
                try EMPTY_STRING.write(to: url, atomically: true, encoding: .utf8)
                return url.path
            }
        }
        catch {
            print(error.localizedDescription)
            return srcPath
        }
        return srcPath
    }
}
