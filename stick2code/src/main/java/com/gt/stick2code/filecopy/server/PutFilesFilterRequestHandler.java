package com.gt.stick2code.filecopy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

public class PutFilesFilterRequestHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(PutFilesFilterRequestHandler.class);
	BufferedInputStream bis;
	BufferedOutputStream bos;
	FileCopyParameters params;

	public PutFilesFilterRequestHandler(FileCopyParameters params,BufferedInputStream bis,
			BufferedOutputStream bos) {
		this.bis = bis;
		this.bos = bos;
		this.params = params;
	}

	public void processFilterFiles() throws ClassNotFoundException, IOException {


		try {

			//Read the File List
			@SuppressWarnings("unchecked")
			List<FileDetails> fileDetailsList = (List<FileDetails>) ReadWriteUtil
					.readInputStreamObject(bis);
			List<FileDetails> filterList = FileCopyUtil.filterExistingFiles(
					fileDetailsList, params.getTargetFile());
			ReadWriteUtil.writeObjectToStream(bos, filterList);
		
			
		} catch (Exception e) {
			logger.error("Error Processing Request", e);
			RequestResponseWrapper wrapper = new RequestResponseWrapper(
					Ack.FAILURE);
			ReadWriteUtil.writeObjectToStream(bos, wrapper);
		}
	}
}
