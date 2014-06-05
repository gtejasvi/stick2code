package com.gt.stick2code.filecopy.client.putfiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;

public class PutFilesHelper implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(PutFilesHelper.class);

	String host;
	int port;
	long timeout = 1000000;
	List<FileDetails> fileDetailList;
	FileCopyParameters params;

	public PutFilesHelper(String host, int port,
			List<FileDetails> fileDetailList, FileCopyParameters params) {
		super();
		this.host = host;
		this.port = port;
		this.fileDetailList = fileDetailList;
		this.params = params;
	}

	public void run() {
		try {
			processPutFilesToTarget();
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
	 */
	private void processPutFilesToTarget() throws UnknownHostException,
			IOException, ClassNotFoundException {
		Socket socket = new Socket(host, port);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());

			if (ReadWriteUtil.connectToServer(bis, bos,
					RequestTypeEnum.PUTFILES, params)) {
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

			PutFilesHelper putFiles = new PutFilesHelper(host, port,
					fileDetailList, fileCopyParameters);

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
