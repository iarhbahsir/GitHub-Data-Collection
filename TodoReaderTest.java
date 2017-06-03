package todoReader;

import static org.junit.Assert.*;

import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jcabi.github.Contents;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;

public class TodoReaderTest {

	static Contents repoCont;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
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
		
		// separate repository for testing
		String repoName = "iarhbahsir/test-repo";
		Repo rp = gh.repos().get(new Coordinates.Simple(repoName));
		repoCont = rp.contents();
	}

	@Test
	public void testCheckIfString() {
		TodoReader todoUnderTest = new TodoReader();
		
		assertEquals("Failed to recognize as string", true, todoUnderTest.checkIfString("/*", "isMultiLineComment = !checkIfString(\"/*\", nextLine);"));
		assertEquals("Failed to recognize as string in second set of quotation marks", true, todoUnderTest.checkIfString("//", "if(!(isComment && nextLine.indexOf(\"/*\") > nextLine.indexOf(\"//\")))"));
		assertEquals("Incorrectly recognized as string", false, todoUnderTest.checkIfString("//", "// TODO use this as a test"));
		assertEquals("Incorrectly recognized as string between two unpaired quotation marks", false, todoUnderTest.checkIfString("b", "String[] tester = {\"a\", /*b*/ \"c\"}; "));
	}
	
	@Test
	public void testReadContents() {
		TodoReader todoUnderTest = new TodoReader();
		
		assertEquals("Failed to find correct number of TODO statements. Found statements are printed", 3, todoUnderTest.readContents(repoCont, "", "master", true));
	}
}
