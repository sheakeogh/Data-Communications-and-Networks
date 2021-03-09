import java.io.*; 
import java.net.*;

public class myClient { 
    public static void main(String argv[]) throws Exception {
    	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        Socket clientSocket = new Socket("localhost", 1000); 
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
        while(clientSocket.isConnected()) {
        	System.out.println(inFromServer.lines());
       		outToServer.writeBytes(inFromUser.readLine() + '\n');
        }
	clientSocket.close();  
    } 
} 
