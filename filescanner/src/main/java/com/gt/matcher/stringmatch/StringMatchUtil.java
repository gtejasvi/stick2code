package com.gt.matcher.stringmatch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringMatchUtil {

	
	public static final Logger logger = LoggerFactory
			.getLogger(StringMatchUtil.class);

	public PatternDetail getStringMatchRegex(List<String> matchStringList1,List<String> matchStringList2){
		List<List<WordMatchDetail>> matchList = getStringDifference(matchStringList1,matchStringList2);
		String regex="^\\s*";
		String shortPattern = "^\\s*";
		boolean isFirstLine = true;
		boolean isPreviousMatch = false;
		for(List<WordMatchDetail> lineMatchList : matchList){
			for(WordMatchDetail match : lineMatchList){
				if(match.isMatch()){
					String regexPattern = Pattern.quote(match.getMainWord());
					regex +=regexPattern + "\\s*";
					isPreviousMatch = true;
				}else{
					if(isPreviousMatch){
						regex += ".+\\s*";
					}
					isPreviousMatch = false;
				}
				
				//logger.debug("MAINWORD::["+match.getMainWord()+"], COMPAREWORD["+match.getCompareWord()+"]");
			}
			
			if(isFirstLine){
				shortPattern = regex;
				isFirstLine = false;
			}
			
		}
		PatternDetail patternDetail = new PatternDetail();
		patternDetail.setPattern(regex);
		patternDetail.setShortPattern(shortPattern);
		patternDetail.setNofOfLines(matchList.size());
		logger.debug(regex);
		return patternDetail;
	}
	
	
	
	public List<List<WordMatchDetail>> getStringDifference(
			List<String> matchStringList1,List<String> matchStringList2 ) {

		logger.debug("getStringDifference");
		List<List<WordMatchDetail>>  lineMatchList = compareString(
						matchStringList1,
						matchStringList2);
				
				//differenceMap.put(key, getMatchPercent(wordPosMatchList));
		
		

		return lineMatchList;
	}

	
	private List<List<WordMatchDetail>> compareString(List<String> mainStringList,
			List<String> compareStringList) {
		int index = 0;
		List<List<WordMatchDetail>> lineMatchList = new ArrayList<List<WordMatchDetail>>();
		while(index < mainStringList.size() && index < compareStringList.size() ){
			String mainLine = mainStringList.get(index);
			String compareLine = compareStringList.get(index);
			
			List<WordMatchDetail> wordMatchDetailList = compareStringLine(mainLine,compareLine);
			//getMatchWeight(wordMatchDetailList);
			index++;
			lineMatchList.add(wordMatchDetailList);

		}
		
		return lineMatchList;
		
	}
	


	/**
	 * Compares two single line strings and returns the count of matching words
	 * 
	 * @param mainString
	 *            String to be compared
	 * @param compareString
	 *            String to be compared with
	 * @return List containing the words that match and do not match. Pass an
	 *         instanitiated field which can be retrieved for further use down
	 *         the line
	 */
	private List<WordMatchDetail> compareStringLine(String mainString,
			String compareString) {
		String[] mainLineWords = mainString.split("\\s");
		String[] compareLineWords = compareString.split("\\s");

		List<WordMatchDetail> wordPosMatchList = new ArrayList<WordMatchDetail>();

		int mainPos = 0;
		int comparePos = 0;

		while (mainPos < mainLineWords.length
				&& comparePos < compareLineWords.length) {
			WordMatchDetail wordMatchDetail = new WordMatchDetail();
			if (mainLineWords[mainPos].equals(compareLineWords[comparePos])) {
				wordMatchDetail.setMainWord(mainLineWords[mainPos]);
				wordMatchDetail.setCompareWord(compareLineWords[mainPos]);
				wordMatchDetail.setMatch(true);
				mainPos++;
				comparePos++;
			} else {
				wordMatchDetail.setCompareWord(compareLineWords[mainPos]);
				comparePos++;
			}
			wordPosMatchList.add(wordMatchDetail);
		}
		WordMatchDetail wordMatchDetail = new WordMatchDetail();
		while (mainPos < mainLineWords.length) {
			wordMatchDetail.setMainWord(mainLineWords[mainPos]);
			wordPosMatchList.add(wordMatchDetail);
			mainPos++;
		}
		while (comparePos < compareLineWords.length) {
			wordMatchDetail.setCompareWord(compareLineWords[mainPos]);
			wordPosMatchList.add(wordMatchDetail);
			comparePos++;
		}

		return wordPosMatchList;

	}

}
