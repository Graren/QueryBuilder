package ogcg.org.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Query {
	private String query;
	private Status status;
	private String table;
	private List<String> columns;
	private Actions action;
	private HashMap<String,String> _where;
	private List<String> _operators;
	private Integer paramsLeft;
	
	private enum Status {
		Uninitialized,
		Select,
		Insert,
		Update,
		Delete,
		Select_From,
		Delete_From,
		Insert_Values,
		Update_Set,
		Where,
		Where_And,
		Where_Or,
		Where_not,
		Prepared,
		Ready
	}
	
	private enum Actions {
		Select,
		Insert,
		Update,
		Delete,
	}
	
	public Query(){
		columns = new ArrayList<String>();
		this._where = new  HashMap<String,String>();
		this._operators = new ArrayList<>();
		this.paramsLeft = 0;
		this.status = Status.Uninitialized;
	}
	
	public Query select(String ... columns) throws Exception{
		if(this.status.equals(Status.Uninitialized)){
			this.action = Actions.Select;
			for(String s : columns){
				if(s.contains("*")){
					this.columns.clear();
					this.columns.add(s);
					break;
				}
				this.columns.add(s);
			}
			this.status = Status.Select;
		}
		else {
			this.exThrown();
		}
		return this;
	}
	
	
	public Query delete(){
		this.action = Actions.Delete;
		this.status = Status.Delete;
		return this;
		
	}
	
	public Query insertInto(String table){
		this.action = Actions.Insert;
		this.status = Status.Insert;
		this.table = table;
		return this;
	}
	
	public Query update(String table){
		this.action = Actions.Update;
		this.status = Status.Update;
		this.table = table;
		return this;
	}
	
	public Query from(String table) 
			throws Exception{

		if(this.status.equals(Status.Select)){
			this.table = table;
			this.status = Status.Select_From;
		}
		else if (this.status.equals(Status.Delete)){
			this.table = table;
			this.status = Status.Delete_From;
		}
		else{
			this.exThrown();
		}
		return this;
	}
	
	public Query set(String col) 
			throws Exception{
		if(this.status.equals(Status.Update) || this.status.equals(Status.Update_Set)){
			this.columns.add(col);
			this.paramsLeft++;
			this.status = Status.Update_Set;
		}
		else{
			this.exThrown();
		}
		return this;
	}
	
	public Query column(String col) 
			throws Exception{
		if(this.status.equals(Status.Insert) || this.status.equals(Status.Insert_Values)){
			this.columns.add(col);
			this.paramsLeft ++;
			this.status = Status.Insert_Values;
		}
		
		else{
			this.exThrown();
		}
		
		return this;
	}
	
	public Query where(String col,String op) 
			throws Exception{
		String logic = "Where";
		if(this.status.equals(Status.Delete_From) ||
			   this.status.equals(Status.Select_From) ||
			   this.status.equals(Status.Update_Set))
		{
			this._where.put(logic,col);
			this._operators.add(op);
			this.paramsLeft ++;
			this.status = Status.Where;
		}
		else{
			exThrown();
		}
		return this;
	}
	
	private Query where(String logic,String col, String op)
			throws Exception{
		if(this.status.equals(Status.Delete_From) ||
				this.status.equals(Status.Select_From) ||
				this.status.equals(Status.Update_Set) ||
				this.status.equals(Status.Where_Or)  ||
				this.status.equals(Status.Where_And)  )
		{
			this._where.put(logic,col);
			this._operators.add(op);
			this.paramsLeft ++;
			this.status = Status.Where;
		}
		else{
			exThrown();
		}
		return this;
	}
	
	public Query and(String col, String op) throws Exception{
		this.status = Status.Where_Or;
		return this.where("AND",col,op);
	}
	
	public Query or(String col, String op) throws Exception{
		this.status = status.Where_And;
		return this.where("OR",col,op);
	}
	
	protected String getPreparedStatement(){
		String q = "";
		if(this.status.equals(Status.Select_From) 	 ||
				   	this.status.equals(Status.Where)  ||
				   	this.status.equals(Status.Insert_Values) ||
					this.status.equals(Status.Where_Or)  ||
					this.status.equals(Status.Where_And)  )
		{
			switch(this.action){
				case Select:
					q = this.buildSelect();
					break;
				case Insert:
					q = this.buildInsert();
					break;
				case Update:
					q = this.buildUpdate();
					break;
				case Delete:
					q = this.buildDelete();
					break;
			}
		}
		this.setPrepared();
		if(this.paramsLeft == 0){
			this.setReady();
		}
		return q;
	}
	
	public void clearWhole(){
		this.query = "";
		this.columns.clear();
		this.action = null;
		this._where.clear();
		this.paramsLeft = 0;
		this.status =  Status.Uninitialized;
		return;
	}
	
	private String buildSelect(){
		String query = "SELECT";
		for (String col: this.columns){
			query += " " + col + " ";
		}
		query += "FROM ";
		query += this.table + " ";
		if(this._where.size() > 0){
			Integer i = 0;
			for(Map.Entry<String,String> k: this._where.entrySet()){
				query += buildWhere(k,i);
			}
		}
		return query;
	}
	
	private String buildInsert(){
		String query = "INSERT INTO ";
		query += this.table + " ";
		query += "(" + this.buildColumns() +")";
		query += " VALUES (";
		for(Integer i = 0 ; i < columns.size() ; i++) {
			if(i.equals(0)){
				query+= "?";
			}
			else{
				query += ",?";
			}
		}
		query += ")";
		return query;
	}
	
	private String buildUpdate(){
		String query = "UPDATE ";
		query += this.table + " ";
		query += "SET ";
		query += this.buildColumnsUpdate() + " ";

		if(this._where.size() > 0){
			Integer i = 0;
			for(Map.Entry<String,String> k: this._where.entrySet()){
				query += buildWhere(k,i);
			}
		}
		return query;
	}
	
	private String buildDelete(){
		String query = "DELETE FROM ";
		query += this.table + " ";
		if(this._where.size() > 0){
			Integer i = 0;
			for(Map.Entry<String,String> k: this._where.entrySet()){
				query += buildWhere(k,i);
			}
		}
		return query;
	}
	
	private String buildColumns(){
		String str = "";
		for(String s : this.columns){
			if(str.equals("")){
				str+= s;
			}
			else{
				str+= "," + s;
			}
		}
		return str;
	}
	
	private String buildColumnsUpdate(){
		String str = "";
		for(String s : this.columns){
			if(str.equals("")){
				str+= s + "= ?";
			}
			else{
				str+= "," + s + "= ?";
			}
		}
		return str;
	}
	
	private String buildWhere(Map.Entry<String, String> k,int index){
		String str = " " +k.getKey()+ " "  + k.getValue()+ " " + this._operators.get(index) + " ?";
		return str;
	}
	
	private void exThrown() throws Exception{
		System.out.print("Misused");
		this.clearWhole();
		throw new Exception ("Please restart");
	}
	
	private void setPrepared(){
		this.status = Status.Prepared;
	}
	
	public Integer getParamsLeft(){
		return this.paramsLeft;
	}
	
	public Boolean isReady(){
		return this.status.equals(Status.Ready) ? true : false;
	}
	
	public void setReady(){
		this.status = Status.Ready;
	}
	public static void main(String[] args) {
		Query q = new Query();
		try {
			System.out.println(q.select("*").from("producto").where("id","=").getPreparedStatement());
			System.out.println(q.insertInto("producto").column("producto.id").column("producto.descript")
					.getPreparedStatement());
			System.out.println(q.update("producto").set("id").set("descript").where("id", "=")
					.getPreparedStatement());
			System.out.println(q.delete().from("producto").where("id", "=").getPreparedStatement());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
