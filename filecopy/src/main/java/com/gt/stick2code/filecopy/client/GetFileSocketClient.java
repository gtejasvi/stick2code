package com.gt.stick2code.filecopy.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.MergeFilesUtil;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class GetFileSocketClient extends Thread {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFileSocketClient.class);

	String host;
	int port;
	int threads = 1;
	long transferChunkSize = 52428800;
	boolean recursive = false;
	boolean overwrite = false;
	boolean securemode = false;
	String key ;
	
	FileCopyParameters fileCopyParameters;

	public GetFileSocketClient(String host, int port,boolean securemode, FileCopyParameters fileCopyParameters,
			 int threads,String key) {
		this.host = host;
		this.port = port;
		this.fileCopyParameters = fileCopyParameters;
		this.threads = (threads < 1) ? 1 : threads;
		this.transferChunkSize = (transferChunkSize < ReadWriteUtil.getSize("1M")) ? ReadWriteUtil.getSize("1M")
				: transferChunkSize;
		this.securemode = securemode;
		this.key = key;

	}

	/**
	 * Usage : <Host> <Port> <Src file/folder> <Targer file/Folder> <Threads> <Encryption Key>
	 * <Max Size>
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException 
	 */
	public static void main(String[] arg) throws IOException,
			ClassNotFoundException, InterruptedException {

		String usage = "Usage : <Host> <Port> <Src file/folder> <Targer file/Folder> <[threads]> <[max file chunk]> <[Encryption Key]>] [ -[r/R recursive][-o/O overwrite][-s secure transfer]]";
		
		List<String> argList = new ArrayList<String>();
		boolean recursive = false;
		boolean overwrite = false;
		boolean securemode = false;
		for(String argument : arg){
			//System.out.println("argument::"+argument);
			if(argument.startsWith("-")){
				
				char[] argCharArr = argument.toLowerCase().toCharArray();
				for(char argChar : argCharArr){
					switch(argChar){
					case '-' : continue;
					case 'r' : recursive = true;break;
					case 'o' : overwrite = true;break;
					case 's' : securemode = true;break;
					default : System.out.println("Invalid Argument :: " + usage);System.exit(0);
					}
				}
				continue;
			}
			
			argList.add(argument);
		}
		String[] args = argList.toArray(new String[0]);
		if (args.length < 4) {
			System.out.println(usage);
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String sourceFile = args[2];
		String targetFile = args[3];
		int threads = 1;
		if (args.length > 4) {
			threads = Integer.parseInt(args[4]);
		}
		long transferChunkSize = 0;
		if (args.length > 5) {
			transferChunkSize = ReadWriteUtil.getSize(args[5]);
		} else {
			transferChunkSize = ReadWriteUtil.getSize("50M");
		}
		
		String key = null;
		if (args.length > 6) {
			key = args[6];
		} else {
			key = FileCopySocketConnectionUtil.DEFAULT_KEY;
		}
		
		FileCopyParameters fileCopyParameters = new FileCopyParameters();
		fileCopyParameters.setRecursive(recursive);
		fileCopyParameters.setOverwrite(overwrite);
		fileCopyParameters.setSourceFile(sourceFile);
		fileCopyParameters.setTargetFile(targetFile);
		fileCopyParameters.setTransferChunkSize(transferChunkSize);
		
		GetFileSocketClient client = new GetFileSocketClient(host,
				port,securemode, fileCopyParameters, threads,key);
		try{
		client.process();
		}catch(Exception e){
			logger.error("Error in Getting the files",e);
		}
	}

	private void process() throws UnknownHostException, IOException,
			ClassNotFoundException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		
		
		
		logger.debug("Sending the Source File to Server::[" + fileCopyParameters.getSourceFile() + "]");
		GetFileListHelper getFileList = new GetFileListHelper(host,
				port,securemode,key);
		List<FileDetails> fileDetailList = getFileList
				.processGetFileListFromSource(fileCopyParameters);
		
		logger.debug("Splitting the File List::");
		if(fileCopyParameters.isOverwrite() == false){
			FileCopyUtil.filterExistingFiles(fileDetailList, fileCopyParameters.getTargetFile());
		}
		if (fileDetailList.size() > 0) {
			List<FileDetails> splitFileDetailsList = FileCopyUtil
					.getSplitFileList(fileDetailList, transferChunkSize);
			
			Map<Integer, List<FileDetails>> threadListMap = FileCopyUtil
					.getFileListForThread(splitFileDetailsList, threads);
			GetFilesHelper getFiles = new GetFilesHelper(host,
					port,securemode, null, fileCopyParameters,key);
			
			getFiles.processGetFilesFromSource(threadListMap, fileCopyParameters);
			MergeFilesUtil mergeFilesUtil = new MergeFilesUtil();
			mergeFilesUtil.mergeFiles(splitFileDetailsList, fileCopyParameters.getTargetFile(), threads);
			FileCopyUtil.validateCopiedFile(fileDetailList, fileCopyParameters.getTargetFile(),threads);
		}
	}

}
