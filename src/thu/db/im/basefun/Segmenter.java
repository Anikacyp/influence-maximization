package thu.db.im.basefun;

import java.io.StringReader;
import java.util.List;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;

/*
 * segment the string, and return the result as a list.
 */
public class Segmenter {

    LexicalizedParser lp;
	public Segmenter()
	{
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	}

	public List<CoreLabel> TokenAPI(String sent) {
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords= tokenizerFactory.getTokenizer(
				new StringReader(sent)).tokenize();
		return rawWords;
	}
	
	/*public static void main(String[]args)
	{
		String title="Graphical editing by example C++ (abstract) i 'em -----------------.</u> -rcb-";
		Segmenter seg=new Segmenter();
		List<CoreLabel> words;
		WordsFilter filter = new WordsFilter();
		words = seg.TokenAPI(title);
		for (int i = 0; i < words.size(); i++) {
			String word = words.get(i).toString().toLowerCase();
			if (!filter.result(word).equals("")) {
				System.out.println(word);
			}
		}
	}*/
}
