package todoReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class Todo implements Comparable<Todo>{
	
	private String content;
	private String fileName;
	private String creationCommitHash;
	private String deletionCommitHash;
	private OffsetDateTime timeOfCreation;
	private OffsetDateTime timeOfDeletion;
	private String fullContent;
	private double specificity;
	private List<TypedDependency> fullContentStructure;
	private List<CoreLabel> fullContentWords;
	private static final String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	private static final LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	private static final PennTreebankLanguagePack tlp = (PennTreebankLanguagePack) lp.treebankLanguagePack();
	private static final GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	TokenizerFactory<CoreLabel> tf = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	
	private static final DateTimeFormatter timeFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	
	public Todo(String content, String fileName)
	{
		setContent(content);
		setFileName(fileName);
	}
	
	public Todo(String content, String fileName, String creationCommitHash, String timeOfCreation)
	{
		setContent(content);
		setFileName(fileName);
		setTimeOfCreation(timeOfCreation);
		setCreationCommitHash(creationCommitHash);
	}

	public void analyzeFullContent() throws IOException, InterruptedException {
		System.out.println(LocalDateTime.now());
		System.out.println(this.getFullContent());
		
		Tree fullContentTree = lp.apply(getFullContentWords());
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(fullContentTree);
	    setFullContentStructure(gs.typedDependenciesCCprocessed());
	    
	    //String[] toSpeciteller = {"cd", "speciteller-master"};
	    //Process toSpecitellerProc = new ProcessBuilder(toSpeciteller).start();
	    //Runtime.getRuntime().exec(toSpeciteller);
	    
	    
	    //String[] fromSpeciteller = {"/bin/bash", "-c", "cd", ".."};
	    //Process fromSpecitellerProc = new ProcessBuilder(fromSpeciteller).start();
	    //Runtime.getRuntime().exec(toSpeciteller);
	    
	    //System.out.println(getFullContentStructure());
	}

	@Override
	public int compareTo(Todo toCompare) {
		
		if(!(getContent().compareTo((toCompare).getContent()) == 0)) {
			return getContent().compareTo((toCompare).getContent());
		}
		if(!(getFileName().compareTo((toCompare).getFileName()) == 0)) {
			return getFileName().compareTo((toCompare).getFileName());
		}
		
		if(getTimeOfDeletion() == null && toCompare.getTimeOfDeletion() == null) {
			return 0;
		}
		
		if(getTimeOfDeletion() == null) {
			return -1;
		}
		
		if(toCompare.getTimeOfDeletion() == null) {
			return 1;
		}
		
		return getTimeOfDeletion().compareTo((toCompare).getTimeOfDeletion());
		
	}
	
	private String elapsedTime(OffsetDateTime from, OffsetDateTime to) {
		
		Duration timeDur = Duration.between(from.toOffsetTime(), to.toOffsetTime());
		Period timePer = Period.between(from.toLocalDate(), to.toLocalDate());
		return timePer.toString() + "; " + timeDur.toString();
	}
	
	public String toString() {
		return "Content:\n" + getFullContent() + "\nSpecificity: " + getSpecificity() + "\nContent Structure:" + getFullContentStructureString() +"\nFile Name: " + getFileName()
			+ "\nTime Of Creation: " + getTimeOfCreation() + "\nTime Of Deletion: " + getTimeOfDeletion()
			+ "\nCreation Commit Hash: " + getCreationCommitHash() + "\nDeletion Commit Hash: " + getDeletionCommitHash()
			+ "\nTime To Complete: " + ((timeOfCreation == null || timeOfDeletion == null) ? "Incomplete":(elapsedTime(timeOfCreation, timeOfDeletion)))
			+ "\n\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((deletionCommitHash == null) ? 0 : deletionCommitHash.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Todo other = (Todo) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (deletionCommitHash == null) {
			if (other.deletionCommitHash != null)
				return false;
		} else if (!deletionCommitHash.equals(other.deletionCommitHash))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		setFullContent(content);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCreationCommitHash() {
		return creationCommitHash;
	}

	public void setCreationCommitHash(String creationCommitHash) {
		this.creationCommitHash = creationCommitHash;
	}

	public String getDeletionCommitHash() {
		return deletionCommitHash;
	}

	public void setDeletionCommitHash(String deletionCommitHash) {
		this.deletionCommitHash = deletionCommitHash;
	}

	public OffsetDateTime getTimeOfCreation() {
		return timeOfCreation;
	}

	public void setTimeOfCreation(String timeOfCreation) {
		this.timeOfCreation = OffsetDateTime.parse(timeOfCreation, timeFormat);
	}

	public OffsetDateTime getTimeOfDeletion() {
		return timeOfDeletion;
	}

	public void setTimeOfDeletion(String timeOfDeletion) {
		this.timeOfDeletion = OffsetDateTime.parse(timeOfDeletion, timeFormat);
	}

	public void setTimeOfCreation(OffsetDateTime timeOfCreation) {
		this.timeOfCreation = timeOfCreation;
	}
	
	public String getFullContent() {
		return fullContent;
	}
	
	public void setFullContent(String fullContent) {
		this.fullContent = fullContent;
		Tokenizer<CoreLabel> t = tf.getTokenizer(new StringReader(getFullContent()));
		setFullContentWords(t.tokenize());
		//System.out.println(getFullContentWords());
	}
	
	public double getSpecificity() {
		return specificity;
	}
	
	public void setSpecificity(double specificity) {
		this.specificity = specificity;
	}

	public List<TypedDependency> getFullContentStructure() {
		return fullContentStructure;
	}
	
	public String getFullContentStructureString() {
		String toReturn = "";
		for(TypedDependency td : getFullContentStructure()) {
			toReturn += "\n" + td.toString();
		}
		return toReturn;
	}
	
	public void setFullContentStructure(List<TypedDependency> fullContentStructure) {
		this.fullContentStructure = fullContentStructure;
	}

	public List<CoreLabel> getFullContentWords() {
		return fullContentWords;
	}

	public void setFullContentWords(List<CoreLabel> fullContentWords) {
		this.fullContentWords = fullContentWords;
	}
	
	
}
