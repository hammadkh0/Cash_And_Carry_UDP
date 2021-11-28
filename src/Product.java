import java.io.*;
import java.util.ArrayList;

public class Product implements Serializable{
    private String productName;
    private int productId;
    private String category;
    private String brand;
    private int quantity;

    public Product(){}
    public Product(String productName, int productId, String category, String brand, int quantity) {
        this.productName = productName;
        this.productId = productId;
        this.category = category;
        this.brand = brand;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getProductName() {
        return this.productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public int getProductId() {
        return this.productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public String getCategory(){
        return category;
    }
    public void setBrand(String brand){
        this.brand = brand;
    }
    public String getBrand(){
        return brand;
    }

    public String toString() {
        return "{" +
                " productName='" + getProductName() + "'" +
                ", productId='" + getProductId() + "'" +
                ", brand='" + getBrand() + "'" +
                ", category='" + getCategory() + "'" +
                ", quantity='" + getQuantity() + "'" +
                "}";
    }
    public static ArrayList<Product> readFromFile() {
        ArrayList<Product> list = new ArrayList<Product>();
        try{
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("product.txt"));
            list = (ArrayList<Product>) input.readObject();
        }
        catch(ClassNotFoundException c){}
        catch(ClassCastException d){}
        catch(IOException i){}

        return list;
    }
    public static void WriteToFile(Product p) {

        ArrayList<Product> list = readFromFile();
        list.add(p);
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("product.txt"));
            output.writeObject(list);
            output.close();
        }
        catch(IOException e){}
    }
}
