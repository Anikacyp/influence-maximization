package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import thu.db.im.graphbuilding.GetAuthorList;
import thu.db.im.graphbuilding.GetCitedIDList;
import thu.db.im.graphbuilding.GetIDListSelfDefine;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu
 * 
 *         for each author,get all the authors who has been influenced by the
 *         author, usually, it is the authors of all cited paper
 */
public class SetAuthoRelation_Out implements Runnable {

	String name;
	private SQLconnection sqLconnection;
	List<String> IDS;
	GetAuthorList getAuthorList;
	GetCitedIDList getCitedIDList;

	public SetAuthoRelation_Out(int cur, int limit, String name) {
		this.name = name;
		this.sqLconnection = new connection().conn();
		IDS = new GetIDListSelfDefine(cur, limit, "citedlist", "paperid").getIDS();
		getAuthorList = new GetAuthorList();
		getCitedIDList = new GetCitedIDList();
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		int count = 0;
		String insert = "insert into outauthor(paperid,name,outauthors)values(?,?,?)";
		try {
			PreparedStatement statement = sqLconnection.conn
					.prepareStatement(insert);
			for (String id : IDS) {
				List<String> citationIDs = getCitedIDList
						.getCitedPapers(Integer.parseInt(id));
				HashMap<String, Integer> cachedAuthors = new HashMap<>();
				List<String> subAuthor = new ArrayList<>();
				for (String curid : citationIDs) {
					subAuthor = getAuthorList.getAuthors(Integer
							.parseInt(curid));
					for (String au : subAuthor) {
						if (cachedAuthors.containsKey(au)) {
							cachedAuthors.put(au, cachedAuthors.get(au) + 1);
						} else {
							cachedAuthors.put(au, 1);
						}
					}
					subAuthor = new ArrayList<>();
				}
				citationIDs = new ArrayList<>();
				String authors = "";
				for (String au : cachedAuthors.keySet()) {
					authors += au + "#" + cachedAuthors.get(au) + ",";
				}
				cachedAuthors = new HashMap<>();
				List<String> tmpAuthor = getAuthorList.getAuthors(Integer
						.parseInt(id));
				for (String tmp : tmpAuthor) {
					statement.setInt(1, Integer.parseInt(id));
					statement.setString(2, tmp);
					statement.setString(3, authors);
					statement.addBatch();
					count++;
					if (count % 1000 == 0) {
						System.out.println(name + ":" + count);
						statement.executeBatch();
						statement.clearBatch();
						this.sqLconnection.disconnectMySQL();
						this.getAuthorList.closeConnection();
						this.getCitedIDList.closeConnection();
						this.getAuthorList = new GetAuthorList();
						this.getCitedIDList = new GetCitedIDList();
						this.sqLconnection = new connection().conn();
						statement = sqLconnection.conn.prepareStatement(insert);
					}
				}
				tmpAuthor = new ArrayList<>();
			}
			statement.executeBatch();
			statement.clearBatch();
			statement.close();
			this.sqLconnection.disconnectMySQL();
			this.getAuthorList.closeConnection();
			this.getCitedIDList.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		int total = 338614;
		int limit = 22574;
		long begin = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(total
				/ limit);
		for (int i = 0; i <= total / limit; i++) {
			SetAuthoRelation_Out test = new SetAuthoRelation_Out(i * limit,
					limit, "T" + i);
			executorService.execute(test);
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {}
		long end = System.currentTimeMillis();
		System.out.println("total time: " + (end - begin) / 1000);
	}
}
