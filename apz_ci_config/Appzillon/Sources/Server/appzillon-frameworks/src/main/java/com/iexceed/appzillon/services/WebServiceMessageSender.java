package com.iexceed.appzillon.services;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class WebServiceMessageSender {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
            ServerConstants.LOGGER_FRAMEWORKS,
            WebServiceMessageSender.class.getName());

    protected HttpsUrlConnectionMessageSender connectionMessageSender = null;

    WebServiceMessageSender() {
        connectionMessageSender = new HttpsUrlConnectionMessageSender();
    }

    public HttpsUrlConnectionMessageSender getHttpMessageSender() {
        try {
            connectionMessageSender.setSslProtocol("ssl");
            connectionMessageSender.setTrustManagers(getTrustManagers());
            connectionMessageSender.setKeyManagers(getKeyManagers());
            connectionMessageSender.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            });

        } catch (NoSuchAlgorithmException ex) {
            LOG.error("NoSuchAlgorithmException", ex);
        } catch (FileNotFoundException ex) {
            LOG.error("FileNotFoundException", ex);
        } catch (KeyStoreException ex) {
            LOG.error("KeyStoreException", ex);
        } catch (IOException ex) {
            LOG.error("IOException", ex);
        } catch (CertificateException ex) {
            LOG.error("CertificateException", ex);
        } catch (UnrecoverableKeyException ex) {
            LOG.error("UnrecoverableKeyException", ex);
        }
        return connectionMessageSender;
    }

    private KeyStore getKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore = null;
        String keystoreFilename = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.KEY_STORE_PATH).toString().trim();

        char[] password = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.KEY_STORE_PIN).toString().trim().toCharArray();
        try (FileInputStream inputstream = new FileInputStream(keystoreFilename)) {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(inputstream, password);
            return keyStore;
        }
    }

    private KeyStore getTrustStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore trustStore = null;
        String keystoreFilename = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                "truststorePath").toString().trim();

        char[] password = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                "truststorePassword").toString().trim().toCharArray();
        try (FileInputStream inputstream = new FileInputStream(keystoreFilename)) {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(inputstream, password);
            return trustStore;
        }
    }

    private KeyManager[] getKeyManagers() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        char[] password = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.KEY_STORE_PIN).toString().trim().toCharArray();
        KeyManagerFactory keymanagerfactory
                = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keymanagerfactory.init(getKeyStore(), password);
        return keymanagerfactory.getKeyManagers();
    }

    private TrustManager[] getTrustManagers() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        TrustManagerFactory trustmanagerfactory
                = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustmanagerfactory.init(getTrustStore());
        return trustmanagerfactory.getTrustManagers();
    }

}
