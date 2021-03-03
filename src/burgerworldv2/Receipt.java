/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burgerworldv2;

import java.util.ArrayList;
import javafx.scene.control.Label;

/**
 *
 * @author mattp
 */
public class Receipt {

    ArrayList<Product> arrOfProd;
    ArrayList<Food> arrOfFood;
    Label recieptLabel;

    public Receipt(ArrayList<Food> arrOfProducts) {
        this.arrOfFood = arrOfProducts;
        this.recieptLabel = new Label();
        this.arrOfProd = new ArrayList<>();
    }

    public Receipt() {
        this.arrOfFood = new ArrayList<>();
        this.recieptLabel = new Label();
        this.arrOfProd = new ArrayList<>();
    }

    public void updateReceipt(ArrayList<Food> arrOfProducts) {
        this.arrOfFood = arrOfProducts;
    }

    private void foodToProd() {
        int amtOfBeef = 0;
        int amtOfBread = 0;
        int amtOfChicken = 0;
        int amtOfWater = 0;
        int amtOfFruit = 0;
        int amtOfVeg = 0;
        int amtOfSpice = 0;
        for (Food f : arrOfFood) {
            for (FoodFeature ff : f.getFeatureArr()) {
                for (Product p : ff.getProdList()) {
                    if (p.getName().equals("Beef")) {
                        amtOfBeef += p.getAmountOfProduct();
                    }
                    if (p.getName().equals("Bread")) {
                        amtOfBread += p.getAmountOfProduct();
                    }
                    if (p.getName().equals("Chicken")) {
                        amtOfChicken += p.getAmountOfProduct();
                    }
                    if (p.getName().equals("Water")) {
                        amtOfWater += p.getAmountOfProduct();
                    }
                    if (p.getName().equals("Fruit")) {
                        amtOfFruit += p.getAmountOfProduct();
                    }
                    if (p.getName().equals("Vegtable")) {
                        amtOfVeg += p.getAmountOfProduct();
                    }
                    if (p.getName().equals("Spice")) {
                        amtOfSpice += p.getAmountOfProduct();
                    }
                }
            }
        }
        arrOfProd.clear();
        arrOfProd.add(new Product("Beef", amtOfBeef));
        arrOfProd.add(new Product("Bread", amtOfBread));
        arrOfProd.add(new Product("Chicken", amtOfChicken));
        arrOfProd.add(new Product("Water", amtOfWater));
        arrOfProd.add(new Product("Fruit", amtOfFruit));
        arrOfProd.add(new Product("Vegtable", amtOfVeg));
        arrOfProd.add(new Product("Spice", amtOfSpice));
    }

    public boolean isThereEnoughInInventory(ArrayList<Product> arrayDB, String outOf) {
        foodToProd();
        for (int i = 0; i < arrayDB.size(); i++) {
            if (arrayDB.get(i).getAmountOfProduct() < arrOfProd.get(i).getAmountOfProduct()) {
                outOf = arrayDB.get(i).getName();
                return false;
            }
        }
        return true;
    }

    public void takeOutOfInventory(ArrayList<Product> arrayDB) {
        for (int i = 0; i < arrayDB.size(); i++) {
            arrayDB.get(i).setAmountOfProd(arrayDB.get(i).getAmountOfProduct()-arrOfProd.get(i).getAmountOfProduct());
        }
        DatabaseConnector.refreshDatabase(arrayDB);
    }

    public void addFood(Food f) {
        arrOfFood.add(f);
    }
    
    public int getNumOfFood() {
        return arrOfFood.size();
    }

    public double getTotalPrice() {
        if (arrOfFood == null) {
            return 0;
        }
        double sum = 0;
        for (Food f : arrOfFood) {
            sum += f.getPrice();
            for (FoodFeature ff : f.getFeatureArr()) {
                sum += ff.getAdditionalCost();
            }
        }
        return sum;
    }

    public ArrayList<Food> getReceipt() {
        return arrOfFood;
    }

    public Label getReceiptLabel() {
        if (arrOfFood == null) {
            return recieptLabel;
        }
        String temp = "";
        for (Food f : arrOfFood) {
            temp += f.getName() + " $" + f.getPrice() + "\n";
            for (FoodFeature ff : f.getFeatureArr()) {
                temp += "     " + ff.getName();
                if (ff.getAdditionalCost() != 0) {
                    temp += " +$" + ff.getAdditionalCost();
                }
                temp += "\n";
            }
        }
        //String.format("%10.2f", temp);
        recieptLabel.setText(temp);
        return recieptLabel;
    }

}
