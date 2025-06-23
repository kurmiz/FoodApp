package com.quickbite.service;

import com.quickbite.model.CartItem;
import com.quickbite.model.Order;
import com.quickbite.util.ConsoleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing orders with comprehensive logging
 */
@Service
public class OrderService {

    private final List<Order> orders;
    private final AtomicLong orderIdGenerator;
    private final CartService cartService;
    private final ConsoleLogger logger;

    @Autowired
    public OrderService(CartService cartService, ConsoleLogger logger) {
        this.orders = new ArrayList<>();
        this.orderIdGenerator = new AtomicLong(1);
        this.cartService = cartService;
        this.logger = logger;

        logger.logSystemEvent("ORDER_SERVICE_INITIALIZED", "OrderService started with empty ArrayList<Order>");
        logger.logOrdersState(orders, 0.0);
    }

    /**
     * Create order from current cart
     */
    public Order createOrderFromCart() {
        if (cartService.isEmpty()) {
            logger.logUserAction("ORDER_CREATION_FAILED", "Cannot create order from empty cart");
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        List<CartItem> cartItems = cartService.getCartItems();
        Long orderId = orderIdGenerator.getAndIncrement();

        logger.logUserAction("ORDER_CREATION_STARTED",
            String.format("Creating order from cart with %d items worth %s",
                cartService.getTotalItemCount(), cartService.getFormattedTotalPrice()));

        Order order = new Order(orderId, cartItems);
        orders.add(order);

        logger.logOrderOperation("ORDER_CREATED", order);
        logger.logOrdersState(orders, getTotalRevenue());

        // Clear the cart after creating order
        cartService.clearCart();

        return order;
    }

    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(Long id) {
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst();
    }

    /**
     * Get total number of orders
     */
    public int getTotalOrderCount() {
        return orders.size();
    }

    /**
     * Get total revenue from all orders
     */
    public double getTotalRevenue() {
        return orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    /**
     * Get formatted total revenue
     */
    public String getFormattedTotalRevenue() {
        return String.format("$%.2f", getTotalRevenue());
    }

    /**
     * Get orders sorted by order time (newest first)
     */
    public List<Order> getOrdersSortedByTime() {
        return orders.stream()
                .sorted((o1, o2) -> o2.getOrderTime().compareTo(o1.getOrderTime()))
                .toList();
    }

    /**
     * Update order status
     */
    public boolean updateOrderStatus(Long orderId, String status) {
        Optional<Order> order = getOrderById(orderId);
        if (order.isPresent()) {
            order.get().setStatus(status);
            return true;
        }
        return false;
    }

    /**
     * Cancel order (remove from orders list)
     */
    public boolean cancelOrder(Long orderId) {
        return orders.removeIf(order -> order.getId().equals(orderId));
    }

    /**
     * Get recent orders (last 10)
     */
    public List<Order> getRecentOrders() {
        return getOrdersSortedByTime().stream()
                .limit(10)
                .toList();
    }

    /**
     * Check if there are any orders
     */
    public boolean hasOrders() {
        return !orders.isEmpty();
    }

    /**
     * Get average order value
     */
    public double getAverageOrderValue() {
        if (orders.isEmpty()) {
            return 0.0;
        }
        return getTotalRevenue() / orders.size();
    }

    /**
     * Get formatted average order value
     */
    public String getFormattedAverageOrderValue() {
        return String.format("$%.2f", getAverageOrderValue());
    }
}
