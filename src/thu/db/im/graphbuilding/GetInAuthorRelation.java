package thu.db.im.graphbuilding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

public class GetInAuthorRelation {
	SQLconnection sqLconnection;
	HashMap<String, Integer> inauthorMap;
	List<String> authors;
	HashMap<String, Integer> citationauthors = new HashMap<>();
	HashMap<String, Map<List<String>, List<String>>> multiMap;
	public GetInAuthorRelation() {
		this.sqLconnection = new connection().conn();
	}

	public void GetInRelation(Integer id) {
		inauthorMap = new HashMap<>();
		authors = new ArrayList<>();
		String query = "select name,inauthors from inauthor where paperid="
				+ id;
		ResultSet set = sqLconnection.Query(query);
		String author, inauthor;
		try {
			while (set.next()) {
				author = set.getString("name");
				inauthor = set.getString("inauthors");
				authors = Arrays.asList(author
						.substring(1, author.length() - 1).split(", "));
				String[] tmp = inauthor.substring(1, inauthor.length() - 1)
						.split(", ");
				if (tmp.length % 2 == 0) {
					for (int i = 0; i < tmp.length / 2; i++) {
						inauthorMap.put(tmp[i * 2],
								Integer.parseInt(tmp[i * 2 + 1]));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void GetMultiIDInRelation(String ids) {
		multiMap=new HashMap<>();
		String query = "select * from inauthor where paperid in("
				+ ids+")";
		ResultSet rs = sqLconnection.Query(query);
		try {
			while(rs.next())
			{
				String paperid=rs.getString("paperid");
				String name=rs.getString("name");
				String authors=rs.getString("inauthors");
				System.out.println(paperid+"\n"+name+"\n"+authors);
				System.out.println("-----------------------------------");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*String author, inauthor;
		try {
			while (set.next()) {
				author = set.getString("name");
				inauthor = set.getString("inauthors");
				authors = Arrays.asList(author
						.substring(1, author.length() - 1).split(", "));
				String[] tmp = inauthor.substring(1, inauthor.length() - 1)
						.split(", ");
				if (tmp.length % 2 == 0) {
					for (int i = 0; i < tmp.length / 2; i++) {
						inauthorMap.put(tmp[i * 2],
								Integer.parseInt(tmp[i * 2 + 1]));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}
	
	public void test()
	{
		String queryString="select * from inauthor where paperid in(10,4,808,809,825)";
		ResultSet rs=sqLconnection.Query(queryString);
		
	}
	
	
	public static void main(String args[]) {
		//GetInAuthorRelation getInAuthorRelation = new GetInAuthorRelation();
		//getInAuthorRelation.GetInRelation(24001);
		//getInAuthorRelation.test();
	}

}
