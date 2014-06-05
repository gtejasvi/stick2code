package com.gt.stick2code.filecopy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.Ack;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestResponseWrapper;

/**
 * This Class Helps the Server thread in processing the GelFilesList Request. It
 * Takes the Folder name, scans through the same, and fetches all the
 * files/Folders within that folder, wraps it in a List and writes it back to
 * the Stream.
 * 
 * @author Ganaraj
 * 
 */
public class GetFileListRequestHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFileListRequestHandler.class);
	BufferedInputStream bis;
	BufferedOutputStream bos;
	int threads;
	FileCopyParameters params;

	public GetFileListRequestHandler(FileCopyParameters params,BufferedInputStream bis,BufferedOutputStream bos,int threads) {
		this.bis = bis;
		this.bos = bos;
		this.threads = threads;
		this.params = params;
	}

	public void processGetFileList() throws ClassNotFoundException, IOException, InterruptedException {
			
			Ack ack = (Ack)ReadWriteUtil.readInputStreamObject(bis);
			logger.debug("Acknowledge Received::"+ack);

			String copyFolder = params.getSourceFile();
			logger.debug("Inside processGetRequest::copyFolder::" + copyFolder);
			File readFile = new File(copyFolder);
			if (readFile.exists()) {
				List<FileDetails> fileList = FileCopyUtil
						.getFileDetailList(copyFolder,params.isRecursive(),threads);
				RequestResponseWrapper response = new RequestResponseWrapper(Ack.SUCCESS);
				response.setObject(fileList);
				ReadWriteUtil.writeObjectToStream(bos, response);
			} else {
				RequestResponseWrapper response = new RequestResponseWrapper(Ack.FAILURE);
				
				ReadWriteUtil.writeObjectToStream(bos, response);
			}
	}

}
