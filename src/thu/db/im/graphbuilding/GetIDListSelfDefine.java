package thu.db.im.graphbuilding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

public class GetIDListSelfDefine {

	SQLconnection sqLconnection;
	int cur,limit;
	String tablename;
	String attr;
	public GetIDListSelfDefine(int cur,int limit,String name,String attr)
	{
		this.tablename=name;
		this.attr=attr;
		this.sqLconnection=new connection().conn();
		this.cur=cur;
		this.limit=limit;
	}
	
	public List<String> getIDS()
	{
		List<String> citationIDS=new ArrayList<>();
		String query="select distinct "+attr+" from "+tablename+" order by paperid asc limit "+cur+","+limit;
		ResultSet rsSet=sqLconnection.Query(query);
		try {
			while(rsSet.next())
			{
				String id=rsSet.getString(attr);
				citationIDS.add(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.sqLconnection.disconnectMySQL();
		return citationIDS;
	}
	
	
	/*public static void main(String args[])
	{
		long begin=System.currentTimeMillis();
		System.out.println(new GetIDList(10,20).getIDS().size());
		long end=System.currentTimeMillis();
		System.out.println("Total time: "+(end-begin)/1000);
	}*/
}
