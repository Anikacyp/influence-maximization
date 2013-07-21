package thu.db.im.graphbuilding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import thu.db.im.mysql.oper.SQLconnection;

/**
 * 
 * @author Yaping Chu
 * for a given term, find its paperid list which contains the term.
 */
public class GetTermIDList {
	SQLconnection sqLconnection;

	public GetTermIDList(SQLconnection sqLconnection) {
		this.sqLconnection = sqLconnection;
	}

	// get the IDList according the the given term
	public List<String> getIDList(String term) {
		String query = "select IDList from termidlist where term=\"" + term
				+ "\"";
		ResultSet rsSet = sqLconnection.Query(query);
		List<String> iDList = new ArrayList<>();
		try {
			while (rsSet.next()) {
				String id = rsSet.getString("IDList");
				iDList = Arrays.asList(id.split(","));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return iDList;
	}
	
	public void closeConnection()
	{
		this.sqLconnection.disconnectMySQL();
	}
}
