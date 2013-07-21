package thu.db.dbdata.cleansing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;

import thu.db.im.basefun.Segmenter;
import thu.db.im.basefun.WordsFilter;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu
 * segment the word(include the title of the paper and the abstract of the paper), 
 * record its paperid, position for each word in the paper. and write it to the database.
 *
 */
public class SetInvertedIndexOfDBLP {

	static public connection conn;
	public SQLconnection sqLconnection;

	@SuppressWarnings("static-access")
	public SetInvertedIndexOfDBLP() {
		this.conn = new connection();
		sqLconnection = conn.conn();
	}

	public void createIndex() {
		Segmenter seg = new Segmenter();
		WordsFilter filter = new WordsFilter();
		List<CoreLabel> words;
		ResultSet resultSet = null;
		String query = "select distinct indexid from dblpcitation union select distinct citationid from dblpcitation;";
		ResultSet rs = sqLconnection.Query(query);
		try {
			String insert = "insert into TermInfos(term,indexid,pos) values(?,?,?)";
			int count = 0;
			PreparedStatement statement = sqLconnection.conn
					.prepareStatement(insert);
			while (rs.next()) {
				int index = rs.getInt("indexid");
				query = "select title,abstract from dblpbase where indexid="
						+ index;
				resultSet = sqLconnection.Query(query);
				resultSet.next();
				String content = resultSet.getString("title");
				content += " " + resultSet.getString("abstract");
				words = seg.TokenAPI(content);
				for (int i = 0; i < words.size(); i++) {
					String word = words.get(i).toString().toLowerCase();
					if (!filter.result(word).equals("")) {
						statement.setString(1, word);
						statement.setInt(2, index);
						statement.setInt(3, i);
						statement.addBatch();
					}
				}
				if (count % 10000 == 0) {
					System.out.println("current number: " + count);
					statement.executeBatch();
					statement.clearBatch();
					resultSet = null;
					words.clear();
					query = null;
				}
				count++;
			}
			statement.executeBatch();
			sqLconnection.disconnectMySQL();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		new SetInvertedIndexOfDBLP().createIndex();
	}
}
