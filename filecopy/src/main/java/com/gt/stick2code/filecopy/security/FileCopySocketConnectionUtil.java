package com.gt.stick2code.filecopy.security;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

/**
 * Utility Class to get a Socket connection, generate AES key etc.
 * 
 * @author Ganaraj
 * 
 */
public class FileCopySocketConnectionUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(FileCopySocketConnectionUtil.class);

	/**
	 * Generates a Random AES key. Algorithm - AES/CBC/NoPadding
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException 
	 */
	public static byte[] getRandomKey() throws NoSuchAlgorithmException, IOException {
		String algorithm = FileCopyUtil.getPropertyVal(FileCopyConstants.ALGORITHM);
		String keyStrength = FileCopyUtil.getPropertyVal(FileCopyConstants.KEY_STRENGTH);
		int strength = Integer.parseInt(keyStrength);
		KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
		keyGen.init(strength);
		SecretKey secretKey = keyGen.generateKey();
		byte[] keyByte = secretKey.getEncoded();
		return keyByte;
	}


	/**
	 * Encrypts the password using a Key as well as
	 * 
	 * @param password
	 * @param sKey
	 * @param timeInMillis
	 * @return
	 * @throws DecoderException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException 
	 */
	public static String encryptPwd(String password, String sKey)
			throws DecoderException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, IOException {
		String algorithm = FileCopyUtil.getPropertyVal(FileCopyConstants.ALGORITHM);
		String cipherMode = FileCopyUtil.getPropertyVal(FileCopyConstants.CIPHER_MODE);
		String padding = FileCopyUtil.getPropertyVal(FileCopyConstants.CIPHER_PADDING);

		byte[] keyBytes = Hex.decodeHex(sKey.toCharArray());
		Key key = new SecretKeySpec(keyBytes, algorithm);
		//Cipher cipher = Cipher.getInstance(algorithm+"/"+cipherMode+"/"+padding);
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		PasswordWrapper pwdWrapper = new PasswordWrapper(password);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objos = new ObjectOutputStream(baos);
			objos.writeObject(pwdWrapper);

			objos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] encbyte = cipher.doFinal(baos.toByteArray());

		char[] hexEncPwd = Hex.encodeHex(encbyte);
		return new String(hexEncPwd);

	}
	
	public static PasswordWrapper decryptPwd(String encPwd, String sKey)
			throws DecoderException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {
		String algorithm = FileCopyUtil.getPropertyVal(FileCopyConstants.ALGORITHM);
		String cipherMode = FileCopyUtil.getPropertyVal(FileCopyConstants.CIPHER_MODE);
		String padding = FileCopyUtil.getPropertyVal(FileCopyConstants.CIPHER_PADDING);

		
		byte[] keyBytes = Hex.decodeHex(sKey.toCharArray());
		Key key = new SecretKeySpec(keyBytes, algorithm);
		Cipher cipher = Cipher.getInstance(algorithm+"/"+cipherMode+"/"+padding);
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		byte[] pwdBytes = Hex.decodeHex(encPwd.toCharArray());
		
		byte[] decbyte = cipher.doFinal(pwdBytes);
		ByteArrayInputStream bais = new ByteArrayInputStream(decbyte);

		ObjectInputStream ois = new ObjectInputStream(bais);
		PasswordWrapper pwdWrapper = (PasswordWrapper)ois.readObject();
		
		return pwdWrapper;

	}
	
	

	/**
	 * Get a Socket Connection. When secure mode is true, opens a socket and
	 * initiates a handshake, if successful returns the socket. If handshake
	 * fails, reads the ccertificate from the server and adds to the truststore
	 * and gets a Socket and returns it.
	 * 
	 * @param host
	 * @param port
	 * @param securemode
	 *            if false, returns a plain socket, whereas returns a SSLSocket
	 *            when true
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws KeyManagementException
	 */
	public static Socket getSocket(String host, int port, boolean securemode)
			throws UnknownHostException, IOException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException,
			KeyManagementException {
		int timeout = Integer.parseInt(FileCopyUtil
				.getPropertyVal(FileCopyConstants.CLIENT_TIMEOUT));
		if (securemode == false) {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), timeout);
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
			logger.info("Error in SSL Socket. Hence loading the socket to keystore::");
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
			// throw new IOException(
			// "Error in Connecting to Server, Could not obtain server certificate chain");
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

		socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket();
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
