package com.gt.stick2code.filecopy.client.putfiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gt.stick2code.filecopy.client.GetFileListHelper;
import com.gt.stick2code.filecopy.common.FileCopyParameters;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;
import com.gt.stick2code.filecopy.common.RequestTypeEnum;

public class PutFileListFilterHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFileListHelper.class);

	String host;
	int port;

	public PutFileListFilterHelper(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public List<FileDetails> process(FileCopyParameters params,
			List<FileDetails> fileDetailsList) throws UnknownHostException,
			IOException, ClassNotFoundException {
		Socket socket = new Socket(host, port);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(socket.getOutputStream());

				if (ReadWriteUtil.connectToServer(bis, bos,
						RequestTypeEnum.PUTFILTERFILESLIST, params)) {
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
