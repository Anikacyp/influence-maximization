package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import thu.db.im.graphbuilding.GetAuthorList;
import thu.db.im.graphbuilding.GetCitationIDList;
import thu.db.im.graphbuilding.GetIDListSelfDefine;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu for each author,get all the authors who has influenced the
 *         author, usually, it is the authors of all cited paper
 */
public class SetAuthorRelation_In implements Runnable {

	String name;
	private SQLconnection sqLconnection;
	List<String> IDS;
	GetAuthorList getAuthorList;
	GetCitationIDList getCitationIDList;

	public SetAuthorRelation_In(int cur, int limit, String name) {
		this.name = name;
		this.sqLconnection = new connection().conn();
		IDS = new GetIDListSelfDefine(cur, limit, "dblpauthor", "paperid")
				.getIDS();
		getAuthorList = new GetAuthorList();
		getCitationIDList = new GetCitationIDList();
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		int count = 0;
		String insert = "insert into inauthor(paperid,name,inauthors)values(?,?,?)";
		try {
			PreparedStatement statement = sqLconnection.conn
					.prepareStatement(insert);
			for (String id : IDS) {
				List<String> citationIDs = getCitationIDList
						.getCitationPapers(Integer.parseInt(id));
				List<String> list = new ArrayList<>();
				List<String> subAuthor = new ArrayList<>();
				if (citationIDs.size() > 0) {
					for (String curid : citationIDs) {
						subAuthor = getAuthorList.getAuthors(Integer
								.parseInt(curid));
						for (String au : subAuthor) {
							if (list.contains(au)) {
								int index = list.indexOf(au) + 1;
								int n = Integer.parseInt(list.get(index)) + 1;
								list.set(index, n + "");
							} else {
								list.add(au);
								list.add(1+"");
							}
						}
						subAuthor = new ArrayList<>();
					}
				}
				citationIDs = new ArrayList<>();
				List<String> tmpAuthor = getAuthorList.getAuthors(Integer
						.parseInt(id));
				statement.setInt(1, Integer.parseInt(id));
				statement.setString(2, tmpAuthor.toString());
				statement.setString(3, list.toString());
				statement.addBatch();
				count++;
				if (count % 1000 == 0) {
					System.out.println(name + ":" + count);
					statement.executeBatch();
					statement.clearBatch();
					this.sqLconnection.disconnectMySQL();
					this.getAuthorList.closeConnection();
					this.getCitationIDList.closeConnection();
					this.getAuthorList = new GetAuthorList();
					this.getCitationIDList = new GetCitationIDList();
					this.sqLconnection = new connection().conn();
					statement = sqLconnection.conn.prepareStatement(insert);

				}
				tmpAuthor = new ArrayList<>();
			}
			statement.executeBatch();
			statement.clearBatch();
			statement.close();
			this.sqLconnection.disconnectMySQL();
			this.getAuthorList.closeConnection();
			this.getCitationIDList.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		int total = 1632442;
		int limit = 108829;
		long begin = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(total
				/ limit);
		for (int i = 0; i <= total / limit; i++) {
			SetAuthorRelation_In test = new SetAuthorRelation_In(i * limit,
					limit, "T" + i);
			executorService.execute(test);
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
		}
		long end = System.currentTimeMillis();
		System.out.println("total time: " + (end - begin) / 1000);
	}

}
