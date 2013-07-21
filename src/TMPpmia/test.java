package TMPpmia;

import java.sql.ResultSet;
import java.sql.SQLException;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

public class test {

	public static void main(String args[])
	{
		String query="select name,inauthors from inauthor where paperid=4";
		SQLconnection sqLconnection=new connection().conn();
		ResultSet rsSet=sqLconnection.Query(query);
		String authors;
		try {
			while(rsSet.next()){
				authors = rsSet.getString("inauthors");
				System.out.println(authors);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
