package com.quickbite.view.pages;

import com.quickbite.model.CartItem;
import com.quickbite.service.CartService;
import com.quickbite.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Cart page view - Shows cart items with remove functionality and checkout option
 */
@PageTitle("QuickBite - Cart")
@Route(value = "cart", layout = MainLayout.class)
public class CartView extends VerticalLayout {

    private final CartService cartService;
    private Grid<CartItem> cartGrid;
    private Div totalSection;
    private Button checkoutButton;

    @Autowired
    public CartView(CartService cartService) {
        this.cartService = cartService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createHeader();
        createCartGrid();
        createTotalSection();
        createActionButtons();
        refreshCart();
    }

    private void createHeader() {
        // Professional cart header with red gradient
        Div headerSection = new Div();
        headerSection.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("padding", "24px")
            .set("border-radius", "16px")
            .set("margin-bottom", "24px")
            .set("box-shadow", "0 8px 32px rgba(226, 55, 68, 0.3)")
            .set("text-align", "center");

        H1 title = new H1("🛒 Your Cart");
        title.getStyle()
            .set("color", "white")
            .set("font-size", "2.2rem")
            .set("font-weight", "700")
            .set("margin", "0")
            .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.3)");

        Paragraph subtitle = new Paragraph("🎨 PROFESSIONAL CART • Review your delicious selections");
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.9)")
            .set("font-size", "1.1rem")
            .set("margin", "8px 0 0 0")
            .set("font-weight", "400");

        headerSection.add(title, subtitle);
        add(headerSection);
    }

    private void createCartGrid() {
        cartGrid = new Grid<>(CartItem.class, false);
        cartGrid.addClassNames(LumoUtility.BorderRadius.LARGE);

        // Apply professional grid styling
        cartGrid.getStyle()
            .set("border", "2px solid #e23744")
            .set("border-radius", "16px")
            .set("overflow", "hidden")
            .set("box-shadow", "0 4px 20px rgba(226, 55, 68, 0.15)");

        // Item name column with enhanced styling
        cartGrid.addColumn(cartItem -> cartItem.getFoodItem().getName())
                .setHeader("🍽️ Item")
                .setFlexGrow(2);

        // Description column
        cartGrid.addColumn(cartItem -> cartItem.getFoodItem().getDescription())
                .setHeader("📝 Description")
                .setFlexGrow(3);

        // Unit price column with currency styling
        cartGrid.addColumn(cartItem -> cartItem.getFoodItem().getFormattedPrice())
                .setHeader("💰 Unit Price")
                .setFlexGrow(1);

        // Quantity column with badge styling
        cartGrid.addColumn(CartItem::getQuantity)
                .setHeader("📊 Quantity")
                .setFlexGrow(1);

        // Total price column with emphasis
        cartGrid.addColumn(CartItem::getFormattedTotalPrice)
                .setHeader("💳 Total")
                .setFlexGrow(1);

        // Actions column with red-themed buttons
        cartGrid.addColumn(new ComponentRenderer<HorizontalLayout, CartItem>(cartItem -> createActionButtons(cartItem)))
                .setHeader("⚡ Actions")
                .setFlexGrow(1);

        cartGrid.setHeight("450px");

        // Style the grid headers with professional theme
        cartGrid.getElement().executeJs(
            "this.shadowRoot.querySelector('thead').style.background = 'linear-gradient(135deg, #e23744 0%, #ff6b35 100%)';" +
            "this.shadowRoot.querySelector('thead').style.color = 'white';" +
            "this.shadowRoot.querySelectorAll('th').forEach(th => {" +
            "  th.style.fontWeight = '600';" +
            "  th.style.fontSize = '14px';" +
            "  th.style.textShadow = '1px 1px 2px rgba(0,0,0,0.3)';" +
            "});"
        );

        add(cartGrid);
    }

    private HorizontalLayout createActionButtons(CartItem cartItem) {
        // Professional remove button with red gradient
        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        removeButton.getElement().setAttribute("aria-label", "Remove item");
        removeButton.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "8px")
            .set("padding", "8px 12px")
            .set("transition", "all 0.3s ease")
            .set("box-shadow", "0 2px 8px rgba(226, 55, 68, 0.3)");

        // Add hover effect
        removeButton.getElement().addEventListener("mouseenter", e ->
            removeButton.getStyle()
                .set("background", "linear-gradient(135deg, #cb1b28 0%, #e23744 100%)")
                .set("transform", "translateY(-2px)")
                .set("box-shadow", "0 4px 12px rgba(226, 55, 68, 0.4)"));
        removeButton.getElement().addEventListener("mouseleave", e ->
            removeButton.getStyle()
                .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
                .remove("transform")
                .set("box-shadow", "0 2px 8px rgba(226, 55, 68, 0.3)"));

        removeButton.addClickListener(e -> removeFromCart(cartItem));

        HorizontalLayout actions = new HorizontalLayout(removeButton);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        return actions;
    }

    private void createTotalSection() {
        totalSection = new Div();
        totalSection.addClassNames(
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE,
            LumoUtility.Margin.Vertical.MEDIUM
        );

        // Apply professional total section styling
        totalSection.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 8px 32px rgba(226, 55, 68, 0.3)")
            .set("border", "3px solid rgba(255,255,255,0.2)");

        add(totalSection);
    }

    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonLayout.setWidthFull();
        buttonLayout.getStyle()
            .set("margin-top", "24px")
            .set("gap", "16px");

        // Continue Shopping Button - Outline style
        Button continueShoppingButton = new Button("Continue Shopping", new Icon(VaadinIcon.ARROW_LEFT));
        continueShoppingButton.getStyle()
            .set("background", "transparent")
            .set("color", "#e23744")
            .set("border", "2px solid #e23744")
            .set("border-radius", "12px")
            .set("padding", "12px 24px")
            .set("font-weight", "600")
            .set("transition", "all 0.3s ease");
        continueShoppingButton.getElement().addEventListener("mouseenter", e ->
            continueShoppingButton.getStyle()
                .set("background", "#e23744")
                .set("color", "white")
                .set("transform", "translateY(-2px)"));
        continueShoppingButton.getElement().addEventListener("mouseleave", e ->
            continueShoppingButton.getStyle()
                .set("background", "transparent")
                .set("color", "#e23744")
                .remove("transform"));
        continueShoppingButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(MenuView.class)));

        // Clear Cart Button - Secondary style
        Button clearCartButton = new Button("Clear Cart", new Icon(VaadinIcon.TRASH));
        clearCartButton.getStyle()
            .set("background", "linear-gradient(135deg, #6c757d 0%, #495057 100%)")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "12px")
            .set("padding", "12px 24px")
            .set("font-weight", "600")
            .set("transition", "all 0.3s ease");
        clearCartButton.getElement().addEventListener("mouseenter", e ->
            clearCartButton.getStyle()
                .set("background", "linear-gradient(135deg, #495057 0%, #343a40 100%)")
                .set("transform", "translateY(-2px)"));
        clearCartButton.getElement().addEventListener("mouseleave", e ->
            clearCartButton.getStyle()
                .set("background", "linear-gradient(135deg, #6c757d 0%, #495057 100%)")
                .remove("transform"));
        clearCartButton.addClickListener(e -> clearCart());

        // Checkout Button - Primary Zomato style
        checkoutButton = new Button("Proceed to Checkout", new Icon(VaadinIcon.ARROW_RIGHT));
        checkoutButton.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "12px")
            .set("padding", "16px 32px")
            .set("font-weight", "700")
            .set("font-size", "16px")
            .set("text-transform", "uppercase")
            .set("letter-spacing", "0.5px")
            .set("transition", "all 0.3s ease")
            .set("box-shadow", "0 4px 15px rgba(226, 55, 68, 0.4)");
        checkoutButton.getElement().addEventListener("mouseenter", e ->
            checkoutButton.getStyle()
                .set("background", "linear-gradient(135deg, #cb1b28 0%, #e23744 100%)")
                .set("transform", "translateY(-3px)")
                .set("box-shadow", "0 6px 20px rgba(226, 55, 68, 0.5)"));
        checkoutButton.getElement().addEventListener("mouseleave", e ->
            checkoutButton.getStyle()
                .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
                .remove("transform")
                .set("box-shadow", "0 4px 15px rgba(226, 55, 68, 0.4)"));
        checkoutButton.addClickListener(e -> proceedToCheckout());

        buttonLayout.add(continueShoppingButton, clearCartButton, checkoutButton);
        add(buttonLayout);
    }

    private void refreshCart() {
        cartGrid.setItems(cartService.getCartItems());
        updateTotalSection();
        updateCheckoutButton();
        
        // Update cart badge in main layout
        if (getParent().isPresent() && getParent().get() instanceof MainLayout mainLayout) {
            mainLayout.updateCartBadge();
        }
    }

    private void updateTotalSection() {
        totalSection.removeAll();

        if (cartService.isEmpty()) {
            // Empty cart state with professional styling
            totalSection.getStyle()
                .set("background", "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)")
                .set("color", "#6c757d")
                .set("border", "2px dashed #e23744");

            Div emptyStateContent = new Div();
            emptyStateContent.getStyle().set("text-align", "center");

            Icon emptyIcon = new Icon(VaadinIcon.CART);
            emptyIcon.getStyle()
                .set("font-size", "3rem")
                .set("color", "#e23744")
                .set("margin-bottom", "16px");

            H2 emptyTitle = new H2("Your cart is empty");
            emptyTitle.getStyle()
                .set("color", "#2c3e50")
                .set("font-weight", "600")
                .set("margin", "0 0 8px 0");

            Paragraph emptyMessage = new Paragraph("Start by browsing our delicious menu!");
            emptyMessage.getStyle()
                .set("color", "#6c757d")
                .set("margin", "0")
                .set("font-size", "1.1rem");

            emptyStateContent.add(emptyIcon, emptyTitle, emptyMessage);
            totalSection.add(emptyStateContent);
        } else {
            // Cart total with professional red gradient styling
            totalSection.getStyle()
                .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
                .set("color", "white")
                .set("border", "3px solid rgba(255,255,255,0.2)");

            HorizontalLayout totalLayout = new HorizontalLayout();
            totalLayout.setWidthFull();
            totalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            totalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            H2 totalLabel = new H2("💳 Cart Total:");
            totalLabel.getStyle()
                .set("color", "white")
                .set("font-weight", "600")
                .set("margin", "0")
                .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.3)");

            Span totalAmount = new Span(cartService.getFormattedTotalPrice());
            totalAmount.getStyle()
                .set("font-size", "2rem")
                .set("font-weight", "700")
                .set("color", "white")
                .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.3)")
                .set("background", "rgba(255,255,255,0.1)")
                .set("padding", "8px 16px")
                .set("border-radius", "8px");

            totalLayout.add(totalLabel, totalAmount);
            totalSection.add(totalLayout);
        }
    }

    private void updateCheckoutButton() {
        checkoutButton.setEnabled(!cartService.isEmpty());
    }

    private void removeFromCart(CartItem cartItem) {
        cartService.removeFromCart(cartItem.getFoodItem());
        refreshCart();
        
        Notification notification = Notification.show(
            cartItem.getFoodItem().getName() + " removed from cart",
            3000,
            Notification.Position.TOP_END
        );
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    private void clearCart() {
        cartService.clearCart();
        refreshCart();
        
        Notification notification = Notification.show(
            "Cart cleared",
            3000,
            Notification.Position.TOP_END
        );
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    private void proceedToCheckout() {
        if (!cartService.isEmpty()) {
            getUI().ifPresent(ui -> ui.navigate(CheckoutView.class));
        }
    }
}
