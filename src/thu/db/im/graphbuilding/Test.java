package thu.db.im.graphbuilding;

import java.util.ArrayList;
import java.util.List;

public class Test {
	List<String>authors=new ArrayList<>();
	GetAuthorList getAuthorList=new GetAuthorList();
	GetCitationIDList getCitationIDList=new GetCitationIDList();
	List<String> totalpaper=new ArrayList<>();
	public void getauthors(int layer,List<String>list)
	{
		if(layer>2)
		{
			return;
		}else{
			for(String id:list){
				if(!totalpaper.contains(id)){
					List<String>tmp=getAuthorList.getAuthors(Integer.parseInt(id));
					for(String au:tmp){
						if(!authors.contains(au))
							authors.add(au);
					}
					List<String>papers=getCitationIDList.getCitationPapers(Integer.parseInt(id));
					totalpaper.add(id);
					if(papers.size()>0){
						getauthors(layer+1,papers);
					}
				}
			}
		}
	}
	public static void main(String args[])
	{
		List<String> list = new ArrayList<>();
		list.add("information");
		GetMultiTermIDList getList = new GetMultiTermIDList();
		list = getList.getCommonIDS(list);
		
		long begin1 = System.currentTimeMillis();
		BuildGraph buildGraph=new BuildGraph();
		System.out.println("common element size: "+list.size());
		buildGraph.buildInAuthorRelation(list, 1);
		System.out.println("Total authors: "+buildGraph.InAuthorMap.size());
		System.out.println("Total authors-new: "+buildGraph.totalAuthor.size());
		System.out.println("Total papers: "+buildGraph.papers.size());
		buildGraph.closeConnection();
		long end1 = System.currentTimeMillis();
		System.out.println("Total time: " + (end1 - begin1) / 1000);
		
		/*long begin = System.currentTimeMillis();
		System.out.println("common element size: "+list.size());
		Test test=new Test();
		test.getauthors(1, list);
		System.out.println(test.authors.size());
		System.out.println(test.totalpaper.size());
		long end = System.currentTimeMillis();
		System.out.println("Total time: " + (end - begin) / 1000);*/
	}
}
