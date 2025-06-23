package com.quickbite.util;

import com.quickbite.model.CartItem;
import com.quickbite.model.FoodItem;
import com.quickbite.model.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for comprehensive console logging of all user operations
 */
@Component
public class ConsoleLogger {

    private static final String SEPARATOR = "=".repeat(80);
    private static final String SUB_SEPARATOR = "-".repeat(40);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logMenuOperation(String operation, FoodItem item) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("🍽️  MENU OPERATION: " + operation);
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        System.out.println(SUB_SEPARATOR);
        if (item != null) {
            System.out.println("📋 Item Details:");
            System.out.println("   ID: " + item.getId());
            System.out.println("   Name: " + item.getName());
            System.out.println("   Description: " + item.getDescription());
            System.out.println("   Category: " + item.getCategory());
            System.out.println("   Price: " + item.getFormattedPrice());
        }
        System.out.println(SEPARATOR + "\n");
    }

    public void logMenuState(List<FoodItem> menuItems) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("📊 CURRENT MENU STATE");
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        System.out.println(SUB_SEPARATOR);
        System.out.println("📈 Total Menu Items: " + menuItems.size());
        
        if (menuItems.isEmpty()) {
            System.out.println("❌ Menu is currently empty");
        } else {
            System.out.println("📋 Menu Items:");
            menuItems.forEach(item -> {
                System.out.printf("   [%d] %s - %s (%s)%n", 
                    item.getId(), item.getName(), item.getFormattedPrice(), item.getCategory());
            });
            
            // Group by category
            System.out.println("\n📂 Items by Category:");
            menuItems.stream()
                .collect(java.util.stream.Collectors.groupingBy(FoodItem::getCategory))
                .forEach((category, items) -> {
                    System.out.println("   " + category + ": " + items.size() + " items");
                });
        }
        System.out.println(SEPARATOR + "\n");
    }

    public void logCartOperation(String operation, CartItem item) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("🛒 CART OPERATION: " + operation);
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        System.out.println(SUB_SEPARATOR);
        if (item != null) {
            System.out.println("🍽️ Item Details:");
            System.out.println("   Name: " + item.getFoodItem().getName());
            System.out.println("   Quantity: " + item.getQuantity());
            System.out.println("   Unit Price: " + item.getFoodItem().getFormattedPrice());
            System.out.println("   Total Price: " + item.getFormattedTotalPrice());
        }
        System.out.println(SEPARATOR + "\n");
    }

    public void logCartState(List<CartItem> cartItems, double totalPrice) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("🛒 CURRENT CART STATE");
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        System.out.println(SUB_SEPARATOR);
        System.out.println("📊 Total Items: " + cartItems.stream().mapToInt(CartItem::getQuantity).sum());
        System.out.println("💰 Total Value: $" + String.format("%.2f", totalPrice));
        
        if (cartItems.isEmpty()) {
            System.out.println("❌ Cart is currently empty");
        } else {
            System.out.println("📋 Cart Contents:");
            cartItems.forEach(item -> {
                System.out.printf("   %s x%d = %s%n", 
                    item.getFoodItem().getName(), 
                    item.getQuantity(), 
                    item.getFormattedTotalPrice());
            });
        }
        System.out.println(SEPARATOR + "\n");
    }

    public void logOrderOperation(String operation, Order order) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("📦 ORDER OPERATION: " + operation);
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        System.out.println(SUB_SEPARATOR);
        if (order != null) {
            System.out.println("📋 Order Details:");
            System.out.println("   Order ID: #" + order.getId());
            System.out.println("   Order Time: " + order.getFormattedOrderTime());
            System.out.println("   Status: " + order.getStatus());
            System.out.println("   Total Items: " + order.getTotalItemCount());
            System.out.println("   Total Amount: " + order.getFormattedTotalAmount());
            System.out.println("   Items:");
            order.getItems().forEach(item -> {
                System.out.printf("     - %s x%d = %s%n", 
                    item.getFoodItem().getName(), 
                    item.getQuantity(), 
                    item.getFormattedTotalPrice());
            });
        }
        System.out.println(SEPARATOR + "\n");
    }

    public void logOrdersState(List<Order> orders, double totalRevenue) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("📊 ORDERS SUMMARY");
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        System.out.println(SUB_SEPARATOR);
        System.out.println("📈 Total Orders: " + orders.size());
        System.out.println("💰 Total Revenue: $" + String.format("%.2f", totalRevenue));
        
        if (orders.isEmpty()) {
            System.out.println("❌ No orders placed yet");
        } else {
            System.out.println("📋 Recent Orders:");
            orders.stream()
                .sorted((o1, o2) -> o2.getOrderTime().compareTo(o1.getOrderTime()))
                .limit(5)
                .forEach(order -> {
                    System.out.printf("   [#%d] %s - %s (%d items)%n", 
                        order.getId(), 
                        order.getFormattedOrderTime(), 
                        order.getFormattedTotalAmount(),
                        order.getTotalItemCount());
                });
            
            if (orders.size() > 5) {
                System.out.println("   ... and " + (orders.size() - 5) + " more orders");
            }
        }
        System.out.println(SEPARATOR + "\n");
    }

    public void logUserAction(String action, String details) {
        System.out.println("\n" + SUB_SEPARATOR);
        System.out.println("👤 USER ACTION: " + action);
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        if (details != null && !details.isEmpty()) {
            System.out.println("📝 Details: " + details);
        }
        System.out.println(SUB_SEPARATOR + "\n");
    }

    public void logSystemEvent(String event, String message) {
        System.out.println("\n🔧 SYSTEM EVENT: " + event);
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        if (message != null && !message.isEmpty()) {
            System.out.println("📝 Message: " + message);
        }
        System.out.println();
    }

    public void logDataSnapshot() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("📸 COMPLETE DATA SNAPSHOT");
        System.out.println("⏰ Time: " + LocalDateTime.now().format(TIME_FORMAT));
        System.out.println("=".repeat(100));
        System.out.println("This snapshot shows the current state of all in-memory data structures:");
        System.out.println("- Menu Items (ArrayList<FoodItem>)");
        System.out.println("- Cart Items (ArrayList<CartItem>)");
        System.out.println("- Orders (ArrayList<Order>)");
        System.out.println("=".repeat(100) + "\n");
    }
}
