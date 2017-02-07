package ogcg.org.jdbc;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryExecutor {
	private Connection conn;
	private PreparedStatement ps;
	private Query query;
	private ResultSet rs;
	private Integer resType;
	private Integer resCon;
	private Integer paramsLeft;
	private Result result;
	
	QueryExecutor(Connection conn,int rs_opt1,int rs_opt2){
		this.ps = null;
		this.query = null;
		this.resType = rs_opt1;
		this.resCon = rs_opt2;
		this.conn = conn;
	}
	
	QueryExecutor(Connection conn){
		this.ps = null;
		this.query = null;
		this.resType = ResultSet.TYPE_SCROLL_SENSITIVE;
		this.resCon = ResultSet.CONCUR_READ_ONLY;
		this.conn = conn;
		this.paramsLeft = 0;
		this.result = new Result();
	}
	
	public Query createQuery(){
		return new Query();
	}
	
	public void prepare(Query q){
		String stmt = q.getPreparedStatement();
		System.out.println(stmt);
		paramsLeft = q.getParamsLeft();
		this.query =q;
		try {
			this.ps = this.conn.prepareStatement(stmt,this.resType,this.resCon);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setInt(Integer arg0, Integer arg1){
		try {
			if(this.paramsLeft > 0 ){
				this.ps.setInt(arg0, arg1);
				this.paramsDec();
			}
			else {
				System.out.println("Fully prepared Statement");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setString(Integer arg0, String arg1){
		try {
			if(this.paramsLeft > 0 ){
				this.ps.setString(arg0, arg1);
				this.paramsDec();
			}
			else {
				System.out.println("Fully prepared Statement");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setBoolean(Integer arg0, Boolean arg1){
		try {
			if(this.paramsLeft > 0 ){
				this.ps.setBoolean(arg0, arg1);
				this.paramsDec();
			}
			else {
				System.out.println("Fully prepared Statement");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setFloat(Integer arg0, Float arg1){
		try {
			if(this.paramsLeft > 0 ){
				this.ps.setFloat(arg0, arg1);
				this.paramsDec();
			}
			else {
				System.out.println("Fully prepared Statement");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setArray(Integer arg0, String[] arg1){
		try {
			if(this.paramsLeft > 0 ){
				this.ps.setArray(arg0, this.conn.createArrayOf("VARCHAR", arg1));
				this.paramsDec();
			}
			else {
				System.out.println("Fully prepared Statement");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setArray(Integer arg0, Integer[] arg1){
		try {
			if(this.paramsLeft > 0 ){
				this.ps.setArray(arg0, this.conn.createArrayOf("Integer", arg1));
				this.paramsDec();
			}
			else {
				System.out.println("Fully prepared Statement");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public QueryExecutor executeQuery() throws Exception{
		try{
			if(this.getParamsLeft().equals(0) && this.query.isReady()){
				ResultSet rs = this.ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				this.result = new Result();
				this.result.buildResultSet(rs, rsmd);
			}
			else{
				throw new Exception("Unfulfilled parameters");
			}
		}
		catch(Exception e){
			throw e;
		}
		return this;
	}
	
	public Boolean execute() throws Exception{
		Boolean a = false;
		try{
			if(this.getParamsLeft().equals(0) && this.query.isReady()){
				a = this.ps.execute();
			}
			else{
				throw new Exception("Unfulfilled parameters");
			}
		}
		catch(Exception e){
			throw e;
		}
		return a;
	}
	
	private void paramsDec(){
		this.paramsLeft--;
		if(this.paramsLeft.equals(0)){
			this.query.setReady();
		}
	}
	
	private Integer getParamsLeft(){
		return this.paramsLeft;
	}
	
	public void clear(){
		this.ps = null;
		this.paramsLeft = 0;
		this.result=null;
		this.query.clearWhole();
		this.query = null;
	}
	
	public Result getResult(){
		return this.result;
	}
	
}
