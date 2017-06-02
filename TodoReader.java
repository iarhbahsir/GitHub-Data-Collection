package todoReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;	
import javax.swing.JPasswordField;

import com.jcabi.github.*;

public class TodoReader {
	
	private static int numTasks = 0;
	
	private static boolean checkIfString(String toCheck, String line) {
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
	
	private static void readContents(Contents cont, String path, String ref) {
		try {
			for(Content co: cont.iterate(path, ref)) {
				
				Content.Smart c = new Content.Smart(co);
				
				try {
					// check to see if JSON object (file) or array (directory)
					// array throws exception
					c.json();
				} catch (javax.json.JsonException e) {
					// go into directory if exception
					if(c.path().contains("/")) {
						readContents(cont, path + "/"
								+ c.path().substring((c.path().lastIndexOf("/")) + 1), "master");
					} else {
						readContents(cont, path + "/" + c.path(), "master");
					}
				}
					
				if(c.path().contains(".java")) {
					Scanner fileScanner = new Scanner(c.raw());
					boolean isComment = false;
					boolean isMultiLineComment = false;
					
					while(fileScanner.hasNextLine()) {
						
						String nextLine = fileScanner.nextLine();
						//change code so that only comment escapes outside of actual code are read
						
						//if contains // or /*, check if inside string literal
						
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
								System.out.println(nextLine.substring(nextLine.indexOf("TODO")));
								numTasks++;
							}
							
							isComment = false;
							
							if(isMultiLineComment && nextLine.contains("*/") 
									&& nextLine.indexOf("*/") > nextLine.indexOf("/*")) {
								isMultiLineComment = false;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) throws IOException {
		// read in Todo comments in .java files from given GitHub repository
		
		// TODO use this as a test blah
		/*
		 * TODO Multi-line test 1
		 * */
		/* TODO Multi-line test 2 */
		int TODO = 0;	// use this as a test
		
		Scanner kb = new Scanner(System.in);
		// usage of basic authentication allows for a much higher rate limit
		System.out.println("Enter your username:");
		String user = kb.nextLine();
		
		// use of JPasswordField allows code to work in IDE console
		String pwd;
		String message = "Enter your password for a higher rate limit";
		if(System.console() == null) { 
		  JPasswordField pf = new JPasswordField();
		  pf.requestFocusInWindow();
		  pwd = JOptionPane.showConfirmDialog(null, pf, message,
				        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION
				        		? new String(pf.getPassword()) : "";
		} else {
			pwd = new String(System.console().readPassword("%s> ", message));
		}
		
		Github gh;
		
		// defaults to lower rate limit/anonymous login if password is not entered
		if(!pwd.equals("")) {
			gh = new RtGithub(user, pwd);
		} else {
			gh = new RtGithub();
		}
		
		while(true) {
			System.out.println("\nEnter username/repository to get the number of TODO items or q to quit:");
			String repoName = kb.nextLine();
			if(repoName.equals("q")) {
				break;
			}
			Repo rp = gh.repos().get(new Coordinates.Simple(repoName));
			Contents repoCont = rp.contents();
			readContents(repoCont, "", "master");
			System.out.println("\nTotal number of tasks: " + numTasks);
			numTasks = 0;
		}
		
		System.out.println("Exited.");
	}
}
