package com.iexceed.appzillon.securityutils;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.utils.ServerConstants;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;

/**
 * Created by diganta.kumar@i-exceed.com on 26/2/18 11:35 AM
 */
public class PemFile {

    private PemObject pemObject;

    public PemFile(String filename) throws IOException {
        String fPath;

        if (Utils.isNullOrEmpty(Logger.propertiesPath)) {
            fPath = filename;
        } else {
            fPath = String.format("%s/%s/%s", Logger.propertiesPath, ServerConstants.SERVER_PROP_FILE_CONSTANT, filename);
        }
        try (InputStream is = PemFile.class.getClassLoader().getResourceAsStream(fPath)) {
            PemReader pemReader = new PemReader(new InputStreamReader(is));
            this.pemObject = pemReader.readPemObject();

        }
    }

    public void write(String filename) throws
            IOException {
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(
                new FileOutputStream(filename)))) {
            pemWriter.writeObject(this.pemObject);

        }
    }

    public PemObject getPemObject() {
        return pemObject;
    }

}
