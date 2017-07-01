package todoReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Scanner;
import java.util.TreeSet;
import todoReader.Todo;

public class TodoReader {

	private TreeSet<Todo> todoKeeper = new TreeSet<Todo>();
	
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
		boolean continueTODO = false;
		Todo toContinue = new Todo("new", "todo");
		
		// first line is always the commit time, second is commit hash
		String commitTime = "";
		String commitHash = "";
		String fileName = "";
		try {
			commitTime = fileScanner.nextLine();
			commitHash = fileScanner.nextLine();
		} catch (java.util.NoSuchElementException e) {
			System.out.println(toRead.getAbsolutePath());
			return;
		}
		
		while(fileScanner.hasNextLine()) {
					
			String nextLine = fileScanner.nextLine();
			// TODO implement in ProcessTODO, add elapsed time (change to localDate/Time), account for single line change of Todos
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
			if(fileName.endsWith(".java")) {
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
							toContinue = new Todo(nextLine, fileName, commitHash, commitTime);
							todoKeeper.add(toContinue);
							continueTODO = true;
						} else {
							// update todos (looking only at changes in code)
							if(nextLine.charAt(0) == '+' && nextLine.charAt(1) != '+') {
								//update addition
								nextLine = nextLine.substring(1);
								nextLine = nextLine.trim();
								Todo toAdd = new Todo(nextLine, fileName);
								if(!todoKeeper.contains(toAdd)) {
									toAdd.setCreationCommitHash(commitHash);
									toAdd.setTimeOfCreation(commitTime);
									todoKeeper.add(toAdd);
									toContinue = new Todo(nextLine, fileName, commitHash, commitTime);
									continueTODO = true;
								}
							}
							
							// set new variable fullContent
							
							else if(nextLine.charAt(0) == '-' && nextLine.charAt(1) != '-') {
								//update deletion
								nextLine = nextLine.substring(1);
								nextLine = nextLine.trim();
								Todo toDelete = new Todo(nextLine, fileName);
								if(todoKeeper.contains(toDelete)) {
									String cHash = todoKeeper.ceiling(toDelete).getCreationCommitHash();
									OffsetDateTime cTime = todoKeeper.ceiling(toDelete).getTimeOfCreation();
									todoKeeper.remove(toDelete);
									toDelete.setCreationCommitHash(cHash);
									toDelete.setTimeOfCreation(cTime);
									toDelete.setDeletionCommitHash(commitHash);
									toDelete.setTimeOfDeletion(commitTime);
									todoKeeper.add(toDelete);
								}
							}
						}
					}
					
					else if(continueTODO) {
						//System.out.println("continue with: " + nextLine);
						todoKeeper.remove(toContinue);
						toContinue.setFullContent(toContinue.getFullContent() + "\n" + nextLine.substring(1).trim());
						todoKeeper.add(toContinue);
					}
					
					isComment = false;
					
					if(isMultiLineComment && nextLine.contains("*/") 
						&& nextLine.indexOf("*/") > nextLine.indexOf("/*")) {
						isMultiLineComment = false;
					}
				} 
				else if(continueTODO) {
					//System.out.println("stop with: " + nextLine);
					continueTODO = false;
				}
			}
		}	
		fileScanner.close();
	}
	
	public String showTodos()
	{
		String toReturn = "";
		
		if(todoKeeper.isEmpty()) {
			return "No TODOs";
		}
		
		for(Todo toAdd: todoKeeper) {
			toReturn += toAdd.toString();
		}
		
		return toReturn;
	}
	
	public static void main(String[] args) throws IOException {
		//Scanner kb = new Scanner(System.in);
		//System.out.println("Enter path to Cloned directory");
		//String clonedPath = kb.nextLine();
		String clonedPath = args[0];
		// while testing
		//String repoName = "GitHub-Data-Collection";
		String repoName = args[1];
		
		File infoDir = new File(clonedPath + "/" + repoName + "-Info");
		String CommitInfoPath = infoDir.getAbsolutePath() + "/" + repoName + "-Output-";
		File OldestCommitInfo = new File(CommitInfoPath + "0.txt");
		TodoReader reader = new TodoReader();
		
		reader.readFileForTodos(OldestCommitInfo, ReadType.INITIALIZE);
		
		int count = 1;
		File commitInfo = new File(CommitInfoPath + count + ".txt");
		while(commitInfo.exists()) {
			reader.readFileForTodos(commitInfo, ReadType.UPDATE);
			count++;
			commitInfo = new File(CommitInfoPath + count + ".txt");
		}
		
		File outputFile = new File(infoDir.getAbsolutePath() + "/" + repoName + "-TODO-Info.txt");
		FileWriter output = new FileWriter(outputFile);
		output.write(reader.showTodos());
		
		System.out.println("Analysis done.");
		
		output.close();
		//kb.close();
	}
}
