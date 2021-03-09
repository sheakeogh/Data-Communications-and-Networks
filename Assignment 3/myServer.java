import java.io.*; 
import java.net.*;
import java.util.*;

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
	public static void processCustomerRequest(String request, int num, int[] arr, List<shop> list, BufferedReader inFromClient, DataOutputStream outToClient, File file) throws Exception {
		//Check if the request was to buy
		if(request.equalsIgnoreCase("yes") == true) {
			//Call the buy function from the shop class
			list.get(num).buy();
			System.out.println("You bought 1 item!");
			outToClient.writeBytes("You bought 1 item!\n");
			arr[num]++;
			//Print the new Stock
			printStock(list, outToClient);
	    		//Update the text file
			writeToFile(list, file);
		}
		//Check if the request was to restock
		else if(request.equalsIgnoreCase("no") == true) {
			System.out.println("Item not bought");
			outToClient.writeBytes("Item not bought\n");
		}
		//If the request was not matched inform the user of an error
		else {
           		System.out.println("Invalid Request!");
			outToClient.writeBytes("Invalid Request!\n");
		}
	}
	
	//Performs the action of the request given by the client
	public static void processVendorRequest(String request, int num, List<shop> list, BufferedReader inFromClient, DataOutputStream outToClient, File file) throws Exception {
		//Check if the request was to restock
		if(request.equalsIgnoreCase("restock") == true) {
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
		else if(request.equalsIgnoreCase("purchases") == true) {
			printPurchases(outToClient);
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

	//Update the text file
	public static void updateDatabase(int cookie, int[] arr, List<shop> list) throws Exception {
		//Initialize my writing variables
		File file = new File("customerDatabase.txt");
		FileWriter fw = new FileWriter(file,true);
		BufferedWriter bw = new BufferedWriter(fw);
		String title = "\n" + cookie + "\n";
		bw.write(title);
		//Iterate through the list and write into the text file
		for(int i = 0; i < arr.length; i++) {
			String newDetails = arr[i] + " " + list.get(i).getItem() + "\n";
			bw.write(newDetails);
		}
		//Close the buffered writer
		bw.close();
	}
	
	public static void printPurchases(DataOutputStream outToClient) throws Exception {
		File file = new File("customerDatabase.txt");
		Scanner scan = new Scanner(file);
		while(scan.hasNext()) {
			String str = scan.nextLine();
			System.out.println(str);
			outToClient.writeBytes(str + "\n");
		}
		scan.close();
	}
	
	//Main function
	public static void main(String args[]) throws Exception {
		//Initialize variables
		String product = "";
		String request = "";
		String type = "";
		int cookie = (int)(Math.random() * (10000) + 1000);
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
		int[] purchases = new int[items.size()];
		//Print welcome message and list of stock
		System.out.println("Welcome to the Grocery Store!\nAre you a customer or a vendor?");
		outToClient.writeBytes("Welcome to the Grocery Store!\nAre you a customer or a vendor?\n");		
		type = inFromClient.readLine();
		if(type.equalsIgnoreCase("Customer") == true) {		
			System.out.println("Customer");
			outToClient.writeBytes("Customer");
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
					System.out.println("Would you like to buy this product? ");
					outToClient.writeBytes("Would you like to buy this product? ");
					request = inFromClient.readLine();
					//Process this request and carry out the action
					processCustomerRequest(request, itemNum, purchases, items, inFromClient, outToClient, file);
				}
		    		else if(itemNum == -1) {
		    			System.out.println("Invalid Product!");
		    			outToClient.writeBytes("Invalid Product!\n");
				}
    			}
			updateDatabase(cookie, purchases, items);
		}
		else if(type.equalsIgnoreCase("Vendor") == true) {	
			System.out.println("Vendor");
			outToClient.writeBytes("Vendor");
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
					System.out.println("What would you like to do? Restock, Change Price or Check Purchases: ");
					outToClient.writeBytes("What would you like to do? Restock, Change Price or Check Purchases: ");
					request = inFromClient.readLine();
					//Process this request and carry out the action
					processVendorRequest(request, itemNum, items, inFromClient, outToClient, file);
				}
		    		else if(itemNum == -1) {
		    			System.out.println("Invalid Product!");
		    			outToClient.writeBytes("Invalid Product!\n");
				}
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
 
