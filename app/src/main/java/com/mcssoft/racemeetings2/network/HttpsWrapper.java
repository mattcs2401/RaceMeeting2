package com.mcssoft.racemeetings2.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HttpsWrapper {

    public HttpsWrapper(URL url) {
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
        catch (Exception ex) {
            result = "Exception: " + ex.getMessage();
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
        TrustsManager trustsManager = new TrustsManager();

        try {
            connection = (HttpsURLConnection) url.openConnection();

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustsManager.getX509Trusts(), new SecureRandom());

            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(new HostNameVerifier());
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

        } catch(Exception ex) {
            Log.d("", ex.getMessage());
        }
        finally {
            return connection;
        }
    }

    private URL url;
}
