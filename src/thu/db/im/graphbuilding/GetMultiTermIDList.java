package thu.db.im.graphbuilding;

import java.util.ArrayList;
import java.util.List;
import thu.db.im.mysql.oper.SQLconnection;
import thu.db.im.mysql.oper.connection;

/**
 * 
 * @author Yaping Chu
 * for a given string, find its contained paperid list separately. and find the common ids.
 */
public class GetMultiTermIDList {
	private GetTermIDList getTermIDList;
	private ListOperation listOperation;
	private SQLconnection sqLconnection;
	private connection conn;

	public GetMultiTermIDList() {
		this.conn = new connection();
		this.sqLconnection = conn.conn();
		this.listOperation = new ListOperation();
		this.getTermIDList = new GetTermIDList(sqLconnection);
	}

	public List<String> getCommonIDS(List<String> terms) {
		List<String> IDList = new ArrayList<>(getTermIDList.getIDList(terms
				.get(0)));
		List<String> tmpList = new ArrayList<>();
		if(terms.size()>1)
		{
			for (String term : terms) {
				tmpList = getTermIDList.getIDList(term);
				IDList = listOperation.getIntersectionList(IDList, tmpList);
			}
		}
		this.getTermIDList.closeConnection();
		return IDList;
	}
}
