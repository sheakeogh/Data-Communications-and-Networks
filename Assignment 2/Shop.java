//Shop class to make it easier to access items and perform actions
class shop {
  //Declaring variables
	private int quantity, price;
	private String item;
	
	//Initializing constructor
	public shop(String item, int quantity, int price) {
		this.item = item;
		this.quantity = quantity;
		this.price = price;
	}
	
	//Returns the name of the Item
	public String getItem() {
		return item;
	}
	
	//Returns the quantity of the Item
	public int getQuantity() {
		return quantity;
	}
	
	//Returns the price of the Item
	public int getPrice() {
		return price;
	}
	
	//Used to buy an item, decreases the quantity by 1
	public int buy() {
		return quantity--;
	}
	
	//Restocks an item to given quantity
	public int restock() {
		quantity = 15;
		return quantity;	
	}
	
	//User gives an input which is used to set the new price
	public int price(int x) {
		price = x;
		
		return price;	
	}

	//Prints all the details of an item
	public String getDetails() {
		return "Name: " + getItem() + ", In Stock: " + getQuantity() + ", Price: " + getPrice() + "\n";
	}
}
