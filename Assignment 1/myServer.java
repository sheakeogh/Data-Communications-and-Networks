import java.net.*; 
  
class myServer { 
    public static void main(String args[]) throws Exception{
    	//Create a welcoming socket with the port 1234
    	ServerSocket welcomeSocket = new ServerSocket(1000);
    	
    	//Once the socket is made print an update to the monitor
    	System.out.println("Server Up");
    	
    	//Hold till contact from the client
    	Socket connectionSocket = welcomeSocket.accept();
    	System.out.println("Connection Made!");
    	
    	//Variable for Client IP
    	String ip = connectionSocket.getLocalSocketAddress().toString();
    	
    	//Print Hello and the IP Address and port number to the monitor
    	System.out.println("Hello from Server! IP Address: " + ip + ", Port: " + connectionSocket.getPort());
    	
    	//Close the output stream and welcome socket
    	connectionSocket.close();
    	welcomeSocket.close();
    	
    	System.out.println("Server Down");
	}
}  
