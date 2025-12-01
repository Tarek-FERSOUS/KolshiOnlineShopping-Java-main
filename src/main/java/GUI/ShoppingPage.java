package GUI;

import org.example.Clothing;
import org.example.Electronics;
import org.example.Product;
import org.example.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ShoppingPage {
    ArrayList<Product> products;
    private User currentUser;

    public void loadProducts() {
        try (Scanner scanner = new Scanner(new File("products.txt"))) {
            while (scanner.hasNextLine()) {
                String productLine = scanner.nextLine();
                // Split the line into product attributes, trimming any leading/trailing whitespace
                String[] productData = productLine.split(",", -1); // Preserve trailing empty strings
                for (int i = 0; i < productData.length; i++) {
                    productData[i] = productData[i].trim(); // Trim each element
                }

                // Create the appropriate Product object based on the product type
                // Format: ID, Name, Quantity, Price, Attr1, Attr2
                org.example.Product product;
                if (productData[0].startsWith("E")) {
                    product = new org.example.Electronics(productData[0], productData[1],
                            Integer.parseInt(productData[2]),
                            Double.parseDouble(productData[3]),
                            productData[4], Integer.parseInt(productData[5]));
                } else if (productData[0].startsWith("C")) {
                    product = new Clothing(productData[0], productData[1],
                            Integer.parseInt(productData[2]),
                            Double.parseDouble(productData[3]),
                            productData[4], productData[5]);
                } else if (productData[0].startsWith("B")) {
                    product = new org.example.Books(productData[0], productData[1],
                            Integer.parseInt(productData[2]),
                            Double.parseDouble(productData[3]),
                            productData[4], productData[5]);
                } else if (productData[0].startsWith("H")) {
                    product = new org.example.HomeGarden(productData[0], productData[1],
                            Integer.parseInt(productData[2]),
                            Double.parseDouble(productData[3]),
                            productData[4], productData[5]);
                } else {
                    continue; // Skip unknown product types
                }

                products.add(product);
            }
            System.out.println("Products loaded successfully from products.txt");
        } catch (IOException e) {
            if (e.getMessage().contains("No such file or directory")) {
                // File doesn't exist, print a custom message
                System.out.println("No saved products found. Starting with a fresh list.");
            } else {
                // Other error, print the original message
                System.err.println("Error loading products: " + e.getMessage());
            }
        }
    }



    ShoppingPage(User user) {
        currentUser = user;  // Store the current user
        products = new ArrayList<>();
        loadProducts();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
