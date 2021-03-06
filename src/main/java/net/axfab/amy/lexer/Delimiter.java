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

public class Delimiter {

	private TokenClass label;
	private String prefix;
	private String suffix;
	private String escape;
	
	public Delimiter(TokenClass label, String prefix, String suffix, String escape) {
		this.label = label;
		this.prefix = prefix;
		this.suffix = suffix;
		this.escape = escape;
	}
	
	public TokenClass getLabel() {
		return label;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public String getEscape() {
		return escape;
	}
}
