package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu
 * for a given paper id, find its cited paperid list, and store it to the database.
 *
 */
public class SetID_CitationList {

	private SQLconnection sqLconnection;

	public SetID_CitationList() {
		this.sqLconnection = new connection().conn();
	}

	public void insertID_CitationList() {
		String insert = "insert into idcitationlist(indexid,citationlist) values(?,?)";
		String query = "select distinct indexid from dblpcitation order by indexid asc";
		ResultSet rsSet = null;
		rsSet = sqLconnection.Query(query);
		int count = 0;
		try {
			PreparedStatement statement = sqLconnection.conn
					.prepareStatement(insert);
			while (rsSet.next()) {
				int id = rsSet.getInt("indexid");
				String citationlist = getCitedIDList(id);
				if (citationlist != "") {
					statement.setInt(1, id);
					statement.setString(2, citationlist);
					statement.addBatch();
					//System.out.println(id + ":" + citationlist);
					if (count % 1000 == 0) {
						System.out.println("current number: "+count);
						statement.executeBatch();
						statement.clearBatch();
					}
					count++;
				}
			}
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getCitedIDList(int queryid) {
		String idList = "";
		String query = "select distinct citationid from dblpcitation where indexid="
				+ queryid + " order by citationid asc";
		ResultSet rsSet = null;
		rsSet = sqLconnection.Query(query);
		try {
			while (rsSet.next()) {
				int id = rsSet.getInt("citationid");
				idList += id + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return idList;
	}

	public static void main(String args[]) {
		long begin=System.currentTimeMillis();
		SetID_CitationList idList = new SetID_CitationList();
		idList.insertID_CitationList();
		long end=System.currentTimeMillis();
		System.out.println("total time: "+(end-begin)/1000);
	}
}
