package org.example;

public class HomeGarden extends Product {
    private String material;
    private String room;

    public HomeGarden(String productID, String productName, int quantity, double price, String material, String room) {
        super(productID, productName, quantity, price);
        this.material = material;
        this.room = room;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String getProductCategory() {
        return "Home & Garden";
    }

    @Override
    public String getInfo() {
        return "Material: " + material + ", For: " + room;
    }

    @Override
    public String toString() {
        return "HomeGarden{" +
                "productID='" + productID + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", material='" + material + '\'' +
                ", room='" + room + '\'' +
                '}';
    }

    @Override
    public String toRowString() {
        return String.format(
                "| %-15s | %-15s | %-15s | %-20s | %-10s | %-13s |",
                getProductID(), getProductName(), getQuantity(), getPrice(), material, room);
    }

    @Override
    public String saveToString() {
        return getProductID() + "," + getProductName() + "," + getQuantity() + "," + getPrice() + "," + material + "," + room;
    }
}
