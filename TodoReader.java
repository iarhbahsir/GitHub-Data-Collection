package todoReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;	
import javax.swing.JPasswordField;

import com.jcabi.github.*;

public class TodoReader 
{
	
	static int numTasks = 0;
	
	private static void printContents(Contents cont, String path, String ref)
	{
		try {
			for(Content co: cont.iterate(path, ref))
			{
				Content.Smart c = new Content.Smart(co);
				try 
				{
					c.json();
				} 
				catch (javax.json.JsonException e) 
				{
					if(c.path().contains("/"))
					{
						printContents(cont, path + "/" + c.path().substring((c.path().lastIndexOf("/")) + 1), "master");
					}
					
					else
					{
						printContents(cont, path + "/" + c.path(), "master");
					}
					
				}
				Scanner fileScanner = new Scanner(c.raw());
				while(fileScanner.hasNextLine())
				{
					String nextLine = fileScanner.nextLine();
					if(!c.path().equals("TODO.txt"))
					{
						if(nextLine.contains("//"))
						{
							if(nextLine.indexOf("TODO") > nextLine.indexOf("//"))
							{
								System.out.println(nextLine);
								numTasks++;
							}
						}
					}
					else
					{
						System.out.println(nextLine);
						numTasks++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) throws IOException 
	{
		//read in Todo.txt file and Todo comments from given GitHub repository and give pertinent information
		//v1: list total number of tasks
		//TODO use this as a test
		int TODO = 0;	//use this as a test
		
		Scanner kb = new Scanner(System.in);
		//usage of basic authentication allows for a much higher rate limit
		System.out.println("Enter your username:");
		String user = kb.nextLine();
		
		String pwd;
		String message = "Enter your password for a higher rate limit";
		
		//Use of JPasswordField allows code to work in IDE console
		if(System.console() == null) 
		{ 
		  JPasswordField pf = new JPasswordField();
		  pf.requestFocusInWindow();
		  pwd = JOptionPane.showConfirmDialog(null, pf, message, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION ? new String(pf.getPassword()) : "";
		}
		else
		{
			pwd = new String(System.console().readPassword("%s> ", message));
		}
		
		Github gh;
		
		if(!pwd.equals(""))
		{
			gh = new RtGithub(user, pwd);
		}
		else
		{
			gh = new RtGithub();
		}
		
		while(true)
		{
			System.out.println("\nEnter username/repository to get the number of TODO items or q to quit:");
			String repoName = kb.nextLine();
			if(repoName.equals("q"))
			{
				break;
			}
			Repo rp = gh.repos().get(new Coordinates.Simple(repoName));
			Contents repoCont = rp.contents();
			printContents(repoCont, "", "master");
			System.out.println("\nTotal number of tasks: " + numTasks);
			numTasks = 0;
		}
		
		System.out.println("Exited.");
	}

}
