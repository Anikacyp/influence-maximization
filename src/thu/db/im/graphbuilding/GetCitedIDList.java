package thu.db.im.graphbuilding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

public class GetCitedIDList {
	private SQLconnection sqLconnection;

	public GetCitedIDList() {
		this.sqLconnection = new connection().conn();
	}

	public List<String> getCitedPapers(int id) {
		List<String> list = new ArrayList<>();
		String query = "select citedids from citedlist where paperid="
				+ id;
		ResultSet rsSet = null;
		rsSet = sqLconnection.Query(query);
		try {
			while (rsSet.next()) {
				String citedlist = rsSet.getString("citedids");
				list = Arrays.asList(citedlist.split(","));
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
