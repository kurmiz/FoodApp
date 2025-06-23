package com.quickbite.service;

import com.quickbite.model.DeliveryAddress;
import com.quickbite.util.ConsoleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing delivery addresses and delivery operations
 */
@Service
public class DeliveryService {

    private final List<DeliveryAddress> addresses;
    private final AtomicLong idGenerator;
    private final ConsoleLogger logger;

    @Autowired
    public DeliveryService(ConsoleLogger logger) {
        this.addresses = new ArrayList<>();
        this.idGenerator = new AtomicLong(1);
        this.logger = logger;
        
        logger.logSystemEvent("DELIVERY_SERVICE_INITIALIZED", "DeliveryService started with empty ArrayList<DeliveryAddress>");
    }

    /**
     * Add a new delivery address
     */
    public DeliveryAddress addAddress(String fullName, String phoneNumber, String addressLine1, 
                                    String addressLine2, String city, String state, String zipCode,
                                    String addressType, String landmark, String deliveryInstructions) {
        Long id = idGenerator.getAndIncrement();
        DeliveryAddress address = new DeliveryAddress(id, fullName, phoneNumber, addressLine1, city, state, zipCode);
        
        address.setAddressLine2(addressLine2);
        address.setAddressType(addressType);
        address.setLandmark(landmark);
        address.setDeliveryInstructions(deliveryInstructions);
        
        // If this is the first address, make it default
        if (addresses.isEmpty()) {
            address.setDefault(true);
        }
        
        addresses.add(address);
        
        logger.logUserAction("DELIVERY_ADDRESS_ADDED", 
            String.format("Added %s address for %s: %s", addressType, fullName, address.getShortAddress()));
        logAddressesState();
        
        return address;
    }

    /**
     * Update an existing address
     */
    public boolean updateAddress(Long id, String fullName, String phoneNumber, String addressLine1, 
                               String addressLine2, String city, String state, String zipCode,
                               String addressType, String landmark, String deliveryInstructions) {
        Optional<DeliveryAddress> existingAddress = findById(id);
        if (existingAddress.isPresent()) {
            DeliveryAddress address = existingAddress.get();
            
            address.setFullName(fullName);
            address.setPhoneNumber(phoneNumber);
            address.setAddressLine1(addressLine1);
            address.setAddressLine2(addressLine2);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
            address.setAddressType(addressType);
            address.setLandmark(landmark);
            address.setDeliveryInstructions(deliveryInstructions);
            
            logger.logUserAction("DELIVERY_ADDRESS_UPDATED", 
                String.format("Updated address #%d: %s", id, address.getShortAddress()));
            logAddressesState();
            
            return true;
        }
        
        logger.logUserAction("ADDRESS_UPDATE_FAILED", "Address with ID #" + id + " not found");
        return false;
    }

    /**
     * Remove an address
     */
    public boolean removeAddress(Long id) {
        Optional<DeliveryAddress> addressToRemove = findById(id);
        boolean removed = addresses.removeIf(address -> address.getId().equals(id));
        
        if (removed && addressToRemove.isPresent()) {
            DeliveryAddress removedAddress = addressToRemove.get();
            
            // If removed address was default, make another one default
            if (removedAddress.isDefault() && !addresses.isEmpty()) {
                addresses.get(0).setDefault(true);
            }
            
            logger.logUserAction("DELIVERY_ADDRESS_REMOVED", 
                String.format("Removed address: %s", removedAddress.getShortAddress()));
            logAddressesState();
        } else {
            logger.logUserAction("ADDRESS_REMOVAL_FAILED", "Address with ID #" + id + " not found");
        }
        
        return removed;
    }

    /**
     * Set an address as default
     */
    public boolean setDefaultAddress(Long id) {
        Optional<DeliveryAddress> targetAddress = findById(id);
        if (targetAddress.isPresent()) {
            // Remove default from all addresses
            addresses.forEach(address -> address.setDefault(false));
            
            // Set new default
            targetAddress.get().setDefault(true);
            
            logger.logUserAction("DEFAULT_ADDRESS_CHANGED", 
                String.format("Set default address to: %s", targetAddress.get().getShortAddress()));
            logAddressesState();
            
            return true;
        }
        
        logger.logUserAction("DEFAULT_ADDRESS_CHANGE_FAILED", "Address with ID #" + id + " not found");
        return false;
    }

    /**
     * Update address coordinates
     */
    public boolean updateAddressCoordinates(Long id, double latitude, double longitude) {
        Optional<DeliveryAddress> address = findById(id);
        if (address.isPresent()) {
            address.get().setLatitude(latitude);
            address.get().setLongitude(longitude);
            
            logger.logUserAction("ADDRESS_COORDINATES_UPDATED", 
                String.format("Updated coordinates for address #%d: %.6f, %.6f", id, latitude, longitude));
            
            return true;
        }
        return false;
    }

    /**
     * Get all addresses
     */
    public List<DeliveryAddress> getAllAddresses() {
        return new ArrayList<>(addresses);
    }

    /**
     * Get default address
     */
    public Optional<DeliveryAddress> getDefaultAddress() {
        return addresses.stream()
                .filter(DeliveryAddress::isDefault)
                .findFirst();
    }

    /**
     * Find address by ID
     */
    public Optional<DeliveryAddress> findById(Long id) {
        return addresses.stream()
                .filter(address -> address.getId().equals(id))
                .findFirst();
    }

    /**
     * Get addresses by type
     */
    public List<DeliveryAddress> getAddressesByType(String type) {
        return addresses.stream()
                .filter(address -> address.getAddressType().equalsIgnoreCase(type))
                .toList();
    }

    /**
     * Calculate estimated delivery time based on distance
     */
    public int calculateDeliveryTime(DeliveryAddress address) {
        // Simple calculation - in real app, would use actual distance/traffic data
        if (address.hasCoordinates()) {
            // Simulate calculation based on coordinates
            return 25 + (int)(Math.random() * 20); // 25-45 minutes
        }
        return 30; // Default 30 minutes
    }

    /**
     * Calculate delivery fee based on distance
     */
    public double calculateDeliveryFee(DeliveryAddress address) {
        // Simple calculation - in real app, would use actual distance
        if (address.hasCoordinates()) {
            // Simulate distance-based pricing
            double baseFee = 2.99;
            double distanceMultiplier = Math.random() * 2; // 0-2x multiplier
            return Math.round((baseFee + distanceMultiplier) * 100.0) / 100.0;
        }
        return 2.99; // Default fee
    }

    /**
     * Check if delivery is available to an address
     */
    public boolean isDeliveryAvailable(DeliveryAddress address) {
        // Simple check - in real app, would check service area
        return address != null && 
               address.getCity() != null && 
               !address.getCity().trim().isEmpty();
    }

    /**
     * Get total number of addresses
     */
    public int getAddressCount() {
        return addresses.size();
    }

    /**
     * Check if addresses list is empty
     */
    public boolean hasAddresses() {
        return !addresses.isEmpty();
    }

    /**
     * Log current addresses state
     */
    private void logAddressesState() {
        logger.logUserAction("ADDRESSES_STATE", 
            String.format("Total addresses: %d, Default: %s", 
                addresses.size(), 
                getDefaultAddress().map(DeliveryAddress::getShortAddress).orElse("None")));
    }
}
