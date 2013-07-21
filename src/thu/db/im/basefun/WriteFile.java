package thu.db.im.basefun;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

//write the string to a file.
public class WriteFile {
	String path;
	BufferedWriter writer;

	public WriteFile(String path) {
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path, true)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void writelines(String line) {
		try {
			writer.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeWriter() {
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
