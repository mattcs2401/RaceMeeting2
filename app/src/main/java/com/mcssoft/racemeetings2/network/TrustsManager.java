package com.mcssoft.racemeetings2.network;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustsManager {

    public TrustsManager() { }

    public TrustManager[] getX509Trusts() throws CertificateException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) { }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) { }

            @Override
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        } };
        return trustAllCerts;
    }
}
