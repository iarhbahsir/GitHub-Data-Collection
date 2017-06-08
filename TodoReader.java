package todoReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;	
import javax.swing.JPasswordField;

//import com.jcabi.github.*;

public class TodoReader {
	
	private static int numTasks = 0;
	
	public static boolean checkIfString(String toCheck, String line) {
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
	
	public static void readFileForTodos(String basePath, String filePath, File writeTo) throws FileNotFoundException {
		
		Scanner fileScanner = new Scanner(new File (basePath + filePath));
		boolean isComment = false;
		boolean isMultiLineComment = false;
		
		while(fileScanner.hasNextLine()) {
					
			String nextLine = fileScanner.nextLine();
			
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
					//write to file
					//substituted for test
					System.out.println(nextLine);
				}
				
				isComment = false;
				
				if(isMultiLineComment && nextLine.contains("*/") 
					&& nextLine.indexOf("*/") > nextLine.indexOf("/*")) {
					isMultiLineComment = false;
				}
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		//File javaFilePaths = new File(args[0]);
		//File writeTo = new File(args[1]);
		Scanner kb = new Scanner(System.in);
		System.out.println("Enter path of repo");
		String basePath = kb.nextLine();
		System.out.println("Enter path of .txt java file list:");
		File javaFilePaths = new File(kb.nextLine());
		System.out.println("Enter path of file to write to:");
		File writeTo = new File(kb.nextLine());
				
		Scanner jfpScanner = new Scanner(javaFilePaths);
		
		while(jfpScanner.hasNext()) {
			String filePath = jfpScanner.nextLine();
			readFileForTodos(basePath, filePath, writeTo);
		}
	}
}
