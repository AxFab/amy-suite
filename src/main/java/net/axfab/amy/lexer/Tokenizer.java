/*    This file is part of AmySuite.

    AmySuite is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as 
    published by the Free Software Foundation, either version 3 of the 
    License, or (at your option) any later version.

    AmySuite is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public 
    License along with AmySuite.
    If not, see <http://www.gnu.org/licenses/>. 
*/
package net.axfab.amy.lexer;

import java.util.Map.Entry;

public class Tokenizer {

	private int row;
	private int column;
	private String text;
	private Token unreadToken;
	private Language lang;
	
	public Tokenizer(Language language) {
		lang = language;
		lang.addPattern(TokenClass.OCTAL, "[+-]?0[0-7]+");
		lang.addPattern(TokenClass.HEXADECIMAL, "[+-]?0x[0-9A-Fa-f]+");
		lang.addPattern(TokenClass.DECIMAL, "[+-]?[0-9]+");
		lang.addPattern(TokenClass.NUMBER, "[+-]?[0-9]+\\.[0-9]+");
	}

	public void setup(String text) {
		setup(text, 1, 1);
	}

	public void setup(String text, int row, int column) {
		this.text = text;
		this.row = row;
		this.column = column;
		this.consume(0);
	}

	public Token read() {
		if (unreadToken != null) {
			Token token = unreadToken;
			unreadToken = null;
			return token;
		}
		
		return readToken();
	}

	public void unread(Token token) {
		if (unreadToken != null) {
			throw new RuntimeException("unread is not implemented for several tokens.");
		}
		unreadToken = token;
	}

	private Token readToken() {
		Token token = null;
		if (text == null || text.length() == 0) {
			return null;
		}
		
		// DELIMITERS
		for (Delimiter rule : lang.getDelimiters()) {
			if (text.startsWith(rule.getPrefix())) {
				return readUntil(rule.getPrefix(), rule.getSuffix(), rule.getEscape(), rule.getLabel());
			}
		}
		
		// OPERATORS & KEYWORDS
		for (Entry<String, TokenClass> keyword : lang.getKeywords()) {
			String keywd = keyword.getKey();
			int lg = keywd.length();
			if (text.length() >= lg && text.substring(0, lg).compareToIgnoreCase(keywd) == 0) {
			// if (text.startsWith()) {
				return readKeyword(keyword.getKey(), keyword.getValue());
			}
		}
		
		// PATTERNS
		for (PatternToken pattern : lang.getPatterns()) {
			if (pattern.isMatching(text)) {
				String value = pattern.getValue();
				token = new Token(value, pattern.getLabel(), row, column);
				consume(value.length());
				return token;
			}
		}
		
		if (Character.isAlphabetic(text.charAt(0)) || text.charAt(0) == '_') {
			token = readIdentifier();
			return token;
		}

		throw new RuntimeException("Can't find the next token: " + text);
	}

	private void consume(int lg) {
		int idx;
		for (idx = 0; idx < lg; ++idx) {
			if (text.charAt(idx) == '\n') {
				row++;
				column = 1;
			} else {
				column++;
			}
		}

		for (; idx < text.length() && Character.isWhitespace(text.charAt(idx)); ++idx) {
			if (text.charAt(idx) == '\n') {
				row++;
				column = 1;
			} else {
				column++;
			}
		}

		text = text.substring(idx);
	}

	private Token readUntil(String prefix, String suffix, String escape, TokenClass type) {
		int idx, origin = prefix.length();
		for (;;) {
			idx = text.indexOf(suffix, origin);
			int pos = idx + suffix.length() - escape.length();
			if (text.indexOf(escape, pos) != pos) {
				break;
			}
			origin = idx + suffix.length();
		}

		String str = text.substring(0, idx + suffix.length());
		consume(str.length());
		return new Token(str, type, row, column);
	}

	private Token readKeyword(String key, TokenClass value) {
		Token token = new Token(key, value, row, column);
		consume(key.length());
		return token;
	}
	

	private Token readIdentifier() {
		int sz = 0;
		while (sz < text.length() && (Character.isAlphabetic(text.charAt(sz)) ||
				Character.isDigit(text.charAt(sz)) ||
				text.charAt(sz) == '_')) {
			++sz;
		}

		String str = text.substring(0, sz);
		consume(sz);
		return new Token(str, TokenClass.IDENTIFIER, row, column);
	}

}
