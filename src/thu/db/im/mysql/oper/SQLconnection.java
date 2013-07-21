package thu.db.im.mysql.oper;

import java.sql.*;

public class SQLconnection
{
	public String server,userName,password,dbName;
	public Connection conn;
	public SQLconnection(String server,String username,String password,String dbname)
	{
		this.server=server;
		this.userName=username;
		this.password=password;
		this.dbName=dbname;
	}
	
	public void connectMySql() throws InstantiationException,SQLException,ClassNotFoundException,IllegalAccessException{
		Class.forName("com.mysql.jdbc.Driver");
		conn=DriverManager.getConnection("jdbc:mysql://"+server+"/"+
		dbName+"?useUnicode=true&characterEncoding=utf-8&jdbcCompliantTruncation=false",
		userName,password);
		if(!conn.isClosed())
			System.out.println("DB connected!");
		else
			System.out.println("Failed to connect the database!");
		useDB(dbName);
	}
	//断开数据库连接
	public void disconnectMySQL (){
		try {
			conn.close();
			System.out.println("DB Closed!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//conn = null;
	}
	//使用数据库
	public void useDB(String dbName)
	{
		String stat="USE "+dbName;
		this.execute(stat);
	}
	
	//执行操作-
	public void execute(String statement)
	{
		try{
		Statement stat;
		stat=this.conn.createStatement();
		stat.execute(statement);
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//执行更新操作
	public int Update (String statement) throws SQLException {
		Statement stat = this.conn.createStatement();
		return stat.executeUpdate(statement);
	}
	
	//执行查询操作
	public ResultSet Query (String statement){
		ResultSet rs = null;
		try {
		Statement stat = this.conn.createStatement();	
		rs = stat.executeQuery(statement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	//执行获取count操作
	public int Count(String statment){
		ResultSet rs=null;
		Statement stat;
		try {
			stat = this.conn.createStatement();
			rs=stat.executeQuery(statment);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}		
	}
}