/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burgerworldv2;

import java.util.ArrayList;

/**
 *
 * @author mattp
 */
public class FoodFeature {
    String name;
    double additionCost;
    ArrayList<Product> arrOfProds;
    
    public FoodFeature(String name, ArrayList<Product> arrOfProds, double additionalCost) {
        this.name = name;
        this.arrOfProds = arrOfProds;
        this.additionCost = additionalCost;
    }
    
    public FoodFeature(String name, double additionalCost) {
        this.name = name;
        this.arrOfProds = new ArrayList<>();
        this.additionCost = additionalCost;
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<Product> getProdList() {
        return arrOfProds;
    }
    
    public double getAdditionalCost() {
        return additionCost;
    }
}
