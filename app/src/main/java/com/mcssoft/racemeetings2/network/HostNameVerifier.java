package com.mcssoft.racemeetings2.network;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class HostNameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) { return true; }
}
