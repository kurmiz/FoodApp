package com.quickbite.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a completed order
 */
public class Order {
    private Long id;
    private List<CartItem> items;
    private double totalAmount;
    private LocalDateTime orderTime;
    private String status;
    private DeliveryAddress deliveryAddress;
    private String deliveryType; // DELIVERY, PICKUP
    private LocalDateTime estimatedDeliveryTime;
    private double deliveryFee;
    private double subtotal;
    private double taxes;
    private String paymentMethod;
    private String specialInstructions;

    // Default constructor
    public Order() {
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.status = "CONFIRMED";
        this.deliveryType = "DELIVERY";
        this.deliveryFee = 2.99;
        this.taxes = 0.0;
        this.paymentMethod = "CASH_ON_DELIVERY";
    }

    // Constructor
    public Order(Long id, List<CartItem> items) {
        this();
        this.id = id;
        this.items = new ArrayList<>(items);
        this.subtotal = calculateSubtotal();
        this.taxes = calculateTaxes();
        this.totalAmount = calculateTotalAmount();
        this.estimatedDeliveryTime = orderTime.plusMinutes(30); // Default 30 minutes
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        this.totalAmount = calculateTotalAmount();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    // Utility methods
    private double calculateSubtotal() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    private double calculateTaxes() {
        return calculateSubtotal() * 0.08; // 8% tax
    }

    private double calculateTotalAmount() {
        return calculateSubtotal() + calculateTaxes() + deliveryFee;
    }

    public String getFormattedTotalAmount() {
        return String.format("$%.2f", totalAmount);
    }

    public String getFormattedSubtotal() {
        return String.format("$%.2f", subtotal);
    }

    public String getFormattedTaxes() {
        return String.format("$%.2f", taxes);
    }

    public String getFormattedDeliveryFee() {
        return String.format("$%.2f", deliveryFee);
    }

    public String getFormattedOrderTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return orderTime.format(formatter);
    }

    public String getFormattedEstimatedDeliveryTime() {
        if (estimatedDeliveryTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return estimatedDeliveryTime.format(formatter);
    }

    public String getDeliveryTimeRange() {
        if (estimatedDeliveryTime == null) return "30-45 mins";
        LocalDateTime start = estimatedDeliveryTime.minusMinutes(5);
        LocalDateTime end = estimatedDeliveryTime.plusMinutes(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return start.format(formatter) + " - " + end.format(formatter);
    }

    public int getTotalItemCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", itemCount=" + items.size() +
                ", totalAmount=" + totalAmount +
                ", orderTime=" + orderTime +
                ", status='" + status + '\'' +
                '}';
    }
}
