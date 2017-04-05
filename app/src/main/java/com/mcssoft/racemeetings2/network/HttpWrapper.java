package com.mcssoft.racemeetings2.network;



import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
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

        try {
            connection = getHttpsConnection();
            stream = connection.getInputStream();

            if (stream != null) {
                result = readStream(stream);
            }
        }
        catch (SocketTimeoutException ex) {
            result = ex.getMessage();
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream)); //, "UTF-8"));
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

            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            connection.setSSLSocketFactory(sslSocketFactory);
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

    private URL url;
}
