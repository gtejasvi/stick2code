package com.gt.stick2code.filecopy;

import java.io.FileNotFoundException;
import java.net.Socket;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gt.stick2code.filecopy.server.FileCopySocketServer;

public class FileCopySocketServerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFileDetailList() throws FileNotFoundException {
		FileCopySocketServer fileCopyServer = new FileCopySocketServer(new Socket(),3);
		
	}

}
