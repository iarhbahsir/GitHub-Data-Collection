package todoReader;
import java.io.IOException;
import java.util.Scanner;

import com.jcabi.github.*;
public class TodoReader {

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
		Contents cont = rp.contents();
		for(Content c: cont.iterate("", "master"))
		{
			
			System.out.println(c.path());
			Scanner fileScanner = new Scanner(c.raw());
			while(fileScanner.hasNextLine())
			{
				String nextLine = fileScanner.nextLine();
				if(!c.path().equals("TODO.txt"))
				{
					if(nextLine.contains("//"))
					{
						if(nextLine.indexOf("TODO") > nextLine.indexOf("//"))
						System.out.println(nextLine);
					}
				}
				else
				{
					System.out.println(nextLine);
				}
			}
		}
		/*Scanner td = new Scanner(cont.get("TODO.txt").raw());
		int numTasks = 0;
		
		while(td.hasNextLine())
		{
			numTasks++;
			System.out.println(td.nextLine());
		}
		
		System.out.println("\nTotal number of tasks: " + numTasks);*/
	}

}
