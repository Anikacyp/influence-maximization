package thu.db.im.graphbuilding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

public class GetOutAuthorRelation {
	SQLconnection sqLconnection;
	HashMap<String, Map<String, Integer>> outauthorMap = new HashMap<>();
	String query = "select name,outauthors from outauthor where paperid=";

	public GetOutAuthorRelation() {
		sqLconnection = new connection().conn();
	}

	public HashMap<String, Map<String, Integer>> GetOutRelation(int id) {
		ResultSet set = sqLconnection.Query(query + id);
		String author, subauthor;
		int count;
		String[] info, outauthors;
		boolean flag = false;
		HashMap<String, Integer> authorMap = new HashMap<>();
		try {
			while (set.next()) {
				author = set.getString("name");
				outauthors = set.getString("outauthors").split(",");
				if (!flag) {
					for (String obj : outauthors) {
						info = obj.split("#");
						subauthor = info[0];
						count = Integer.parseInt(info[1]);
						if (!authorMap.containsKey(subauthor)) {
							authorMap.put(subauthor, count);
						}
					}
					flag=true;
				}
				if(!outauthorMap.containsKey(author))
				{
					outauthorMap.put(author, authorMap);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return outauthorMap;
	}
	
	public static void main(String args[]) {
		long begin=System.currentTimeMillis();
		GetOutAuthorRelation getOutAuthorRelation = new GetOutAuthorRelation();
		HashMap<String, Map<String, Integer>> map = getOutAuthorRelation
				.GetOutRelation(4);
		//86603
		for (String key : map.keySet()) {
			Map<String, Integer> tmpMap = map.get(key);
			System.out.print(key + "\t");
			for (String obj : tmpMap.keySet()) {
				System.out.print(obj + "-" + tmpMap.get(obj) + "\t");
			}
			System.out.println();
		}
		long end=System.currentTimeMillis();
		System.out.println("total time: "+(end-begin)/1000);
	}
}
