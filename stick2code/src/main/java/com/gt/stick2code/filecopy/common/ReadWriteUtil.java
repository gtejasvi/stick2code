package com.gt.stick2code.filecopy.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadWriteUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(ReadWriteUtil.class);
	/**
	 * Read an object from the input Stream. Steps:1) Read the message length as
	 * the first few bytes indicate the length of the message to follow. 3) Cast
	 * the object to RequestResponseWrapper and Check the Ack for Success or
	 * Failure message 4) Return the Object witin the wrapper
	 * 
	 * @param is
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Object readInputStreamObject(InputStream is)
			throws ClassNotFoundException, IOException {
		//logger.debug("getInputStreamObject");
		long messageLength = getMessageLength(is);
		byte[] bytesToRead = new byte[FileCopyConstants.MAX_READ_SIZE];
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		long totalReadLength = 0;
		while (totalReadLength < messageLength) {
			int readLength = is.read(bytesToRead);
			totalReadLength += readLength;
			//logger.debug("getInputStreamObject::ReadLength::" + readLength);
			byteOutputStream.write(bytesToRead, 0, readLength);

		}
		logger.debug("Object Bytes Read::" + byteOutputStream.size());
		ObjectInputStream objIntputStream = new ObjectInputStream(
				new ByteArrayInputStream(byteOutputStream.toByteArray()));
		
		RequestResponseWrapper response = (RequestResponseWrapper) objIntputStream
				.readObject();
		if (response.getAck() == Ack.FAILURE) {
			throw new IOException("The server responded with a failure Ack");
		}
		return response.getObject();
	}

	/**
	 * Reads the input stream for the Message length. It is determined using the
	 * following logic 1) Read the first byte and convert it to the int
	 * equivalent which will give the length of the header. 2) Read the Number
	 * of bytes indicated by the header byte, and Serialize the bytes to Object.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static long getMessageLength(InputStream is) throws IOException {
		byte[] bytesToRead = new byte[1];
		int readLength = is.read(bytesToRead);
		if (readLength != 1) {
			throw new IOException("Unable to read the Length Byte::readLength"
					+ readLength);
		}
		int headerLength = (int) bytesToRead[0];
		//logger.debug("headerLength::" + headerLength);

		bytesToRead = new byte[headerLength];
		readLength = is.read(bytesToRead);
		String length = "";
		for (byte readbyte : bytesToRead) {
			length += (char) readbyte;
		}
		long messageLength = Long.parseLong(length);
		//logger.debug("messageLength::" + messageLength);
		return messageLength;

	}

	/**
	 * Takes the Object to the written to the stream, wraps it with a
	 * RequestResponseWrapper and Sets the Acknowledgement to Success. Writes
	 * the object (wrapper) to stream
	 * 
	 * @param os
	 * @param obj
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void writeObjectToStream(OutputStream os, Object obj)
			throws ClassNotFoundException, IOException {
		RequestResponseWrapper wrapper = new RequestResponseWrapper(Ack.SUCCESS);
		wrapper.setObject(obj);
		writeObjectToStream(os, wrapper);
		os.flush();
	}

	/**
	 * Takes the RequestResponseWrapper as input and writes it to the
	 * OutputStream.
	 * 
	 * @param os
	 * @param wrapper
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void writeObjectToStream(OutputStream os,
			RequestResponseWrapper wrapper) throws ClassNotFoundException,
			IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream objectByteOS = new ObjectOutputStream(baos);
		objectByteOS.writeObject(wrapper);
		objectByteOS.flush();
		logger.debug("writeObjectToStream:size:" + baos.toByteArray().length);
		byte[] objectBBytes = baos.toByteArray();
		byte[] lengthBytes = getLengthDigitBytes(objectBBytes.length);
		os.write(lengthBytes);
		os.write(objectBBytes);
		os.flush();
	}

	/**
	 * Converts the long to equivalent String and gets the bytes corresponding
	 * to each of the digits of the inputs
	 * 
	 * @param length
	 * @return
	 */
	private static byte[] getLengthDigitBytes(long length) {
		String lengthString = "" + length;
		int size = lengthString.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(size);
		char[] lengthChar = lengthString.toCharArray();
		byte[] lengthByte = new byte[lengthChar.length + 1];
		lengthByte[0] = (byte) size;
		for (int iCnt = 0; iCnt < lengthChar.length; iCnt++) {
			lengthByte[iCnt + 1] = (byte) lengthChar[iCnt];
		}

		return lengthByte;
	}

	/**
	 * Parses the String for size notations k|K -- Kilobytes, m|M -- Megabyte
	 * and g|G -- Giga Bytes. Based on the notation multiples the numeric part
	 * with the equivalent and returns the size in Bytes Gets the
	 * 
	 * @param sSize
	 * @return
	 */
	public static long getSize(String sSize) {
		sSize = sSize.toLowerCase();
		int sizeLengt = sSize.length();
		char multchar = sSize.toCharArray()[sizeLengt - 1];
		long size = 0;
		if (multchar == 'k') {
			sSize = sSize.substring(0, sizeLengt - 1);
			size = Long.parseLong(sSize);
			size = size * 1024;
		} else if (multchar == 'm') {
			sSize = sSize.substring(0, sizeLengt - 1);
			size = Long.parseLong(sSize);
			size = size * 1024 * 1024;
		} else if (multchar == 'g') {
			sSize = sSize.substring(0, sizeLengt - 1);
			size = Long.parseLong(sSize);
			size = size * 1024 * 1024 * 1024;
		} else {
			size = Long.parseLong(sSize);
		}
		return size;
	}

	/**
	 * Reads the response Object and if read correctly returns true.
	 * 
	 * @param is
	 * @return On Success returns true, on failure Acknowledgement throws
	 *         IOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 *             On IO Exception of on Failure Acknowledgement
	 */
	public static boolean getAcknowledgement(InputStream is)
			throws ClassNotFoundException, IOException {
		Object obj = readInputStreamObject(is);
		
		if(obj != null && obj.getClass().isEnum() && obj == Ack.FAILURE){
			
				return false;
		}
		return true;
	}
	
	public static void closeOutputStream(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (Exception e) {

		}
	}

	public static void closeInputStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception e) {

		}
	}

	public static void closeSocket(Socket socket) {
		try {
			if (socket != null && socket.isClosed() == false) {
				socket.close();
			}
		} catch (Exception e) {
		}
	}
	
	public static boolean connectToServer(BufferedInputStream bis, BufferedOutputStream bos,RequestTypeEnum requestType,FileCopyParameters params) throws ClassNotFoundException, IOException{
		logger.info("Connecting:: To Service::"+requestType);
		ReadWriteUtil.writeObjectToStream(bos, requestType);
		
		if (ReadWriteUtil.getAcknowledgement(bis)) {
			logger.info("Service Connected::"+requestType+", Sending Params....");
			ReadWriteUtil.writeObjectToStream(bos, params);
			if (ReadWriteUtil.getAcknowledgement(bis)){
				logger.info("Connected:: To Service::"+requestType);
				return true;
			}
				
		}
		return false;
	}
	
	public static RequestTypeEnum getRequestType(InputStream is, OutputStream os)
			throws IOException, ClassNotFoundException {
		RequestTypeEnum requestTypeEnum = (RequestTypeEnum) ReadWriteUtil
				.readInputStreamObject(is);
		RequestResponseWrapper responseWrapper = new RequestResponseWrapper(
				Ack.SUCCESS);
		responseWrapper.setObject(Ack.SUCCESS);
		ReadWriteUtil.writeObjectToStream(os, responseWrapper);
		return requestTypeEnum;

	}
}
