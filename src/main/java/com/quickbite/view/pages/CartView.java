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
        H1 title = new H1("🛒 Your Cart");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);
        
        add(title);
    }

    private void createCartGrid() {
        cartGrid = new Grid<>(CartItem.class, false);
        cartGrid.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.MEDIUM);
        
        // Item name column
        cartGrid.addColumn(cartItem -> cartItem.getFoodItem().getName())
                .setHeader("Item")
                .setFlexGrow(2);

        // Description column
        cartGrid.addColumn(cartItem -> cartItem.getFoodItem().getDescription())
                .setHeader("Description")
                .setFlexGrow(3);

        // Unit price column
        cartGrid.addColumn(cartItem -> cartItem.getFoodItem().getFormattedPrice())
                .setHeader("Unit Price")
                .setFlexGrow(1);

        // Quantity column
        cartGrid.addColumn(CartItem::getQuantity)
                .setHeader("Quantity")
                .setFlexGrow(1);

        // Total price column
        cartGrid.addColumn(CartItem::getFormattedTotalPrice)
                .setHeader("Total")
                .setFlexGrow(1);

        // Actions column
        cartGrid.addColumn(new ComponentRenderer<HorizontalLayout, CartItem>(cartItem -> createActionButtons(cartItem)))
                .setHeader("Actions")
                .setFlexGrow(1);

        cartGrid.setHeight("400px");
        add(cartGrid);
    }

    private HorizontalLayout createActionButtons(CartItem cartItem) {
        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        removeButton.getElement().setAttribute("aria-label", "Remove item");
        removeButton.addClickListener(e -> removeFromCart(cartItem));

        HorizontalLayout actions = new HorizontalLayout(removeButton);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        return actions;
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
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        Button continueShoppingButton = new Button("Continue Shopping", new Icon(VaadinIcon.ARROW_LEFT));
        continueShoppingButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        continueShoppingButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(MenuView.class)));

        Button clearCartButton = new Button("Clear Cart", new Icon(VaadinIcon.TRASH));
        clearCartButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearCartButton.addClickListener(e -> clearCart());

        checkoutButton = new Button("Proceed to Checkout", new Icon(VaadinIcon.ARROW_RIGHT));
        checkoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
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
            Paragraph emptyMessage = new Paragraph("Your cart is empty. Start by browsing our menu!");
            emptyMessage.addClassNames(LumoUtility.TextColor.SECONDARY);
            emptyMessage.getStyle().set("text-align", "center");
            totalSection.add(emptyMessage);
        } else {
            HorizontalLayout totalLayout = new HorizontalLayout();
            totalLayout.setWidthFull();
            totalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            totalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            H2 totalLabel = new H2("Total:");
            totalLabel.addClassNames(LumoUtility.Margin.NONE);

            Span totalAmount = new Span(cartService.getFormattedTotalPrice());
            totalAmount.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.SUCCESS
            );

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
