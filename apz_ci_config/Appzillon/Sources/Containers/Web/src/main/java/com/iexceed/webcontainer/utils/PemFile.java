package com.iexceed.webcontainer.utils;

import com.iexceed.webcontainer.startup.WebContextListener;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;

public class PemFile {

  private PemObject pemObject;

  public PemFile(String filename) throws FileNotFoundException, IOException {
    
if(WebContextListener.propertiesPath !=null && !"".equals(WebContextListener.propertiesPath)) {
      filename = String.format("%s/%s",WebContextListener.propertiesPath,filename);
    }
    try (InputStream is = PemFile.class.getClassLoader().getResourceAsStream(filename);
         PemReader pemReader = new PemReader(new InputStreamReader(is))) {
      this.pemObject = pemReader.readPemObject();

    }
  }

  public void write(String filename) throws FileNotFoundException,
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
