package thu.db.im.mysql.oper;

import java.sql.SQLException;

public class connection {
	public SQLconnection conn()
	{
		SQLconnection sql=new SQLconnection("127.0.0.1", "root", "anika", "dblp");
		try {
			sql.connectMySql();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sql;		
	}
}
