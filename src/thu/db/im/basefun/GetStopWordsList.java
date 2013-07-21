package thu.db.im.basefun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
 * 获取停用词表为List
 */
public class GetStopWordsList {
	BufferedReader reader;
	ArrayList<String> list;
	public GetStopWordsList()
	{
		File file=new File(System.getProperty("user.dir")+"/doc/stop words/uniq_stop_words.txt");
		try {
			reader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		list=new ArrayList<>();
	}
	
	public ArrayList<String> getList()
	{
		String line;
		try {
			while((line=reader.readLine())!=null)
			{
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
