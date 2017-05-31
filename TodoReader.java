package todoReader;
import java.io.IOException;
import java.util.Scanner;

import com.jcabi.github.*;
public class TodoReader {

	public static void main(String[] args) throws IOException 
	{
		//read in Todo.txt file from given GitHub repository and give pertinent information
		//v1: list total number of tasks
		
		Github gh = new RtGithub();
		Repo rp = gh.repos().get(new Coordinates.Simple("iarhbahsir/GitHub-Data-Collection"));
		Contents cont = rp.contents();
		Scanner td = new Scanner(cont.get("TODO.txt").raw());
		int numTasks = 0;
		
		while(td.hasNextLine())
		{
			numTasks++;
			System.out.println(td.nextLine());
		}
		
		System.out.println("\nTotal number of tasks: " + numTasks);
	}

}
