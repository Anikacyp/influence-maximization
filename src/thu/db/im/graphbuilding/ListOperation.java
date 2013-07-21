package thu.db.im.graphbuilding;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Yaping Chu
 * for a given two list, do some operations.(the union of the two list,
 * the intersection of the two list. and the ration of the intersection and union.)
 *
 */
public class ListOperation {

	public ListOperation() {

	}

	/*
	 * get ratio of two list,the ratio=common/total,the result is a string,which
	 * contains the common element number and total element number, separated by
	 * the blank
	 */

	public String getRatioOfTwoArray(List<String> list1, List<String> list2) {
		int common = 0;
		int total = 0;
		if (list1.size() > list2.size()) {
			total = list1.size();
			for (String obj : list2) {
				if (list1.contains(obj)) {
					common++;
				} else {
					total++;
				}
			}
		} else {
			total = list2.size();
			for (String obj : list1) {
				if (list2.contains(obj)) {
					common++;
				} else {
					total++;
				}
			}
		}
		if (common <= 5)
			return "";
		else
			return common + " " + total;
	}

	// get common element of two List
	public List<String> getIntersectionList(List<String> list1, List<String> list2) {
		List<String> commonList = new ArrayList<>();
		/*
		 * List<String> commonList = new ArrayList<>(list1);
		 * commonList.retainAll(list2); return commonList;
		 */
		if (list1.size() > list2.size()) {
			for (String obj : list2) {
				if (list1.contains(obj)) {
					commonList.add(obj);
				}
			}
		} else {
			for (String obj : list1) {
				if (list2.contains(obj)) {
					commonList.add(obj);
				}
			}
		}
		return commonList;
	}

	// get total element of two List.
	public List<String> getUnionList(List<String> list1, List<String> list2) {
		List<String> totalList = new ArrayList<>();
		if (list1.size() > list2.size()) {
			for (String obj : list1) {
				totalList.add(obj);
			}
			for (String obj : list2) {
				if (!totalList.contains(obj)) {
					totalList.add(obj);
				}
			}
		} else {
			for (String obj : list2) {
				totalList.add(obj);
			}
			for (String obj : list1) {
				if (!totalList.contains(obj)) {
					totalList.add(obj);
				}
			}
		}
		return totalList;
	}
}
