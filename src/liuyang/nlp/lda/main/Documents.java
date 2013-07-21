package liuyang.nlp.lda.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thu.db.im.basefun.Segmenter;
import thu.db.im.basefun.WordsFilter;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Class for corpus which consists of M documents
 * 
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class Documents {

	ArrayList<Document> docs;
	LinkedHashMap<String, Integer> termToIndexMap;
	ArrayList<String> indexToTermMap;
	Map<String, Integer> termCountMap;
	static WordsFilter filter = new WordsFilter();

	public Documents() {
		docs = new ArrayList<Document>();
		termToIndexMap = new LinkedHashMap<String, Integer>();
		indexToTermMap = new ArrayList<String>();
		termCountMap = new HashMap<String, Integer>();
	}

	//@SuppressWarnings("resource")
	public void readDocs(Segmenter seg, String docsPath) {
		BufferedReader reader;
	 // int n = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(docsPath)));
			String line = "";
			while ((line = reader.readLine()) != null) {
				
				/*if (n == 1000)
					return;*/
				//System.out.println(line);
				Document doc = new Document(seg, line, termToIndexMap,
						indexToTermMap, termCountMap);
				docs.add(doc);
				//n++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class Document {
		int[] docWords;

		public Document(Segmenter seg, String lines,
				LinkedHashMap<String, Integer> termToIndexMap,
				ArrayList<String> indexToTermMap,
				Map<String, Integer> termCountMap) {
			List<CoreLabel> words = new List<CoreLabel>() {
				public boolean add(CoreLabel e) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void add(int index, CoreLabel element) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean addAll(Collection<? extends CoreLabel> c) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean addAll(int index,
						Collection<? extends CoreLabel> c) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void clear() {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean contains(Object o) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean containsAll(Collection<?> c) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public CoreLabel get(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int indexOf(Object o) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public boolean isEmpty() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public Iterator<CoreLabel> iterator() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int lastIndexOf(Object o) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public ListIterator<CoreLabel> listIterator() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public ListIterator<CoreLabel> listIterator(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public boolean remove(Object o) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public CoreLabel remove(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public boolean removeAll(Collection<?> c) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean retainAll(Collection<?> c) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public CoreLabel set(int index, CoreLabel element) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public List<CoreLabel> subList(int fromIndex, int toIndex) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Object[] toArray() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public <T> T[] toArray(T[] a) {
					// TODO Auto-generated method stub
					return null;
				}
			};
			// word segmentation
			words = seg.TokenAPI(lines);
			// Remove stop words and noise words
			for (int i = 0; i < words.size(); i++) {
				String term = words.get(i).toString().toLowerCase();
				if (filter.result(term).equals("") || isNoiseWord(term)) {
					words.remove(i);
					i--;
				}
			}
			// Transfer word to index
			/*
			 * termToIndexMap为一个HashMap,key为term,value为当前term在所有term中的第几个，从第一个文档开始计算
			 * indexToTermMap为ArrayList
			 * <String>，同termToIndexMap对应，第i个为的value为term
			 * termCountMap记录该term出现的次数
			 * ，为HashMap<String,Integer>,其中String为term，Integer为当前term出现的次数
			 */

			// docWords保存该文档中所有term在termToIndexMap中的位置
			this.docWords = new int[words.size()];
			for (int i = 0; i < words.size(); i++) {
				String word = words.get(i).toString().toLowerCase();
				if (!termToIndexMap.containsKey(word)) {
					int newIndex = termToIndexMap.size();
					termToIndexMap.put(word, newIndex);
					indexToTermMap.add(word);
					termCountMap.put(word, new Integer(1));
					docWords[i] = newIndex;
				} else {
					docWords[i] = termToIndexMap.get(word);
					termCountMap.put(word, termCountMap.get(word) + 1);
				}
			}
			words.clear();
		}

		public boolean isNoiseWord(String string) {
			// TODO Auto-generated method stub
			string = string.toLowerCase().trim();
			Pattern MY_PATTERN = Pattern.compile(".*[a-zA-Z]+.*");
			Matcher m = MY_PATTERN.matcher(string);
			// filter @xxx and URL
			if (string.matches(".*www\\..*") || string.matches(".*\\.com.*")
					|| string.matches(".*http:.*"))
				return true;
			if (!m.matches()) {
				return true;
			} else
				return false;
		}

	}
}
