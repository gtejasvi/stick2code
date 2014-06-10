package com.gt.stick2code.filecopy;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gt.stick2code.filecopy.security.FileCopySocketConnectionUtil;

public class FileCopyKeyGenTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetRandomKey() throws NoSuchAlgorithmException {
		byte[] key = FileCopySocketConnectionUtil.getRandomKey();
		;
		System.out.println(Hex.encodeHex(key));
	}

}
