# QuickBite CLI Logging System

## Overview
The QuickBite application now includes comprehensive CLI logging that shows all user operations and data storage in real-time. This helps you see exactly how data is being managed in memory.

## What Gets Logged

### 🍽️ Menu Operations
- **ITEM_ADDED**: When new food items are added to the menu
- **ITEM_UPDATED**: When existing items are modified
- **ITEM_REMOVED**: When items are deleted from the menu
- **Menu State**: Current state of the ArrayList<FoodItem>

### 🛒 Cart Operations
- **ITEM_ADDED_TO_CART**: When items are added to cart
- **ITEM_QUANTITY_UPDATED**: When item quantities are changed
- **ITEM_REMOVED_FROM_CART**: When items are removed
- **CART_CLEARED**: When the entire cart is cleared
- **Cart State**: Current state of the ArrayList<CartItem>

### 📦 Order Operations
- **ORDER_CREATED**: When orders are placed
- **ORDER_CREATION_FAILED**: When order creation fails
- **Orders Summary**: Current state of the ArrayList<Order>

### 👤 User Actions
- All user interactions with detailed descriptions
- Navigation between pages
- Form submissions and validations

### 🔧 System Events
- Service initialization
- Data structure creation
- Application startup events

## Sample Log Output

When you start the application, you'll see:

```
================================================================================
🔧 SYSTEM EVENT: MENU_SERVICE_INITIALIZED
⏰ Time: 2024-01-15 10:30:45
📝 Message: MenuService started with empty ArrayList<FoodItem>

================================================================================
📊 CURRENT MENU STATE
⏰ Time: 2024-01-15 10:30:45
----------------------------------------
📈 Total Menu Items: 0
❌ Menu is currently empty
================================================================================

================================================================================
🔧 SYSTEM EVENT: CART_SERVICE_INITIALIZED
⏰ Time: 2024-01-15 10:30:45
📝 Message: CartService started with empty ArrayList<CartItem>

================================================================================
🛒 CURRENT CART STATE
⏰ Time: 2024-01-15 10:30:45
----------------------------------------
📊 Total Items: 0
💰 Total Value: $0.00
❌ Cart is currently empty
================================================================================
```

When you add a menu item:

```
================================================================================
🍽️  MENU OPERATION: ITEM_ADDED
⏰ Time: 2024-01-15 10:35:22
----------------------------------------
📋 Item Details:
   ID: 1
   Name: Margherita Pizza
   Description: Fresh mozzarella, tomatoes, and basil on thin crust
   Category: Main Courses
   Price: $13.99
================================================================================

================================================================================
📊 CURRENT MENU STATE
⏰ Time: 2024-01-15 10:35:22
----------------------------------------
📈 Total Menu Items: 1
📋 Menu Items:
   [1] Margherita Pizza - $13.99 (Main Courses)

📂 Items by Category:
   Main Courses: 1 items
================================================================================
```

When you add items to cart:

```
================================================================================
🛒 CART OPERATION: ITEM_ADDED_TO_CART
⏰ Time: 2024-01-15 10:37:15
----------------------------------------
🍽️ Item Details:
   Name: Margherita Pizza
   Quantity: 1
   Unit Price: $13.99
   Total Price: $13.99
================================================================================

----------------------------------------
👤 USER ACTION: NEW_CART_ITEM
⏰ Time: 2024-01-15 10:37:15
📝 Details: Added Margherita Pizza to cart
----------------------------------------

================================================================================
🛒 CURRENT CART STATE
⏰ Time: 2024-01-15 10:37:15
----------------------------------------
📊 Total Items: 1
💰 Total Value: $13.99
📋 Cart Contents:
   Margherita Pizza x1 = $13.99
================================================================================
```

When you place an order:

```
----------------------------------------
👤 USER ACTION: ORDER_CREATION_STARTED
⏰ Time: 2024-01-15 10:40:33
📝 Details: Creating order from cart with 1 items worth $13.99
----------------------------------------

================================================================================
📦 ORDER OPERATION: ORDER_CREATED
⏰ Time: 2024-01-15 10:40:33
----------------------------------------
📋 Order Details:
   Order ID: #1
   Order Time: Jan 15, 2024 10:40
   Status: CONFIRMED
   Total Items: 1
   Total Amount: $13.99
   Items:
     - Margherita Pizza x1 = $13.99
================================================================================

================================================================================
📊 ORDERS SUMMARY
⏰ Time: 2024-01-15 10:40:33
----------------------------------------
📈 Total Orders: 1
💰 Total Revenue: $13.99
📋 Recent Orders:
   [#1] Jan 15, 2024 10:40 - $13.99 (1 items)
================================================================================
```

## How to See the Logs

1. **Start the Application**: Run `mvn spring-boot:run`
2. **Watch the Console**: All logs appear in the terminal where you started the application
3. **Perform Actions**: 
   - Go to Menu Management and add food items
   - Browse the menu and add items to cart
   - Proceed through checkout
   - View the admin panel

## Data Storage Verification

The logs clearly show:
- **No Mock Data**: All ArrayLists start empty
- **Dynamic Addition**: Items are added through the UI
- **Real-time Updates**: Every operation is logged immediately
- **Memory Storage**: All data is stored in ArrayList collections
- **Data Relationships**: How cart items reference food items, orders contain cart items

## Benefits

1. **Transparency**: See exactly what's happening with your data
2. **Debugging**: Easy to track down issues
3. **Learning**: Understand how the application manages state
4. **Verification**: Confirm that no mock data is being used
5. **Monitoring**: Track user behavior and system performance

## Technical Implementation

- **ConsoleLogger**: Centralized logging utility
- **Service Integration**: All services use the logger
- **Structured Output**: Consistent formatting with timestamps
- **Real-time**: Logs appear immediately when actions occur
- **Comprehensive**: Covers all major operations and state changes
