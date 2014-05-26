package com.gt.matcher.stringmatch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileScanner {

	private static final Logger logger = LoggerFactory
			.getLogger(FileScanner.class);

	private BufferedReader br;
	private List<String> fileLineList;
	private int lineNumber = 1;
	private Map<String, PatternDetail> patternDetailMap;
	private int fileReadBuffer = 0;
	private StringBuilder bufferedLine;

	public FileScanner(String fileName,
			Map<String, PatternDetail> patternDetailMap) throws IOException {
		this.patternDetailMap = patternDetailMap;
		br = new BufferedReader(new FileReader(fileName));
		fileLineList = new LinkedList<String>();
		String line = null;

		for (String key : patternDetailMap.keySet()) {
			PatternDetail patternDetail = patternDetailMap.get(key);
			if (fileReadBuffer < patternDetail.getNofOfLines()) {
				fileReadBuffer = patternDetail.getNofOfLines();
			}
		}

		fileReadBuffer += 5;
		bufferedLine = new StringBuilder();
		while (null != (line = br.readLine())
				&& fileLineList.size() < fileReadBuffer) {
			fileLineList.add(line);
			bufferedLine.append(line + "\n");
		}
	}

	private void addMatchLine(int lineNumber,String patternName,Map<String,List<Integer>> patternPositionMap){
		List<Integer> matchLineArray = patternPositionMap.get(patternName);
		if(matchLineArray == null){
			matchLineArray = new ArrayList<Integer>();
			patternPositionMap.put(patternName, matchLineArray);
		}
		matchLineArray.add(lineNumber);
	}
	
	public Map<String,List<Integer>> matchInFile() throws IOException {
		Map<String,List<Integer>> patternPositionMap = new HashMap<String, List<Integer>>();
		do {
			for (String patternName : patternDetailMap.keySet()) {
				PatternDetail patternDetail = patternDetailMap.get(patternName);
				
				if(patternDetail.getShortPattern().matcher(bufferedLine).lookingAt()){
					if(patternDetail.getPattern().matcher(bufferedLine).lookingAt()){
						addMatchLine(lineNumber,patternName,patternPositionMap);
					}
					
				}
				
			}
		}while (null != getNextLine());
		
		return patternPositionMap;
	}

	private String getNextLine() throws IOException {

		String line = br.readLine();
		if (line != null) {
			fileLineList.add(line);
			bufferedLine.append(line + "\n");
			lineNumber++;
		}
		if (fileLineList.isEmpty()) {
			return null;
		}
		
		//logger.debug("1::Line["+lineNumber+"]"+fileLineList.get(0));
		//logger.debug("2::Line["+lineNumber+"]"+bufferedLine.substring(0, fileLineList.get(0).length() + 1));
		
		bufferedLine.delete(0, fileLineList.get(0).length() + 1);
		fileLineList.remove(0);
		if (fileLineList.isEmpty()) {
			return null;
		}
		
		return fileLineList.get(0);
	}


}
