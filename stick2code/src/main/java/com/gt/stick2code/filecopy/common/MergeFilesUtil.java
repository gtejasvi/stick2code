package com.gt.stick2code.filecopy.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeFilesUtil implements Runnable{
	
	private static final Logger logger = LoggerFactory
			.getLogger(MergeFilesUtil.class);
	
	List<FileDetails> fileDetailsList;
	String targetRelativePath;
	boolean status;
	Exception ex;
	
	private MergeFilesUtil(List<FileDetails> fileDetailsList,String targetRelativePath){
		super();
		status = false;
		this.fileDetailsList = fileDetailsList;
		this.targetRelativePath = targetRelativePath;
	}
	
	public MergeFilesUtil(){
		super();
	}

	@Override
	public void run() {
		try {
			
			MergeFilesUtil.mergeSplitFile(fileDetailsList, targetRelativePath);
			status = true;
		} catch (IOException e) {
			logger.error("Error in Merging::",e);
			ex = e;
		}
		
	}
	
	public void mergeFiles(List<FileDetails> fileDetailsListIn,String targetRelativePath,int threads) throws IOException, InterruptedException{

		threads = threads < 1 ? 1 : threads;

		List<FileDetails> mergeFileDetailsList = new ArrayList<FileDetails>();
		Map<String,List<FileDetails>> fileDetailMap = new HashMap<String, List<FileDetails>>();
		for (FileDetails fileDetails : fileDetailsListIn) {
			if (fileDetails.getFileLength() > fileDetails
					.getPartialFileLength()) {
				
				
				mergeFileDetailsList =  fileDetailMap.get(fileDetails.getZipFileName());
				if(mergeFileDetailsList == null){
					mergeFileDetailsList = new ArrayList<FileDetails>();
					System.out.println("Merge::"+fileDetails.getZipFileName());
					fileDetailMap.put(fileDetails.getZipFileName(), mergeFileDetailsList);
				}
				mergeFileDetailsList.add(fileDetails);
			}
		}
		
		
		
		Thread[] threadArr = new Thread[threads];
		List<MergeFilesUtil> statusList = new ArrayList<MergeFilesUtil>();
		
		for(List<FileDetails> fileDetailsList : fileDetailMap.values()){
			
			
			Collections.sort(fileDetailsList, new FileDetailComparator());
			
			
			MergeFilesUtil util = new MergeFilesUtil(fileDetailsList,targetRelativePath);
			statusList.add(util);
			synchronized (threadArr) {
				Thread th = getThread(threadArr,util);
				th.start();
			}
		}
		
		for(Thread th : threadArr){
			if(th != null && th.isAlive()){
				th.join();
			}
		}
		
		for(MergeFilesUtil util : statusList){
			if(util.getExecStatus() == false){
				throw new IOException("Error in Merging the files",ex);
			}
		}
	}
	int index = 0;
	private Thread getThread(Thread[] threadArr,MergeFilesUtil util) throws InterruptedException{
		int count = 0;
		do{
			if(index == threadArr.length){
				index = 0;
			}
			if(threadArr[index] == null || threadArr[index].isAlive() == false){
				//System.out.println("Thread ["+index+"] Activated");
				return threadArr[index++] = new Thread(util);
			}
			if(count++ > 100){
				count = 0;
				Thread.sleep(100);
				
			}
			
		}while(true);
	}

	public static void mergeSplitFile(List<FileDetails> fileDetailsList,
			String targFile) throws IOException {
		List<FileDetails> splitFileDetailsList = new ArrayList<FileDetails>();

		for (FileDetails fileDetails : fileDetailsList) {
			if (fileDetails.getFileLength() > fileDetails
					.getPartialFileLength()) {
				splitFileDetailsList.add(fileDetails);
			}
		}
		
		System.out.println("mergeSplitFile::---->"+splitFileDetailsList);
		
		Collections.sort(splitFileDetailsList, new FileDetailComparator());
		BufferedOutputStream bos = null;
		try {
			for (FileDetails fileDetails : splitFileDetailsList) {
				System.out.println("fileDetails.getFilePart()::"+fileDetails.getFilePart());
				if (fileDetails.getFilePart() == 1) {
					FileOutputStream fos = new FileOutputStream(new File(
							targFile + File.separator
									+ fileDetails.getZipFileName()), true);
					if (bos != null) {
						bos.flush();
						ReadWriteUtil.closeOutputStream(bos);
					}
					

					bos = new BufferedOutputStream(fos);
					logger.info("Merging File::" + fileDetails.getZipFileName());
					System.out.println("Merging File::"
							+ fileDetails.getZipFileName());

					continue;
				}
				
				String combineFile = targFile + File.separator
						+ fileDetails.getZipFilePartName();
				System.out.print("Adding File -->" + fileDetails.getZipFilePartName());
				BufferedInputStream bis = null;
				try {
					bis = new BufferedInputStream(new FileInputStream(
							combineFile));
					byte[] readBytes = new byte[FileCopyConstants.MAX_READ_SIZE];
					int length = 0;
					while (-1 != (length = bis.read(readBytes))) {
						bos.write(readBytes, 0, length);
					}
					System.out.println(" ..Done ");

				} finally {
					ReadWriteUtil.closeInputStream(bis);
				}
				File file = new File(combineFile);
				file.delete();
			}
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					ReadWriteUtil.closeOutputStream(bos);
				}
			} catch (Exception e) {
			}

		}

	}
	
	public boolean getExecStatus(){
		return status;
	}

	
	
}
