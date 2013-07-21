package thu.db.im.basefun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/*
 * cleaning the original stopwords,remove the duplicate ones. 
 */
public class StopWords {

	BufferedReader reader;
	BufferedWriter writer;
	ArrayList<String>list;
	public StopWords(String file1,String file2)
	{
		try {
			reader=new BufferedReader(new InputStreamReader(new FileInputStream(new File(file1))));
			writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file2))));
			list=new ArrayList<String>();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void uniq()
	{
		getwords();
		uniq_words();
	}
	public void getwords()
	{
		String line="";
		try {
			while((line=reader.readLine())!=null)
			{
				line=line.toLowerCase();
				if(!list.contains(line))
				{
					list.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void uniq_words()
	{
		try {
			for(int i=0;i<list.size();i++)
				writer.write(list.get(i)+"\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		String file1=System.getProperty("user.dir")+"/doc/stop words/stop words.txt";
		String file2=System.getProperty("user.dir")+"/doc/stop words/uniq_stop_words.txt";
		new StopWords(file1, file2).uniq();
	}
}
