import java.io.Serializable;

public class Message implements Serializable {
    private String option;
    private String prodName;
    private Buyer buyer;
    private int quantity;
    private int status;
    private Product products;
    private String text;

    public Message(String text, int status){
        this.text = text;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
    public String getText() {
        return text;
    }
    public Message(String option, Product prd) {
        this.option = option;
        this.products = prd;
    }

    public Message(String prodName, int quantity, Buyer buyer){
        this.prodName = prodName;
        this.quantity = quantity;
        this.buyer = buyer;
    }

    public Buyer getBuyer() {
        return buyer;
    }
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Product getProducts() {
        return products;
    }

    public void setProducts(Product products) {
        this.products = products;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
