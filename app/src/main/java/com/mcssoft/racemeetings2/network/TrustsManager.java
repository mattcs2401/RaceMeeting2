package com.mcssoft.racemeetings2.network;

import android.util.Log;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustsManager {

    public TrustsManager() { }

    public TrustManager[] getX509Trusts() {
        TrustManager[] trustAllCerts = null;
        try {
            trustAllCerts = new TrustManager[] { new X509TrustsManager() };
//            trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(X509Certificate[] chain, String authType) {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] chain, String authType) {
//                }
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            }};
        } catch(Exception ex) {
            Log.d("", ex.getMessage());
        } finally {
            return trustAllCerts;
        }
    }

    private class X509TrustsManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
