package org.example;

public class Books extends Product {
    private String author;
    private String genre;

    public Books(String productID, String productName, int quantity, double price, String author, String genre) {
        super(productID, productName, quantity, price);
        this.author = author;
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String getProductCategory() {
        return "Books";
    }

    @Override
    public String getInfo() {
        return "Author: " + author + ", Genre: " + genre;
    }

    @Override
    public String toString() {
        return "Books{" +
                "productID='" + productID + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }

    @Override
    public String toRowString() {
        return String.format(
                "| %-15s | %-15s | %-15s | %-20s | %-10s | %-13s |",
                getProductID(), getProductName(), getQuantity(), getPrice(), author, genre);
    }

    @Override
    public String saveToString() {
        return getProductID() + "," + getProductName() + "," + getQuantity() + "," + getPrice() + "," + author + "," + genre;
    }
}
