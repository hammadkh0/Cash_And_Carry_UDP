import java.io.*;
import java.util.ArrayList;

public class Buyer implements Serializable{
    private String name;
    private String address = "House: 111 St: 0 City: XYZ";
    private int age;

    public Buyer(){}
    public Buyer(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getAddress() {
        return address;
    }

    public String toString() {
        return "{" +
                " buyerName='" + getName() + "'" +
                ", buyerAge='" + getAge() + "'" +
                ", address='" + getAddress() + "'" +
                "}";
    }
    public static ArrayList<Buyer> readFromFile() {
        ArrayList<Buyer> list = new ArrayList<Buyer>();
        try{
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("buyer.txt"));
            list = (ArrayList<Buyer>) input.readObject();
        }
        catch(ClassNotFoundException c){}
        catch(ClassCastException d){}
        catch(IOException i){}

        return list;
    }
    public static void WriteToFile(Buyer b) {

        ArrayList<Buyer> list = readFromFile();
        list.add(b);
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("buyer.txt"));
            output.writeObject(list);
            output.close();
        }
        catch(IOException e){}
    }
}
