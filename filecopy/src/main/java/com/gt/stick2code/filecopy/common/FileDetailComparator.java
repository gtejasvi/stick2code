package com.gt.stick2code.filecopy.common;

import java.util.Comparator;

public class FileDetailComparator implements Comparator<FileDetails> {


	public int compare(FileDetails det1, FileDetails det2) {

		if(det1 == null && det2 == null){
			return 0;
		}else if(det1 == null){
			return 1;
		}else if(det2 == null){
			return -1;
		}
		
		int diff1 = det1.getZipFileName().compareTo(det2.getZipFileName());
		if(diff1 == 0){
			return det1.getFilePart() - det2.getFilePart();
		}
		
		return diff1;
	}

}
