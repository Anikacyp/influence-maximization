package liuyang.nlp.lda.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import thu.db.im.basefun.Segmenter;

import liuyang.nlp.lda.com.FileUtil;
import liuyang.nlp.lda.com.WriteFiles;
import liuyang.nlp.lda.conf.ConstantConfig;
import liuyang.nlp.lda.conf.PathConfig;

/**Liu Yang's implementation of Gibbs Sampling of LDA
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class LdaGibbsSampling {
	
	public static class modelparameters {
		float alpha = 0.5f; //usual value is 50 / K
		float beta = 0.1f;//usual value is 0.1
		int topicNum = 20;
		int iteration = 100;
		int saveStep = 10;
		int beginSaveIters = 50;
	}
	
	/**Get parameters from configuring file. If the 
	 * configuring file has value in it, use the value.
	 * Else the default value in program will be used
	 * @param ldaparameters
	 * @param parameterFile
	 * @return void
	 */
	private static void getParametersFromFile(modelparameters ldaparameters,
			String parameterFile) {
		// TODO Auto-generated method stub
		ArrayList<String> paramLines = new ArrayList<String>();
		FileUtil.readLines(parameterFile, paramLines);
		for(String line : paramLines){
			String[] lineParts = line.split("\t");
			switch(parameters.valueOf(lineParts[0])){
			case alpha:
				ldaparameters.alpha = Float.valueOf(lineParts[1]);
				break;
			case beta:
				ldaparameters.beta = Float.valueOf(lineParts[1]);
				break;
			case topicNum:
				ldaparameters.topicNum = Integer.valueOf(lineParts[1]);
				break;
			case iteration:
				ldaparameters.iteration = Integer.valueOf(lineParts[1]);
				break;
			case saveStep:
				ldaparameters.saveStep = Integer.valueOf(lineParts[1]);
				break;
			case beginSaveIters:
				ldaparameters.beginSaveIters = Integer.valueOf(lineParts[1]);
				break;
			}
		}
	}
	
	public enum parameters{
		alpha, beta, topicNum, iteration, saveStep, beginSaveIters;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//需要处理的文件路径
		//String originalDocsPath = PathConfig.ldaDocsPath;
		String originalDocsPath = PathConfig.dblpPath;
		//结果保存路径
		String resultPath = PathConfig.LdaResultsPath;
		//LDA参数文件路径
		String parameterFile= ConstantConfig.LDAPARAMETERFILE;
		//从文件中读取训练参数
		modelparameters ldaparameters = new modelparameters();
		getParametersFromFile(ldaparameters, parameterFile);
		
		//stanford分词
		Segmenter seg=new Segmenter();
		Documents docSet = new Documents();
		docSet.readDocs(seg,originalDocsPath);
		//输出每个文档中的字符，由于当前文档中的所有词都存储在了docWords里，
		//所以按照docWords的所存储的顺序从indexToTermMap中取就可以获取当前文档的所有term
		WriteFiles writeFiles =new WriteFiles(PathConfig.docTermPaht);
		String content ="";
		for(int i=0;i<docSet.docs.size();i++)
		{
			int[] docWords;
			docWords=docSet.docs.get(i).docWords;
			for(int j=0;j<docWords.length;j++)
				content+=docSet.indexToTermMap.get(docWords[j])+" ";
			content+="\r\n";
			writeFiles.writeStrings(content);
			content="";
		}
		writeFiles.closeWriter();
		
		content="";
		writeFiles=new WriteFiles(PathConfig.termCountMap);
		//输出termCountMap,即term和在所有文档中总共出现的次数统计
		for(Object o:docSet.termCountMap.keySet())
		{
			content=o+" "+docSet.termCountMap.get(o)+"\r\n";
			writeFiles.writeStrings(content);
		}
		writeFiles.closeWriter();
		content="";
		writeFiles=new WriteFiles(PathConfig.termToIndexMap);
		//输出termToIndexMap的结果，即term和它在当前term集合中的排序
		for(Object o:docSet.termToIndexMap.keySet())
		{
			content =o+" "+docSet.termToIndexMap.get(o)+"\r\n";
			writeFiles.writeStrings(content);	
		}
		writeFiles.closeWriter();

		content="";
		writeFiles=new WriteFiles(PathConfig.indexToTermMap);
		//打印出第i个indexToTermMap对应的term
		for(int i=0;i<docSet.indexToTermMap.size();i++)
		{
			content=i+" "+docSet.indexToTermMap.get(i)+"\r\n";
			writeFiles.writeStrings(content);
		}
		writeFiles.closeWriter();

		FileUtil.mkdir(new File(resultPath));
		LdaModel model = new LdaModel(ldaparameters);
		System.out.println("1 Initialize the model ...");
		model.initializeModel(docSet);
		System.out.println("2 Learning and Saving the model ...");
		model.inferenceModel(docSet);
		System.out.println("3 Output the final model ...");
		model.saveIteratedModel(ldaparameters.iteration, docSet);
		
		System.out.println("Done!");/**/
	}
}
