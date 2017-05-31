package todoReader;
import java.io.IOException;
import java.util.Scanner;

import javax.json.Json;

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
				//System.out.println(c.path());
				try 
				{
					c.json();
				} 
				catch (javax.json.JsonException e) 
				{
					if(c.path().contains("/"))
					{
						//System.out.println(path + "/" + c.path().substring((c.path().lastIndexOf("/") + 1)));
						printContents(cont, path + "/" + c.path().substring((c.path().lastIndexOf("/")) + 1), "master");
					}
					
					else
					{
						//System.out.println(path + "/" + c.path());
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
		//read in Todo.txt file from given GitHub repository and give pertinent information
		//v1: list total number of tasks
		//TODO use this as a test
		int TODO = 0;	//use this as a test
		
		Github gh = new RtGithub();
		Scanner kb = new Scanner(System.in);
		System.out.println("Enter username/repository:");
		String repoName = kb.nextLine();
		//iarhbahsir/GitHub-Data-Collection
		Repo rp = gh.repos().get(new Coordinates.Simple(repoName));
		Contents repoCont = rp.contents();
		printContents(repoCont, "", "master");
		System.out.println("\nTotal number of tasks: " + numTasks);
	}

}
