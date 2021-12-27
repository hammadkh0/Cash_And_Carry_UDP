import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server implements Serializable{
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    private static DatagramPacket sendPacket;
    private static DatagramPacket receivePacket;
    private static InetAddress IPAddress;

    public static void main(String[] args)throws Exception{
        DatagramSocket serverSocket = new DatagramSocket(9876);

        /* THE FIRST PACKET RECEIVED BY SERVER. IT CONTAINS BUYER'S INFORMATION AND WILL BE STORED IN THE
         BUYER.TXT FILE */
        receivePacket = new DatagramPacket(receiveData,receiveData.length);
        serverSocket.receive(receivePacket);
        byte[] data =receivePacket.getData();
        Buyer buyer  = (Buyer) PacketHandler.createObject(data);
        //GENERATE A LIST CONTAINING ALL BUYERS
        ArrayList<Buyer> buyerList = Buyer.readFromFile();
        int i;
        for ( i = 0; i < buyerList.size(); i++) {
            if(buyerList.get(i).getName().equals(buyer.getName())){
                // IF BUYER ALREADY EXISTS, NO NEED TO SEARCH FURTHER
                break;
            }
        }
        /*  IF THE PROVIDED BUYER DOES NOT EXIST, THEN THE LIST HAS BEEN TRAVERSED COMPLETELY AND i WILL be EQUAL
            TO THE SIZE OF THE LIST. ADD THIS BUYER TO THE BUYER FILE */
        if(i == buyerList.size()){
            Buyer.WriteToFile(buyer);
        }

        while(true){

            // RECEIVE THE PACKET FROM CLIENT TELLING ABOUT WHICH OPERATION TO BE PERFORMED
            receivePacket = new DatagramPacket(receiveData,receiveData.length);
            serverSocket.receive(receivePacket);
            byte[] data1 =receivePacket.getData();

            // CREATE A MESSAGE OBJECT CONTAINING THAT OPTION
            Message msg = (Message) PacketHandler.createObject(data1);
            System.out.println(msg.getOption());

            System.out.println("Program started");

            if(msg.getOption().equals("add")){
                S_Add(msg,serverSocket);
            }
            else if(msg.getOption().equals("view")){
                S_View(serverSocket);
            }
            else if(msg.getOption().equals("search")) {
                S_Search(serverSocket);
            }
            else if (msg.getOption().equals("purchase")){
                S_Purchase(serverSocket);
            }
            else if (msg.getOption().equals("history")){
                S_History(serverSocket);
            }
        }
    }
    public static void S_Add (Message msg, DatagramSocket serverSocket) throws Exception {

        IPAddress = receivePacket.getAddress();
        // TAKE THE PRODUCT OBJECT FROM THE MESSAGE OBJECT AND STORE IT IN THE FILE
        Product p = msg.getProducts();
        Product.WriteToFile(p);
        System.out.println(p.toString());
        String confirmation = "Successfully Added the Product";

        // TELL THE CLIENT THAT THE PROUCT HAS BEEN ADDED SUCCESSFULLY. THE STATUS = 2 WILL BE USED FOR IT
        Message message = new Message(confirmation,2);
        // CREATE A PACKET OF THIS MESSAGE OBJECT AND SEND IT TO THE CLIENT
        sendData = PacketHandler.createPacket(message);
        sendPacket =new DatagramPacket(sendData, sendData.length,IPAddress,receivePacket.getPort());
        serverSocket.send(sendPacket);
    }

    public static void S_View(DatagramSocket serverSocket) throws Exception {
        // CREATE AN ARRAYLIST OF THE PRODUCTS AND CREATE A PACKET OF IT TO BE SENT TO THE CLIENT
        ArrayList<Product> list1 =  Product.readFromFile();
        sendData = PacketHandler.createPacket(list1);

        IPAddress = receivePacket.getAddress();
        sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,receivePacket.getPort());
        serverSocket.send(sendPacket);
    }

    public static void S_Search(DatagramSocket serverSocket) throws Exception {

        // CREATE A LIST OF PRODUCTS FROM FILE
        ArrayList<Product> list1 =  Product.readFromFile();
        // RECEIVE A PACKET FROM CLIENT THAT CONTAINS THE NAME OF THE OBJECT TO BE SEARCHED
        receivePacket = new DatagramPacket(receiveData,receiveData.length);
        serverSocket.receive(receivePacket);

        String sent = PacketHandler.createObject(receiveData).toString();
        System.out.println(sent);

        ArrayList<Product> list2 =  new ArrayList<Product>();

        // SEARCH FOR THE OBJECT CONTAINING THE PROVIDED NAME. IF IT EXISTS ADD IT TO ANOTHER LIST
        for(int i =0; i<list1.size(); i++){
            if(list1.get(i).getProductName().equals(sent)) {
                list2.add(0, list1.get(i));
                break;
            }
        }
        // IF NO ITEM FOUND THEN SEND A STRING TO CLIENT ELSE SEND THE ARRAYLIST OBJECT
        if(list2.isEmpty()){
            String message = "*** No Product Found ***";
            sendData = PacketHandler.createPacket(message);
        }
        else{
            sendData = PacketHandler.createPacket(list2);
        }
        IPAddress = receivePacket.getAddress();
        sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,receivePacket.getPort());
        serverSocket.send(sendPacket);
    }

    public static void S_Purchase(DatagramSocket serverSocket) throws Exception{
        /* RECEIVE THE PACKET COTAINING THE NAME AND QUANTITY OF PRODUCT AND THE BUYER WHO IS PURCHASING */
        receivePacket = new DatagramPacket(receiveData,receiveData.length);
        serverSocket.receive(receivePacket);
        IPAddress = receivePacket.getAddress();

        Message mesg = (Message)PacketHandler.createObject(receiveData);
        String itemName = mesg.getProdName();
        int itemQuantity = mesg.getQuantity();

        ArrayList<Product> list1 =  Product.readFromFile();
        int check = 0, i;

        for ( i = 0; i< list1.size(); i++){
            // if the product name matches the name in the product list
            if (list1.get(i).getProductName().equals(itemName)){

                /* if the buyer requested more items than available for the required product, the loop will break
                 and eror message will be sent to the client */
                if (itemQuantity > list1.get(i).getQuantity()) {
                    check = 1;
                    break;
                }

                // subtract the items being purchased from total items and write updated list to the file
                list1.get(i).setQuantity(list1.get(i).getQuantity() - mesg.getQuantity());

                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("product.txt"));
                output.writeObject(list1);
                break;            }
        }
        String str;
        if (i == list1.size()){
            str = "Could not find the product";
        }
        else if (check == 1){
            str = "Cannot purchase more than available amount";

        } else{
            str = "Item Purchased Successfully";
            check = 2;
        }

        /* SEND A PACKET CONTAINIG THE MESSAGE THAT EITHER PRODUCT HAS BEEN PURCHASED OR NOT */
        Message msg = new Message(str, check);
        sendData = PacketHandler.createPacket(msg);
        sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,receivePacket.getPort());
        serverSocket.send(sendPacket);

        // IF PURCHASE WAS SUCCESSFUL THEN PROCEED TO ADD THAT ITEM TO HISTORY
        if(check == 2){
            PurchaseHistory phs = new PurchaseHistory();
            phs.setBuyer(mesg.getBuyer());
//            System.out.println(list1.get(i).toString());
            phs.setProduct(list1.get(i));
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            formatter.format(date);
            phs.setDate(date);

            // write the purchase history object to history.txt file
            PurchaseHistory.WriteToFile(phs);
            str = "Item Added to History";
            msg = new Message(str,2);

            // Send the packet telling client that item has been added to the history
            sendData = PacketHandler.createPacket(msg);
            sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,receivePacket.getPort());
            serverSocket.send(sendPacket);
        }

    }
    public static void S_History(DatagramSocket serverSocket)throws Exception{

        /* RECEIVE A PACKET CONTAINING CURRENT BUYER  */
        receivePacket = new DatagramPacket(receiveData,receiveData.length);
        serverSocket.receive(receivePacket);
        Buyer buyer = (Buyer) PacketHandler.createObject(receiveData);

        IPAddress = receivePacket.getAddress();
        ArrayList<PurchaseHistory> historyList = PurchaseHistory.readFromFile();
        System.out.println(historyList.size());

        // FILTER OUT THE ROWS THAT HAVE HISTORY OF OTHER BUYERS AND LEAVE ONLY THE CURRENT BUYER'S HISTORY
        for (int i = 0; i < historyList.size(); i++) {
            if (!historyList.get(i).getBuyer().getName().equals(buyer.getName())) {
//                System.out.println(historyList.get(i).toString());
                historyList.remove(i);
                i--;
            }
        }
        // SEND THE UPDATED LIST TO THE CLIENT TO BE VIEWED
        sendData = PacketHandler.createPacket(historyList);
        sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,receivePacket.getPort());
        serverSocket.send(sendPacket);
    }
}