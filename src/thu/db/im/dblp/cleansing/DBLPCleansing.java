package thu.db.im.dblp.cleansing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class DBLPCleansing {
	BufferedReader reader;
	File filepath;
	BaseDBLPInfos write2db;

	public DBLPCleansing(String path) {
		write2db = new BaseDBLPInfos();
		filepath = new File(path);
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filepath), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void getFileInfo() {
		String line;
		boolean execute = false;
		try {
			line = reader.readLine();
			System.out.println("Total iterms: " + Integer.parseInt(line));
			int sum = 0;
			Paper paper = new Paper();
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					sum++;
					if (sum == 1508325)
						execute = true;
					if (execute & sum > 1508325) {

						write2db.write2DB(paper);
						// paper.
						paper = new Paper();
					}
					if (sum % 10000 == 0)
						System.out.println(sum);
				} else {
					if (execute) {
						String type = getcontent(line);
						switch (type) {
						case "title":
							paper.setTitle(line.substring(2));
							break;
						case "authors":
							paper.setAuthor(line.substring(2).split(","));
							break;
						case "year":
							paper.setYear(Integer.parseInt(line.substring(2)));
							break;
						case "publication":
							paper.setPublication(line.substring(2));
							break;
						case "index":
							paper.setIndex(Long.parseLong(line.substring(6)));
							break;
						case "citation":
							paper.setCitation(Long.parseLong(line.substring(2)));
							break;
						case "abstract":
							paper.setAbstract(line.substring(2));
							break;
						default:
							break;
						}
					}
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getcontent(String linestr) {
		String line = linestr.substring(0, 2);
		switch (line) {
		case "#*":
			return "title";
		case "#@":
			return "authors";
		case "#t":
			return "year";
		case "#c":
			return "publication";
		case "#i":
			return "index";
		case "#%":
			return "citation";
		case "#!":
			return "abstract";
		default:
			return "";
		}
	}

	public static void main(String args[]) {
		DBLPCleansing dblp = new DBLPCleansing(System.getProperty("user.dir")
				+ "/doc/extract doc/DBLPOnlyCitationOct19.txt");
		dblp.getFileInfo();
	}
}