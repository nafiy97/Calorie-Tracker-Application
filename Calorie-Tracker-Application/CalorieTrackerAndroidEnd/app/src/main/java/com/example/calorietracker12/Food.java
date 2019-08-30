package com.example.calorietracker12;

public class Food {
    Short foodId;
    String foodName;
    String foodCategory;
    Double foodCalorie;
    String foodSeverunit;
    Double foodServeamount;

    public Food() {
    }

    public Food(Short foodId) {
        this.foodId = foodId;
    }

    public Food(Short foodId, String foodName, String foodCategory, Double foodCalorie, String foodSeverunit, Double foodServeamount, Double foodFat) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodCategory = foodCategory;
        this.foodCalorie = foodCalorie;
        this.foodSeverunit = foodSeverunit;
        this.foodServeamount = foodServeamount;
        this.foodFat = foodFat;
    }

    public Short getFoodId() {
        return foodId;
    }

    public void setFoodId(Short foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public Double getFoodCalorie() {
        return foodCalorie;
    }

    public void setFoodCalorie(Double foodCalorie) {
        this.foodCalorie = foodCalorie;
    }

    public String getFoodSeverunit() {
        return foodSeverunit;
    }

    public void setFoodSeverunit(String foodSeverunit) {
        this.foodSeverunit = foodSeverunit;
    }

    public Double getFoodServeamount() {
        return foodServeamount;
    }

    public void setFoodServeamount(Double foodServeamount) {
        this.foodServeamount = foodServeamount;
    }

    public Double getFoodFat() {
        return foodFat;
    }

    public void setFoodFat(Double foodFat) {
        this.foodFat = foodFat;
    }

    Double foodFat;
}
