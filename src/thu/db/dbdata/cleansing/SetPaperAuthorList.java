package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu
 * the original sql file is dblpauthor.sql.
 * from this sql file, for each paperid, find its authors, and store it as a string,
 * and separated by the comma(",")
 *
 */
public class SetPaperAuthorList implements Runnable{
	private SQLconnection sqLconnection;
	int cur=0,limit=0;
	String name;
	public SetPaperAuthorList(int cur,int limit,String name)
	{
		this.name=name;
		this.cur=cur;
		this.limit=limit;
		this.sqLconnection=new connection().conn();
	}
	public ResultSet getPaperIDSet()
	{
		String query="select distinct paperid from dblpauthor limit "+cur+","+limit;
		ResultSet PaperIDList=null;
		PaperIDList=sqLconnection.Query(query);
		return PaperIDList;
	}
	
	public String getAuthor(SQLconnection connection,int id)
	{
		String query="select author from dblpauthor where paperid="+id;
		ResultSet authorSet=connection.Query(query);
		String author="";
		try {
			while(authorSet.next())
			{
				author+=authorSet.getString("author")+",";
			}
			authorSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return author;
	}
	
	@Override
	public void run() {
		ResultSet paperIDSet=getPaperIDSet();
		String insert="insert into paperauthorList(paperid,authorlist) values(?,?);";
		PreparedStatement statement=null;
		int count=0;
		try {
			SQLconnection connection=new connection().conn();
			statement=connection.conn.prepareStatement(insert);
			while(paperIDSet.next())
			{
				int id=paperIDSet.getInt("paperid");
				String author=getAuthor(connection,id);
				statement.setInt(1, id);
				statement.setString(2, author);
				statement.addBatch();
				count++;
				if(count%1000==0)
				{
					statement.executeBatch();
					statement.clearBatch();
					System.out.println(name+"-current count: "+count);
					connection.disconnectMySQL();
					connection=new connection().conn();
					statement.close();
					statement=connection.conn.prepareStatement(insert);
				}
			}
			statement.executeBatch();
			statement.clearBatch();
			statement.close();
			connection.disconnectMySQL();
			paperIDSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		long begin=System.currentTimeMillis();
		int total=1632442;
		int limit=108829;
		ExecutorService executorService=Executors.newFixedThreadPool(total/limit);
		String name="";
		for(int i=0;i<=total/limit;i++)
		{
			name="T"+i;
			executorService.execute(new SetPaperAuthorList(i*limit, limit,name));
		}
		executorService.shutdown();
		while(!executorService.isTerminated()){}
		long end=System.currentTimeMillis();
		System.out.println("total time: "+(end-begin)/1000+" seconds.");
	}
	
}
