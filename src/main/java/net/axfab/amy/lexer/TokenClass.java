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

public enum TokenClass {
	COMMENT,
	STRING,
	IDENTIFIER,
	OPERATOR,
	KEYWORD1,
	KEYWORD2,
	KEYWORD3,
	KEYWORD4,
	
	OCTAL, HEXADECIMAL, DECIMAL, NUMBER
}
