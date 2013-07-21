package thu.db.im.graphbuilding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu for a given id, find its citation list if it exist.
 */
public class GetCitationIDList {
	private SQLconnection sqLconnection;

	public GetCitationIDList() {
		this.sqLconnection = new connection().conn();
	}

	public List<String> getCitationPapers(int id) {
		List<String> list = new ArrayList<>();
		String query = "select citationids from citationlist where paperid="
				+ id;
		ResultSet rsSet = null;
		rsSet = sqLconnection.Query(query);
		try {
			while (rsSet.next()) {
				String citation = rsSet.getString("citationids");
				list = Arrays.asList(citation.split(","));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public void closeConnection()
	{
		this.sqLconnection.disconnectMySQL();
	}
}
