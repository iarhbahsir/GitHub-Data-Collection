package todoReader;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import edu.stanford.nlp.ling.CoreLabel;

public class TodoTreeSet extends TreeSet<Todo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8385345907942588632L;
	private int numCompleted = 0;
	private double completionRate = 0;
	private double totalSpecificity = 0;
	private HashMap<String, Integer> wordFrequency = new HashMap<String, Integer>();
	
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
		//String[] todoWords = toAdd.getFullContent().substring(toAdd.getFullContent().indexOf("TODO")).split("\\s+");
		List<CoreLabel> todoWords = toAdd.getFullContentWords();
		Integer currFreq;
		for(CoreLabel word: todoWords) {
			if(word != null) {
				try {
					currFreq = (Integer) wordFrequency.remove(word.toString());
					if(currFreq == null) { currFreq = 0;}
				} catch (java.lang.NullPointerException e) {
					currFreq = 0;
				}
				//System.out.println(currFreq);
				wordFrequency.put(word.toString(), ++currFreq);
			}
		}
	}
	
	private void removeFromFreqTable(Todo toRemove) {
		//String[] todoWords = toRemove.getFullContent().substring(toRemove.getFullContent().indexOf("TODO")).split("\\s+");
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
		
		String toReturn = "TODOs: \n";
		
		for(Todo toAdd: this) {
			toReturn += toAdd.toString();
		}
		
		toReturn += "\n\nFrequency Table: \n";
		
		for(String word: wordFrequency.keySet()) {
			toReturn += "'" + word + "' : " + wordFrequency.get(word) + "\n";
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
		return completionRate;
	}
	public void setCompletionRate(double completionRate) {
		this.completionRate = completionRate;
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
	
	
}
