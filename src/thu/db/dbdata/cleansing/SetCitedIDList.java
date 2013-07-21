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
 * @author Administrator for each cited paper, find all the papers that cite the
 *  paper.
 */
public class SetCitedIDList implements Runnable {

	private SQLconnection sqLconnection;
	private int cur = 0, limit = 0;

	public SetCitedIDList(int cur, int limit) {
		this.cur = cur;
		this.limit = limit;
		this.sqLconnection = new connection().conn();
	}

	public ResultSet GetCitedIDS() {
		ResultSet set = null;
		String query = "select distinct citationid from dblpcitation order by citationid asc limit "
				+ cur + "," + limit;
		set = sqLconnection.Query(query);
		return set;
	}

	public String getCitedList(int id) {
		String query = "select paperid from dblpcitation where citationid="
				+ id + " order by paperid asc";
		ResultSet rs = sqLconnection.Query(query);
		String list = "";
		try {
			while (rs.next()) {
				int citedid = rs.getInt("paperid");
				list += citedid + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void run() {
		ResultSet ids = GetCitedIDS();
		String insert = "insert into citedList(paperid,citedlist) values(?,?)";
		int num = 0;
		try {
			PreparedStatement statement = sqLconnection.conn
					.prepareStatement(insert);
			while (ids.next()) {
				int id = ids.getInt("citationid");
				if(!isIDexist(id))
				{
					String list = getCitedList(id);
					statement.setInt(1, id);
					statement.setString(2, list);
					statement.addBatch();
					num++;
					if (num % 1000 == 0) {
						statement.executeBatch();
						statement.clearBatch();
						System.out.println("current num: " + num);
					}
				}else
					continue;
			}
			statement.executeBatch();
			statement.clearBatch();
			statement.close();
			ids.close();
			this.sqLconnection.disconnectMySQL();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isIDexist(int id)
	{
		String select="select count(*)from citedlist where paperid="+id;
		int count=0;
		count=sqLconnection.Count(select);
		if(count!=0)
			return true;
		else
			return false;
	}

	public static void main(String args[]) {
		long begin = System.currentTimeMillis();
		int total = 338614;
		int limit = 22574;
		ExecutorService executorService = Executors.newFixedThreadPool(total
				/ limit);
		for (int i = 0; i <= total / limit; i++) {
			executorService.execute(new SetCitedIDList(i * limit, limit));
		}
		executorService.shutdown();
		while(!executorService.isTerminated()) {}
		long end = System.currentTimeMillis();
		System.out.println("total time: " + (end - begin) / 1000 + " seconds.");
	}
}
