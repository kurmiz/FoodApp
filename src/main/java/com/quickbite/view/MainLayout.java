package com.quickbite.view;

import com.quickbite.service.CartService;
import com.quickbite.view.pages.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Main layout for the QuickBite application
 * Provides navigation and consistent header/sidebar across all pages
 */
public class MainLayout extends AppLayout {

    private final CartService cartService;
    private Span cartBadge;

    @Autowired
    public MainLayout(CartService cartService) {
        this.cartService = cartService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("🍕 QuickBite");
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        );

        // Cart button with badge
        Div cartButton = createCartButton();

        HorizontalLayout header = new HorizontalLayout(
            new DrawerToggle(),
            logo,
            cartButton
        );
        
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM
        );

        addToNavbar(header);
    }

    private Div createCartButton() {
        cartBadge = new Span();
        cartBadge.getElement().getThemeList().add("badge error");
        cartBadge.getStyle().set("position", "absolute")
                .set("top", "-8px")
                .set("right", "-8px")
                .set("min-width", "16px")
                .set("height", "16px")
                .set("border-radius", "50%")
                .set("background", "var(--lumo-error-color)")
                .set("color", "white")
                .set("font-size", "10px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        Button cartButton = new Button(new Icon(VaadinIcon.CART));
        cartButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        cartButton.addClickListener(e -> cartButton.getUI().ifPresent(ui ->
            ui.navigate(CartView.class)));

        // Create a wrapper div for the button and badge
        Div cartWrapper = new Div();
        cartWrapper.getStyle().set("position", "relative");
        cartWrapper.add(cartButton, cartBadge);

        updateCartBadge();
        return cartWrapper;
    }

    private void createDrawer() {
        VerticalLayout navigation = new VerticalLayout();
        navigation.setSizeFull();
        navigation.setPadding(false);
        navigation.setSpacing(false);

        // Navigation links
        navigation.add(
            createNavLink("🏠 Home", HomeView.class, VaadinIcon.HOME),
            createNavLink("📋 Menu", MenuView.class, VaadinIcon.MENU),
            createNavLink("🛒 Cart", CartView.class, VaadinIcon.CART),
            createNavLink("📍 Delivery Addresses", DeliveryAddressView.class, VaadinIcon.LOCATION_ARROW),
            createNavLink("⚙️ Menu Management", MenuManagementView.class, VaadinIcon.COG),
            createNavLink("👨‍💼 Admin", AdminView.class, VaadinIcon.USER_STAR)
        );

        addToDrawer(navigation);
    }

    private RouterLink createNavLink(String text, Class<?> navigationTarget, VaadinIcon icon) {
        RouterLink link = new RouterLink(text, (Class<? extends Component>) navigationTarget);
        link.addComponentAsFirst(new Icon(icon));
        link.setTabIndex(-1);
        link.addClassNames(
            LumoUtility.Display.FLEX,
            LumoUtility.AlignItems.CENTER,
            LumoUtility.Padding.Horizontal.MEDIUM,
            LumoUtility.Padding.Vertical.SMALL,
            LumoUtility.TextColor.BODY,
            "nav-link"
        );

        // Add hover effect
        link.getElement().addEventListener("mouseenter", e -> 
            link.getElement().getStyle().set("background-color", "var(--lumo-contrast-5pct)"));
        link.getElement().addEventListener("mouseleave", e -> 
            link.getElement().getStyle().remove("background-color"));

        return link;
    }

    public void updateCartBadge() {
        int itemCount = cartService.getTotalItemCount();
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisible(true);
        } else {
            cartBadge.setVisible(false);
        }
    }
}
