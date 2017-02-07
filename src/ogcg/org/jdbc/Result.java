package ogcg.org.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Result {
	private String[][] resultSet;
	private HashMap<String,String> columns;
	private String[] columnNames;
	
	Result(){
		this.resultSet = null;
		this.columns = new HashMap<>();
		columnNames = null;
	}
	
	public Result buildResultSet(ResultSet rs, ResultSetMetaData rsmd) throws SQLException{
		List<String[]> rsArr = new ArrayList<>();
		List<String> columnNames = new ArrayList<>();
		for(Integer i = 0 ; i < rsmd.getColumnCount(); i++){
			this.getColumns().put(rsmd.getColumnName(i+1), rsmd.getColumnClassName(i+1));
			columnNames.add(rsmd.getColumnName(i+1));
		}
		this.columnNames = columnNames.toArray(new String[rsmd.getColumnCount()]);
		rs.beforeFirst();
		for(Integer i = 0 ; rs.next() ; i++){
			String [] arr = new String[rsmd.getColumnCount()];
			for(Integer j = 0; j < rsmd.getColumnCount() ; j++){
				arr[j] = rs.getObject(j+1).toString();
			}
			rsArr.add(arr);
		}
		this.resultSet = rsArr.toArray(new String[rsArr.size()][rsmd.getColumnCount()]);
		return this;
	}
	
	/**
	 * @return the result
	 */
	public String[][] getResultSet() {
		return resultSet;
	}

	/**
	 * @param result the result to set
	 */
	private void setResult(String[][] result) {
		this.resultSet = result;
	}

	/**
	 * @return the columns
	 */
	public HashMap<String, String> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	private void setColumns(HashMap<String, String> columns) {
		this.columns = columns;
	}
	
	public String[] getColumnNames(){
		return this.columnNames;
	}
}
