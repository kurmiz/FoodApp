package com.quickbite.model;

import java.util.Objects;

/**
 * Represents an item in the shopping cart
 */
public class CartItem {
    private FoodItem foodItem;
    private int quantity;

    // Default constructor
    public CartItem() {}

    // Constructor
    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    // Constructor with default quantity of 1
    public CartItem(FoodItem foodItem) {
        this(foodItem, 1);
    }

    // Getters and Setters
    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Utility methods
    public double getTotalPrice() {
        return foodItem != null ? foodItem.getPrice() * quantity : 0.0;
    }

    public String getFormattedTotalPrice() {
        return String.format("$%.2f", getTotalPrice());
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(foodItem, cartItem.foodItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodItem);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "foodItem=" + foodItem +
                ", quantity=" + quantity +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
