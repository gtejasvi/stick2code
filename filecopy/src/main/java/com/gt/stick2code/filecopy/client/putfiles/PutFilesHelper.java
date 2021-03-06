package com.gt.stick2code.filecopy.client.putfiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;
import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class PutFilesHelper implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(PutFilesHelper.class);

	String host;
	int port;
	boolean securemode=false;
	long timeout = 1000000;
	List<FileDetails> fileDetailList;
	FileCopyParameters params;
	String password;
	String key;

	public PutFilesHelper(String host, int port,boolean securemode,
			List<FileDetails> fileDetailList, FileCopyParameters params,
			String password, String key) {
		super();
		this.host = host;
		this.port = port;
		this.securemode = securemode;
		this.fileDetailList = fileDetailList;
		this.params = params;
		this.password = password;
		this.key = key;
	}

	public void run() {
		try {
			try {
				processPutFilesToTarget();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			logger.error("", e);
		} catch (ClassNotFoundException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * Opens a Socket Connection with server Moves the file to the server.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws DecoderException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws CertificateException 
	 * @throws KeyStoreException 
	 * @throws KeyManagementException 
	 */
	private void processPutFilesToTarget() throws UnknownHostException,
			IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, KeyManagementException, KeyStoreException, CertificateException {
		//Socket socket = new Socket(host, port);
		Socket socket = FileCopySocketConnectionUtil.getSocket(host, port,securemode);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());

			if (ReadWriteUtil.connectToServer(bis, bos,
					RequestTypeEnum.PUTFILES, params, password,key)) {
				ReadWriteUtil.writeObjectToStream(bos, fileDetailList);
				if (ReadWriteUtil.getAcknowledgement(bis)) {
					ZipOutputStream zipOutputStream = new ZipOutputStream(bos);
					FileCopyUtil.writeZippedFileToStream(zipOutputStream,
							fileDetailList, params.getSourceFile());
				}
			}

		} finally {
			ReadWriteUtil.closeInputStream(bis);
			ReadWriteUtil.closeOutputStream(bos);
			ReadWriteUtil.closeSocket(socket);
		}
	}

	public void processPutFilesToTarget(
			Map<Integer, List<FileDetails>> threadListMap,
			FileCopyParameters fileCopyParameters) {

		Thread[] threads = new Thread[threadListMap.size()];
		int index = 0;
		for (int thread : threadListMap.keySet()) {
			List<FileDetails> fileDetailList = threadListMap.get(thread);

			PutFilesHelper putFiles = new PutFilesHelper(host, port,securemode,
					fileDetailList, fileCopyParameters,password, key);

			Thread th = new Thread(putFiles);
			threads[index++] = th;
			logger.info("Thread Started Id::[" + index + "], Files to Process["
					+ fileDetailList.size() + "]");
			th.start();
		}

		for (Thread putFiles : threads) {
			try {
				putFiles.join();
			} catch (InterruptedException e) {
				logger.warn("Thread Caused error::", e);
			}
		}
	}

}
