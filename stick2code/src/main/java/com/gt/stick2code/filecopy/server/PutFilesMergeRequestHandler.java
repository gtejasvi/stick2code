package com.gt.stick2code.filecopy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.Ack;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.MergeFilesUtil;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestResponseWrapper;

public class PutFilesMergeRequestHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(PutFilesMergeRequestHandler.class);
	BufferedInputStream bis;
	BufferedOutputStream bos;
	FileCopyParameters params;
	int threads;
	
	public PutFilesMergeRequestHandler(FileCopyParameters params,BufferedInputStream bis,
			BufferedOutputStream bos,int threads) {
		this.bis = bis;
		this.bos = bos;
		this.params = params;
		this.threads = threads < 1 ? 1 : threads;
	}

	public void process() throws ClassNotFoundException, IOException {


		try {

			//Read the File List
			@SuppressWarnings("unchecked")
			List<FileDetails> fileDetailsList = (List<FileDetails>) ReadWriteUtil
					.readInputStreamObject(bis);
			MergeFilesUtil mergeFilesUtil = new MergeFilesUtil();
			mergeFilesUtil.mergeFiles(fileDetailsList, params.getTargetFile(),threads );
			
			ReadWriteUtil.writeObjectToStream(bos, Ack.SUCCESS);
		
			
		} catch (Exception e) {
			logger.error("Error Processing Request", e);
			RequestResponseWrapper wrapper = new RequestResponseWrapper(
					Ack.FAILURE);
			ReadWriteUtil.writeObjectToStream(bos, wrapper);
		}
	}
}
