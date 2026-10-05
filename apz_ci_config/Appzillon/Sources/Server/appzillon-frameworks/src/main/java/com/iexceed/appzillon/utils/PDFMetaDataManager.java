package com.iexceed.appzillon.utils;

import com.iexceed.appzillon.domain.service.FileService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class PDFMetaDataManager {

    // Public method to insert some metadata to the PDF document
    public static void insertMetadata(String filePath) throws InvalidPasswordException, IOException {
        File file = new FileService().getFile(filePath);
        // The info object will contain all our meta data
        PDDocument document = PDDocument.load(file);
        PDDocumentInformation info = new PDDocumentInformation();
        info.setAuthor("");
        info.setCreator("");
        info.setProducer("");
        info.setCreationDate(Calendar.getInstance());
        info.setModificationDate(Calendar.getInstance());
        // This is to add the custom meta data to the document
        PDStream stream = new PDStream(document);
        info.setCustomMetadataValue("random-hash", md5Java(stream.toByteArray()));

        document.setDocumentInformation(info);
        // Let us save the new document somewhere...
        File file1 = new FileService().getFile(filePath);
        file.delete();
        file1.createNewFile();
        document.save(file1);
    }

    public static String md5Java(byte[] message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(message);

            // converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            digest = sb.toString();
        } catch (NoSuchAlgorithmException ex) {

        }

        return digest;
    }
}
