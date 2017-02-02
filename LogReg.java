
import java.io.*;
import java.util.*;

public class LogReg {
	    HashMap<String,Integer> vocab = new HashMap<>(); 
		HashMap<String,Double> weights=new HashMap<>();
		double learningRate=0.001;
		double lambda=0.3;
		HashMap<String,HashMap<String,Integer>> countWithClass = new HashMap<>();
		HashMap<String,WordStruct> wordsList=new HashMap<>();
		double totalSpamWordsCount=0,totalHamWordsCount=0,totalWordCount=0,totalVocabWordCOunt=0;;
		double totalHamProbabilty=0,totalSpamProbability=0;
		HashSet<String> stopWords = new HashSet<String>();
		
		
		
		
		public void Vocabulary(String Path) {
			try{
				File[] files = new File(Path).listFiles();
				String Class="";
				for (File f : files) {
					File[] infiles = f.listFiles();
					int totalClassWords=0;
					HashMap<String,Integer> classCount = new HashMap<>();
					Class=f.getName();
					System.out.println(Class);
					for (File inputFile : infiles) {
						BufferedReader bufread = new BufferedReader(new FileReader(inputFile));
						String line= "";
						while ((line = bufread.readLine()) != null) {
							line=line.toLowerCase();
							String[] tokens = line.split("\\s+");
							
							for (String token : tokens) {
								totalClassWords++;
								if(!vocab.containsKey(token)){
									vocab.put(token, 0);
								}
							}
						}
					}
					for(String word: vocab.keySet()){
						weights.put(word, 0.0);
					}
					
				}
			}catch(Exception e){
				System.out.println("Error occured in NaiveBayes:createVocabulary method \n ");
				e.printStackTrace();
			}
		}
		
		public HashMap<String, Integer> countWords(File inputFile){
			HashMap<String, Integer> count = new HashMap<>();
			
			try{
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				String line ="";
				while((line=br.readLine())!=null){
					line=line.toLowerCase();
					String[] words = line.split("\\s+");
					for (String word : words) {
						/*if(stopWords.contains(word)){
							continue;
						}*/
						if(count.containsKey(word)){
							count.put(word, count.get(word)+1);
						}
						else{
							count.put(word, 1);
						}
					
					}
				}
			}
			catch(Exception e){
				System.out.println("Exception occured");
			}
			return count;
		}
		
		public double sigmoid(double x){
			return 1.0/(1.0+Math.exp(-x));
		}
		
		public void processDocument(File inputFile, double classifier){
			HashMap<String, Integer> wordCount = countWords(inputFile);
			double totalVal=0.0;
			for(String word: wordCount.keySet()){
				totalVal+=wordCount.get(word)*vocab.get(word);
			}
			double predictedVal = sigmoid(totalVal);
			for(String word: wordCount.keySet()){
				double weight=learningRate*(classifier-predictedVal)*wordCount.get(word);
				weights.put(word,weights.get(word)+weight);
			}
		}
		
		public void train(String inputPath){
			try{
				File[] folderList = new File(inputPath).listFiles();
				String classifier="";
				double cla;
				for (File folder : folderList) {
					File[] fileList = folder.listFiles();
					HashMap<String,Integer> classCount = new HashMap<>();
					classifier=folder.getName();
					if(classifier=="spam"){
						cla=0.0;
					}
					else{
						cla=1.0;
					}
					//System.out.println("classifier -- "+" Count of Files: "+fileList.length);
					int totalClassifierWords=0;
					for(File input: fileList){
						processDocument(input, cla);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		public void modify(){
			for(String word: vocab.keySet()){
				double actualWeight=vocab.get(word);
				double pre = weights.get(word);
				double modified=actualWeight+pre-(learningRate*lambda*actualWeight);
				weights.put(word, modified+weights.get(word));
			}	
		}
		
		public void addStopWords(String inputFile){
			 try{

					BufferedReader reader = new BufferedReader(new FileReader(inputFile));
					String line= "";
					while ((line = reader.readLine()) != null) {
						String[] words = line.split("\\s+");
						for (String word : words) {
							if(!stopWords.contains(word)){
								stopWords.add(word);
							}
						}
						
					}
				
			 }catch(Exception e){
				 System.out.println("Error occured in NaiveBayes: addStopWords method \n ");
					e.printStackTrace();
			 }
		 }
		
		
		
		public void test(String inputPath){
			int filecount = 0;
			try{
				File[] folderList = new File(inputPath).listFiles();
			     for (File folder : folderList) {
					File[] fileList = folder.listFiles();
					int totalClassifierWords=0;
					for(File input: fileList){
						filecount++;
						findClass(input);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("Test filecount is"+filecount);
		}
		
		public void findClass(File inputFile)
		{
                HashMap<String, Integer> testcount = new HashMap<>();
                int totval = 0;;
               
			
			try{
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				String line ="";
				while((line=br.readLine())!=null){
					line=line.toLowerCase();
					String[] words = line.split("\\s+");
					for (String word : words) {
						/*if(stopWords.contains(word)){
							continue;
						}*/
						if(testcount.containsKey(word)){
							testcount.put(word, testcount.get(word)+1);
						}
						else{
							testcount.put(word, 1);
						}
					
					}
				}
			
			}
			catch(Exception e){
				System.out.println("Exception occured");
			}
			for(String word: testcount.keySet()){
				if(vocab.get(word)!=null)
				{	
				totval+=testcount.get(word)*weights.get(word);}}
			    Double result = sigmoid(totval);
			
			
			if(result<0.5)
			{
				System.out.println("File is ham");
				
			}
			else{
				
				System.out.println("File is spam");
			}
		}
		public static void main(String[] args) {
			
			String training="";
			String test="";
			
			if(args.length<2)
				System.out.println("No enough arguments.");
			else{
				
				training=args[0];
				test=args[1];
				
			}	
			LogReg LG = new LogReg();
			LG.Vocabulary(training);
			for(int i=0;i<100;i++){
				LG.train(training);
				LG.modify();
				
			}
			
			LG.test(test);
			
		}
	}



