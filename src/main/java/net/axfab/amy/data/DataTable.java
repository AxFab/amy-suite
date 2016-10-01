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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DataTable {

	private String name;
	private int selectedRow;
	private ArrayList<DataRow> rows = new ArrayList<>();
	private Map<String, DataColumn> columns = new HashMap<>();
	
	public DataTable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public DataTable addColumn(DataColumn column) {
		String name = column.getName();
		columns.put(name, column.copy());
		return this;
	}
	
	public boolean rewindRow() {
		selectedRow = -1;
		return nextRow();
	}
	
	public boolean nextRow() {
		return ++selectedRow < rows.size();
	}
	
	public void fetchRow(String table, DataRow obj) throws DataError {
		DataRow row = getRow();
		for (Entry<String, DataColumn> column : columns.entrySet()) {
			String key = table + "." + column.getKey();
			DataValue value = row.get(column.getKey());
			if (value == null) {
				throw new DataError("Error on column matching: " + column.getKey());
			} else if (value.getType() != column.getValue().getType()) {
				throw new DataError("Error on column type: " + column.getKey());
			}
			obj.put(key, value.copy());
		}
	}
	
	public DataTable pushRow(DataRow data) throws DataError {
		DataRow row = new DataRow();
		for (Entry<String, DataColumn> column : columns.entrySet()) {
			String key = column.getKey();
			DataValue value = data.getColumn(key);
			if (value == null) {
				throw new DataError("Unknown column named: " + key);
			} 
			row.put(key, value.copy());
		}
		rows.add(row);
		return this;
	}
	
	public void dump() {
		for (DataRow row : rows) {
			row.dump();
			System.out.println("  ------");
		}
	}

	private DataRow getRow() {
		return rows.get(selectedRow);
	}

	public DataColumn getColumn(String name) {
		DataColumn value = columns.get(name);
		if (value == null) {
			for (String column : columns.keySet()) {
				int k = column.lastIndexOf('.');
				if (column.substring(k+1).equals(name)) {
					value = columns.get(column);
					break;
				}
			}
		}
		return value;
	}


}
