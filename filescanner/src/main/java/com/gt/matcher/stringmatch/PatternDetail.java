package com.gt.matcher.stringmatch;

import java.util.regex.Pattern;

public class PatternDetail {

	int nofOfLines = 0;
	Pattern pattern = null;
	Pattern shortPattern = null;
	
	public int getNofOfLines() {
		return nofOfLines;
	}
	public void setNofOfLines(int nofOfLines) {
		this.nofOfLines = nofOfLines;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public void setPattern(String regex) {
		this.pattern = Pattern.compile(regex);
	}
	public Pattern getShortPattern() {
		return shortPattern;
	}
	public void setShortPattern(Pattern shortPattern) {
		this.shortPattern = shortPattern;
	}
	
	public void setShortPattern(String regex) {
		this.shortPattern = Pattern.compile(regex);
	}
	
}
