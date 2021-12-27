import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    private static byte[] sendData = new byte[1024];
    private static byte[] receiveData = new byte[1024];
    private static DatagramPacket sendPacket;
    private static DatagramPacket receivePacket;
    private static Scanner scan = new Scanner(System.in);
    private static BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception{

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("DESKTOP-OJ8ORHE");

        Buyer buyer = new Buyer();
        System.out.print("Enter your name: ");
        buyer.setName(inFromUser.readLine());
        System.out.print("Enter your age: ");
        int age;
        while(!scan.hasNextInt()){
            System.out.print("Enter the correct age: ");
            scan.next();
        }
        age=scan.nextInt();
        buyer.setAge(age);

        //SEND A PACKET CONTAINING BUYER INFORMATION TO THE SERVER TO ADD THE BUYER TO A FILE
        byte[] data = PacketHandler.createPacket(buyer);
        sendPacket = new DatagramPacket(data, data.length,IPAddress,9876);
        clientSocket.send(sendPacket);
        System.out.println("\nWELCOME!!!!\n");
        while(true)
        {
            Scanner input = new Scanner(System.in);
            System.out.print("1. Add Entity Data \n2. View Records \n3. Search Record \n4. Purchase an Item " +
                    "\n5. View Your Purchase History \n6. Exit\n");
            System.out.print(">>> Enter an Option: ");

            int x;
            while(!scan.hasNextInt()){
                System.out.print("Not a number!!! Enter an integer value:");
                scan.next();
            }
            x=scan.nextInt();
            while(x < 1 || x > 6){
                System.out.print("*** Value must be between 1 and 6 *** : ");

                while(!scan.hasNextInt()){
                    System.out.print("Not a number!!! Enter an integer value:");
                    scan.next();
                }
                x=scan.nextInt();
            }
            if(x == 1){
                AddItems(clientSocket,IPAddress);
            }
            else if(x == 2){
                ViewItems(clientSocket,IPAddress);
            }
            else if(x == 3){
                SearchItems(clientSocket,IPAddress);
            }
            else if (x == 4){
                purchaseItems(clientSocket,IPAddress, buyer);
            }
            else if (x == 5){
                viewHistory(clientSocket,IPAddress, buyer);
            }
            else if (x == 6){
                System.out.println("Program Terminated");
                System.exit(0);
            }
        }
    }

    public static void AddItems(DatagramSocket clientSocket, InetAddress IPAddress) throws Exception {
        Product prd = new Product();
        System.out.print("Enter Product Name: ");
        prd.setProductName(inFromUser.readLine());
        System.out.print("Enter a product ID: ");
//        int id;
        while(!scan.hasNextInt()){
            System.out.print("Not a number!!! Enter an integer value: ");
            scan.next();
        }
//        id=scan.nextInt();
        prd.setProductId(scan.nextInt());
        System.out.print("Enter a Product Brand: ");
        prd.setBrand(inFromUser.readLine());
        System.out.print("Enter a Product Category: ");
        prd.setCategory(inFromUser.readLine());
        System.out.print("Enter the number of Items: ");
//        int items;
        while(!scan.hasNextInt()){
            System.out.print("Not a number!!! Enter an integer value:");
            scan.next();
        }
//        items=scan.nextInt();
        prd.setQuantity(scan.nextInt());

        /* ADD THE PRODUCT OBJECT TO THE MESSAGE CLASS ALONG WITH "ADD" OPTION
        TO TELL SERVER TO ADD THE PRODUCT TO A FILE */
        Message msg = new Message("add",prd);

        //SEND THE PACKET TO SERVER
        byte[] data = PacketHandler.createPacket(msg);
        sendPacket = new DatagramPacket(data, data.length,IPAddress,9876);
        clientSocket.send(sendPacket);

        /* RECEIVING PACKET FROM SERVER THAT CONTAINS INFORMATION WHETHER THE
        PRODUCT IS ADDED SUCCESSFULLY OR NOT */
        receivePacket=new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        msg = (Message) PacketHandler.createObject(receiveData);
        String confirmation = msg.getText();
        confirmation = confirmation.trim();
        System.out.println("\n*** "+confirmation+" ***\n");
    }

    public static void ViewItems(DatagramSocket clientSocket, InetAddress IPAddress) throws Exception {
        /* CREATE A MESSAGE OBJECT WITH "VIEW" OPTION TO TELL SERVER TO VIEW THE PRODUCT ITEMS IN
        PRODUCT FILE. IT DOES NOT NEED A PRODUCT OBJECT SO NULL IS PASSED */
        Message msg = new Message("view",null);

        //SENDING THE MESSAGE OBJECT TO SERVER
        byte[] data = PacketHandler.createPacket(msg);
        sendPacket = new DatagramPacket(data, data.length,IPAddress,9876);
        clientSocket.send(sendPacket);

        //RECEIVING THE PRODUCT LIST FROM SERVER AND CONVERTING IT TO AN ARRAY LIST TO BE PRINTED
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        data = receivePacket.getData();
        ArrayList<Product> prd = (ArrayList<Product>) PacketHandler.createObject(data);

        displayItems(prd);
    }
    public static void SearchItems(DatagramSocket clientSocket, InetAddress IPAddress) throws Exception {

        /* CREATE A MESSAGE OBJECT WITH "SEARCH" OPTION TO TELL SERVER TO SEARCH THE PRODUCT ITEMS IN
        PRODUCT FILE. IT DOES NOT NEED A PRODUCT OBJECT SO NULL IS PASSED. */
        Message msg = new Message("search",null);
        byte[] data = PacketHandler.createPacket(msg);
        sendPacket = new DatagramPacket(data, data.length,IPAddress,9876);
        clientSocket.send(sendPacket);

        /* AFTER THE SERVER KNOWS TO SEARCH FOR PRODUCT WE WILL PROVIDE SERVER WITH THE PRODUCT NAME TO
        BE SEARCHED */
        System.out.print("Enter the Name: ");
        Scanner input = new Scanner(System.in);
        String search = input.nextLine();
        System.out.println(search);

        // CREATES A PACKET OF THE STRING OBJECT CONTAINING NAME OF THE PRODUCT AND SEND IT TO THE SERVER
        sendData = PacketHandler.createPacket(search);
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,9876);
        clientSocket.send(sendPacket);

        /* RECEIVES A PACKET FROM SERVER THAT EITHER CONTAINS THE SEARCHED OBJECT OR A MESSAGE SAYING
        THE PRODUCT DOES NOT EXISTS */
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        data= receivePacket.getData();

        Object packet = PacketHandler.createObject(data);
        /* IF PRODUCT DOES NOT EXIST, SERVER WILL SEND A OBJECT CONTAINING THE MESSAGE. THAT OBJECT WILL BE
         AN INSTANCE OF A STRING SINCE A STRING WAS SENT */
        if(packet instanceof String){
            ((String) packet).trim();
            System.out.println("\n"+packet);
        }
        else {  // SERVER WILL SEND THE  PRODUCT OBJECT AND IT WILL BE CONVERTED TO AN ARRAY LIST
            ArrayList<Product> prd = (ArrayList<Product>) packet;
            displayItems(prd);
        }
    }

    public static void purchaseItems(DatagramSocket clientSocket, InetAddress IPAddress, Buyer buyer) throws Exception {
        /* BEFORE PURCHASING, THE SERVER WILL SHOW THE LIST CONTAINING ALL PRODUCTS */
        ViewItems(clientSocket,IPAddress);

        /*  CREATE A MESSAGE OBJECT WITH "PURCHASE" OPTION TO TELL SERVER TO PURCHASE A PRODUCT ITEM IN
        PRODUCT FILE. IT DOES NOT NEED A PRODUCT OBJECT SO NULL IS PASSED. */
        Message msg = new Message("purchase",null);
        byte[] data = PacketHandler.createPacket(msg);
        sendPacket = new DatagramPacket(data, data.length,IPAddress,9876);
        clientSocket.send(sendPacket);

        /* AS SERVER KNOWS TO PURCHASE THE ITEM, BUYER WILL PROVIDE THE NAME AND AMOUNT REQUIRED OF THE
        PRODUCT */
        System.out.print("Enter the item name you want to purchase: ");
        String prodName = inFromUser.readLine();
        System.out.print("Enter the number of items to purchase: ");
        int quantity;
        while(!scan.hasNextInt()){
            System.out.print("Not a number!!! Enter an integer value:");
            scan.next();
        }
        quantity =scan.nextInt();

        // BUYER CANNOT ORDER A NEGATIVE OR A ZER0 AMOUNT OF PRODUCTS
        while(quantity <= 0){
            System.out.print("*** Quantity cannot be zero or less than zero *** : ");

            while(!scan.hasNextInt()){
                System.out.print("Not a number!!! Enter an integer value:");
                scan.next();
            }
            quantity=scan.nextInt();
        }

        /* 3 THINGS WILL BE PASSED IN THE MESSAGE. THE PRODUCT NAME, THE QUANTITY AND THE NAME OF BUYER
         PURCHASING TEH PRODUCT. THIS OBJECT WILL BE SENT TO THE SERVER */
        Message mes = new Message(prodName,quantity,buyer);
        data = PacketHandler.createPacket(mes);
        sendPacket = new DatagramPacket(data, data.length,IPAddress,9876);
        clientSocket.send(sendPacket);

        /* MESSAGE SHOWING THAT THE ITEM HAS BEEN PURCHASED OR NOT */
        receivePacket=new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        Message message = (Message) PacketHandler.createObject(receiveData);
        String str = message.getText();
        str = str.trim();
        System.out.println(str);

        /* IF THE PRODUCT HAS BEEN PURCHASED SUCCESSFULLY THEN MESSAGE WILL CONTAIN A STATUS = 2
         OTHERWISE IT WILL SHOW THAT PURCHASE HAS FAILED AND ITEM WILL NOT BE ADDED TO HISTORY */
        if(message.getStatus() == 2){

            // MESSAGE RECEIVED TELLING THAT ITEM HAS BEEN ADDED TO HISTORY
            receivePacket=new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            message = (Message) PacketHandler.createObject(receiveData);
            str = new String(message.getText());
            str = str.trim();
            System.out.println(str);
        }
    }

    public static void viewHistory(DatagramSocket clientSocket, InetAddress IPAddress, Buyer buyer) throws Exception {
        // SAME AS ABOVE
        Message msg = new Message("history",null);
        byte[] data = PacketHandler.createPacket(msg);
        sendPacket = new DatagramPacket(data, data.length,IPAddress,9876);
        clientSocket.send(sendPacket);

        /* SEND A PACKET CONTAINING THE BUYER USING THE APP CURRENTLY. THIS WILL BE USED TO FILTER OUT THE
         HISTORY OF CURRENT BUYER FROM OTHER BUYER'S IN THE HISTORY.TXT FILE  */
        sendData = PacketHandler.createPacket(buyer);
        sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,9876);
        clientSocket.send(sendPacket);

        /* RECEIVE A PACKET FROM SERVER CONTAINING THE OBJECT THAT HAS THE BUYER'S HISTORY */
        receivePacket = new DatagramPacket(receiveData,receiveData.length);
        clientSocket.receive(receivePacket);
        ArrayList<PurchaseHistory> historyList = (ArrayList<PurchaseHistory>) PacketHandler.createObject(receiveData);
        displayHistoryItems(historyList);
    }

    public static void displayItems(ArrayList<Product> list){
        System.out.println("----------------------------------------------------------------");
        System.out.println("Name\t\tID\tBrand\t\tCategory\t\tItems");
        System.out.println("----------------------------------------------------------------");
        for(int i =0; i<list.size(); i++){
            System.out.print(list.get(i).getProductName() +"\t");
            System.out.print(list.get(i).getProductId() +"\t");
            System.out.print(list.get(i).getBrand()+"\t");
            System.out.print(list.get(i).getCategory()+"\t");
            System.out.print(list.get(i).getQuantity()+"\n");
        }
        System.out.println("----------------------------------------------------------------");
    }
    public static void displayHistoryItems(ArrayList<PurchaseHistory> list){
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Name\t\tProduct\t\tBrand\t\tCategory\t\tDate");
        System.out.println("----------------------------------------------------------------------------");
        for(int i =0; i<list.size(); i++){
            System.out.print(list.get(i).getBuyer().getName() +"\t\t");
            System.out.print(list.get(i).getProduct().getProductName() +"\t");
            System.out.print(list.get(i).getProduct().getBrand()+"\t");
            System.out.print(list.get(i).getProduct().getCategory()+"\t");
            System.out.print(list.get(i).getDate()+"\n");
        }
        System.out.println("----------------------------------------------------------------------------");
    }
}
