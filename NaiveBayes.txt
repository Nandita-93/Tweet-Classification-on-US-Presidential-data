

import java.io.*;
import java.util.*;

public class NaiveBayes {
HashMap<String,Integer> vocab = new HashMap<>(); 

HashMap<String,WordStruct> wordsList=new HashMap<>();
double totSpamtokensCount=0,totHamtokensCount=0,tottokensCount=0,toVocabtokensCOunt=0;;
double totHamProb=0,totSpamProb=0;
HashMap<String,HashMap<String,Integer>> countinClass = new HashMap<>();


HashSet<String> stop = new HashSet<String>();
 public void addStopWords(String inputFile){
	 try{

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String line= "";
			while ((line = reader.readLine()) != null) {
				String[] words = line.split("\\s+");
				for (String word : words) {
					if(!stop.contains(word)){
						stop.add(word);
					}
				}
				
			}
		
	 }catch(Exception e){
		 System.out.println("Error occured in addStopWords method \n ");
			e.printStackTrace();
	 }
 }
 
 public double Count(String Class){
		
		
		if(Class.equals("ham")){
			return totHamtokensCount;
		}
		if(Class.equals("total")){
			return tottokensCount;
		}
		if(Class.equals("spam")){
			return totSpamtokensCount;
		}
		return 0;
	}

	public void createVocab(String Path) {
		try{
			File[] folders = new File(Path).listFiles();
			String Classid="";
			for (File folder : folders) {
				File[] files = folder.listFiles();
				HashMap<String,Integer> classCount = new HashMap<>();
				Classid=folder.getName();
				System.out.println("classid -- "+Classid+" Count of Files: "+files.length);
				int totClassidWords=0;
				for (File inputFile : files) {
					BufferedReader reader = new BufferedReader(new FileReader(inputFile));
					String line= "";
					while ((line = reader.readLine()) != null) {
						line=line.toLowerCase();
						String[] words = line.split("\\s+");
						
						for (String word : words) {
							/*if(stopWords.contains(word)){
								continue;
							}*/
							totClassidWords++;
							if(vocab.containsKey(word)){
								vocab.put(word, vocab.get(word)+1);
							}
							else{
								vocab.put(word, 1);
							}
							if(classCount.containsKey(word)){
								classCount.put(word, classCount.get(word)+1);
							}else{
								classCount.put(word, 1);
							}
						}
						
						
					}
				}
				if(Classid.equals("spam")){
					totSpamtokensCount=totClassidWords;
					countinClass.put("spam", classCount);
				}
				else{
					totHamtokensCount=totClassidWords;
					countinClass.put("ham", classCount);
				}
				
				
			}
		}catch(Exception e){
			System.out.println("Error occured in NaiveBayes:createVocabulary method \n ");
			e.printStackTrace();
		}
	}
	
	public double getTotalprobability(String category){
		if(category.equals("spam")){
			return totSpamProb;
		}
		if(category.equals("ham")){
			return totHamProb;
		}
		return 0;
	}
	public void findTotalVocabsCOunt(){
		
	}
	public void ProbOfWords(){
		try{
			wordsList=new HashMap<>();
			for (String word : vocab.keySet()) {
				WordStruct wordObject = new WordStruct(word);
				if(countinClass.get("spam").containsKey(word)){
					wordObject.spamCount=countinClass.get("spam").get(word);
					toVocabtokensCOunt+=wordObject.spamCount;
				}
				else{
					wordObject.spamCount=0;
				}
				
				if(countinClass.get("ham").containsKey(word)){
					wordObject.hamCount=countinClass.get("ham").get(word);
					toVocabtokensCOunt+=wordObject.hamCount;
				}
				else{
					wordObject.hamCount=0;
				}
				wordObject.spamProb=(wordObject.spamCount+1)/(totSpamtokensCount+vocab.size());
				wordObject.hamProb=(wordObject.hamCount+1)/(totHamtokensCount+vocab.size());
				wordsList.put(word, wordObject);
				
				
			}
		}catch(Exception e){
			System.out.println("Error occured in NaiveBayes: findProbabilityOfWords method \n ");
			e.printStackTrace();
		}
		
	}
	public void validation(String TestPath){
		try{
			File[] folderList = new File(TestPath).listFiles();
			String classid="";
			for (File folder : folderList) {
				HashMap<String,Integer> classCount = new HashMap<>();
				File[] fileList = folder.listFiles();
				
				classid=folder.getName();
				double filecount=0,successCount=0;
				for (File inputFile : fileList) {
					String line;
					String predClassid="";
					filecount++;
					BufferedReader reader = new BufferedReader(new FileReader(inputFile));
					double hamProb=0,spamProb=0;
					double nologhamProb=1,nologspamProb=1;
					while ((line = reader.readLine()) != null) {
						line=line.toLowerCase();
						String[] words = line.split("\\s+");
						for (String word : words) {
							if(wordsList.containsKey(word)){
								hamProb+=Math.log10(wordsList.get(word).hamProb);
								spamProb+=Math.log10(wordsList.get(word).spamProb);
								
							}
						}
					}
					
					hamProb+=Math.log10(totHamProb);
					spamProb+=Math.log10(totSpamProb);
					
					if(hamProb>spamProb){
						predClassid="ham";
					}else{
						predClassid="spam";
					}
					
					if(predClassid.equals(classid)){
						successCount++;
					}
				}
				
				System.out.println("Probability of "+classid+":"+(successCount/filecount));
				System.out.println("Count of "+classid+":"+(successCount));
				
			}
			
			
		}catch(Exception e){
			System.out.println("Error occured in NaiveBayes: validateFiles method \n ");
			e.printStackTrace();
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
		NaiveBayes NB = new NaiveBayes();
		NB.createVocab(training);
		NB.tottokensCount=NB.totHamtokensCount+NB.totSpamtokensCount;
		System.out.println("Spam Words  :"+(NB.totHamtokensCount));
		System.out.println("Ham Words   :"+(NB.totSpamtokensCount));
		System.out.println("Total Words :"+(NB.tottokensCount));
		System.out.println("Vocabulary Size  :"+(NB.vocab.size()));
         NB.totHamProb=(3.0/4.0);
		 NB.totSpamProb=(1.0/4.0);
		System.out.println("Spam Probability  "+NB.totSpamProb);
		System.out.println("Ham Probability  "+NB.totHamProb);
		NB.ProbOfWords();
		NB.validation(test);;
		
		
		
	}

}

