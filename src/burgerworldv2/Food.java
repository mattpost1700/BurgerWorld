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
class Food {
    ArrayList<FoodFeature> arrOfFoodFeatures;
    double price;
    String name;
    
    public Food (String name, double price, ArrayList<FoodFeature> arr) {
        this.name = name;
        this.price = price;
        this.arrOfFoodFeatures = arr;
    }
    
    public Food() {
        
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public double getTotalPrice() {
        double sum = 0;
        for(FoodFeature ff: arrOfFoodFeatures) {
            sum+=ff.getAdditionalCost();
        }
        sum+=price;
        return sum;
    }
    
    public ArrayList<FoodFeature> getFeatureArr() {
        return arrOfFoodFeatures;
    }
}
