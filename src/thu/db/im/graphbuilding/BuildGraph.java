package thu.db.im.graphbuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildGraph {

	GetInAuthorRelation getInAuthorRelation;
	GetCitationIDList getCitationIDList;
	HashMap<String, HashMap<String, Integer>>InAuthorMap = new HashMap<>();
	List<String>papers;
	List<String>totalAuthor=new ArrayList<>();

	String cachedID="";
	public BuildGraph() {
		this.getCitationIDList = new GetCitationIDList();
		getInAuthorRelation=new GetInAuthorRelation();
		papers = new ArrayList<>();
		
	}

	/*public void buildingGraph(int layer, List<String> list, boolean flag,
			HashMap<String, List<String>> cachedauthor) {
		if (layer > 1) {
			return;
		} else {
			HashMap<String, List<String>> cachedAuthorsTmp = new HashMap<>();
			List<String> authortemp = new ArrayList<>();
			List<String> citationauthors = new ArrayList<>();
			List<String> citationids = new ArrayList<>();
			for (String obj : list) {
				if (!papers.contains(obj)) {
					papers.add(obj);
					if (flag) {
						authortemp = cachedauthor.get(obj);
					} else {
						authortemp = getAuthorList.getAuthors(Integer
								.parseInt(obj));
					}
					citationids = getCitationIDList.getCitationPapers(Integer
							.parseInt(obj));
					if (citationids.size() > 0) {
						for (String id : citationids) {
							citationauthors = getAuthorList
									.getAuthors(Integer.parseInt(id));
							cachedAuthorsTmp.put(id, citationauthors);
							for (String au : authortemp) {
								List<String> tmp = authorMap.get(au);
								if (tmp == null)
									tmp = new ArrayList<>();
								for (String citationau : citationauthors) {
									if (!tmp.contains(citationau)) {
										tmp.add(citationau);
									}
								}
								authorMap.put(au, tmp);
							}
						}
						buildingGraph(layer + 1, citationids, true,
								cachedAuthorsTmp);
					}
				}
			}
		}
	}*/
	
	public void UnionMap(HashMap<String, HashMap<String, Integer>> mastermap,List<String>tmpAuthors,HashMap<String, Integer>tmpInauthorMap)
	{
		for(String key:tmpAuthors)
		{
			if(mastermap.containsKey(key))
			{
				HashMap<String, Integer>tmpHashMap=mastermap.get(key);
				for(String obj:tmpInauthorMap.keySet())
				{
					if(tmpHashMap.containsKey(obj))
					{
						
						tmpHashMap.put(obj, tmpHashMap.get(obj)+tmpInauthorMap.get(obj));
					}else {
						tmpHashMap.put(obj, tmpInauthorMap.get(obj));
					}
				}
				mastermap.put(key, tmpHashMap);
			}else {
				mastermap.put(key, tmpInauthorMap);
			}
		}
	}
	
	public void buildInAuthorRelation(List<String> list,int layer)
	{
		if(layer>2)
		{
			return;
		}else{
			List<String> citationids = new ArrayList<>();
			for(String id:list)
			{
				if(!papers.contains(id))
				{
					cachedID+=id+",";
					/*getInAuthorRelation.GetInRelation(Integer.parseInt(id));
					List<String>tmpAuthor=getInAuthorRelation.authors;
					HashMap<String, Integer> tmpInauthorMap=getInAuthorRelation.inauthorMap;
					if(tmpAuthor.size()>0)
						UnionMap(InAuthorMap, tmpAuthor, tmpInauthorMap);
					papers.add(id);
					if(citationids.size()>0){
						citationids=getCitationIDList.getCitationPapers(Integer.parseInt(id));
						buildInAuthorRelation(citationids, layer+1);
					}*/
				}
				cachedID="";
			}
			cachedID=cachedID.substring(0, cachedID.length()-1);
			String id[]=cachedID.split(",");
			
			
		}
	}
	public void buildOutAuthorRelation(List<String>list){
		
	}

	public void closeConnection() {
		this.getInAuthorRelation.sqLconnection.disconnectMySQL();
		this.getCitationIDList.closeConnection();
	}
}
