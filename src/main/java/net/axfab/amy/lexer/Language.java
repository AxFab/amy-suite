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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Language {

	private static final Logger logger = Logger.getLogger("lexer");
	private List<Delimiter> delimiters = new LinkedList<>();
	private Map<String, TokenClass> keywords = new LinkedHashMap<>();
	private List<PatternToken> patterns = new LinkedList<>();

	public static Language load(String url) {
		InputStream input = Language.class.getClassLoader().getResourceAsStream(url);
		try {
			if (input == null) {
				input = new FileInputStream(new File(url));
			}
		} catch (FileNotFoundException ex) {
			logger.log(Level.SEVERE, "Fail to load language, no file at " + url);
		}

		if (input == null) {
			return null;
		}

		try {
			Language lang = new Language();
			lang.load(input, url);
			return lang;
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Fail to load language, error reading file at " + url);
			return null;
		}
	}

	public void addDelimiter(TokenClass label, String prefix, String suffix, String escape) {
		this.delimiters.add(new Delimiter(label, prefix, suffix, escape));
	}

	public void addKeyword(String value, TokenClass category) {
		this.keywords.put(value, category);
	}

	public void addPattern(TokenClass label, String value) {
		this.patterns.add(new PatternToken(label, value));
	}

	List<Delimiter> getDelimiters() {
		return delimiters;
	}

	Set<Entry<String, TokenClass>> getKeywords() {
		return keywords.entrySet();
	}

	List<PatternToken> getPatterns() {
		return patterns;
	}

	private void load(InputStream input, String url) throws IOException {
		int status = 0;
		TokenClass category = TokenClass.COMMENT;
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			if (line.startsWith("#") || line.matches("^\\s*$")) {
				continue;
			} else if (line.startsWith("  -")) {
				line = line.substring(3).trim();
				if (status == 0) {
					addKeyword(line, category);
					continue;
				} 
				
				int k = line.indexOf(':');
				String key = line.substring(0, k);
				TokenClass label = TokenClass.valueOf(key);
				String value = line.substring(k+1);
				if (status == 1) {
					addPattern(label, value);
				} else {
					String[] values = value.split("\\|");
					if (values.length == 3) {
						addDelimiter(label, values[0].trim(), values[1].trim(), values[2].trim());
					} else if (values.length == 2) {
						addDelimiter(label, values[0].trim(), values[1].trim(), null);
					} else {
						logger.log(Level.WARNING, "Delimiter patterns must have 2 or 3 values splited by '|' at " + url + ": " + line);
					}
				}
			} else if (line.endsWith(":")) {
				line = line.substring(0, line.indexOf(':')).trim();
				if (line.equals("Delimeters")) {
					status = 2;
				} else if (line.equals("Patterns")) {
					status = 1;
				} else {
					status = 0;
					category = TokenClass.valueOf(line);
				}
			} else {
				logger.log(Level.WARNING, "Pattern unrecognized on langugage file at " + url + ": " + line);
			}
		}	
	}

}
