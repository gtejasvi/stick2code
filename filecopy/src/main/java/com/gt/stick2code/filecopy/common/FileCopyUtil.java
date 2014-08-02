package com.gt.stick2code.filecopy.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCopyUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(FileCopyUtil.class);

	public static List<FileDetails> getFileDetailList(String fileToCopy,
			boolean recursive, int threads) throws IOException,
			InterruptedException {
		File copyFile = new File(fileToCopy);
		if (copyFile.exists() == false) {
			return null;
		}

		String relativePath = copyFile.getAbsoluteFile().getAbsolutePath();
		copyFile = copyFile.getAbsoluteFile();
		List<FileDetails> fileDetailList = new ArrayList<FileDetails>();
		BigInteger totalFileSize = getFileList(fileDetailList, relativePath,
				copyFile, recursive);

		printSize(totalFileSize);
		FileCopyUtil.updateCrc32Value(fileDetailList, relativePath, threads);
		logger.debug("relativePath[" + relativePath + "]");
		return fileDetailList;
	}

	private static void printSize(BigInteger fileSize) {
		long size = 1024;
		double sizeKb = fileSize.divide(new BigInteger("" + (size)))
				.doubleValue();
		double sizeMb = fileSize.divide(new BigInteger("" + (size * size)))
				.doubleValue();
		double sizeGb = fileSize.divide(
				new BigInteger("" + (size * size * size))).doubleValue();
		logger.info("Total File Size :: [" + sizeKb + " Kb], [" + sizeMb
				+ " Mb], [" + sizeGb + " Gb]");

	}

	private static BigInteger getFileList(List<FileDetails> fileDetailList,
			String relativePath, File copyFile, boolean recursive)
			throws IOException {
		BigInteger totalFileSize = new BigInteger("0");
		if (copyFile.isDirectory()) {
			for (File folderFile : copyFile.listFiles()) {
				if (recursive || folderFile.isDirectory() == false) {
					BigInteger fileSize = getFileList(fileDetailList,
							relativePath, folderFile, recursive);
					totalFileSize = totalFileSize.add(fileSize);
				}
			}
		} else {
			FileDetails fileDetail = new FileDetails();
			fileDetail.setZipFileName(getRelativeFile(relativePath, copyFile));
			fileDetail.setZipFilePartName(fileDetail.getZipFileName());
			fileDetail.setFileLength(copyFile.length());
			fileDetail.setPartialFileLength(copyFile.length());
			fileDetail.setFilePart(1);
			fileDetail.setOffset(0);
			/*
			 * fileDetail.setCrc32Value(getCrc(copyFile, 0, copyFile.length())
			 * .getValue());
			 */
			fileDetail.setLastModified(copyFile.lastModified());
			fileDetail.setReadable(copyFile.canRead());
			fileDetail.setExecutable(copyFile.canExecute());
			fileDetail.setWritable(copyFile.canWrite());
			System.out.println("fileDetail::"+fileDetail);
			fileDetailList.add(fileDetail);
			return new BigInteger("" + fileDetail.getPartialFileLength());
		}
		

		return totalFileSize;

	}

	public static void updateCrc32Value(List<FileDetails> fileDetailList,
			String relativePath, int threads) throws InterruptedException,
			IOException {
		Map<FileDetails, CRC32> crc32Map = getCrcMap(fileDetailList,
				relativePath, threads);
		for (FileDetails fileDetails : crc32Map.keySet()) {
			fileDetails.setCrc32Value(crc32Map.get(fileDetails).getValue());
		}
	}

	public static Map<FileDetails, CRC32> getCrcMap(
			List<FileDetails> fileDetailList, String relativePath, int threads)
			throws InterruptedException, IOException {
		Crc32Util crc32Util = new Crc32Util();
		Map<FileDetails, CRC32> crc32Map = crc32Util.executeThread(
				fileDetailList, relativePath, threads);
		return crc32Map;
	}



	private static String getRelativeFile(String relativePath, File file)
			throws FileNotFoundException {
		String absolutePath = file.getAbsolutePath();
		String zipEntryFileName = absolutePath;
		if (absolutePath.startsWith(relativePath)) {
			zipEntryFileName = absolutePath.substring(relativePath.length());
			if (zipEntryFileName.startsWith(File.separator)) {
				zipEntryFileName = zipEntryFileName.substring(1);
			}

		} else {
			throw new FileNotFoundException("Invalid Absolute Path");
		}
		/*
		 * logger.debug("zipEntryFileName:::" + relativePath.length() + "::" +
		 * zipEntryFileName);
		 */

		return zipEntryFileName;

	}



	/**
	 * Writes the Zip stream to the folder/file specified. Creates folders if
	 * already not present.
	 * 
	 * @param zipInputStream
	 * @param fileDetailsList
	 * @param targFile
	 * @throws IOException
	 */
	public static void extractFileToTarget(ZipInputStream zipInputStream,
			List<FileDetails> fileDetailsList, String targFile)
			throws IOException {
		ZipEntry zipEntry = null;
		Map<String, FileDetails> zipMap = new HashMap<String, FileDetails>();
		for (FileDetails fileDetails : fileDetailsList) {
			zipMap.put(fileDetails.getZipFileName(), fileDetails);
		}
		while (null != (zipEntry = zipInputStream.getNextEntry())) {
			String fileName = zipEntry.getName();
			String absolutePath = "";
			if (zipEntry.isDirectory() == false && targFile.endsWith(fileName)) {
				absolutePath = targFile;
			} else {
				absolutePath = targFile + File.separator + fileName;
			}
			File outFile = new File(absolutePath);
			if (null != zipMap.get(fileName)) {
				FileDetails fileDetails = zipMap.get(fileName);
				outFile.setExecutable(fileDetails.isExecutable());
				outFile.setReadable(fileDetails.isReadable());
				outFile.setWritable(fileDetails.isWritable());
			}
			/*
			 * System.out.println("----[" + outFile.getName() + "], filesize[" +
			 * zipEntry.getCompressedSize() + "]");
			 */

			if (zipEntry.isDirectory()) {
				File zipEntryFolder = new File(zipEntry.getName());
				if (zipEntryFolder.exists() == false) {
					outFile.mkdirs();
				}

				continue;
			} else {
				if (zipEntry.isDirectory() == false
						&& !targFile.endsWith(fileName)) {
					File parentFolder = outFile.getParentFile();
					if (parentFolder.exists() == false) {
						if (null != zipMap.get(fileName)) {
							FileDetails fileDetails = zipMap.get(fileName);
							parentFolder.setExecutable(fileDetails
									.isExecutable());
							parentFolder.setReadable(fileDetails.isReadable());
							parentFolder.setWritable(fileDetails.isWritable());
						}
						parentFolder.mkdirs();

					}
				}
			}

			// System.out.println("ZipEntry::"+zipEntry.getCompressedSize());
			BufferedOutputStream bos = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(outFile));
				long fileLength = (int) zipEntry.getCompressedSize();

				byte[] fileByte = new byte[FileCopyConstants.MAX_READ_SIZE];
				long totalRead = 0;
				int readSize = 0;
				while (totalRead < fileLength) {
					readSize = zipInputStream.read(fileByte);
					totalRead += readSize;
					bos.write(fileByte, 0, readSize);
				}
				bos.flush();
			} finally {
				if (bos != null) {

					bos.close();
				}
			}

			logger.info("File Copied :: " + fileName);
			System.out.println("File Copied :: " + fileName);
			// logger.debug("ZipEntry::" + zipEntry.getCompressedSize());

		}

	}

	public static void writeZippedFileToStream(ZipOutputStream zipOutputStream,
			List<FileDetails> fileDetailsList, String sourceRelativePath)
			throws IOException {
		
		for (FileDetails fileDetails : fileDetailsList) {
			File source = new File(sourceRelativePath);
			String copyFileName = null;
			if(source.isDirectory()){
				copyFileName = source.getAbsolutePath() + File.separator
						+ fileDetails.getZipFileName();
			}else{
				copyFileName = sourceRelativePath;
			}
			
			File file = new File(copyFileName);
			if (file.exists() == false) {
				throw new FileNotFoundException("File["
						+ file.getAbsolutePath() + "] Not Found");
			}
			System.out.println("fileDetails::"+fileDetails);
			ZipEntry zipEntry = new ZipEntry(fileDetails.getZipFilePartName());
			zipEntry.setMethod(ZipEntry.STORED);
			zipEntry.setSize(fileDetails.getPartialFileLength());
			zipEntry.setCompressedSize(fileDetails.getPartialFileLength());
			/*
			 * logger.debug("file.length[" + file.length() + "], offset[" +
			 * fileDetails.getOffset() + "]");
			 */
			System.out.println("Zipping the File ::"+fileDetails.getZipFilePartName());
			CRC32 crc = null;
			crc = Crc32Util.getCrc(file, fileDetails.getOffset(),
					fileDetails.getPartialFileLength());
			zipEntry.setCrc(crc.getValue());
			zipOutputStream.putNextEntry(zipEntry);

			writeZippedFileToStream(zipOutputStream, file, fileDetails);
		}
		zipOutputStream.flush();

	}

	

	public static void validateCopiedFile(List<FileDetails> fileDetailsList,
			String targFile, int threads) throws IOException,
			InterruptedException {
		Map<FileDetails, CRC32> crcDetailMap = FileCopyUtil.getCrcMap(
				fileDetailsList, targFile, threads);
		for (FileDetails fileDetails : fileDetailsList) {
			CRC32 crc = crcDetailMap.get(fileDetails);// getCrc(file, 0,
														// file.length());
			String fileCheck = " ..Corrupted";
			if (fileDetails != null
					&& crc.getValue() == fileDetails.getCrc32Value()) {
				fileCheck = " .. Ok .. ";
			}

			System.out.println("Status ::" + fileCheck + "[ "
					+ fileDetails.getZipFileName() + " ]");
		}

	}

	public static List<FileDetails> filterExistingFiles(
			List<FileDetails> fileDetailsList, String targetFile)
			throws IOException {

		Iterator<FileDetails> iterator = fileDetailsList.iterator();
		while (iterator.hasNext()) {

			FileDetails fileDetails = iterator.next();

			File file = new File(targetFile + File.separator
					+ fileDetails.getZipFileName());
			if (file.exists()) {
				CRC32 crc = Crc32Util.getCrc(file, 0, file.length());
				if (fileDetails != null
						&& crc.getValue() == fileDetails.getCrc32Value()) {
					iterator.remove();
					System.out.println("File [" + fileDetails.getZipFileName()
							+ "] already exists, Ignored");
				}
			}
		}

		return fileDetailsList;
	}



	private static void writeZippedFileToStream(
			ZipOutputStream zipOutputStream, File file, FileDetails fileDetails)
			throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			bis.skip(fileDetails.getOffset());
			int readBytes = 0;
			long totalReadLength = 0;
			byte[] fileByte = new byte[FileCopyConstants.MAX_READ_SIZE];
			long readLength = fileDetails.getPartialFileLength();

			logger.info("Transfer File ::" + fileDetails.getZipFilePartName());

			while (-1 != (readBytes = bis.read(fileByte))) {
				totalReadLength += readBytes;
				// System.out.println("length::"+readBytes);
				/*
				 * logger.debug("totalReadLength[" + totalReadLength +
				 * "],readLength[" + readLength + "]");
				 */
				if (totalReadLength > readLength) {

					long diff = totalReadLength - readLength;
					/*
					 * logger.debug("totalReadLength::[" + totalReadLength +
					 * "],readLength[" + readLength + "],[" + readBytes + "]");
					 */
					readBytes = readBytes - (int) diff;

				}
				zipOutputStream.write(fileByte, 0, readBytes);
				if (totalReadLength > readLength) {
					break;
				}
			}
		} finally {
			if (bis != null) {

				bis.close();
			}
		}
	}

	public static List<FileDetails> getSplitFileList(
			List<FileDetails> fileDetailList, long maxTransferSize) {
		List<FileDetails> splitFileDetailsList = new ArrayList<FileDetails>();
		for (FileDetails fileDetails : fileDetailList) {
			if (fileDetails.getFileLength() > maxTransferSize) {
				int filePart = 1;
				for (long offset = 0; offset < fileDetails.getFileLength();) {
					FileDetails splitFileDetail = new FileDetails();
					
					String additionalFileExtn = (filePart > 1 ? ".filemerge.part." + filePart
							: "");
					
					logger.debug("Spliting file Size::Cnt["+filePart+"]["+maxTransferSize+"], FIleSize["+fileDetails.getFileLength()+"], offset["+offset+"]");
					splitFileDetail.setZipFileName(fileDetails
							.getZipFileName());
					splitFileDetail.setZipFilePartName(fileDetails.getZipFileName()
							+ additionalFileExtn);
					long partialLength = (offset + maxTransferSize > fileDetails
							.getFileLength() ? (fileDetails.getFileLength() - offset)
							: maxTransferSize);

					splitFileDetail.setFileLength(fileDetails.getFileLength());
					splitFileDetail.setPartialFileLength(partialLength);
					splitFileDetail.setOffset(offset);
					splitFileDetail.setFilePart(filePart++);
					splitFileDetail.setReadable(fileDetails.isReadable());
					splitFileDetail.setWritable(fileDetails.isWritable());
					splitFileDetail.setExecutable(fileDetails.isExecutable());
					splitFileDetailsList.add(splitFileDetail);
					/*
					 * logger.debug("fileName[" + fileDetails.getZipFileName() +
					 * "]Partial File Length[" + partialLength + "],part[" +
					 * splitFileDetail.getFilePart() + "]offset[" + offset +
					 * "]");
					 */
					offset += maxTransferSize;
				}

			} else {
				splitFileDetailsList.add(fileDetails);

			}
		}

		return splitFileDetailsList;
	}

	public static Map<Integer, List<FileDetails>> getFileListForThread(
			List<FileDetails> fileDetailList, int threads) {
		Map<Integer, List<FileDetails>> threadListMap = new HashMap<Integer, List<FileDetails>>();
		if (threads == 1) {
			threadListMap.put(0, fileDetailList);
			return threadListMap;
		}
		BigInteger totalFileSize = new BigInteger("0");

		for (FileDetails fileDetails : fileDetailList) {
			BigInteger fileSize = new BigInteger(""
					+ fileDetails.getPartialFileLength());
			totalFileSize = totalFileSize.add(fileSize);
			// logger.info("FIles to be Transferred:["+fileDetails.getZipFileName()+"]: "+(fileSize.divide(new
			// BigInteger(""+(1024*1024))))+"M");
		}

		BigInteger maxThreadFileSize = totalFileSize.divide(new BigInteger(""
				+ threads));
		logger.info("Total Size of Files to be Transferred:: "
				+ (totalFileSize.divide(new BigInteger("" + (1024 * 1024))))
				+ "M");

		Map<Integer, BigInteger> threadSizeMap = new HashMap<Integer, BigInteger>();
		BigInteger threadListFileSize = new BigInteger("0");
		int index = 0;
		List<FileDetails> threadList = new ArrayList<FileDetails>();
		threadListMap.put(index, threadList);
		for (FileDetails fileDetails : fileDetailList) {
			BigInteger fileSize = new BigInteger(""
					+ fileDetails.getPartialFileLength());
			// logger.debug("threadListFileSize["+threadListFileSize+"]::fileSize["+fileSize+"]");
			int compare = threadListFileSize.add(fileSize).compareTo(
					maxThreadFileSize);
			// logger.debug("["+threadListFileSize+"]threadListFileSize2["+threadListFileSize.add(fileSize)+"]::fileSize["+maxThreadFileSize+"],comapre["+compare+"]");
			if (compare > 0) {
				if (index < threads) {

					index++;
					threadListFileSize = new BigInteger("0");
					threadList = new ArrayList<FileDetails>();
					threadListMap.put(index, threadList);
				}

			}
			threadListFileSize = threadListFileSize.add(fileSize);
			threadSizeMap.put(index, threadListFileSize);
			threadList.add(fileDetails);
		}

		index = threads;
		if (null != threadListMap.get(index)) {

			List<FileDetails> extraList = threadListMap.get(index);
			threadListMap.remove(index);
			threadSizeMap.remove(index);

			for (FileDetails fileDetails : extraList) {
				int minPosition = -1;
				BigInteger minSize = new BigInteger("" + 0);
				for (int position : threadSizeMap.keySet()) {
					if (minPosition == -1) {
						minPosition = position;
						minSize = threadSizeMap.get(position);
						continue;
					}
					if (threadSizeMap.get(position).compareTo(minSize) < 0) {
						minSize = threadSizeMap.get(position);
						minPosition = position;
					}
				}
				// System.out.println("minPosition::" + minPosition);
				threadList = threadListMap.get(minPosition);
				threadList.add(fileDetails);
				threadListFileSize = threadSizeMap.get(minPosition);
				threadListFileSize = threadListFileSize.add(new BigInteger(""
						+ fileDetails.getPartialFileLength()));
				threadSizeMap.put(minPosition, threadListFileSize);
			}

		}

		return threadListMap;
	}
	
	static Properties properties = null;
	public static String getPropertyVal(String propertyName) throws IOException{
		if(properties == null){
			
			loadProperties();
		}
		
		return properties.getProperty(propertyName);
	}
	
	static synchronized void  loadProperties() throws IOException{
		if(properties != null){
			return;
		}

		InputStream is = (new FileCopyUtil()).getClass().getResourceAsStream("/FileCopy.properties");
		Properties prop = new Properties();
		prop.load(is);
		properties = prop;
		
	}

	
}
