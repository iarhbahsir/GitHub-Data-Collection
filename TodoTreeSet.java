package todoReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;

import edu.stanford.nlp.ling.CoreLabel;

public class TodoTreeSet extends TreeSet<Todo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8385345907942588632L;
	private int numCompleted = 0;
	private double totalSpecificity = 0;
	private HashMap<String, Integer> wordFrequency = new HashMap<String, Integer>();
	private BoxAndWhiskerItem completeBoxPlot;
	private BoxAndWhiskerItem incompleteBoxPlot;
	
	public void analyze() throws IOException, InterruptedException {
		ArrayList<Double> completeSpecificities = new ArrayList<Double>();
		ArrayList<Double> incompleteSpecificities = new ArrayList<Double>();
		
		String toWrite = "";
		for(Todo toAnalyze: this) {
			toAnalyze.analyzeFullContent();
			toWrite += toAnalyze.getFullContentWords().toString() + "\n";
		}
		
		File currDir = new File("");
	    
	    
	    File specificityIn = new File(currDir.getAbsolutePath() + "/" + "speciteller-master/specificityIn.txt");
	    if(!specificityIn.exists()) {
	    	specificityIn.createNewFile();
	    }
	    File specificityOut = new File(currDir.getAbsolutePath()+ "/" + "speciteller-master/specificityOut.txt");
	    if(!specificityOut.exists()) {
	    	specificityOut.createNewFile();
	    }
	    
	    
	    FileWriter fw = new FileWriter(specificityIn, false);
	    fw.write(toWrite);
	    fw.flush();
	    fw.close();
	    String[] specitellerCommand = {"python", "speciteller-master/speciteller.py", "--inputfile", "inputfile", "--outputfile", "predfile"};
	    specitellerCommand[3] = "speciteller-master/specificityIn.txt";
	    specitellerCommand[5] = "speciteller-master/specificityOut.txt";
	    Process specitellerCommandProc = new ProcessBuilder(specitellerCommand).start();
	    BufferedReader br = new BufferedReader(new InputStreamReader(specitellerCommandProc.getErrorStream()));
	    String read = br.readLine();
	    while(read != null) {
	    	System.out.println(read);
	    	read = br.readLine();
	    }
	    specitellerCommandProc.waitFor();
	    
	    Scanner fs = new Scanner(specificityOut);
	    
	    for(Todo analyzed: this) {
	    	double specificity = Double.parseDouble(fs.nextLine());
	    	analyzed.setSpecificity(specificity);
	    	this.setTotalSpecificity(totalSpecificity + specificity);
	    	if(analyzed.getDeletionCommitHash() == null) {
				incompleteSpecificities.add(analyzed.getSpecificity());
			} else {
				completeSpecificities.add(analyzed.getSpecificity());
			}
	    }
	    
	    completeBoxPlot = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(completeSpecificities);
	    incompleteBoxPlot = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(incompleteSpecificities);
	}
	
	public boolean add(Todo toAdd) {
		addToFreqTable(toAdd);
		if(toAdd.getTimeOfDeletion() != null) {
			numCompleted++;
		}
		return super.add(toAdd);
	}
	
	public boolean remove(Todo toRemove) {
		removeFromFreqTable(toRemove);
		if(toRemove.getTimeOfDeletion() != null) {
			numCompleted--;
		}
		return super.remove(toRemove);
	}
	
	private void addToFreqTable(Todo toAdd) {
		List<CoreLabel> todoWords = toAdd.getFullContentWords();
		Integer currFreq;
		for(CoreLabel word: todoWords) {
			if(word != null) {
				try {
					currFreq = (Integer) wordFrequency.remove(word.toString());
					//currFreq = wordFrequency.remove(word.toString());
					if(currFreq == null) { currFreq = 0;}
				} catch (java.lang.NullPointerException e) {
					currFreq = 0;
				}
				wordFrequency.put(word.toString(), ++currFreq);
				
			}
		}
	}
	
	private void removeFromFreqTable(Todo toRemove) {
		List<CoreLabel> todoWords = toRemove.getFullContentWords();
		Integer currFreq;
		for(CoreLabel word: todoWords) {
			if(word != null) {
				try {
					currFreq = (Integer) wordFrequency.remove(word.toString());
					if(currFreq == null) { currFreq = 0;}
				} catch (java.lang.NullPointerException e) {
					currFreq = 0; //results in negative frequency, exposing error in adding to wordFreqeuncy
				}
				
				wordFrequency.put(word.toString(), --currFreq);
			}	
		}
	}
	
	public String toString() {
		
		if(this.isEmpty()) {
			return "No TODOs";
		}
		
		String toReturn = "Number of TODOs: " + size() + "\nAverage Specificity: " + getAvgSpecificity()
			+ "Specificites Box Plots: (min, q1, mean, median, q3, max)"
			+ "\n\tIncomplete TODO: " + incompleteBoxPlot.getMinRegularValue() + ", " + incompleteBoxPlot.getQ1() + ", " + incompleteBoxPlot.getMean() + ", " + incompleteBoxPlot.getMedian() + ", " + incompleteBoxPlot.getQ3() + ", " + incompleteBoxPlot.getMaxRegularValue()
			+ "\n\tCompleted TODO: " + completeBoxPlot.getMinRegularValue() + ", " + completeBoxPlot.getQ1() + ", " + completeBoxPlot.getMean() + ", " + completeBoxPlot.getMedian() + ", " + completeBoxPlot.getQ3() + ", " + completeBoxPlot.getMaxRegularValue()
			+ "\nCompletion Rate: " + getCompletionRate() + "%\n\n\nTODOs:\n\n";
	
		for(Todo toAdd: this) {
			toReturn += toAdd.toString();
		}
		
		toReturn += "\n\nFrequency Table: \n";
		
		//TODO permanently change HashMap to TreeSet

		TreeSet<Word> sortedWordFreq = new TreeSet<Word>(new WordComparator());
		for(String word: wordFrequency.keySet()) {
			sortedWordFreq.add(new Word(word, wordFrequency.get(word)));
		}
		
		//for(String word: wordFrequency.keySet()) {
		for(Word word: sortedWordFreq) {
			toReturn += "'" + word.word + "' : " + word.frequency + "\n";
		}
		return toReturn;
	}
	
	public int getNumCompleted() {
		return numCompleted;
	}
	public void setNumCompleted(int numCompleted) {
		this.numCompleted = numCompleted;
	}
	public double getCompletionRate() {
		return (((double) getNumCompleted())/((double) size()))*100.0;
	}
	public double getAvgSpecificity() {
		return totalSpecificity/this.size();
	}
	public void setTotalSpecificity(double totalSpecificity) {
		this.totalSpecificity = totalSpecificity;
	}
	public HashMap<String, Integer> getWordFrequency() {
		return wordFrequency;
	}
	public void setWordFrequency(HashMap<String, Integer> wordFrequency) {
		this.wordFrequency = wordFrequency;
	}
	
	private class Word {
		public String word;
		public Integer frequency;
		
		public Word(String w, Integer f) {
			word = w;
			frequency = f;
		}
	}
	
	private class WordComparator implements Comparator<Word> {

		public int compare(Word a, Word b) {
			if(a.frequency == b.frequency) {
				return a.word.compareTo(b.word);
			}
			return a.frequency.compareTo(b.frequency);
		}
	}
}
