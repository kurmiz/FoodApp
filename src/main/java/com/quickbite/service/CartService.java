package com.quickbite.service;

import com.quickbite.model.CartItem;
import com.quickbite.model.FoodItem;
import com.quickbite.util.ConsoleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing shopping cart operations with comprehensive logging
 */
@Service
public class CartService {

    private final List<CartItem> cartItems;
    private final ConsoleLogger logger;

    @Autowired
    public CartService(ConsoleLogger logger) {
        this.cartItems = new ArrayList<>();
        this.logger = logger;

        logger.logSystemEvent("CART_SERVICE_INITIALIZED", "CartService started with empty ArrayList<CartItem>");
        logger.logCartState(cartItems, 0.0);
    }

    /**
     * Add item to cart
     */
    public void addToCart(FoodItem foodItem) {
        addToCart(foodItem, 1);
    }

    /**
     * Add item to cart with specified quantity
     */
    public void addToCart(FoodItem foodItem, int quantity) {
        Optional<CartItem> existingItem = findCartItemByFoodItem(foodItem);

        if (existingItem.isPresent()) {
            // If item already exists, increase quantity
            CartItem item = existingItem.get();
            int oldQuantity = item.getQuantity();
            item.setQuantity(item.getQuantity() + quantity);

            logger.logCartOperation("ITEM_QUANTITY_UPDATED", item);
            logger.logUserAction("CART_ITEM_UPDATED",
                String.format("Updated %s quantity from %d to %d", foodItem.getName(), oldQuantity, item.getQuantity()));
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem(foodItem, quantity);
            cartItems.add(newItem);

            logger.logCartOperation("ITEM_ADDED_TO_CART", newItem);
            logger.logUserAction("NEW_CART_ITEM", "Added " + foodItem.getName() + " to cart");
        }

        logger.logCartState(cartItems, getTotalPrice());
    }

    /**
     * Remove item from cart
     */
    public boolean removeFromCart(FoodItem foodItem) {
        Optional<CartItem> itemToRemove = findCartItemByFoodItem(foodItem);
        boolean removed = cartItems.removeIf(item -> item.getFoodItem().equals(foodItem));

        if (removed && itemToRemove.isPresent()) {
            logger.logCartOperation("ITEM_REMOVED_FROM_CART", itemToRemove.get());
            logger.logUserAction("CART_ITEM_REMOVED", "Removed " + foodItem.getName() + " from cart");
            logger.logCartState(cartItems, getTotalPrice());
        } else {
            logger.logUserAction("CART_REMOVAL_FAILED", "Item " + foodItem.getName() + " not found in cart");
        }

        return removed;
    }

    /**
     * Remove cart item by index
     */
    public boolean removeFromCart(int index) {
        if (index >= 0 && index < cartItems.size()) {
            CartItem removedItem = cartItems.get(index);
            cartItems.remove(index);

            logger.logCartOperation("ITEM_REMOVED_BY_INDEX", removedItem);
            logger.logUserAction("CART_ITEM_REMOVED_BY_INDEX", "Removed item at index " + index);
            logger.logCartState(cartItems, getTotalPrice());

            return true;
        }
        logger.logUserAction("CART_REMOVAL_FAILED", "Invalid index: " + index);
        return false;
    }

    /**
     * Update quantity of an item in cart
     */
    public boolean updateQuantity(FoodItem foodItem, int newQuantity) {
        if (newQuantity <= 0) {
            return removeFromCart(foodItem);
        }
        
        Optional<CartItem> existingItem = findCartItemByFoodItem(foodItem);
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    /**
     * Get all cart items
     */
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    /**
     * Get total number of items in cart
     */
    public int getTotalItemCount() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Calculate total price of all items in cart
     */
    public double getTotalPrice() {
        return cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    /**
     * Get formatted total price
     */
    public String getFormattedTotalPrice() {
        return String.format("$%.2f", getTotalPrice());
    }

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    /**
     * Clear all items from cart
     */
    public void clearCart() {
        int itemCount = cartItems.size();
        double totalValue = getTotalPrice();
        cartItems.clear();

        logger.logUserAction("CART_CLEARED", String.format("Cleared %d items worth %s", itemCount, String.format("$%.2f", totalValue)));
        logger.logCartState(cartItems, 0.0);
    }

    /**
     * Find cart item by food item
     */
    private Optional<CartItem> findCartItemByFoodItem(FoodItem foodItem) {
        return cartItems.stream()
                .filter(item -> item.getFoodItem().equals(foodItem))
                .findFirst();
    }

    /**
     * Check if food item is already in cart
     */
    public boolean isInCart(FoodItem foodItem) {
        return findCartItemByFoodItem(foodItem).isPresent();
    }

    /**
     * Get quantity of specific food item in cart
     */
    public int getQuantityInCart(FoodItem foodItem) {
        return findCartItemByFoodItem(foodItem)
                .map(CartItem::getQuantity)
                .orElse(0);
    }
}
