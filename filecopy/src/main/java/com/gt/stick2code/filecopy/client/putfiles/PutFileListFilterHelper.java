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

import com.gt.stick2code.filecopy.client.GetFileListHelper;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;
import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class PutFileListFilterHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFileListHelper.class);

	String host;
	int port;
	boolean securemode= false;
	String key;
	String password;

	public PutFileListFilterHelper(String host, int port,boolean securemode,String password,String key) {
		this.host = host;
		this.port = port;
		this.securemode = securemode;
		this.password = password;
		this.key = key;
	}

	public List<FileDetails> process(FileCopyParameters params,
			List<FileDetails> fileDetailsList) throws UnknownHostException,
			IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, KeyManagementException, KeyStoreException, CertificateException {
		//Socket socket = new Socket(host, port);
		Socket socket = FileCopySocketConnectionUtil.getSocket(host, port,securemode);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());

				if (ReadWriteUtil.connectToServer(bis, bos,
						RequestTypeEnum.PUTFILTERFILESLIST, params,password,key)) {
					logger.info("PUTFILTERFILESLIST Request Acknowledged");
					ReadWriteUtil.writeObjectToStream(bos, fileDetailsList);
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
