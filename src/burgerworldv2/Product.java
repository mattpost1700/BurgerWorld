/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burgerworldv2;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author mattp
 */
public class Product {

    int id, amountOfProduct, fullNumOfProduct;
    String name;
    double price;
    Button btn;

    public Product() {
    }

    public Product(int id, String name, double price, int amountOfProduct, int fullNumOfProduct) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amountOfProduct = amountOfProduct;
        this.fullNumOfProduct = fullNumOfProduct;

        this.btn = new Button("Reorder");
        btn.setOnAction((ActionEvent e) -> {
            Stage reorderWindow = new Stage();
            FlowPane root = new FlowPane();
            reorderWindow.getIcons().add(new Image(new File("resources/icon.png").toURI().toString()));

            TextField reorderAmount = new TextField("Enter Quanity");
            Button submitButton = new Button("Submit");
            VBox vb = new VBox(5);
            vb.setAlignment(Pos.CENTER);

            vb.getChildren().add(reorderAmount);
            vb.getChildren().add(submitButton);

            submitButton.setOnAction((ActionEvent e2) -> {
                try {
                    int amountToAdd = Integer.parseInt(reorderAmount.getText());
                    this.amountOfProduct += amountToAdd;
                    reorderWindow.close();
                } catch (Exception ex) {}
            });

            vb.setPadding(new Insets(5, 5, 5, 5));
            root.getChildren().add(vb);
            root.setAlignment(Pos.CENTER);

            Scene scene = new Scene(root, 270, 70);

            reorderWindow.setTitle("Reorder Window");
            reorderWindow.setScene(scene);
            reorderWindow.show();
        });
    }
    
    public Product(String name, int amountOfProduct) {
        this.name = name;
        this.amountOfProduct = amountOfProduct;
    }
    
    public void setAmountOfProd(int amt) {
        amountOfProduct = amt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getFullNumOfProduct() {
        return fullNumOfProduct;
    }

    public int getAmountOfProduct() {
        return amountOfProduct;
    }

    public int getRelativity() {
        double temp = (double) amountOfProduct / (double) fullNumOfProduct;
        temp *= 100;
        temp = Math.round(temp);
        return (int) temp;
    }

    public Button getButton() {
        return btn;
    }

    public String getFormattedPrice() {
        return String.format("$%10.2f", price);
    }

    //for debugging purposes
    public String toString() {
        return "ID: " + id + " Name: " + name + " Price: " + price + " FullNumofProd: " + fullNumOfProduct;
    }
}
