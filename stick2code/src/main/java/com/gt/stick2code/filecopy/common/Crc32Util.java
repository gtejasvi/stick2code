package com.gt.stick2code.filecopy.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crc32Util implements Runnable{

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(Crc32Util.class);
	
	FileDetails fileDetails;
	File file; 
	boolean status;
	CRC32 crc32;
	Exception e;
	
	private Crc32Util(FileDetails fileDetails,File file){
		super();
		this.fileDetails = fileDetails;
		this.file = file;
		this.status = false;
	}
	
	public Crc32Util(){
		super();
	}
	
	@Override
	public void run() {
		try {
			crc32 = Crc32Util.getCrc(file, fileDetails.getOffset(), fileDetails.getPartialFileLength());
			status = true;
			//logger.debug("CRC32 Value ::"+crc32.getValue());
		} catch (IOException e) {
			this.e = e;
		}
	}
	
	public Map<FileDetails,CRC32> executeThread(List<FileDetails> fileDetailList,String relativePath,int threads) throws InterruptedException, IOException {
		threads = threads < 1 ? 1 : threads;
		Thread[] threadArr = new Thread[threads];
		List<Crc32Util> statusList = new ArrayList<Crc32Util>();
		
		for(FileDetails fileDetails : fileDetailList){
			File file = new File(relativePath + File.separator + fileDetails.getZipFileName());
			Crc32Util getCrcUtil = new Crc32Util(fileDetails,file);
			statusList.add(getCrcUtil);
			synchronized (threadArr) {
				Thread th = getThread(threadArr,getCrcUtil);
				th.start();
			}
		}
		
		for(Thread th : threadArr){
			if(th != null && th.isAlive()){
				th.join();
			}
		}
		
		Map<FileDetails,CRC32> crc32Map = new HashMap<FileDetails, CRC32>();
		for(Crc32Util util : statusList){
			if(util.getExecStatus() == false){
				throw new IOException("Error in Getting the CrC32 value",e);
			}
			crc32Map.put(util.getFileDetails(), util.getCrc());
		}
		return crc32Map;
	}
	
	int index = 0;
	
	private Thread getThread(Thread[] threadArr,Crc32Util getCrc) throws InterruptedException{
		int count = 0;
		do{
			if(index == threadArr.length){
				index = 0;
			}
			if(threadArr[index] == null || threadArr[index].isAlive() == false){
				//System.out.println("Thread ["+index+"] Activated");
				return threadArr[index++] = new Thread(getCrc);
			}
			if(count++ > 100){
				count = 0;
				Thread.sleep(100);
				
			}
			
		}while(true);
	}
	
	
	public static CRC32 getCrc(File file, long offset, long readLength)
			throws IOException {

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		try {
			bis.skip(offset);
			int readBytes = 0;
			long totalReadLength = 0;
			CRC32 crc = new CRC32();
			byte[] fileByte = new byte[FileCopyConstants.MAX_READ_SIZE];
			while (-1 != (readBytes = bis.read(fileByte))) {
				totalReadLength += readBytes;
				// System.out.println("length::"+readBytes);
				// logger.debug("totalReadLength[" + totalReadLength
				// + "],readLength[" + readLength + "]");
				if (totalReadLength > readLength) {

					long diff = totalReadLength - readLength;
					/*
					 * logger.debug("totalReadLength::[" + totalReadLength +
					 * "],readLength[" + readLength + "],[" + readBytes + "]");
					 */
					readBytes = readBytes - (int) diff;

				}
				crc.update(fileByte, 0, readBytes);
				if (totalReadLength > readLength) {
					break;
				}
			}
			return crc;
		} finally {
			bis.close();
		}

	}
	public boolean getExecStatus(){
		return status;
	}

	public FileDetails getFileDetails(){
		return this.fileDetails;
	}
	
	public long getCrcValue(){
		return this.crc32.getValue();
	}
	
	public CRC32 getCrc(){
		return this.crc32;
	}
}
