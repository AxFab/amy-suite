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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Language {

	private List<Delimiter> delimiters = new LinkedList<>();
	private Map<String, TokenClass> keywords = new HashMap<>();
	private List<Delimiter> patterns = new LinkedList<>();

	public static Language load(String url) {
		InputStream input = Language.class.getClassLoader().getResourceAsStream(url);
		try {
			if (input == null) {
				input = new FileInputStream(new File(url));
			}
		} catch (FileNotFoundException ex) {
			// TODO log
		}

		if (input == null) {
			return null;
		}

		try {
			Language lang = new Language();
			lang.load(input);
			return lang;
		} catch (IOException ex) {
			// TODO -- log
			return null;
		}
	}

	public void addDelimiter(String label, String prefix, String suffix, String escape) {
		this.delimiters.add(new Delimiter(label, prefix, suffix, escape));
	}

	public void addKeyword(String value, TokenClass category) {
		this.keywords.put(value, category);
	}

	public void addPattern(String key, String value) {
		// TODO Auto-generated method stub
	}

	List<Delimiter> getDelimiters() {
		return delimiters;
	}

	Set<Entry<String, TokenClass>> getKeywords() {
		return keywords.entrySet();
	}

	List<Delimiter> getPatterns() {
		return patterns;
	}

	private void load(InputStream input) throws IOException {
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
				String value = line.substring(k+1);
				if (status == 1) {
					addPattern(key, value);
				} else {
					String[] values = value.split("\\|");
					if (values.length == 3) {
						addDelimiter(key, values[0].trim(), values[1].trim(), values[2].trim());
					} else if (values.length == 2) {
						addDelimiter(key, values[0].trim(), values[1].trim(), null);
					} else {
						// TODO log
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
				// TODO log
			}
		}	
	}

}
