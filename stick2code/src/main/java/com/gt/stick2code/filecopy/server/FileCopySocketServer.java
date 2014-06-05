package com.gt.stick2code.filecopy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.Ack;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;

/**
 * This class acts as the Server for Copying the File. It will start andl listen
 * on the Specified Port. Sequence on action: 1. Receive the Request -- This
 * should always contain the RequestType 2. An Acknowledgement is sent Back to
 * the client 3. Invoke the required Handle from where further processing will
 * be handled by the helper class
 * 
 * @author Ganaraj
 * 
 */
public class FileCopySocketServer extends Thread {

	private static final Logger logger = LoggerFactory
			.getLogger(FileCopySocketServer.class);
	private static ServerSocket serverSocket;
	private Socket socket;
	private int threads;

	public FileCopySocketServer(Socket socket,int threads) {
		this.socket = socket;
		if(threads < 1){
			threads = 1;
		}
		this.threads = threads;
	}

	public static void main(String[] args) throws IOException {

		int port = 50000;

		if (args.length > 0) {
			String sPort = args[0];
			port = Integer.parseInt(sPort);

		}
		int threads = 1;
		if(args.length > 1){
			threads = Integer.parseInt(args[1]);
		}

		serverSocket = new ServerSocket(port);
		while (true) {
			try {
				FileCopySocketServer fileCopyServerThread = new FileCopySocketServer(
						serverSocket.accept(),threads);
				fileCopyServerThread.start();

			} catch (Exception e) {
				logger.error("Error Processing Request", e);
			}
		}
	}

	public void run() {
		System.out.println("Thread Started");
		try {
			process();
		} catch (Exception e) {

			logger.error("Error In Thread", e);
			try {

				BufferedOutputStream bos = new BufferedOutputStream(
						socket.getOutputStream());
				ReadWriteUtil.writeObjectToStream(bos, Ack.FAILURE);
				bos.close();
			} catch (Exception e1) {

				logger.error("Error In Thread", e1);
			}
		}
	}

	private void process() throws IOException, ClassNotFoundException, InterruptedException {
		BufferedInputStream bis = new BufferedInputStream(
				socket.getInputStream());
		BufferedOutputStream bos = new BufferedOutputStream(
				socket.getOutputStream());
		logger.info("Fetch the Request Type");
		RequestTypeEnum requestType = ReadWriteUtil.getRequestType(bis, bos);
		logger.info("Request Received::["+requestType+"]");
		FileCopyParameters params = (FileCopyParameters) ReadWriteUtil
				.readInputStreamObject(bis);
		ReadWriteUtil.writeObjectToStream(bos, Ack.SUCCESS);
		if (requestType == RequestTypeEnum.GETFILELIST) {
			logger.info("Calling the Get File List Handler");
			GetFileListRequestHandler getFileList = new GetFileListRequestHandler(params,bis, bos,threads);
			getFileList.processGetFileList();
		} else if (requestType == RequestTypeEnum.GETFILES) {
			GetFilesRequestHandler getFiles = new GetFilesRequestHandler(params,bis, bos);
			getFiles.processGetFiles();
		} else if(requestType == RequestTypeEnum.PUTFILTERFILESLIST){
			PutFilesFilterRequestHandler filterRequest = new PutFilesFilterRequestHandler(params,bis, bos);
			filterRequest.processFilterFiles();
		} else if(requestType == RequestTypeEnum.PUTFILES){
			PutFilesRequestHandler putFiles = new PutFilesRequestHandler(params,bis, bos);
			putFiles.processFiles();
		}else if(requestType == RequestTypeEnum.PUTFILEMERGE){
			PutFilesMergeRequestHandler merge = new PutFilesMergeRequestHandler(params, bis, bos,threads);
			merge.process();
		}else if(requestType == RequestTypeEnum.PUTFILEVALIDATE){
			PutFilesValidateRequestHandler validate = new PutFilesValidateRequestHandler(params, bis, bos,threads);
			validate.process();
		}
		bos.flush();
		ReadWriteUtil.closeInputStream(bis);
		ReadWriteUtil.closeOutputStream(bos);
		socket.close();

	}

}
