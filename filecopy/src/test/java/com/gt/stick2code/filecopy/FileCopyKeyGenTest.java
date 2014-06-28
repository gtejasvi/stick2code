package com.gt.stick2code.filecopy;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
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
	public void testGetRandomKey() throws NoSuchAlgorithmException, IOException {
		byte[] key = FileCopySocketConnectionUtil.getRandomKey();
		;
		System.out.println(Hex.encodeHex(key));
	}
	
	@Test
	public void testEncPwd() throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException {
		byte[] keyByte = FileCopySocketConnectionUtil.getRandomKey();
		String keyString = new String(Hex.encodeHex(keyByte));
		String pwd = FileCopySocketConnectionUtil.encryptPwd("TEST", keyString);
		System.out.println("Pwd::"+pwd);
	
	}


}
