import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; 

public class myServer {
	//Creates a list from the text file
	public static List<shop> readFile(File file) throws Exception {
		//Initialize the variables
		List<shop> list = new ArrayList<shop>();
		Scanner scan = new Scanner(file);
		String s;
		int quantity, price;
		
		//While there is a next line to read we loop through the text file
       	 	while(scan.hasNext()) {
        		s = scan.next();
        		quantity = scan.nextInt();
        		price = scan.nextInt();
        		//Make a new shop and add it to the list
        		shop details = new shop(s, quantity, price);
        		list.add(details);
        	}
        	//Close the scanner to reduce leakage
		scan.close();
		//return the list
		return list;
	}

	//Prints the stock to the client and the server
	public static void printStock(List<shop> list, DataOutputStream outToClient) throws Exception {
		//Iterate through the list printing its details 
		for(int i = 0; i < list.size(); i++) {
			System.out.print(((shop)list.get(i)).getDetails());
			outToClient.writeBytes(((shop)list.get(i)).getDetails());
		}
	}
	
	//Search for a product in our list, if it is found return the index, if it doesn't exit return -1
	public static int searchProduct(String product, List<shop> list) {
		int i;
		//Iterate through the list checking each item name return index if there is a match
		for(i = 0; i < list.size(); i++) {
			if(list.get(i).getItem().equalsIgnoreCase(product) == true) {
				return i;
			}
		}
		return -1;
	}
	
	//Performs the action of the request given by the client
	public static void processRequest(String request, int num, List<shop> list, BufferedReader inFromClient, DataOutputStream outToClient, File file) throws Exception {
		//Check if the request was to buy
		if(request.equalsIgnoreCase("buy") == true) {
			//Call the buy function from the shop class
			list.get(num).buy();
			System.out.println("You bought 1 item!");
			outToClient.writeBytes("You bought 1 item!\n");
			//Print the new Stock
			printStock(list, outToClient);
	    		//Update the text file
			writeToFile(list, file);
		}
		//Check if the request was to restock
		else if(request.equalsIgnoreCase("restock") == true) {
			//Call the restock function from the shop class
			list.get(num).restock();
			System.out.println("You restocked!");
			outToClient.writeBytes("You restocked!\n");
			//Print the new Stock
			printStock(list, outToClient);
	    		//Update the text file
	    		writeToFile(list, file);
		}
		//Check if the request was to change the price
		else if(request.equalsIgnoreCase("price") == true) {
			//Request for a new price
			System.out.println("What is the new price: ");
			outToClient.writeBytes("What is the new price: ");
			//Read the new price
			int price = Integer.parseInt(inFromClient.readLine());
			System.out.println(price);
			//Pass as parameter to the price function in the shop class
			list.get(num).price(price);
			System.out.println("You have changed the price!");
			outToClient.writeBytes("You have changed the price!\n");
			//Print the new Stock
			printStock(list, outToClient);
	    		//Update the text file
	    		writeToFile(list, file);
		}
		//Check if the request was to add a new item
		else if(request.equalsIgnoreCase("add") == true) {
			//Request for a new item, its quantity, and price
           		System.out.println("Add name, quantity and price:");
			outToClient.writeBytes("Add name, quantity and price: \n");
			//Read the new item, its quantity, and price
           		String newItem = inFromClient.readLine();
			System.out.println(newItem);
			//Using regex split the string into the name, quantity, and price
			String[] split = newItem.split("\\s+");
			//Make a new shop and add it to the list
			shop details = new shop(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			list.add(details);
			//Print the new Stock
			printStock(list, outToClient);
			//Update the data.txt file
			writeToFile(list, file);
		}
		//Check if the request was to exit		
		else if(request.equalsIgnoreCase("exit")) {
			
		}
		//If the request was not matched inform the user of an error
		else {
           		System.out.println("Invalid Request!");
			outToClient.writeBytes("Invalid Request!\n");
		}
	}
	
	//Update the text file
	public static void writeToFile(List<shop> list, File file) throws Exception {
        	//Initialize my writing variables
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		//Iterate through the list and write into the text file
		for(int i = 0; i < list.size(); i++) {
			String newDetails = list.get(i).getItem() + " " + list.get(i).getQuantity() + " " + list.get(i).getPrice() + "\n";
			bw.write(newDetails);
		}
		//Close the buffered writer
		bw.close();
	}

	//Main function
    	public static void main(String args[]) throws Exception {
		//Initialize variables
		String product = "";
		String request = "";
		//Create a welcoming socket with the port 1234
		ServerSocket welcomeSocket = new ServerSocket(1000);
		//Once the socket is made print an update
		System.out.println("Server Up");
		//Hold till contact from the client
		Socket connectionSocket = welcomeSocket.accept();
		System.out.println("Connection Made!");
		//Function to read from the client
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		//Function to send to the client
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
		//Variable for Client IP
		String ip = connectionSocket.getLocalSocketAddress().toString();
		//Print Hello and the IP Address and port number to the monitor
		System.out.println("Hello from Server! IP Address: " + ip + ", Port: " + connectionSocket.getPort());
		//Make file variable and create list from data file
		File file = new File("data.txt");
		List<shop> items = new ArrayList<shop>(readFile(file));
		//Print welcome message and list of stock
		System.out.println("Welcome to the Grocery Store! This is what we have in stock:");
		outToClient.writeBytes("Welcome to the Grocery Store! This is what we have in stock:\n");		
		printStock(items, outToClient);
		//While loop to run the shop if user types exit they leave the shop
		while(product.equalsIgnoreCase("exit") == false && request.equalsIgnoreCase("exit") == false) {
			//Ask for product and search for it
			System.out.println("Ask for a product: ");
			outToClient.writeBytes("Ask for product: ");
	    		product = inFromClient.readLine();
	    		System.out.println(product);
	    		int itemNum = searchProduct(product, items);
	    		if(itemNum != -1) {
				//Print the details of this product 
				System.out.print(((shop)items.get(itemNum)).getDetails());
				outToClient.writeBytes(((shop)items.get(itemNum)).getDetails());
				//Ask for a request and see what they want to do with the product
				System.out.println("What would you like to do? Buy, Restock, Change Price or Add New Item: ");
				outToClient.writeBytes("What would you like to do? Buy, Restock, Change Price or Add New Item: ");
				request = inFromClient.readLine();
				//Process this request and carry out the action
				processRequest(request, itemNum, items, inFromClient, outToClient, file);
	    		}
	    		else if(itemNum == -1) {
		    		System.out.println("Invalid Product!");
		   	 	outToClient.writeBytes("Invalid Product!\n");
	    		}
    		}
		//Print a goodbye message
		System.out.println("You have left the Grocery Store! Thank you for Visiting!");
		outToClient.writeBytes("You have left the Grocery Store! Thank you for Visiting!");
		//Close the sockets
		connectionSocket.close();
		welcomeSocket.close();
		//Notify that the server is now down
		System.out.println("Server Down");
    	}
}
