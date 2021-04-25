package evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
//import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;



import util.MatrixUtilities;

/**
 * @author Giuseppe Manco
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ClusteringEvaluator {

	
	double[][] ct;
	int nrows;
	int ncols;
	double n;
	double Z;
	double snr, snc;

	public ClusteringEvaluator(double[][] mat, boolean summaries) {
		if (mat != null) {
			n = 0;
			if (summaries) {
				nrows = mat.length - 1;
				ncols = mat[0].length - 1;
			} else {
				nrows = mat.length;
				ncols = mat[0].length;
			}
			ct = new double[nrows + 1][ncols + 1];

			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					ct[i][j] = mat[i][j];
					n += ct[i][j];
					Z += ct[i][j] * ct[i][j];
					ct[i][ncols] += ct[i][j];
					ct[nrows][j] += ct[i][j];
					ct[nrows][ncols] += ct[i][j];
				}
			}
			snr = 0;
			for (int i = 0; i < nrows; i++)
				snr += ct[i][ncols] * ct[i][ncols];

			snc = 0;
			for (int i = 0; i < ncols; i++)
				snc += ct[nrows][i] * ct[nrows][i];

		}
	}

	public double getRandMeasure() {
		double a, d, M;
		a = getA();
		d = getD();
		M = getM();

		return (a + d) / M;
	}
	
	public double getCorrectedRandMeasure() {
		 double num, den;
		 num = (Z/2.0 - n/2.0)- (2.0/(n*(n-1)))*(snc -n)/2.0*(snr-n)/2.0;
		 den =1.0/2.0*((snc -n)/2.0 + (snr-n)/2.0)- (2.0/(n*(n-1)))*(snc -n)/2.0*(snr-n)/2.0;
		 return num/den;
	}

	/**
	 * @return
	 */
	private double getM() {
		return n * (n - 1) / 2;
	}

	/**
	 * @return
	 */
	private double getA() {
		return 1.0 / 2 * Z - n / 2;
	}

	/**
	 * @return
	 */
	private double getB() {
		return 1.0 / 2 * (snc - Z);
	}

	private double getC() {
		return 1.0 / 2 * (snr - Z);
	}

	/**
	 * @return
	 */
	private double getD() {
		// return getM() -getA() -getB() -getC();
		return (n * n + Z - (snr + snc)) / 2;
	}

	/**
	 * @return
	 */
	public double getGammaMeasure() {
		double a = getA();
		double b = getB();
		double c = getC();
		// double d = getD();
		double M = getM();
		double m1 = a + b, m2 = a + c;
		double gamma = (M * a - m1 * m2)
				/ Math.sqrt(m1 * m2 * (M - m1) * (M - m2));
		return gamma;
	}

	/**
	 * @return
	 */
	public double getFowlMeasure() {
		double a = getA();
		double b = getB();
		double c = getC();

		return a / (Math.sqrt((a + c) * (a + b)));
	}

	/**
	 * @return
	 */
	public double getJaccMeasure() {
		double a = getA();
		double b = getB();
		double c = getC();
		return a / (a + b + c);
	}

	public double getFMeasure() {
		double f[] = new double[ncols];
		double prec, rec, find;

		for (int i = 0; i < nrows; i++)
			for (int j = 0; j < ncols; j++) {
				prec = ct[i][j] / ct[i][ncols];
				rec = ct[i][j] / ct[nrows][j];
				if (prec != 0 && rec != 0)
					find = 2.0 * prec * rec / (prec + rec);
				else
					find = 0;
				if (find > f[j])
					f[j] = find;
			}
		find = 0;
		for (int j = 0; j < f.length; j++)
			find += f[j] * ct[nrows][j] / ct[nrows][ncols];
		return find;
	}

	/**
	 * @return
	 */
	public double getError() {
		int[] cm = new int[nrows];

		for (int i = 0; i < nrows; i++) {
			cm[i] = -1;
			double max = 0;
			for (int j = 0; j < ncols; j++)
				if (ct[i][j] > max) {
					cm[i] = j;
					max = ct[i][j];
				}
		}
		double err = 0;
		for (int i = 0; i < nrows; i++)
			for (int j = 0; j < ncols; j++)
				if (j != cm[i])
					err += ct[i][j];
		err /= ct[nrows][ncols];

		return err;
	}

	public double getWeigthedError() {
		double err = getError();
		if (nrows > ncols)
			err *= nrows * 1.0 / ncols;
		else
			err *= ncols * 1.0 / nrows;

		return err;
	}
	
	
	/*public double getMutualInfo(){
			double mi = 0, p_xy,p_x,p_y;
			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					p_xy = ct[i][j]/n;
					p_x = ct[i][ncols]/n;
					p_y = ct[ncols][j]/n;
					mi+= p_xy*Math.log(p_xy/(p_x*p_y));
				}
			}
			return mi;
	}
		 
	 public double getNMInfo(){
		    double nmi = getMutualInfo();
		    
		    double h_x = 0, h_y = 0, p_x,p_y;
		    
		    for (int i = 0; i < nrows; i++) {
		      p_x = ct[i][ncols]/n;
		      h_x += p_x*Math.log(p_x);
		    }
		    for (int j = 0; j < ncols; j++) {
		       p_y = ct[nrows][j]/n;
		        h_y += p_y*Math.log(p_y);
		    }
		    return (nmi/(h_x + h_y));
	 }*/
	
	public static void evaluate(int n_real_clusters,  HashMap<Integer, Integer> truth,HashMap<Integer, Integer> model){
		evaluate(n_real_clusters,n_real_clusters ,truth, model);
	}
	
	public static void evaluate(int n_real_clusters, int n_predicted_clusters, HashMap<Integer, Integer> truth,HashMap<Integer, Integer> model){
		double[][] a = new double[n_real_clusters][n_predicted_clusters];
		for(Integer element:truth.keySet()){
			Integer realclass=truth.get(element);
			Integer predictedClass=model.get(element);
			if(realclass==null || predictedClass==null){
				a[realclass-1][0]++;
				continue;
			//	throw new RuntimeException("Exception on element\t"+element);
			}
			a[realclass-1][predictedClass-1]++;
		}
		ClusteringEvaluator e = new ClusteringEvaluator(a, false);
		System.out.println(" - - - Clustering Evaluation - - - - ");
		System.out.println("F-Index:\t\t" + e.getFMeasure());
		System.out.println("Jaccard Statistics:\t" + e.getJaccMeasure());
		System.out.println("Rand Statistics:\t" + e.getRandMeasure());
		System.out.println("ARI Statistics:\t" + e.getCorrectedRandMeasure());
		System.out.println("Fowlkes Statistics:\t" + e.getFowlMeasure());
		System.out.println("Gamma Statistics:\t" + e.getGammaMeasure());
		System.out.println("Error:\t\t\t" + e.getError());
//		System.out.println("Mutual:\t\t\t" + e.getMutualInfo());
//		System.out.println("NMI:\t\t\t" + e.getNMInfo());

		System.out.println("Confusion matrix");
		MatrixUtilities.print(a);
		
		
		System.out.println();
		System.out.println(" - - - - - - - END - - - - - - - ");
	}
	

	public static void main(String[] args) throws Exception {
		System.out.println(" - - - - Clustering Evaluation - - - - ");
		if(args.length==0){
			printUsage();
			return;	
		}
		
	
		int realnclusters=0;
		int prenclusters=0;
		String model="";
		String groundTruth="";
		String name="";
		String result="";
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("--help")) {
				printUsage();
				return;
			}
			
