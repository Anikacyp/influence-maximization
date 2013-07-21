package thu.db.im.basefun;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * decide whether a word belong to the stopword, if it is, return the "" string
 * otherwise return the word.
 */
public class WordsFilter {
	ArrayList<String> list;
	GetStopWordsList stops;
	Matcher matcher;
	String reg1, reg2;

	public WordsFilter() {
		stops = new GetStopWordsList();
		list = stops.getList();
		reg1 = "[0-9]+?";
		//reg2 = "[.,:;\"'=-]+?";
		reg2 = "[.,:;=()'-_=@!~`<>?/¿·�¯ ¡#]";
	}

	public String result(String string) {
		if (list.contains(string)) {
			return "";
		}
		if (string.length() <= 2)
			return "";
		if ((matcher = Pattern.compile(reg2).matcher(string)).find())
			return "";
		if ((matcher = Pattern.compile(reg1).matcher(string)).find())
			return "";
		/*if(string.equals("-lrb-")||string.equals("-rrb-"))
			return "";*/
		else 
			return string;
	}
}
