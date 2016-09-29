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

import java.util.LinkedList;
import java.util.List;

public class DataTable {

	private List<DataColumn> columns = new LinkedList<DataColumn>();
	
	public DataTable(String name) {
	}

	public void addColumn(DataColumn column) {
		columns.add(column.copy());
	}

	public void addColumn(String name, String type, int length, boolean repeating) {
		columns.add(new DataColumn(name, type, length, repeating));
	}

	public void addColumn(String name, String type, int length) {
		columns.add(new DataColumn(name, type, length));
	}

	public void addColumn(String name, String type) {
		columns.add(new DataColumn(name, type));
	}
	
	
	
}
