package com.gt.stick2code.filecopy.common;

import java.io.Serializable;

public class FileDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1876048113480749490L;
	
	String zipFileName;
	String zipFilePartName;
	long fileLength;
	long offset;
	long partialFileLength;
	int filePart;
	long crc32Value;
	long lastModified;
	boolean readable;
	boolean writable;
	boolean executable;

	
	public String getZipFileName() {
		return zipFileName;
	}
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}
	
	public String getZipFilePartName() {
		return zipFilePartName;
	}
	public void setZipFilePartName(String zipFilePartName) {
		this.zipFilePartName = zipFilePartName;
	}
	public long getFileLength() {
		return fileLength;
	}
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public long getPartialFileLength() {
		return partialFileLength;
	}
	public void setPartialFileLength(long partialFileLength) {
		this.partialFileLength = partialFileLength;
	}
	public int getFilePart() {
		return filePart;
	}
	public void setFilePart(int filePart) {
		this.filePart = filePart;
	}
	public long getCrc32Value() {
		return crc32Value;
	}
	public void setCrc32Value(long crc32Value) {
		this.crc32Value = crc32Value;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	public boolean isReadable() {
		return readable;
	}
	public void setReadable(boolean readable) {
		this.readable = readable;
	}
	public boolean isWritable() {
		return writable;
	}
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	public boolean isExecutable() {
		return executable;
	}
	public void setExecutable(boolean executable) {
		this.executable = executable;
	}

	public String toString(){
		return zipFilePartName;
	}

}
