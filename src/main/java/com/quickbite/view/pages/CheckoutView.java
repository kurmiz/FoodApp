package com.quickbite.view.pages;

import com.quickbite.model.CartItem;
import com.quickbite.model.DeliveryAddress;
import com.quickbite.model.Order;
import com.quickbite.service.CartService;
import com.quickbite.service.DeliveryService;
import com.quickbite.service.OrderService;
import com.quickbite.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Checkout page view - Displays order summary and handles order confirmation
 */
@PageTitle("QuickBite - Checkout")
@Route(value = "checkout", layout = MainLayout.class)
public class CheckoutView extends VerticalLayout implements BeforeEnterObserver {

    private final CartService cartService;
    private final OrderService orderService;
    private Grid<CartItem> orderSummaryGrid;
    private Div totalSection;

    @Autowired
    public CheckoutView(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createHeader();
        createOrderSummary();
        createTotalSection();
        createActionButtons();
        refreshOrderSummary();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Redirect to cart if cart is empty
        if (cartService.isEmpty()) {
            event.forwardTo(CartView.class);
        }
    }

    private void createHeader() {
        H1 title = new H1("💳 Checkout");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);
        
        Paragraph subtitle = new Paragraph("Review your order before confirming");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY);
        
        add(title, subtitle);
    }

    private void createOrderSummary() {
        H2 summaryTitle = new H2("Order Summary");
        summaryTitle.addClassNames(LumoUtility.TextColor.PRIMARY);
        
        orderSummaryGrid = new Grid<>(CartItem.class, false);
        orderSummaryGrid.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.MEDIUM);
        
        // Item name column
        orderSummaryGrid.addColumn(cartItem -> cartItem.getFoodItem().getName())
                .setHeader("Item")
                .setFlexGrow(2);

        // Unit price column
        orderSummaryGrid.addColumn(cartItem -> cartItem.getFoodItem().getFormattedPrice())
                .setHeader("Unit Price")
                .setFlexGrow(1);

        // Quantity column
        orderSummaryGrid.addColumn(CartItem::getQuantity)
                .setHeader("Quantity")
                .setFlexGrow(1);

        // Total price column
        orderSummaryGrid.addColumn(CartItem::getFormattedTotalPrice)
                .setHeader("Total")
                .setFlexGrow(1);

        orderSummaryGrid.setHeight("300px");
        
        add(summaryTitle, orderSummaryGrid);
    }

    private void createTotalSection() {
        totalSection = new Div();
        totalSection.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.LARGE,
            LumoUtility.Margin.Vertical.MEDIUM
        );
        
        add(totalSection);
    }

    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonLayout.setWidthFull();

        Button backToCartButton = new Button("Back to Cart", new Icon(VaadinIcon.ARROW_LEFT));
        backToCartButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backToCartButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(CartView.class)));

        Button confirmOrderButton = new Button("Confirm Order", new Icon(VaadinIcon.CHECK));
        confirmOrderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        confirmOrderButton.addClickListener(e -> confirmOrder());

        buttonLayout.add(backToCartButton, confirmOrderButton);
        add(buttonLayout);
    }

    private void refreshOrderSummary() {
        orderSummaryGrid.setItems(cartService.getCartItems());
        updateTotalSection();
    }

    private void updateTotalSection() {
        totalSection.removeAll();
        
        VerticalLayout totalLayout = new VerticalLayout();
        totalLayout.setPadding(false);
        totalLayout.setSpacing(false);

        // Item count
        HorizontalLayout itemCountLayout = new HorizontalLayout();
        itemCountLayout.setWidthFull();
        itemCountLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        
        Span itemCountLabel = new Span("Total Items:");
        Span itemCount = new Span(String.valueOf(cartService.getTotalItemCount()));
        itemCountLayout.add(itemCountLabel, itemCount);

        // Total amount
        HorizontalLayout totalAmountLayout = new HorizontalLayout();
        totalAmountLayout.setWidthFull();
        totalAmountLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        totalAmountLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 totalLabel = new H3("Total Amount:");
        totalLabel.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.PRIMARY);

        Span totalAmount = new Span(cartService.getFormattedTotalPrice());
        totalAmount.addClassNames(
            LumoUtility.FontSize.XLARGE,
            LumoUtility.FontWeight.BOLD,
            LumoUtility.TextColor.SUCCESS
        );

        totalAmountLayout.add(totalLabel, totalAmount);

        totalLayout.add(itemCountLayout, totalAmountLayout);
        totalSection.add(totalLayout);
    }

    private void confirmOrder() {
        try {
            Order order = orderService.createOrderFromCart();
            showOrderConfirmationDialog(order);
            
            // Update cart badge in main layout
            if (getParent().isPresent() && getParent().get() instanceof MainLayout mainLayout) {
                mainLayout.updateCartBadge();
            }
            
        } catch (Exception e) {
            Notification notification = Notification.show(
                "Error creating order: " + e.getMessage(),
                5000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showOrderConfirmationDialog(Order order) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setModal(true);
        confirmationDialog.setDraggable(false);
        confirmationDialog.setResizable(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Success icon and message
        Icon successIcon = new Icon(VaadinIcon.CHECK_CIRCLE);
        successIcon.addClassNames(LumoUtility.IconSize.LARGE, LumoUtility.TextColor.SUCCESS);

        H2 successTitle = new H2("Order Confirmed!");
        successTitle.addClassNames(LumoUtility.TextColor.SUCCESS, LumoUtility.Margin.Bottom.MEDIUM);

        Paragraph thankYouMessage = new Paragraph("Thank you for your order! Your delicious food will be prepared shortly.");
        thankYouMessage.getStyle().set("text-align", "center");

        // Order details
        Div orderDetails = new Div();
        orderDetails.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.Margin.Vertical.MEDIUM
        );

        Paragraph orderIdText = new Paragraph("Order ID: #" + order.getId());
        orderIdText.addClassNames(LumoUtility.FontWeight.BOLD);

        Paragraph orderTimeText = new Paragraph("Order Time: " + order.getFormattedOrderTime());
        Paragraph totalAmountText = new Paragraph("Total Amount: " + order.getFormattedTotalAmount());
        totalAmountText.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SUCCESS);

        orderDetails.add(orderIdText, orderTimeText, totalAmountText);

        // Action buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button continueShoppingButton = new Button("Continue Shopping", new Icon(VaadinIcon.SHOP));
        continueShoppingButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        continueShoppingButton.addClickListener(e -> {
            confirmationDialog.close();
            getUI().ifPresent(ui -> ui.navigate(MenuView.class));
        });

        Button viewOrdersButton = new Button("View Orders", new Icon(VaadinIcon.LIST));
        viewOrdersButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        viewOrdersButton.addClickListener(e -> {
            confirmationDialog.close();
            getUI().ifPresent(ui -> ui.navigate(AdminView.class));
        });

        buttonLayout.add(continueShoppingButton, viewOrdersButton);

        dialogLayout.add(successIcon, successTitle, thankYouMessage, orderDetails, buttonLayout);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }
}
