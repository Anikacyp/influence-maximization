package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu
 * for each paper, statistic its total term count
 * and write it to the database, the schema is paperid,termcount.
 *
 */
public class SetPaperTermCount {

	private SQLconnection sqLconnection;
	private ResultSet rs = null;

	public SetPaperTermCount(SQLconnection sqLconnection) {
		this.sqLconnection = sqLconnection;
	}

	public int getCount(int id) {
		String query = "select count(*)from terminvertedindex where paperid="
				+ id;
		int count = sqLconnection.Count(query);
		return count;
	}

	public ResultSet getPaperID() {
		String query = "select distinct paperid from terminvertedindex order by paperid asc";
		ResultSet rsSet = null;
		rsSet = sqLconnection.Query(query);
		return rsSet;
	}

	public void doLoop() {
		String insert="insert into paperTermcount(paperid,count)values(?,?);";
		int num=0;
		try {
			PreparedStatement statement=sqLconnection.conn.prepareStatement(insert);
			this.rs=getPaperID();
			while (rs.next()) {
				int id = rs.getInt("paperid");
				int termcount = getCount(id);
				statement.setInt(1, id);
				statement.setInt(2, termcount);
				statement.addBatch();
				num++;
				if(num%1000==0)
				{
					statement.executeBatch();
					statement.clearBatch();
					System.out.println(num);
				}
				//System.out.println(id + "-" + termcount);
			}
			statement.executeBatch();
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String args[]) {
		long begin = System.currentTimeMillis();
		SQLconnection sqLconnection = new connection().conn();
		SetPaperTermCount setPaperTermCount = new SetPaperTermCount(
				sqLconnection);
		setPaperTermCount.doLoop();
		long end = System.currentTimeMillis();
		System.out.println("total time: " + (end - begin) / 1000);
	}
}
