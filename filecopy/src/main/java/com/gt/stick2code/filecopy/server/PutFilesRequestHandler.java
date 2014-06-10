package com.gt.stick2code.filecopy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.Ack;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestResponseWrapper;

public class PutFilesRequestHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(PutFilesRequestHandler.class);
	BufferedInputStream bis;
	BufferedOutputStream bos;
	FileCopyParameters params;

	public PutFilesRequestHandler(FileCopyParameters params,BufferedInputStream bis,
			BufferedOutputStream bos) {
		this.bis = bis;
		this.bos = bos;
		this.params = params;
	}

	public void processFiles() throws ClassNotFoundException, IOException {


		try {

			RequestResponseWrapper wrapper = new RequestResponseWrapper(
					Ack.SUCCESS);
			ReadWriteUtil.writeObjectToStream(bos, wrapper);

			
			@SuppressWarnings("unchecked")
			List<FileDetails> fileDetailList = (List<FileDetails>)ReadWriteUtil.readInputStreamObject(bis);
			ReadWriteUtil.writeObjectToStream(bos, wrapper);

			
			
			ZipInputStream zipInputStream = new ZipInputStream(bis);
			FileCopyUtil.extractFileToTarget(zipInputStream,
					fileDetailList, params.getTargetFile());
		
			
		} catch (Exception e) {
			logger.error("Error Processing Request", e);
			RequestResponseWrapper wrapper = new RequestResponseWrapper(
					Ack.FAILURE);
			ReadWriteUtil.writeObjectToStream(bos, wrapper);
		}
	}
}
