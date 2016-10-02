package net.axfab.amy.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternToken {

	private TokenClass label;
	private Pattern pattern;
	private Matcher match;
	
	public PatternToken(TokenClass label, String pattern) {
		this.label = label;
		this.pattern = Pattern.compile("^" + pattern);
	}
	
	public TokenClass getLabel() {
		return label;
	}
	
	public boolean isMatching(String str) {
		match = pattern.matcher(str);
		return match.matches();
	}
	
	public String getValue() {
		return match.group(0);
	}
	
}
