package com.gt.stick2code.filecopy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gt.stick2code.filecopy.common.FileCopyUtil;
import com.gt.stick2code.filecopy.common.FileDetails;
import com.gt.stick2code.filecopy.common.ReadWriteUtil;

public class FileCopyUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		file = file.getCanonicalFile();
	}
	File file = new File(".");

	@Test
	public void testgetMaxSize(){
		ReadWriteUtil.getSize("1024M");
	}
	
	@Test
	public void testGetFileDetailList() throws IOException, InterruptedException {
		
		System.out.println(file.getCanonicalPath());
		List<FileDetails> fileDetailList = FileCopyUtil.getFileDetailList(file.getAbsolutePath(),true,3);
		Assert.assertTrue(fileDetailList.size() > 0);
	}
	
	//@Test
	public void testWriteZippedFile() throws IOException, InterruptedException {
		System.out.println(file.getCanonicalPath());
		List<FileDetails> fileDetailList = FileCopyUtil.getFileDetailList(file.getAbsolutePath(),true,3);
		ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream("D:\\temp\\stick2code.zip")));
		
		//FileCopyUtil.extractFileToTarget(zipInputStream,);
		
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream("D:\\temp\\stick2code.zip")));
		for(FileDetails fileDetails : fileDetailList){
			fileDetails.setOffset(0);
			fileDetails.setPartialFileLength(fileDetails.getFileLength());
		}
		FileCopyUtil.writeZippedFileToStream(zipOutputStream, fileDetailList,"D:\\temp\\stick2code.zip");
		zipOutputStream.flush();
		zipOutputStream.close();
		Assert.assertTrue(fileDetailList.size() > 0);
	}
	
	@Test
	public void testThreadSplit() throws IOException, InterruptedException{
		List<FileDetails> fileDetailList = FileCopyUtil.getFileDetailList(file.getAbsolutePath(),true,3);
		long maxTransferSize = ReadWriteUtil.getSize("50M");
		List<FileDetails> splitFileDetailList = FileCopyUtil.getSplitFileList(fileDetailList, maxTransferSize);
		Map<Integer,List<FileDetails>> threadListMap = FileCopyUtil.getFileListForThread(splitFileDetailList, 8);
		System.out.println(threadListMap.size());
	}

}
