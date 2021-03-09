import java.net.*;
import java.io.*; 
  
class myServer { 
    public static void main(String args[]) throws Exception{
    	//Create a welcoming socket with the port 1234
    	ServerSocket welcomeSocket = new ServerSocket(1000);
    	
    	//Once the socket is made print an update to the monitor
    	System.out.println("Server Up");
    	
    	//Hold till contact from the client
    	Socket connectionSocket = welcomeSocket.accept();
    	System.out.println("Connection Made!");

        //Function to send to the client
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
    	
    	//Variable for Client IP
    	String greeting = "Hello from Server! IP Address: " + connectionSocket.getLocalSocketAddress().toString() + ", Port: " + connectionSocket.getPort();
    	
    	//Print Hello and the IP Address and port number to the monitor
    	System.out.println(greeting);
    	outToClient.writeBytes(greeting);
    	//Close the output stream and welcome socket
    	connectionSocket.close();
    	welcomeSocket.close();
    	
    	System.out.println("Server Down");
	}
}  
