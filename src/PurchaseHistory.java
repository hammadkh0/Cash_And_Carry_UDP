import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class PurchaseHistory implements Serializable{
    private Buyer buyer;
    private Product product;
    private java.util.Date date;

    public PurchaseHistory(){}
    public PurchaseHistory(Buyer buyer, Product product, Date date) {
        this.buyer = buyer;
        this.product = product;
        this.date = date;
    }

    public Buyer getBuyer() {
        return buyer;
    }
    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() {
        return "{" +
                " buyerName='" + getBuyer().getName() + "'" +
                ", productName='" + getProduct().getProductName() + "'" +
                ", productBrand='" + getProduct().getBrand() + "'" +
                ", productCategory='" + getProduct().getCategory() + "'" +
                ", date='" + getDate() + "'" +
                "}";
    }

    public static ArrayList<PurchaseHistory> readFromFile() {
        ArrayList<PurchaseHistory> list = new ArrayList<PurchaseHistory>();
        try{
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("history.txt"));
            list = (ArrayList<PurchaseHistory>) input.readObject();
        }
        catch(ClassNotFoundException c){}
        catch(ClassCastException d){}
        catch(IOException i){}

        return list;
    }
    public static void WriteToFile(PurchaseHistory phs) {

        ArrayList<PurchaseHistory> list = readFromFile();
        list.add(phs);
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("history.txt"));
            output.writeObject(list);
            output.close();
        }
        catch(IOException e){}
    }
}
