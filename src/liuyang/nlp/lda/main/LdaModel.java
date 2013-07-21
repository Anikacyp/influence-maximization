package liuyang.nlp.lda.main;

/**Class for Lda model
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import liuyang.nlp.lda.com.FileUtil;
import liuyang.nlp.lda.com.WriteFiles;
import liuyang.nlp.lda.conf.PathConfig;

public class LdaModel {

	int[][] doc;// word index array
	int V, K, M;// vocabulary size, topic number, document number
	int[][] z;// topic label array
	float alpha; // doc-topic dirichlet prior parameter
	float beta; // topic-word dirichlet prior parameter
	int[][] nmk;// given document m, count times of topic k. M*K
	double[][] theta;// Parameters for doc-topic distribution M*K
	int[][] nkt;// given topic k, count times of term t. K*V
	double[][] phi;// Parameters for topic-word distribution K*V
	int[] nmkSum;// Sum for each row in nmk
	int[] nktSum;// Sum for each row in nkt
	int iterations;// Times of iterations
	int saveStep;// The number of iterations between two saving
	int beginSaveIters;// Begin save model at this iteration

	public LdaModel(LdaGibbsSampling.modelparameters modelparam) {
		// TODO Auto-generated constructor stub
		alpha = modelparam.alpha;
		beta = modelparam.beta;
		iterations = modelparam.iteration;
		K = modelparam.topicNum;
		saveStep = modelparam.saveStep;
		beginSaveIters = modelparam.beginSaveIters;
	}

	public void initializeModel(Documents docSet) {
		// TODO Auto-generated method stub
		// 文档个数
		M = docSet.docs.size();
		System.out.println("文档个数" + M);
		// term个数
		V = docSet.termToIndexMap.size();
		System.out.println("term 个数" + V);
		// 文档-主题矩阵,其中行(hang)为文档，列为主题
		nmk = new int[M][K];
		// doc-topic参数矩阵
		theta = new double[M][K];
		// 主题-term矩阵，其中行为主题，列为term
		nkt = new int[K][V];
		// topic-term参数矩阵
		phi = new double[K][V];
		
		nmkSum = new int[M];
		nktSum = new int[K];
		// initialize documents index array
		doc = new int[M][];
		/*
		 * doc为doc-term矩阵，其中行为文档编号，列的长度为该文档所包含的term个数，
		 * 而doc[m][n]表示的第m个文档第n个词在termToIndexMap中的位置。
		 * 这样形成一个关于文档和该文档term的整体矩阵，根据doc[m][n]的值，
		 * 通过indexToTermMap可以知道这个位置的term内容
		 */
		for (int m = 0; m < M; m++) {
			// Notice the limit of memory
			int N = docSet.docs.get(m).docWords.length;
			doc[m] = new int[N];
			for (int n = 0; n < N; n++) {
				doc[m][n] = docSet.docs.get(m).docWords[n];
			}
		}

		// initialize topic lable z for each word
		/*
		 * z为M*N的矩阵，其中M代表第m个文档，N代表第m个文档所含的term个数。z[m][n]代表第m个文档第n个term所属topic。
		 */
		z = new int[M][];
		for (int m = 0; m < M; m++) {
			int N = docSet.docs.get(m).docWords.length;
			z[m] = new int[N];
			for (int n = 0; n < N; n++) {
				int initTopic = (int) (Math.random() * K);// From 0 to K - 1
				z[m][n] = initTopic;
				// number of words in doc m assigned to topic initTopic add 1
				/*
				 * nmk[M][K]，其中M代表文档，K代表主题,nmk[m][k]表示第m个文档属于第k个主题多少次
				 */
				nmk[m][initTopic]++;
				// number of terms doc[m][n] assigned to topic initTopic add 1
				/*
				 * nkt[K][V],其中K表示主题，V表示term nkt[k][t]表示term t属于topic k多少次
				 */
				nkt[initTopic][doc[m][n]]++;
				// total number of words assigned to topic initTopic add 1
				/*
				 * nktSum[K]，其中K表示主题 nktSum[k]表示term中所有属于k主题的个数。也等同于nkt[k][i]之和
				 */
				nktSum[initTopic]++;
			}
			// total number of words in document m is N
			/*
			 * nmkSum[M]，其中M表示文档，nmkSum[m]表示文档m有多少个term。
			 */
			nmkSum[m] = N;
		}
	}

	public void inferenceModel(Documents docSet) throws IOException {
		// TODO Auto-generated method stub
		if (iterations < saveStep + beginSaveIters) {
			System.err
					.println("Error: the number of iterations should be larger than "
							+ (saveStep + beginSaveIters));
			System.exit(0);
		}
		for (int i = 0; i < iterations; i++) {
			// System.out.println("Iteration " + i);
			if ((i >= beginSaveIters)
					&& (((i - beginSaveIters) % saveStep) == 0)) {
				// Saving the model
				System.out.println("Saving model at iteration " + i + " ... ");
				// Firstly update parameters
				updateEstimatedParameters();
				// Secondly print model variables
				saveIteratedModel(i, docSet);
			}

			// Use Gibbs Sampling to update z[][]
			for (int m = 0; m < M; m++) {
				int N = docSet.docs.get(m).docWords.length;
				for (int n = 0; n < N; n++) {
					// Sample from p(z_i|z_-i, w)
					int newTopic = sampleTopicZ(m, n);
					z[m][n] = newTopic;
				}
			}
		}
		
		//save the information about nmk, nkt, nmkSum, nktSum,phi,theta
		
		//here write the matrix nmk to the txt file
		String line="";
		WriteFiles writeFiles =new WriteFiles(PathConfig.nktPath);
		for(int i=0;i<nkt.length;i++)
		{
			for (int j=0;j<nkt[i].length;j++)
			{
				line+=nkt[i][j]+" ";
			}
			line+="\n";
			writeFiles.writeStrings(line);
			line="";
		}
		writeFiles.closeWriter();

		line="";
		 writeFiles =new WriteFiles(PathConfig.nmkPath);
		for(int i=0;i<nmk.length;i++)
		{
			for (int j=0;j<nmk[i].length;j++)
			{
				line+=nmk[i][j]+" ";
			}
			line+="\n";
			writeFiles.writeStrings(line);
			line="";
		}
		writeFiles.closeWriter();
	}

	private void updateEstimatedParameters() {
		// TODO Auto-generated method stub
		// System.out.println("beta: "+beta+"--"+"alpha"+alpha);
		for (int k = 0; k < K; k++) {
			for (int t = 0; t < V; t++) {
				phi[k][t] = (nkt[k][t] + beta) / (nktSum[k] + V * beta);
			}
		}

		for (int m = 0; m < M; m++) {
			for (int k = 0; k < K; k++) {
				theta[m][k] = (nmk[m][k] + alpha) / (nmkSum[m] + K * alpha);
			}
		}
	}

	private int sampleTopicZ(int m, int n) {
		// TODO Auto-generated method stub
		// Sample from p(z_i|z_-i, w) using Gibbs upde rule

		// Remove topic label for w_{m,n}
		int oldTopic = z[m][n];
		nmk[m][oldTopic]--;
		nkt[oldTopic][doc[m][n]]--;
		nmkSum[m]--;
		nktSum[oldTopic]--;

		// Compute p(z_i = k|z_-i, w)
		double[] p = new double[K];
		for (int k = 0; k < K; k++) {
			p[k] = (nkt[k][doc[m][n]] + beta) / (nktSum[k] + V * beta)
					* (nmk[m][k] + alpha) / (nmkSum[m] + K * alpha);
		}

		// Sample a new topic label for w_{m, n} like roulette
		// Compute cumulated probability for p
		for (int k = 1; k < K; k++) {
			p[k] += p[k - 1];
		}
		double u = Math.random() * p[K - 1]; // p[] is unnormalised
		int newTopic;
		for (newTopic = 0; newTopic < K; newTopic++) {
			if (u < p[newTopic]) {
				break;
			}
		}

		// Add new topic label for w_{m, n}
		nmk[m][newTopic]++;
		nkt[newTopic][doc[m][n]]++;
		nmkSum[m]++;
		nktSum[newTopic]++;
		return newTopic;
	}

	public void saveIteratedModel(int iters, Documents docSet)
			throws IOException {
		// TODO Auto-generated method stub
		// lda.params lda.phi lda.theta lda.tassign lda.twords
		// lda.params
		String resPath = PathConfig.LdaResultsPath;
		String modelName = "lda_" + iters;
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("alpha = " + alpha);
		lines.add("beta = " + beta);
		lines.add("topicNum = " + K);
		lines.add("docNum = " + M);
		lines.add("termNum = " + V);
		lines.add("iterations = " + iterations);
		lines.add("saveStep = " + saveStep);
		lines.add("beginSaveIters = " + beginSaveIters);
		FileUtil.writeLines(resPath + modelName + ".params", lines);
		
		// lda.phi K*V
		BufferedWriter writer = new BufferedWriter(new FileWriter(resPath
				+ modelName + ".phi"));
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < V; j++) {
				writer.write(phi[i][j] + "\t");
			}
			writer.write("\n");
		}
		writer.close();

		// lda.theta M*K
		writer = new BufferedWriter(new FileWriter(resPath + modelName
				+ ".theta"));
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < K; j++) {
				writer.write(theta[i][j] + "\t");
			}
			writer.write("\n");
		}
		writer.close();

		// lda.tassign
		//topic assigned to the term for each document
		writer = new BufferedWriter(new FileWriter(resPath + modelName
				+ ".tassign"));
		for (int m = 0; m < M; m++) {
			for (int n = 0; n < doc[m].length; n++) {
				writer.write(doc[m][n] + ":" + z[m][n] + "\t");
			}
			writer.write("\n");
		}
		writer.close();

		// lda.twords phi[][] K*V
		//topic words
		writer = new BufferedWriter(new FileWriter(resPath + modelName
				+ ".twords"));
		int topNum = 20; // Find the top 20 topic words in each topic
		for (int i = 0; i < K; i++) {
			List<Integer> tWordsIndexArray = new ArrayList<Integer>();
			for (int j = 0; j < V; j++) {
				tWordsIndexArray.add(new Integer(j));
			}
			Collections.sort(tWordsIndexArray, new LdaModel.TwordsComparable(
					phi[i]));
			writer.write("topic " + i + ":\t");
			for (int t = 0; t < topNum; t++) {
				writer.write(docSet.indexToTermMap.get(tWordsIndexArray.get(t))
						+ " " + phi[i][tWordsIndexArray.get(t)] + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		//only topics
		writer = new BufferedWriter(new FileWriter(resPath + modelName
				+ ".topicwords"));
		//int topNum = 20; // Find the top 20 topic words in each topic
		for (int i = 0; i < K; i++) {
			List<Integer> tWordsIndexArray = new ArrayList<Integer>();
			for (int j = 0; j < V; j++) {
				tWordsIndexArray.add(new Integer(j));
			}
			Collections.sort(tWordsIndexArray, new LdaModel.TwordsComparable(
					phi[i]));
			writer.write("topic " + i + ":\t");
			for (int t = 0; t < topNum; t++) {
				writer.write(docSet.indexToTermMap.get(tWordsIndexArray.get(t))
						+"\t");
			}
			writer.write("\n");
		}
		writer.close();
	}

	public class TwordsComparable implements Comparator<Integer> {

		public double[] sortProb; // Store probability of each word in topic k

		public TwordsComparable(double[] sortProb) {
			this.sortProb = sortProb;
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			// Sort topic word index according to the probability of each word
			// in topic k
			if (sortProb[o1] > sortProb[o2])
				return -1;
			else if (sortProb[o1] < sortProb[o2])
				return 1;
			else
				return 0;
		}
	}
}
