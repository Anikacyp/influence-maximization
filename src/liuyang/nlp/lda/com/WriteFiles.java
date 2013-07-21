package liuyang.nlp.lda.com;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import liuyang.nlp.lda.conf.PathConfig;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * @mail Anikacyp@gmail.com
 * @author Yaping Chu
 * 
 */
public class WriteFiles {
	public connection conn;
	public SQLconnection sqLconnection;
	BufferedWriter writer;

	public WriteFiles() {
		this.conn = new connection();
		sqLconnection = conn.conn();
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(PathConfig.dblpPath, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public WriteFiles(String file) {
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void WriteDBLPToFile() {
		ResultSet resultSet = null;
		String query = "select distinct indexid from dblpcitation union select distinct citationid from dblpcitation;";
		ResultSet rs = sqLconnection.Query(query);
		try {
			int n = 0;
			String content = "";
			while (rs.next()) {
				n++;
				int index = rs.getInt("indexid");
				query = "select title,abstract from dblpbase where indexid="
						+ index;
				resultSet = sqLconnection.Query(query);
				resultSet.next();
				content += resultSet.getString("title") + " "
						+ resultSet.getString("abstract") + "\r\n";
				if (n == 10) {
					writeStrings(content);
					// System.out.println(content);
					content = "";
					n = 0;
					return;
				}
			}
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void writeStrings(String content) {
		try {
			writer.flush();
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeWriter() {
		try {
			this.writer.flush();
			this.writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		// new WriteFiles().WriteDBLPToFile();
	}
}
