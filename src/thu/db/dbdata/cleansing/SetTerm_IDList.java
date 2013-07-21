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
 * this get from the terminvertedindex table.
 * for each unique term, find which paper contains it, and put the paperid as a string
 * and separated id by the comma (",").
 * since it is fast to get the result.
 *
 */
public class SetTerm_IDList implements Runnable{

	private ResultSet rsSet;
	private connection conn;
	private SQLconnection sqLconnection;
	private String name;
	public SetTerm_IDList()
	{
		
	}
	public SetTerm_IDList(String name,ResultSet resultSet)
	{
		this.rsSet=resultSet;
		this.conn=new connection();
		this.sqLconnection=this.conn.conn();
		this.name=name;
	}
	@Override
	public void run() {
		String insert="insert into TermIDList (term,IDList) values(?,?)";
		String IDList="";
		int count=0;
		try {
			PreparedStatement statement = sqLconnection.conn
					.prepareStatement(insert);
			while(rsSet.next())
			{
				String term=rsSet.getString("term");
				ResultSet rs=null;
				String idquery="select distinct indexid from terminvertedindex where term=\""+term+"\" order by indexid asc";
				rs=sqLconnection.Query(idquery);
				while(rs.next())
				{
					int id=rs.getInt("indexid");
					IDList+=id+",";
				}
				statement.setString(1, term);
				statement.setString(2, IDList);
				statement.addBatch();
				IDList="";
				count++;
				if(count%100==0)
				{
					System.out.println(name+"--"+count);
					statement.executeBatch();
					statement.clearBatch();
					IDList="";
				}
			}
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		//total count is 41411,each time fetch 2000 records
		int total=41411;
		int limit=2000;
		connection conn;
		SQLconnection sqLconnection;
		conn=new connection();
		sqLconnection=conn.conn();
		ExecutorService executorService = Executors.newFixedThreadPool(total/limit);
		long begin=System.currentTimeMillis();
		for(int i=0;i<=total/limit;i++)
		{
			String query="select term from term5index limit "+(i*limit)+","+limit;
			ResultSet rsSet=null;
			String name="T"+i;
			rsSet=sqLconnection.Query(query);
			executorService.execute(new SetTerm_IDList(name,rsSet));
		}

		executorService.shutdown();
		while(!executorService.isTerminated())
		{}
		long end=System.currentTimeMillis();
		System.out.println("total time of multi thread: "+(end-begin)/1000);
		
		/*String query="select term from term5index limit 41000,1000";
		ResultSet rsSet=null;
		rsSet=sqLconnection.Query(query);
		ThreadOfTermIDList r1=new ThreadOfTermIDList("Last",rsSet);
		Thread T1=new Thread(r1);
		T1.start();*/
	}

}
