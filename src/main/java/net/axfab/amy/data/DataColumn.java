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

import net.axfab.amy.expr.Resolver;
import net.axfab.amy.expr.Primitive;

public class DataColumn {

	private String name;
	private Primitive type;
	private int length;
	private Resolver method;

	public DataColumn(String name, Primitive type, int length) {
		this.name = name;
		this.type = type;
		this.length = length;
	}

	public DataColumn(String name, Primitive type) {
		this(name, type, 0);
	}

	public DataColumn(Resolver expr, String alias) {
		this.name = alias;
		this.type = Primitive.Undefined;
		this.method = expr;
	}

	public DataColumn copy() {
		return new DataColumn(name, type, length);
	}

	public String getName() {
		return name;
	}

	public Primitive getType() {
		return type;
	}

	public Resolver getMethod() {
		return method;
	}
}
