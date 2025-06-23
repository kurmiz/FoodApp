package com.quickbite.service;

import com.quickbite.model.FoodItem;
import com.quickbite.util.ConsoleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing menu items dynamically with comprehensive logging
 */
@Service
public class MenuService {

    private final List<FoodItem> menuItems;
    private final AtomicLong idGenerator;
    private final ConsoleLogger logger;

    @Autowired
    public MenuService(ConsoleLogger logger) {
        this.menuItems = new ArrayList<>();
        this.idGenerator = new AtomicLong(1);
        this.logger = logger;

        logger.logSystemEvent("MENU_SERVICE_INITIALIZED", "MenuService started with empty ArrayList<FoodItem>");
        logger.logMenuState(menuItems);
    }

    /**
     * Add a new food item to the menu
     */
    public FoodItem addMenuItem(String name, String description, double price, String category) {
        Long id = idGenerator.getAndIncrement();
        FoodItem newItem = new FoodItem(id, name, description, price, category);
        menuItems.add(newItem);

        logger.logMenuOperation("ITEM_ADDED", newItem);
        logger.logMenuState(menuItems);

        return newItem;
    }

    /**
     * Update an existing food item
     */
    public boolean updateMenuItem(Long id, String name, String description, double price, String category) {
        Optional<FoodItem> existingItem = findById(id);
        if (existingItem.isPresent()) {
            FoodItem item = existingItem.get();
            FoodItem oldItem = new FoodItem(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getCategory());

            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategory(category);

            logger.logMenuOperation("ITEM_UPDATED", item);
            logger.logUserAction("MENU_ITEM_MODIFIED", "Updated item #" + id + " from '" + oldItem.getName() + "' to '" + item.getName() + "'");
            logger.logMenuState(menuItems);

            return true;
        }
        logger.logUserAction("MENU_UPDATE_FAILED", "Item with ID #" + id + " not found");
        return false;
    }

    /**
     * Remove a food item from the menu
     */
    public boolean removeMenuItem(Long id) {
        Optional<FoodItem> itemToRemove = findById(id);
        boolean removed = menuItems.removeIf(item -> item.getId().equals(id));

        if (removed && itemToRemove.isPresent()) {
            logger.logMenuOperation("ITEM_REMOVED", itemToRemove.get());
            logger.logMenuState(menuItems);
        } else {
            logger.logUserAction("MENU_REMOVAL_FAILED", "Item with ID #" + id + " not found");
        }

        return removed;
    }

    /**
     * Get all menu items
     */
    public List<FoodItem> getAllMenuItems() {
        return new ArrayList<>(menuItems);
    }

    /**
     * Get menu items by category
     */
    public List<FoodItem> getMenuItemsByCategory(String category) {
        return menuItems.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    /**
     * Get all available categories
     */
    public List<String> getCategories() {
        return menuItems.stream()
                .map(FoodItem::getCategory)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Find food item by ID
     */
    public Optional<FoodItem> findById(Long id) {
        return menuItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    /**
     * Search menu items by name
     */
    public List<FoodItem> searchByName(String searchTerm) {
        return menuItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }

    /**
     * Check if menu is empty
     */
    public boolean isEmpty() {
        return menuItems.isEmpty();
    }

    /**
     * Get total number of menu items
     */
    public int getMenuItemCount() {
        return menuItems.size();
    }
}
