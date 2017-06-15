package todoReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TodoReader {

	private HashMap<Todo, String> todoKeeper = new HashMap<Todo, String>();
	
	private enum ReadType {
		INITIALIZE, UPDATE
	}
	
	public boolean checkIfString(String toCheck, String line) {
		int quoteMark1 = line.indexOf("\"");
		
		while(quoteMark1 != -1) {
			int quoteMark2 = line.indexOf("\"", quoteMark1 + 1);
			
			if(quoteMark2 != -1 && line.substring(quoteMark1 + 1, quoteMark2).contains(toCheck)) {
				return true;
			}
			
			if(quoteMark1 != -1) {
				line = line.substring(quoteMark1 + 1);
				quoteMark1 = line.indexOf("\"");
			}
			if(quoteMark1 != -1) {
				line = line.substring(quoteMark1 + 1);
				quoteMark1 = line.indexOf("\"");
			}
		}
		
		return false;
	}
	
	public void readFileForTodos(File toRead, ReadType howToRead) throws FileNotFoundException {
		
		Scanner fileScanner = new Scanner(toRead);
		boolean isComment = false;
		boolean isMultiLineComment = false;
		
		// first line is always the commit time (unimplemented), second is commit hash
		String commitTime = fileScanner.nextLine();
		String commitHash = fileScanner.nextLine();
		String fileName = "";
		
		while(fileScanner.hasNextLine()) {
					
			String nextLine = fileScanner.nextLine();
			// TODO check for file name
			if(howToRead == ReadType.INITIALIZE && nextLine.equals("!D@E#L$I%M^I&T*E(R)")) {
				fileName = new String(nextLine);
			}
			else if(howToRead == ReadType.UPDATE && nextLine.startsWith("--- ") && !nextLine.endsWith("/null")) {
				fileName = new String(nextLine.substring(nextLine.indexOf("/")));
			}
			else if(howToRead == ReadType.UPDATE && nextLine.startsWith("+++ ") && !nextLine.endsWith("/null")) {
				fileName = new String(nextLine.substring(nextLine.indexOf("/")));
			}
			// if contains // or /*, check if inside string literal
			
			if(nextLine.contains("//")) {
				isComment = !checkIfString("//", nextLine);
			}
			if(nextLine.contains("/*") && !isMultiLineComment) {
				if(!(isComment && nextLine.indexOf("/*") > nextLine.indexOf("//")))
				{
					isMultiLineComment = !checkIfString("/*", nextLine);
				}
			}
			
			if(isComment || isMultiLineComment) {
				if(nextLine.substring(nextLine.indexOf(";") + 1).contains("TODO")) {
					// found Todo
					if(howToRead == ReadType.INITIALIZE) {
						// initialize todos for oldest examined commit (looking at all code, not just changes)
						todoKeeper.put(new Todo(nextLine, fileName, commitHash, commitTime), "");
					} else {
						// update todos (looking only at changes in code)
						if(nextLine.charAt(0) == '+' && nextLine.charAt(1) != '+') {
							//update addition
							nextLine = nextLine.substring(1);
							nextLine = nextLine.trim();
							Todo toAdd = new Todo(nextLine, fileName);
							if(!todoKeeper.containsKey(toAdd)) {
								toAdd.setCreationCommitHash(commitHash);
								//System.out.println(toAdd.getCreationCommitHash());
								toAdd.setTimeOfCreation(commitTime);
								//System.out.println(toAdd);
								//System.out.println(toAdd.getTimeOfCreation());
								todoKeeper.put(toAdd, "");
							}
						}
						else if(nextLine.charAt(0) == '-' && nextLine.charAt(1) != '-') {
							//update deletion
							nextLine = nextLine.substring(1);
							nextLine = nextLine.trim();
							Todo toDelete = new Todo(nextLine, fileName);
							if(todoKeeper.containsKey(toDelete)) {
								String v = todoKeeper.get(toDelete);
								todoKeeper.remove(toDelete, v);
								toDelete.setDeletionCommitHash(commitHash);
								toDelete.setTimeOfDeletion(commitTime);
								todoKeeper.put(toDelete, v);
							}
						}
					}
				}
				
				isComment = false;
				
				if(isMultiLineComment && nextLine.contains("*/") 
					&& nextLine.indexOf("*/") > nextLine.indexOf("/*")) {
					isMultiLineComment = false;
				}
			}
		}
		fileScanner.close();
	}
	
	public void showTodos()
	{
		System.out.println(todoKeeper.keySet());
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner kb = new Scanner(System.in);
		System.out.println("Enter path to Cloned directory");
		String clonedPath = kb.nextLine();
		// while testing
		String repoName = "GitHub-Data-Collection";
		
		File infoDir = new File(clonedPath + "/" + repoName + "-Info");
		String CommitInfoPath = infoDir.getAbsolutePath() + "/" + repoName + "-Output-";
		File OldestCommitInfo = new File(CommitInfoPath + "0.txt");
		TodoReader reader = new TodoReader();
		
		reader.readFileForTodos(OldestCommitInfo, ReadType.INITIALIZE);
		
		int count = 1;
		File commitInfo = new File(CommitInfoPath + count + ".txt");
		while(commitInfo.exists()) {
			reader.readFileForTodos(commitInfo, ReadType.UPDATE);
			commitInfo = new File(CommitInfoPath + count + ".txt");
			count++;
		}
		
		System.out.println("Done");
		
		reader.showTodos();
		
		kb.close();
	}
}
