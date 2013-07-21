package thu.db.im.graphbuilding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

public class GetAuthorList {
	private SQLconnection sqLconnection;
	private List<String> authorList;
	public GetAuthorList()
	{
		this.sqLconnection=new connection().conn();
		authorList=new ArrayList<>();
	}
	public List<String> getAuthors(int id){
		String query="select authors from authorlist where paperid="+id;
		ResultSet rs=sqLconnection.Query(query);
		try {
			rs.next();
			String []authors=rs.getString("authors").split(",");
			authorList=Arrays.asList(authors);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return authorList;
	}
	
	public void closeConnection()
	{
		this.sqLconnection.disconnectMySQL();
	}
	
	/*public static void main(String args[])
	{
		GetAuthorList getAuthorList=new GetAuthorList();
		List<String>tmp=getAuthorList.getAuthors(343);
		System.out.println(tmp.size()+"\t"+tmp.toString());
		GetInAuthorRelation getInAuthorRelation=new GetInAuthorRelation();
		getInAuthorRelation.GetInRelation(343);
		List<String>authorList=getInAuthorRelation.authors;
		System.out.println(authorList.size()+"\t"+authorList.toString());
	}*/
}
