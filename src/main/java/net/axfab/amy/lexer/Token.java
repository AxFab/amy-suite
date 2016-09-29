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

public class Token {

	private String text;
	private TokenClass type;
	private int row;
	private int column;
	
	public Token(String text, TokenClass type, int row, int column) {
		this.text = text;
		this.type = type;
		this.row = row;
		this.column = column;
	}

	public String getLitteral() {
		return text;
	}
	
	@Override
	public String toString() {
		return type + ": " + text;
	}

	public boolean isEquals(String string) {
		return text.equals(string);
	}

	public boolean isOfType(TokenClass type) {
		return this.type == type;
	}

	public void setLitteral(String text) {
		this.text = text;
	}
	
	public String getPosition() {
		return "ligne " + row + ", column " + column;
	}
}
