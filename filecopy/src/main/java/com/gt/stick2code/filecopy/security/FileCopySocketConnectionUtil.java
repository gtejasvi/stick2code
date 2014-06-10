package com.gt.stick2code.filecopy.security;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyConstants;
import com.gt.stick2code.filecopy.common.FileCopyUtil;

public class FileCopySocketConnectionUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(FileCopySocketConnectionUtil.class);
	public static final String DEFAULT_KEY = "550b62e8018e8b42ef2c31996746a84cc3bfee63ff7bcfafbf5091a37dbed09b";

	public static byte[] getRandomKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance(ALGO);
		keyGen.init(256);
		SecretKey secretKey = keyGen.generateKey();
		byte[] keyByte = secretKey.getEncoded();
		return keyByte;
	}

	public static final String ALGO = "AES/CBC/NoPadding";

	public static String encryptPwd(String password, String sKey,
			long timeInMillis) throws DecoderException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyBytes = Hex.decodeHex(sKey.toCharArray());
		Key key = new SecretKeySpec(keyBytes, ALGO);
		Cipher cipher = Cipher.getInstance(ALGO);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		String pwdInput = "PASSWORD=" + password + "$^#TIMEINMILLIS="
				+ timeInMillis;
		byte[] encbyte = cipher.doFinal(pwdInput.getBytes());

		char[] hexEncPwd = Hex.encodeHex(encbyte);
		return new String(hexEncPwd);

	}

	public static Socket getSocket(String host, int port,boolean securemode)
			throws UnknownHostException, IOException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException,
			KeyManagementException {
		int timeout = Integer.parseInt(FileCopyUtil.getPropertyVal(FileCopyConstants.CLIENT_TIMEOUT));
		if(securemode == false){
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host,port), timeout);
			return socket;
		}
		SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault()
				.createSocket();
		socket.setSoTimeout(timeout);
		socket.connect(new InetSocketAddress(host, port));
		try {
			socket.startHandshake();
			return socket;

		} catch (SSLException s) {
			socket.close();
			logger.info(
					"Error in SSL Socket. Hence loading the socket to keystore::");
		}

		
		File certFile = new File("./filecopykeystoreclient.jks");
		InputStream in = new BufferedInputStream(new FileInputStream(certFile));
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, "password".toCharArray());
		in.close();
		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory
				.getTrustManagers()[0];

		CustomTrustManager trustManager = new CustomTrustManager(
				defaultTrustManager);
		context.init(null, new TrustManager[] { trustManager }, null);
		SSLSocketFactory factory = context.getSocketFactory();
		socket = (SSLSocket) factory.createSocket(host, port);
		 try {
	            System.out.println("Starting SSL handshake...");
	         
	            socket.startHandshake();
	            socket.close();
	            System.out.println();
	            System.out.println("No errors, certificate is already trusted");
	        } catch (SSLException e) {
	            System.out.println();
	            e.printStackTrace(System.out);
	        }
		
		X509Certificate[] chain = trustManager.chain;
		if (chain == null) {
			logger.error("Could not obtain server certificate chain");
			//throw new IOException(
				//	"Error in Connecting to Server, Could not obtain server certificate chain");
		}

		System.out.println();
		System.out.println("Server sent " + chain.length + " certificate(s):");
		System.out.println();
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			System.out.println(" " + (i + 1) + " Subject "
					+ cert.getSubjectDN());
			System.out.println("   Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			System.out.println("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			System.out.println("   md5     " + toHexString(md5.digest()));
			System.out.println();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		int k;
		while (true) {
			System.out
					.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");

			String line = reader.readLine().trim();

			try {
				k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
				break;
			} catch (NumberFormatException e) {
				System.out.println("Enter a valid Number");
			}
		}

		X509Certificate cert = chain[k];
		String alias = host + "-" + (k + 1);
		ks.setCertificateEntry(alias, cert);

		OutputStream out = new FileOutputStream("./filecopykeystoreclient.jks");
		ks.store(out, "password".toCharArray());
		out.close();
		
		socket = (SSLSocket) SSLSocketFactory.getDefault()
				.createSocket();
		socket.setSoTimeout(timeout);
		socket.connect(new InetSocketAddress(host, port));
		socket.startHandshake();
		
		return socket;

	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	private static class CustomTrustManager implements X509TrustManager {

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

}
