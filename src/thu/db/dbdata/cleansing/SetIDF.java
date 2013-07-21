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
 * for each term find the times that it exist in all the papers, only the unique ones..
 * because we has to calculate the inverse 
 * 对每个唯一的term，计算它在所有文档中出现的文档次数，不重复计算。为了计算idf(t)=log10(D/w)，
 * 其中D为文档集中的文档个数,w为包含term t的文档个数。
 */

public class SetIDF implements Runnable{
	private SQLconnection sqLconnection;
	private String name;
	private ResultSet rsSet=null;
	private int cur=0,limit=0;
	public SetIDF(String name,int cur,int limit)
	{
		this.name=name;
		this.cur=cur;
		this.limit=limit;
		this.rsSet=null;
		this.sqLconnection=new connection().conn();
	}
	
	public void run()
	{
		rsSet=getResultSet(cur, limit);
		String insert="insert into idf(term,docnum,idf)values(?,?,?)";
		int no=0;
		try {
			PreparedStatement statement=sqLconnection.conn.prepareStatement(insert);
			while(rsSet.next())
			{
				String term=rsSet.getString("term");
				int count=gettermCount(term);
				double idf=Math.log10((double)475748/count);
				statement.setString(1, term);
				statement.setInt(2, count);
				statement.setDouble(3, idf);
				statement.addBatch();
				no++;
				if(no%1000==0)
				{
					statement.executeBatch();
					statement.clearBatch();
					System.out.println(name+":"+no);
				}
			}
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int gettermCount(String term)
	{
		String query="select count(distinct indexid) from terminvertedindex where term=\""+term+"\"";
		int rsSet=0;
		rsSet=sqLconnection.Count(query);
		return rsSet;
	}
	public ResultSet getResultSet(int cur,int limit)
	{
		String query="select distinct term from terminvertedindex order by term asc limit "+cur+","+limit;
		rsSet=sqLconnection.Query(query);
		return rsSet;
	}
	
	
	public static void main(String args[])
	{
		long begin=System.currentTimeMillis();
		int total=219521;
		int limit=20000;
		ExecutorService executorService = Executors.newFixedThreadPool(total/limit);
		for(int i=0;i<=total/limit;i++)
		{
			String name="T"+i;
			executorService.execute(new SetIDF(name,i*limit,limit));
		}
		executorService.shutdown();
		while(!executorService.isTerminated()){}
		long end=System.currentTimeMillis();
		System.out.println("total time: "+(end-begin)/1000);
	}	
}
