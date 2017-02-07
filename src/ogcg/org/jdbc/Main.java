package ogcg.org.jdbc;

import java.util.HashMap;
import java.util.Map;

import ogcg.org.jtest.Damson;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JDBCOConnection con = new JDBCOConnection(5432, "productos", "postgres", "hcacogcg", "localhost");
		con.startConnection();
		QueryExecutor qs = con.Query();
		try {
			qs.prepare(qs.createQuery().select("*").from("producto").where("id", "<="));
			qs.setInt(1, 5);
			qs.setString(2,"hel");
			Result res = qs.executeQuery().getResult();
			String[][] result = res.getResultSet();
			HashMap<String,String> columns = res.getColumns();
			String[] columnNames = res.getColumnNames();
			for(int i = 0 ; i < columnNames.length ; i++){
				System.out.println(columnNames[i]);
			}
			for(Map.Entry<String, String> k: columns.entrySet()){
				System.out.println(k.getKey() + ":" + k.getValue());
			}
			for(int i = 0 ; i < result.length ; i++){
				for(int j = 0; j < result[0].length ; j++){
					System.out.println(result[i][j]);
				}
			}
			Damson d = new Damson();
			d.addElem("DAM", columnNames,result);
			System.out.println(d);
			System.out.println("*************************************");
			qs.clear();
			qs.prepare(qs.createQuery().delete().from("producto").where("id", "="));
			qs.setInt(1,13);
			qs.execute();
			qs.prepare(qs.createQuery().select("*").from("producto"));
			res = null;
			result = null;
			columns = null;
			d = null;
			res = qs.executeQuery().getResult();
			result = res.getResultSet();
			columns = res.getColumns();
			columnNames = res.getColumnNames();
			d = new Damson();
			d.addElem("DAM2", columnNames,result);
			System.out.println(d);
			System.out.println("*************************************");
			System.out.println("*************************************");

			qs.clear();
			qs.prepare(qs.createQuery().insertInto("producto")
					.column("id").column("descript") );
			qs.setInt(1,13);
			qs.setString(2, "potato");
			qs.execute();
			qs.prepare(qs.createQuery().select("*").from("producto"));
			res = null;
			result = null;
			columns = null;
			d = null;
			res = qs.executeQuery().getResult();
			result = res.getResultSet();
			columns = res.getColumns();
			columnNames = res.getColumnNames();
			d = new Damson();
			d.addElem("DAM2", columnNames,result);
			System.out.println(d);
			System.out.println("*************************************");
			System.out.println("*************************************");
			qs.clear();
			qs.prepare(qs.createQuery().update("producto").set("descript").where("id", "="));
			qs.setString(1, "chayote");
			qs.setInt(2,13);
			qs.execute();
			qs.prepare(qs.createQuery().select("*").from("producto"));
			res = null;
			result = null;
			columns = null;
			d = null;
			res = qs.executeQuery().getResult();
			result = res.getResultSet();
			columns = res.getColumns();
			columnNames = res.getColumnNames();
			d = new Damson();
			d.addElem("DAM2", columnNames,result);
			System.out.println(d);
			System.out.println("*************************************");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
