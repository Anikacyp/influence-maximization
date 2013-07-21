package thu.db.im.dblp.cleansing;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

public class BaseDBLPInfos {

	static connection connection = null;
	SQLconnection sql;
	public BaseDBLPInfos()
	{
		if(connection == null){
			connection=new connection();
		}
		sql=connection.conn();
	}
	public void write2DB(Paper paper)
	{		
		PreparedStatement stmt = null;
		//dblp-basic
		String query="insert into dblpbase(IndexID,Title,year,publicationVenue,Abstract) values(?,?,?,?,?)";
		try {
			stmt=sql.conn.prepareStatement(query);
			stmt.setLong(1, paper.getIndex());
			stmt.setString(2, paper.getTitle());
			stmt.setInt(3, paper.getYear());
			stmt.setString(4, paper.getPublication());
			stmt.setString(5, paper.getAbstract());
			stmt.execute();
			stmt.close();
			
			//dblpauthor
			query="insert into dblpauthor(IndexID,Author) values(?,?)";
			for(String author:paper.getAuthor())
			{
				stmt=sql.conn.prepareStatement(query);
				//System.out.print(author+"--");
				stmt.setLong(1, paper.getIndex());
				stmt.setString(2, author);
				stmt.execute();
				stmt.close();
			}
			
			//dblpcitation
			query="insert into dblpcitation(IndexID,citationID) values(?,?)";
			for(long cit:paper.getCitation())
			{
				stmt=sql.conn.prepareStatement(query);
				stmt.setLong(1, paper.getIndex());
				stmt.setLong(2, cit);
				stmt.execute();
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
