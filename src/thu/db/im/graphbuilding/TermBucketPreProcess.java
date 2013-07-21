package thu.db.im.graphbuilding;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * @author Yaping Chu
 * @mail Anikacyp@gmail.com
 * for each terms, find its correlated terms, which they co-occurrence a lot.
 * and for a given query term, we get its correlated terms, and build the link map.
 * to get a precise result.
 */
public class TermBucketPreProcess {
	ArrayList<String> termList = new ArrayList<>();
	private connection conn;
	GetTermIDList getIDList;
	ListOperation listOpe = new ListOperation();
	private SQLconnection sqLconnection;

	// the ratio to cluster the bucket for two different terms
	double threshold = 0.01;

	// constructor function
	public TermBucketPreProcess() {
		this.conn = new connection();
		sqLconnection = conn.conn();
		getIDList = new GetTermIDList(sqLconnection);
	}

	// 
	/**
	 * loading information from database and do statistics.
	 */
	public void termCompare() {
		String term = "select term from term5index";
		ResultSet termSet = null;
		termSet = sqLconnection.Query(term);
		initTermList(termSet);
		int count = 0;
		for (String word : termList) {
			if (count == 2)
				return;
			int currentID = termList.indexOf(word) + 1;
			for (int i = currentID; i < termList.size(); i++) {
				String tmpTerm = termList.get(i);
				String result = listOpe
						.getRatioOfTwoArray(getIDList.getIDList(word),
								getIDList.getIDList(tmpTerm));
				if (result != "") {
					String info[] = result.split(" ");
					int common = Integer.parseInt(info[0]);
					int total = Integer.parseInt(info[1]);
					double ratio = (double) common / total;
					System.out.println(word + "-" + tmpTerm + ":" + common
							+ "-" + total + ":" + ratio);
				}
			}
			count++;
		}

	}

	// initialize the termList which contains all the terms
	public void initTermList(ResultSet rs) {
		try {
			while (rs.next()) {
				String term = rs.getString("term");
				if (!termList.contains(term)) {
					termList.add(term);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		long begin = System.currentTimeMillis();
		TermBucketPreProcess termBucket = new TermBucketPreProcess();
		termBucket.termCompare();
		long end = System.currentTimeMillis();
		System.out.println("total time: " + (end - begin) / 1000);
	}
}
