package com.gt.stick2code.filecopy.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;

public class GetFilesHelper implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFilesHelper.class);

	String host;
	int port;
	long timeout = 1000000;
	List<FileDetails> fileDetailList;
	FileCopyParameters params;

	public GetFilesHelper(String host, int port,
			List<FileDetails> fileDetailList,
			FileCopyParameters params) {
		super();
		this.host = host;
		this.port = port;
		this.fileDetailList = fileDetailList;
		this.params = params;
	}

	public void run() {
		try {
			processGetFilesFromSource();
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
	 */
	private void processGetFilesFromSource() throws UnknownHostException,
			IOException, ClassNotFoundException {
		Socket socket = new Socket(host, port);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());
			if (ReadWriteUtil.connectToServer(bis, bos,
					RequestTypeEnum.GETFILES, params)) {
				
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
			
			GetFilesHelper fileCopyGetFiles = new GetFilesHelper(host, port,
					fileDetailList, fileCopyParameters);

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
