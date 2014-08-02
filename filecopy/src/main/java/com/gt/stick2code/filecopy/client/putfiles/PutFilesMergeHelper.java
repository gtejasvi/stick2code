package com.gt.stick2code.filecopy.client.putfiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;
import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class PutFilesMergeHelper {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(PutFilesMergeHelper.class);

	String host;
	int port;
	boolean securemode = false;
	long timeout = 1000000;
	List<FileDetails> fileDetailList;
	FileCopyParameters params;
	String password;
	String key;

	public PutFilesMergeHelper(String host, int port,boolean securemode,
			List<FileDetails> fileDetailList, FileCopyParameters params,
			String password, String key) {
		super();
		this.host = host;
		this.port = port;
		this.securemode = securemode;
		this.fileDetailList = fileDetailList;
		this.params = params;
		this.password = password;
		this.key = key;
	}

	/**
	 * Opens a Socket Connection with server and Indicates the server to merge
	 * the files.
	 * 
	 * @param fileDetailList
	 *            FileDetails In case of mutliple threads, this might have a
	 *            partial list of files
	 * @param targetFile
	 *            Folder/File where the fetched file has to be placed
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
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
	public void process() throws UnknownHostException, IOException,
			ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, KeyManagementException, KeyStoreException, CertificateException {
		//Socket socket = new Socket(host, port);
		Socket socket = FileCopySocketConnectionUtil.getSocket(host, port,securemode);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());

			if (ReadWriteUtil.connectToServer(bis, bos,
					RequestTypeEnum.PUTFILEMERGE, params, password, key)) {
				ReadWriteUtil.writeObjectToStream(bos, fileDetailList);

				if (ReadWriteUtil.getAcknowledgement(bis) == false) {
					throw new IOException("Server Responded with Merge Failure");
				}
			}

		} finally {
			ReadWriteUtil.closeInputStream(bis);
			ReadWriteUtil.closeOutputStream(bos);
			ReadWriteUtil.closeSocket(socket);
		}
	}

}
