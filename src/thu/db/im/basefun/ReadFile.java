package thu.db.im.basefun;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

//read file operations
public class ReadFile {
	
	BufferedReader reader;
	public ReadFile(String file)
	{
		try {
			reader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public String readFiles()
	{
		int n=0;
		String line="";
		try {
			while((line=reader.readLine())!=null)
			{
				n++;
				System.out.println(line);
				if(n==50)
					return "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void closeReader()
	{
		try {
			this.reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		String path="data/LdaResults/lda_100.twords";
		new ReadFile(path).readFiles();
	}
}
