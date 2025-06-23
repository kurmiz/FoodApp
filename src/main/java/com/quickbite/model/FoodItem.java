package com.quickbite.model;

import java.util.Objects;

/**
 * Represents a food item in the restaurant menu
 */
public class FoodItem {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;
    private String imageFileName;
    private double rating;
    private int reviewCount;
    private boolean isVegetarian;
    private boolean isVegan;
    private boolean isSpicy;
    private String preparationTime;
    private boolean isPopular;
    private double originalPrice; // For discount display
    private String tags; // Comma-separated tags

    // Default constructor
    public FoodItem() {}

    // Constructor with essential fields
    public FoodItem(Long id, String name, String description, double price, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.rating = 4.0; // Default rating
        this.reviewCount = 0;
        this.isVegetarian = false;
        this.isVegan = false;
        this.isSpicy = false;
        this.preparationTime = "20-30 mins";
        this.isPopular = false;
        this.originalPrice = price;
    }

    // Constructor with image
    public FoodItem(Long id, String name, String description, double price, String category, String imageUrl) {
        this(id, name, description, price, category);
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public boolean isVegan() {
        return isVegan;
    }

    public void setVegan(boolean vegan) {
        isVegan = vegan;
    }

    public boolean isSpicy() {
        return isSpicy;
    }

    public void setSpicy(boolean spicy) {
        isSpicy = spicy;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    // Utility methods
    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getFormattedOriginalPrice() {
        return String.format("$%.2f", originalPrice);
    }

    public boolean hasDiscount() {
        return originalPrice > price;
    }

    public int getDiscountPercentage() {
        if (!hasDiscount()) return 0;
        return (int) Math.round(((originalPrice - price) / originalPrice) * 100);
    }

    public String getFormattedRating() {
        return String.format("%.1f", rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return Objects.equals(id, foodItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }
}
