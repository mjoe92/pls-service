package de.vw.paso.client.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.hc.core5.ssl.SSLContexts;

public class SSLUtil {

    private static SSLContext instance;

    public static SSLContext getSSLContext() throws GeneralSecurityException, IOException {
        if (instance == null) {
            instance = createSSLContext();
        }

        return instance;
    }

    public static void loadPkiCardAndSetSSLFactory() throws GeneralSecurityException, IOException {
        //        SSLContext sslContext = getSSLContext();
        //        configureSSLSocket(sslContext);
    }

    public static void resetSSLToDefault() throws GeneralSecurityException {
        SSLContext defaultContext = SSLContext.getInstance("Default");
        configureSSLSocket(defaultContext);
    }

    private static void configureSSLSocket(SSLContext sslContext) {
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        SSLContext.setDefault(sslContext);
    }

    private static SSLContext createSSLContext() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
        keyStore.load(null, null);

        return SSLContexts.custom().loadKeyMaterial(keyStore, null).setProtocol("TLSv1.2").build();
    }
}
