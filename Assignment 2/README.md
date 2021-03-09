Assignment 2

Exercise 1
Write a server application which manages an online stock database which stores items along with their quantity and price (e.g widgets, quantity = 100, price = 2cents). The entries can be stored as a text file in the following form:

    Name1: quantity: price
    Name 2: quantity: price
    …………..

The server will accept connections from clients; each client sends the name of an item to the server, the server searches for that item’s quantity and price, and returns the information to the server.

Exercise 2
Based on the client (telnet) and server modules developed in exercise 1 develop a simple query protocol that allows a client to operate on the online stock database via the server. The operations should include BUY and RESTOCK (changing quatity), ADD (add entry for new item) and PRICE (change item price).