//			if (args[i].equals("-c")) {
//				nclusters = Integer.parseInt(args[i + 1]);
//				i++;
//			}
			
			
			if (args[i].equals("-c")) {
				model = args[i + 1];
				i++;
			}
			
			
			if (args[i].equals("-g")) {
				groundTruth = args[i + 1];
				i++;
			}
			 if (args[i].equals("-i")) {
					
					name=args[i+1];
					i++;
			
					
				}
	           if (args[i].equals("-o")) {
					
					result=args[i+1];
					i++;
			
					
				}

			
		
			
		}// for each args
	
	
		
		FileWriter Result = new FileWriter(result,true);
		Result.write("Name\t#RealComm\tPreComm\tF-Index\tJaccard\tRand\tARI\tFowlkes\tGamma\tError\n");
	
		BufferedReader in_ground = new BufferedReader(new FileReader(groundTruth));
		
		System.out.println("Reading ground truth from \t"+groundTruth);
	
		BufferedReader in_model = new BufferedReader(new FileReader(model));
		
		System.out.println("Reading model from \t"+model);
		
//	nclusters=//in_model.readLine().length();

		String strLine;
		while ((strLine = in_ground.readLine()) != null)   {
			realnclusters++;
		}
		while ((strLine = in_model.readLine()) != null)   {
			prenclusters++;
		}
		

	
		in_model = new BufferedReader(new FileReader(model));
		in_ground = new BufferedReader(new FileReader(groundTruth));
		
		double[][] a = new double[realnclusters][prenclusters];
		Hashtable<Integer, Integer> t = new Hashtable<Integer, Integer>();
		int elem, real_clas,pred_class;
		int communitiname=-1;
		for (String line = in_ground.readLine(); line != null; line = in_ground.readLine()) {
			communitiname++;
			StringTokenizer tok = new StringTokenizer(line, "\t ");
			
			while(tok.countTokens()>0){
			elem = Integer.parseInt(tok.nextToken());
			real_clas = communitiname;
			if (!t.contains(elem))
				t.put(elem, real_clas);
			}
		}
		
		
		in_ground.close();
		communitiname=-1;
		for (String line = in_model.readLine(); line != null; line = in_model.readLine()) {
			communitiname++;
			StringTokenizer tok = new StringTokenizer(line, "\t ");
			while(tok.countTokens()>0){
			elem = Integer.parseInt(tok.nextToken());
			pred_class = communitiname;
			if (t.containsKey(elem)) {
				real_clas = t.get(elem);
			//System.out.println(real_clas+"\t"+pred_class+"\n");
				a[real_clas][pred_class]++;
				
			}
			}
		}

		in_model.close();
		
		ClusteringEvaluator e = new ClusteringEvaluator(a, false);
		
		Result.write(name+"\t"+realnclusters+"\t"+prenclusters+"\t"
		+e.getFMeasure()+"\t"
		+e.getJaccMeasure()+"\t"
		+e.getRandMeasure()+"\t"
		+e.getCorrectedRandMeasure()+"\t"
		+e.getFowlMeasure()+"\t"
		+e.getGammaMeasure()+"\t"
		+e.getError()
		+"\n");
//		System.out.println("F-Index:\t\t" + e.getFMeasure());
//		System.out.println("Jaccard Statistics:\t" + e.getJaccMeasure());
//		System.out.println("Rand Statistics:\t" + e.getRandMeasure());
//		System.out.println("ARI Statistics:\t" + e.getCorrectedRandMeasure());
//		System.out.println("Fowlkes Statistics:\t" + e.getFowlMeasure());
//		System.out.println("Gamma Statistics:\t" + e.getGammaMeasure());
//		System.out.println("Error:\t\t\t" + e.getError());
//	/*	System.out.println("Mutual:\t\t\t" + e.getMutualInfo());
//		System.out.println("NMI:\t\t\t" + e.getNMInfo());
//		System.out.println("Mutual:\t\t\t" + e.getMutualInfo());
//		System.out.println("NMI:\t\t\t" + e.getNMInfo());*/
//		System.out.println();
//		System.out.println(" - - - - - - - END - - - - - - - ");
		Result.close();
	}//main
	

	private static void printUsage() {
		System.out.println("-c <clusterModel> -g <groundTruth> ");
	}//printUsage
	
	
}
