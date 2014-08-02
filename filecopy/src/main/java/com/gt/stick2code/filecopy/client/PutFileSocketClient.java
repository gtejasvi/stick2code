package com.gt.stick2code.filecopy.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.client.putfiles.PutFileListFilterHelper;
import com.gt.stick2code.filecopy.client.putfiles.PutFilesHelper;
import com.gt.stick2code.filecopy.client.putfiles.PutFilesMergeHelper;
import com.gt.stick2code.filecopy.client.putfiles.PutFilesValidateHelper;
import com.gt.stick2code.filecopy.common.FileCopyConstants;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class PutFileSocketClient extends Thread {

	private static final Logger logger = LoggerFactory
			.getLogger(PutFileSocketClient.class);

	String host;
	int port;
	int threads = 1;
	boolean recursive = false;
	boolean overwrite = false;
	boolean securemode = false;

	FileCopyParameters params;
	String password;
	String key;

	public PutFileSocketClient(String host, int port,boolean securemode,
			FileCopyParameters params, int threads, String password,String key) {
		this.host = host;
		this.port = port;
		this.params = params;
		this.threads = (threads < 1) ? 1 : threads;
		this.password = password;
		this.key = key;
		this.securemode = securemode;

	}

	/**
	 * Usage : <Host> <Port> <Src file/folder> <Targer file/Folder> <Threads>
	 * <Max Size>
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 * @throws DecoderException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws CertificateException 
	 * @throws KeyStoreException 
	 * @throws KeyManagementException 
	 */
	public static void main(String[] arg) throws IOException,
			ClassNotFoundException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, KeyManagementException, KeyStoreException, CertificateException {

		String usage = "Usage : <Host> <Port> <Src file/folder> <Targer file/Folder> <[threads]> <[max file chunk]> [-R for Recursive]";

		List<String> argList = new ArrayList<String>();
		boolean recursive = false;
		boolean overwrite = false;
		boolean securemode = false;
		for (String argument : arg) {
			if (argument.startsWith("-")) {

				char[] argCharArr = argument.toLowerCase().toCharArray();
				for (char argChar : argCharArr) {
					switch (argChar) {
					case '-':
						continue;
					case 'r':
						recursive = true;
						break;
					case 'o':
						overwrite = true;
						break;
					case 's' :
						securemode = true;
						break;
					default:
						System.out.println("Invalid Argument :: " + usage);
						System.exit(0);
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
		int argCnt = 0;
		String host = args[argCnt++];
		int port = Integer.parseInt(args[argCnt++]);
		String sourceFile = args[argCnt++];
		String targetFile = args[argCnt++];

		String password = null;
		if (args.length > argCnt) {
			password = args[argCnt++];
		} else {
			password = "";
		}

		
		int threads = 1;
		if (args.length > argCnt) {
			threads = Integer.parseInt(args[argCnt++]);
		}
		long transferChunkSize = 0;
		if (args.length > argCnt) {
			transferChunkSize = ReadWriteUtil.getSize(args[argCnt++]);
		} else {
			transferChunkSize = ReadWriteUtil.getSize("50M");
		}

		String key = null;
		if (args.length > argCnt) {
			key = args[argCnt++];
		} else {
			key = FileCopyUtil.getPropertyVal(FileCopyConstants.DEFAULT_KEY);
		}

		FileCopyParameters fileCopyParameters = new FileCopyParameters();
		fileCopyParameters.setRecursive(recursive);
		fileCopyParameters.setOverwrite(overwrite);
		fileCopyParameters.setSourceFile(sourceFile);
		fileCopyParameters.setTargetFile(targetFile);
		fileCopyParameters.setTransferChunkSize(transferChunkSize);
		
		logger.debug("File Transfer Size ::"+ transferChunkSize);

		PutFileSocketClient client = new PutFileSocketClient(host, port,securemode,
				fileCopyParameters, threads, password,key);
		client.process();
	}

	private void process() throws UnknownHostException, IOException,
			ClassNotFoundException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, KeyManagementException, KeyStoreException, CertificateException {
		logger.info("Fetch the File List");
		List<FileDetails> fileDetailList = FileCopyUtil.getFileDetailList(
				params.getSourceFile(), params.isRecursive(), threads);
		logger.info("File List Fetched:: Total Files ::["
				+ fileDetailList.size() + "]");
		List<FileDetails> filteredFileDetailsList = fileDetailList;
		if (params.isOverwrite() == false) {
			PutFileListFilterHelper filter = new PutFileListFilterHelper(host,
					port,securemode, password,key);
			filteredFileDetailsList = filter.process(params, fileDetailList);
		}
		logger.info("Files To Send ::" + filteredFileDetailsList.size());
		if (fileDetailList.size() > 0) {
			List<FileDetails> splitFileDetailsList = FileCopyUtil
					.getSplitFileList(filteredFileDetailsList,
							params.getTransferChunkSize());
			Map<Integer, List<FileDetails>> threadListMap = FileCopyUtil
					.getFileListForThread(splitFileDetailsList, threads);
			PutFilesHelper putFiles = new PutFilesHelper(host, port,securemode, null,
					params, password, key);
			putFiles.processPutFilesToTarget(threadListMap, params);

			PutFilesMergeHelper mergeHelper = new PutFilesMergeHelper(host,
					port, securemode,splitFileDetailsList, params,password, key);
			mergeHelper.process();

			logger.info("Files Merged Successfully");
			System.out.println("Files Merged Successfully");

			PutFilesValidateHelper validationHelper = new PutFilesValidateHelper(
					host, port, securemode,splitFileDetailsList, params, password,key);
			validationHelper.process();

			logger.info("Files Validation Successfully..");
			System.out.println("Files Validation Successfully..");

		} else {
			System.out.println("Nothing To Move .....");
		}
	}

}
