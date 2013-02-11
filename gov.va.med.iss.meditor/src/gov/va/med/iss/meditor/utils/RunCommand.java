package gov.va.med.iss.meditor.utils;

import java.io.*;

public class RunCommand {

    public static void main(String args[]) {

        String s = null;

	    // run the Unix "ps -ef" command
            // using the Runtime exec method:
            //Process p = Runtime.getRuntime().exec("C:\\Program Files\\Internet Explorer\\iexplore c:\\Documents and Settings\\vhaisfiveyj\\My Documents\\091130 html test1.htm");
        	String filename = "c:\\Documents and Settings\\vhaisfiveyj\\My Documents\\091130 html test1.htm";
        	runCommand(filename);
    }
    
    static void runCommand(String filename) {
    	try {
            Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+filename);
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p.getErrorStream()));

        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
    }
}
