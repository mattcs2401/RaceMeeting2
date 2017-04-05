package com.mcssoft.racemeetings2.utility;



import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpWrapper {

    public HttpWrapper(URL url) {
        this.url = url;
    }

    public String remoteRequest() {

        InputStream stream = null;
        String result = "";
        HttpsURLConnection connection = null;

//        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS) //MODERN_TLS)
//                .tlsVersions(TlsVersion.TLS_1_2)
//                .cipherSuites(
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
//                .build();

//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectionSpecs(Collections.singletonList(spec))
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();

//        OkHttpClient client = getUnsafeOkHttpClient();
//        Request request = new Request.Builder().url(url).build();

        try {
            connection = getHttpsConnection();
            connection.connect();
            stream = connection.getInputStream();

//            Response response = client.newCall(request).execute();
//            String bp = response.body().string();
//            Headers responseHeaders = response.headers();


            if (stream != null) {
                result = readStream(stream);
            }
        }
        catch (Exception ex) {
            Log.d("", ex.getMessage());
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
            if(stream != null) {
                try {
                    stream.close();
                } catch(Exception ex) {
                    Log.d("", ex.getMessage());
                }
            }
        }
        return result;
    }

    private String readStream(InputStream stream) throws IOException {
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        stream.close();
        return sb.toString();
    }

    public HttpsURLConnection getHttpsConnection() {
        HttpsURLConnection connection = null;

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            connection = (HttpsURLConnection) url.openConnection();

            connection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            connection.setDefaultSSLSocketFactory(sslSocketFactory);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

        } catch(Exception ex) {
            Log.d("", ex.getMessage());
        }
        finally {
            return connection;
        }
    }

//    public static OkHttpClient getUnsafeOkHttpClient() {
//
//        try {
//            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                    .tlsVersions(TlsVersion.TLS_1_1)
//                    .tlsVersions(TlsVersion.TLS_1_2)
//                    .cipherSuites(
//                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
//                    .build();
//
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(
//                        java.security.cert.X509Certificate[] chain,
//                        String authType) throws CertificateException {
//                }
//
//                @Override
//                public void checkServerTrusted(
//                        java.security.cert.X509Certificate[] chain,
//                        String authType) throws CertificateException {
//                }
//
//                @Override
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            } };
//
//            // Install the all-trusting trust manager
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//            // Create an ssl socket factory with our all-trusting manager
//            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .connectionSpecs(Collections.singletonList(spec))
//            .sslSocketFactory(sslSocketFactory)
//            .hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                }
//            })
//            .readTimeout(120, TimeUnit.SECONDS)
//            .connectTimeout(120, TimeUnit.SECONDS)
//            .build();
//
//            return okHttpClient;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


    private URL url;
}
