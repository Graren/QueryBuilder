package ogcg.org.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCOConnection {
	
	private Integer port;
	private String dbName;
	private String user;
	private String password;
	private String host;
	private Connection conn ;
	private QueryExecutor exe;
	
	JDBCOConnection (Integer port, String dbName, String user, String password, String host) {
		this.setPort(port);
		this.setDbName(dbName);
		this.setUser(user);
		this.setPassword(password);
		this.setHost(host);
		startConnection();
	}
	
	public void startConnection(){
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager
					.getConnection("jdbc:postgresql://"+ this.host + ":" +this.port +"/" + this.dbName,
							this.user, this.password);
			this.conn = conn;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.conn = conn;
	}
	
	public Boolean isConnected(){
		Boolean a = true;
		if(this.conn == null){
			a = false;
			return a;
		}
		return a;
	}
	
	public QueryExecutor Query(){
		QueryExecutor exe = new QueryExecutor(this.getConn());
		this.exe = exe;
		return exe;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the conn
	 */
	private Connection getConn() {
		return conn;
	}

	/**
	 * @param conn the conn to set
	 */
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	
	
}
