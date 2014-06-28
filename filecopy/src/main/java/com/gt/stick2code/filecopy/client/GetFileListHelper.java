package com.gt.stick2code.filecopy.client;

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
import javax.net.ssl.SSLSocket;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.common.Ack;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;
import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class GetFileListHelper {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GetFileListHelper.class);
	
	String host;
	int port;
	String password;
	String key;
	boolean securemode;
	
	public GetFileListHelper(String host,int port,boolean securemode,String password,String key){
		this.host = host;
		this.port = port;
		this.password = password;
		this.key = key;
		this.securemode = securemode;
	}
	
	/**
	 * This method is used to to Fetch the List of Files along with its size,
	 * crc32 value from the source(in this case the server) Steps1: Send the
	 * Request Type Step2: Read an acknowledgement of receiving the Request type
	 * Step3: Send the source folder on the server side Step4: Read the
	 * ArrayList containing all the files within the source folder.
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @throws KeyManagementException 
	 * @throws DecoderException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 */
	public List<FileDetails> processGetFileListFromSource(FileCopyParameters params)
			throws UnknownHostException, IOException, ClassNotFoundException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException {
		//Socket socket = new Socket(host, port);
		Socket socket = FileCopySocketConnectionUtil.getSocket(host, port,securemode);
		
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());
			
			if (ReadWriteUtil.connectToServer(bis, bos, RequestTypeEnum.GETFILELIST, params,password,key)) {
				ReadWriteUtil.writeObjectToStream(bos, Ack.READY);

				@SuppressWarnings("unchecked")
				List<FileDetails> fileDetailList = (List<FileDetails>) ReadWriteUtil
						.readInputStreamObject(bis);
				return fileDetailList;
			}
			throw new IOException(
					"Server returned a Failure Acknowledgement. For more details check the Server");
		} finally {
			ReadWriteUtil.closeInputStream(bis);
			ReadWriteUtil.closeOutputStream(bos);
			ReadWriteUtil.closeSocket(socket);
		}

	}
}
