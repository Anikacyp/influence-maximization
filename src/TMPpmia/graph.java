package TMPpmia;

import java.util.ArrayList;

public class graph {
	private static boolean built=false;
	private static int n=0,m=0;
	private ArrayList<Integer> degree;
	private ArrayList<Integer>index;
	private ArrayList<Edge> edges;
	
	public void qsort_edges(int h,int t)
	{
		if(h<t)
		{
			int i=h,j=t;
			Edge midEdge=edges.get((i+j)/2);
			edges.set((i+j)/2, edges.get(i));
			
			while(i<j){
				while((i<j)&&((edges.get(j).u>midEdge.u)||((edges.get(j).u==midEdge.u)&&(edges.get(j).v>midEdge.v))))
					j--;
				if(i<j){
					edges.set(i, edges.get(j));
				}
				while((i<j)&&((edges.get(i).u<midEdge.u)||((edges.get(i).u==midEdge.u)&&(edges.get(i).v<midEdge.v))))
					i++;
				if(i<j){
					edges.set(j, edges.get(i));
					j--;
				}
			}
			edges.set(i, midEdge);
			qsort_edges(h, i-1);
			qsort_edges(i+1, t);
		}
	}
	
	
	
}
