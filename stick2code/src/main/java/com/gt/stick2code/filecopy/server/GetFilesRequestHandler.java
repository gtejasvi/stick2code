package com.gt.stick2code.filecopy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;

public class GetFilesRequestHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFilesRequestHandler.class);
	
	BufferedInputStream bis;
	BufferedOutputStream bos;
	FileCopyParameters params;

	public GetFilesRequestHandler(FileCopyParameters params,BufferedInputStream bis,BufferedOutputStream bos) {
		this.bis = bis;
		this.bos = bos;
		this.params = params;
	}
	
	public void processGetFiles() throws ClassNotFoundException, IOException {
		
		@SuppressWarnings("unchecked")
		List<FileDetails> fileDetailsList = (List<FileDetails>)ReadWriteUtil.readInputStreamObject(bis);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bos);
		FileCopyUtil.writeZippedFileToStream(zipOutputStream, fileDetailsList,params.getSourceFile());
		zipOutputStream.flush();
		logger.debug("Server processGetRequest Done");
	}
}
