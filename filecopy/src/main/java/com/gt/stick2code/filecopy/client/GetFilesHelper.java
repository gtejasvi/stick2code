package com.gt.stick2code.filecopy.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;
import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class GetFilesHelper implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFilesHelper.class);

	String host;
	int port;
	List<FileDetails> fileDetailList;
	FileCopyParameters params;
	String key;
	boolean securemode;

	public GetFilesHelper(String host, int port,boolean securemode,
			List<FileDetails> fileDetailList, FileCopyParameters params,
			String key) {
		super();
		this.host = host;
		this.port = port;
		this.securemode = securemode;
		this.fileDetailList = fileDetailList;
		this.params = params;
		this.key = key;
	}

	public void run() {
		try {

			processGetFilesFromSource();
		} catch (KeyManagementException e) {
			logger.error("", e);
		} catch (KeyStoreException e) {
			logger.error("", e);
		} catch (NoSuchAlgorithmException e) {
			logger.error("", e);
		} catch (CertificateException e) {
			logger.error("", e);

		} catch (UnknownHostException e) {
			logger.error("", e);
		} catch (ClassNotFoundException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * Opens a Socket Connection with server and fetches the Files from the
	 * server.
	 * 
	 * @param fileDetailList
	 *            FileDetails In case of mutliple threads, this might have a
	 *            partial list of files
	 * @param targetFile
	 *            Folder/File where the fetched file has to be placed
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 */
	private void processGetFilesFromSource() throws UnknownHostException,
			IOException, ClassNotFoundException, KeyManagementException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException {

		// Socket socket = new Socket(host,port);
		// SSLSocket socket =
		// (SSLSocket)SocketFactory.getDefault().createSocket(host, port);
		Socket socket = FileCopySocketConnectionUtil.getSocket(
				host, port,securemode);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());
			if (ReadWriteUtil.connectToServer(bis, bos,
					RequestTypeEnum.GETFILES, params, key)) {

				ReadWriteUtil.writeObjectToStream(bos, fileDetailList);
				ZipInputStream zipInputStream = new ZipInputStream(bis);
				FileCopyUtil.extractFileToTarget(zipInputStream,
						fileDetailList, params.getTargetFile());
			}
		} finally {
			ReadWriteUtil.closeInputStream(bis);
			ReadWriteUtil.closeOutputStream(bos);
			ReadWriteUtil.closeSocket(socket);
		}
	}

	public void processGetFilesFromSource(
			Map<Integer, List<FileDetails>> threadListMap,
			FileCopyParameters fileCopyParameters) {

		Thread[] threads = new Thread[threadListMap.size()];
		int index = 0;
		for (int thread : threadListMap.keySet()) {
			List<FileDetails> fileDetailList = threadListMap.get(thread);

			GetFilesHelper fileCopyGetFiles = new GetFilesHelper(host, port,securemode,
					fileDetailList, fileCopyParameters, key);

			Thread th = new Thread(fileCopyGetFiles);
			threads[index++] = th;
			logger.info("Thread Started Id::[" + index + "], Files to Process["
					+ fileDetailList.size() + "]");
			th.start();
		}

		for (Thread fileCopyGetFiles : threads) {
			try {
				fileCopyGetFiles.join();
			} catch (InterruptedException e) {
				logger.warn("Thread Caused error::", e);
			}
		}
	}

}
