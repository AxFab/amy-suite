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
package net.axfab.amy.data;

import net.axfab.amy.expr.Expression;

public class DataColumn {

	private String name;
	private String type;
	private int length;
	private boolean repeating;

	public DataColumn(String name, String type, int length, boolean repeating) 
	{
		this.name = name;
		this.type = type;
		this.length = length;
		this.repeating = repeating;
	}

	public DataColumn(String name, String type, int length) 
	{
		this(name, type, length, false);
	}

	public DataColumn(String name, String type) 
	{
		this(name, type, 0, false);
	}

	public DataColumn(Expression expr, String alias) {
		// TODO
		
	}

	public DataColumn copy() {
		return new DataColumn(name, type, length, repeating);
	}

}
