package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;


/**
 * 
 * @author Administrator
 * for each word in each paper, calculate its term frequency.
 * and store it into the table tf,which its schema is term, paperid, count, totalcount and tf value.
 */

public class SetTF implements Runnable {
	private SQLconnection sqLconnection;
	private String name;
	private ResultSet PaperIDList = null;
	private int cur = 0, limit = 0;

	// constructor function,initialize the sqlconnection, the thread name
	public SetTF(String name, int cur, int limit) {
		this.sqLconnection = new connection().conn();
		this.name = name;
		this.cur = cur;
		this.limit = limit;
	}

	// get term count of a single paper for a given term and paperid
	public Map<String, Integer> getTerms(SQLconnection sqLconnection,int paperID) {
		String query = "select term from terminvertedindex where paperID="+ paperID;
		ResultSet terms=sqLconnection.Query(query);
		Map<String, Integer> termcountMap=new HashMap<>();
		try {
			while(terms.next())
			{
				String term=terms.getString("term");
				if(termcountMap.containsKey(term))
					termcountMap.put(term, termcountMap.get(term)+1);
				else {
					termcountMap.put(term, 1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return termcountMap;
	}

	// get total term count for a given paperid
	public int getTotalTermCount(SQLconnection sqLconnection, int paperID) {
		String query = "select totalcount from papertermcount where paperID="
				+ paperID;
		int count = 0;
		ResultSet rs = sqLconnection.Query(query);
		try {
			rs.next();
			count = rs.getInt("totalcount");
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	// get the terms for the current thread
	public ResultSet getResultSet() {
		String query = "select paperid from papertermcount limit " + cur + "," + limit;
		return sqLconnection.Query(query);
	}

	// the run function, which do the main job.
	@Override
	public void run() {
		PaperIDList = getResultSet();
		String insert = "insert into tf(term,paperid,count,totalcount,tf) values(?,?,?,?,?);";
		int record = 0;
		try {
			SQLconnection connection = new connection().conn();
			PreparedStatement statement = connection.conn
					.prepareStatement(insert);
			int totalcount=0;
			while (PaperIDList.next()) {
				Map<String, Integer> termcountMap = new HashMap<>();
				int id = PaperIDList.getInt("paperid");
				termcountMap =getTerms(connection, id);
				totalcount=getTotalTermCount(connection, id);
				for(String term:termcountMap.keySet())
				{
					int count=termcountMap.get(term);
					float tf=(float)count/totalcount;
					statement.setString(1, term);
					statement.setInt(2, id);
					statement.setInt(3, count);
					statement.setInt(4, totalcount);
					statement.setDouble(5, tf);
					statement.addBatch();
					record++;
					if (record % 1000 == 0) {
						System.out.println(name + "-record number: " + record);
						statement.executeBatch();
						statement.clearBatch();
						connection.disconnectMySQL();
						connection = new connection().conn();
						statement.close();
						statement = connection.conn.prepareStatement(insert);
					}
				}
			}
			statement.executeBatch();
			statement.clearBatch();
			statement.close();
			PaperIDList.close();
			connection.disconnectMySQL();
			this.sqLconnection.disconnectMySQL();
			connection.disconnectMySQL();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		long begin = System.currentTimeMillis();
		int total = 475748;
		int limit = 31716;
		ExecutorService executorService = Executors.newFixedThreadPool(total
				/ limit);
		for (int i = 0; i <= total / limit; i++) {
			String name = "T" + i;
			executorService.execute(new SetTF(name, i * limit, limit));
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
		}
		long end = System.currentTimeMillis();
		System.out.println("total time: " + (end - begin) / 1000);
	}
}
