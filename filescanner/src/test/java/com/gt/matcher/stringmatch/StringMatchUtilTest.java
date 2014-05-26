package com.gt.matcher.stringmatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

public class StringMatchUtilTest {


	static List<String> list1 = new ArrayList<String>();
	static List<String> list2 = new ArrayList<String>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	
		BufferedReader br1 = new BufferedReader(new FileReader("./sample/match1.txt"));
		String line = null;
		while(null != (line = br1.readLine())){
			list1.add(line);
		}
		br1.close();
		
		BufferedReader br2 = new BufferedReader(new FileReader("./sample/match2.txt"));
		while(null != (line = br2.readLine())){
			list2.add(line);
		}
	}

	
	
	public void testNewLine(){
		System.out.print("Test"+"\n"+"Test2");
	}
	
	@Test
	public void testGetStringDifferencePercentage() throws IOException {
		StringMatchUtil stringMatchUtil = new StringMatchUtil();
		
		
		PatternDetail pattern = stringMatchUtil.getStringMatchRegex(list1,list2);
		
		String fileName = "./sample/filescan_error.log";
		
		Map<String,PatternDetail> patternDetailMap = new HashMap<String,PatternDetail>();
		patternDetailMap.put("1",pattern);
		
		FileScanner fileScanner = new FileScanner(fileName, patternDetailMap);
		fileScanner.matchInFile();
		
		
	}

}
