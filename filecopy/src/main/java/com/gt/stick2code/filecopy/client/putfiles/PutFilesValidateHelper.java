package com.gt.stick2code.filecopy.client.putfiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;

public class PutFilesValidateHelper  {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(PutFilesValidateHelper.class);

	String host;
	int port;
	long timeout = 1000000;
	List<FileDetails> fileDetailList;
	FileCopyParameters params;
	String key;

	public PutFilesValidateHelper(String host, int port,
			List<FileDetails> fileDetailList, FileCopyParameters params,String key) {
		super();
		this.host = host;
		this.port = port;
		this.fileDetailList = fileDetailList;
		this.params = params;
		this.key = key;
	}

	
	/**
	 * Opens a Socket Connection with server and Indicates the server to merge the files.
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
	public void process() throws UnknownHostException,
			IOException, ClassNotFoundException {
		Socket socket = new Socket(host, port);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());

			if (ReadWriteUtil.connectToServer(bis, bos,
					RequestTypeEnum.PUTFILEVALIDATE, params,key)) {
				ReadWriteUtil.writeObjectToStream(bos, fileDetailList);
				if(false == ReadWriteUtil.getAcknowledgement(bis)){
					throw new IOException("Server Responded with Validation Failure");
				}
				
			}

		} finally {
			ReadWriteUtil.closeInputStream(bis);
			ReadWriteUtil.closeOutputStream(bos);
			ReadWriteUtil.closeSocket(socket);
		}
	}

	

}
