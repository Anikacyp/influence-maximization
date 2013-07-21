package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Administrator
 * find the terms that paper contains it bigger than 5 times
 * since a lot of terms exist only a few times, that don't make any sense.
 */
public class SetTermBiggerThan5Times {
	private connection conn;
	private SQLconnection sqLconnection;
	public SetTermBiggerThan5Times()
	{
		this.conn = new connection();
		this.sqLconnection = conn.conn();
	}

	public void getTermPaperIDBigThan5() {
		String sql = "select distinct term from terminvertedindex order by term asc";
		String insert = "insert into term5index (term) values(?)";
		ResultSet rs = sqLconnection.Query(sql);
		try {
			PreparedStatement statement = sqLconnection.conn
					.prepareStatement(insert);
			int count = 0;
			while (rs.next()) {
				String term = rs.getString("term");
				sql = "select count(distinct indexid) from terminvertedindex where term=\""
						+ term + "\"";
				if (sqLconnection.Count(sql) > 5) {
					statement.setString(1, term);
					statement.addBatch();
				}
				if (count % 1000== 0) {
					System.out.println(count);
					statement.executeBatch();
					statement.clearBatch();
				}
				count++;
			}
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		new SetTermBiggerThan5Times().getTermPaperIDBigThan5();
	}
}
