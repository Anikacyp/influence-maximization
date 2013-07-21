package thu.db.im.dblp.cleansing;

//paper object
import java.util.ArrayList;

public class Paper {
		String title;
		String[] author;
		int year;
		String publication;
		long index;
		ArrayList<Long> citation;
		String Abstract;

		public Paper() {
			title = "";
			year = 0;
			publication = "";
			index = -1;
			Abstract = "";
			citation = new ArrayList<Long>();
		}

		//set value
		public void setTitle(String title) {
			this.title = title;
		}

		public void setAuthor(String[] authors) {
			this.author = authors;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public void setPublication(String publication) {
			this.publication = publication;
		}

		public void setIndex(long index) {
			this.index = index;
		}

		public void setCitation(long citation) {
			this.citation.add(citation);
		}

		public void setAbstract(String Abstract) {
			this.Abstract = Abstract;
		}
		
		//get value
		public String getTitle() {
			return title;
		}

		public String[] getAuthor() {
			return author;
		}

		public int getYear() {
			return year;
		}

		public String getPublication() {
			return publication;
		}

		public long getIndex() {
			return index;
		}

		public ArrayList<Long> getCitation() {
			return citation;
		}

		public String getAbstract() {
			return Abstract;
		}
		/*public void print()
		{
			System.out.println("title: "+title);
			System.out.println("year: "+year);
			System.out.println("publication: "+publication);
			System.out.println("index: "+index);
			System.out.println("Abstract: "+Abstract);
			System.out.println("author:");
			for(String au:this.author)
			System.out.print(au+",");
			System.out.println();
			System.out.println("citation:");
			for(long cit:citation)
			System.out.print(cit+",");
		}*/
	}

