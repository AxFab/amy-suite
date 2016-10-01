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

import java.util.HashMap;

public class DataRow extends HashMap<String, DataValue> {

	private static final long serialVersionUID = -5036812566522516486L;

	public DataValue getColumn(String name) {
		DataValue value = get(name);
		if (value == null) {
			for (String column : keySet()) {
				int k = column.lastIndexOf('.');
				if (column.substring(k+1).equals(name)) {
					value = get(column);
					break;
				}
			}
		}
		return value;
	}
	
	public void putColumn() {
		
	}
	
	public void dump() {
		for (Entry<String, DataValue> data : entrySet()) {
			System.out.println(data.getKey() + " -> " + data.getValue().getValue());
		}
	}
}
