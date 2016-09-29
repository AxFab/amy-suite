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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Tokenizer {

	private int row;
	private int column;
	private String text;
	private Token unreadToken;
	private static Map<String, TokenClass> keywords;

	
	static {
		keywords = new HashMap<String, TokenClass>();
		keywords.put("!", TokenClass.OPERATOR);
		keywords.put("+", TokenClass.OPERATOR);
		keywords.put("-", TokenClass.OPERATOR);
		keywords.put("*", TokenClass.OPERATOR);
		keywords.put("/", TokenClass.OPERATOR);
		keywords.put("%", TokenClass.OPERATOR);
		keywords.put("==", TokenClass.OPERATOR);
		keywords.put("!=", TokenClass.OPERATOR);
		keywords.put("=", TokenClass.OPERATOR);
		keywords.put("<<", TokenClass.OPERATOR);
		keywords.put("<=", TokenClass.OPERATOR);
		keywords.put("<", TokenClass.OPERATOR);
		keywords.put(">>", TokenClass.OPERATOR);
		keywords.put(">=", TokenClass.OPERATOR);
		keywords.put(">", TokenClass.OPERATOR);

		keywords.put("SELECT", TokenClass.KEYWORD1);
		keywords.put("FROM", TokenClass.KEYWORD1);
		keywords.put("WHERE", TokenClass.KEYWORD1);
		keywords.put("WITH", TokenClass.KEYWORD1);
		keywords.put("RECURSIVE", TokenClass.KEYWORD1);
		keywords.put("ALL", TokenClass.KEYWORD1);
		keywords.put("ORDER", TokenClass.KEYWORD1);
		keywords.put("BY", TokenClass.KEYWORD1);
		keywords.put("LIMIT", TokenClass.KEYWORD1);
		keywords.put("OFFSET", TokenClass.KEYWORD1);
		keywords.put("HAVING", TokenClass.KEYWORD1);
		keywords.put("VALUES", TokenClass.KEYWORD1);
		keywords.put("AS", TokenClass.KEYWORD1);
		keywords.put("DISTINCT", TokenClass.KEYWORD1);
		keywords.put("CAST", TokenClass.KEYWORD1);
		keywords.put("COLLATE", TokenClass.KEYWORD1);
		keywords.put("NOT", TokenClass.KEYWORD1);
		keywords.put("LIKE", TokenClass.KEYWORD1);
		keywords.put("GLOB", TokenClass.KEYWORD1);
		keywords.put("REGEXP", TokenClass.KEYWORD1);
		keywords.put("MATCH", TokenClass.KEYWORD1);
		keywords.put("ESCAPE", TokenClass.KEYWORD1);
		keywords.put("IS", TokenClass.KEYWORD1);
		keywords.put("ISNULL", TokenClass.KEYWORD1);
		keywords.put("NOTNULL", TokenClass.KEYWORD1);
		keywords.put("NOT", TokenClass.KEYWORD1);
		keywords.put("BETWEEN", TokenClass.KEYWORD1);
		keywords.put("AND", TokenClass.KEYWORD1);
		keywords.put("EXISTS", TokenClass.KEYWORD1);
		keywords.put("CASE", TokenClass.KEYWORD1);
		keywords.put("WHEN", TokenClass.KEYWORD1);
		keywords.put("THEN", TokenClass.KEYWORD1);
		keywords.put("ELSE", TokenClass.KEYWORD1);
		keywords.put("END", TokenClass.KEYWORD1);
		
		keywords.put("NULL", TokenClass.KEYWORD2);
		keywords.put("NULLSTRING", TokenClass.KEYWORD2);
		keywords.put("NULLDATE", TokenClass.KEYWORD2);
	}
	
	public Tokenizer() {

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
		unreadToken = token;
	}

	private Token readToken() {
		if (text.startsWith("#"))
			return readUntil("#", "\n", null, TokenClass.COMMENT);
		else if (text.startsWith("//"))
			return readUntil("//", "\n", null, TokenClass.COMMENT);
		else if (text.startsWith("\""))
			return readUntil("\"", "\"", "\\\"", TokenClass.STRING);
		else if (text.startsWith("'"))
			return readUntil("'", "'", "''", TokenClass.STRING);
		
		for (Entry<String, TokenClass> keyword : keywords.entrySet()) {
			if (text.startsWith(keyword.getKey())) {
				return readKeyword(keyword.getKey(), keyword.getValue());
			}
		}
		
		if (Character.isDigit(text.charAt(0))) {
			return readNumber();
		} else if (Character.isAlphabetic(text.charAt(0)) || text.charAt(0) == '_') {
			return readIdentifier();
		}

		return null;
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

	private Token readNumber() {
		return null;
	}

	private Token readIdentifier() {
		int sz = 0;
		while (Character.isAlphabetic(text.charAt(sz)) ||
				Character.isDigit(text.charAt(sz)) ||
				text.charAt(sz) == '_') {
			++sz;
		}

		String str = text.substring(0, sz);
		consume(sz);
		return new Token(str, TokenClass.IDENTIFIER, row, column);
	}

}
