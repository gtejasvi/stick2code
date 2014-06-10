package com.gt.stick2code.filecopy.common;

import java.io.Serializable;

public class FileCopyParameters implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8713408969972829766L;
	
	private String sourceFile;
	private String targetFile;
	private boolean recursive;
	private boolean overwrite;
	private long transferChunkSize;
	
	public String getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getTargetFile() {
		return targetFile;
	}
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}
	public boolean isRecursive() {
		return recursive;
	}
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
	public boolean isOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	public long getTransferChunkSize() {
		return transferChunkSize;
	}
	public void setTransferChunkSize(long transferChunkSize) {
		this.transferChunkSize = transferChunkSize;
	}


}
