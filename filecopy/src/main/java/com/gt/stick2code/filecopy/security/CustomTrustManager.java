package com.gt.stick2code.filecopy.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CustomTrustManager implements X509TrustManager {
	
	private final X509TrustManager trustManager;
	private X509Certificate[] chain;
	
	public CustomTrustManager(X509TrustManager trustManager) {
		super();
		this.trustManager = trustManager;
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		throw new UnsupportedOperationException();

	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		this.chain = chain;
		trustManager.checkServerTrusted(chain, authType);

	}

	public X509Certificate[] getAcceptedIssuers() {
		throw new UnsupportedOperationException();
	}

}
