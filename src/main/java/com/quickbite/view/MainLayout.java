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
        // Professional header with red gradient background
        H1 logo = new H1("🍕 QuickBite");
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        );
        logo.getStyle()
            .set("color", "white")
            .set("font-weight", "700")
            .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.3)");

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
            LumoUtility.Padding.Vertical.MEDIUM,
            LumoUtility.Padding.Horizontal.LARGE
        );

        // Apply professional red gradient background
        header.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("box-shadow", "0 4px 20px rgba(226, 55, 68, 0.3)")
            .set("border-bottom", "3px solid #cb1b28");

        addToNavbar(header);
    }

    private Div createCartButton() {
        // Professional cart badge with red accent
        cartBadge = new Span();
        cartBadge.getElement().getThemeList().add("badge error");
        cartBadge.getStyle().set("position", "absolute")
                .set("top", "-8px")
                .set("right", "-8px")
                .set("min-width", "20px")
                .set("height", "20px")
                .set("border-radius", "50%")
                .set("background", "linear-gradient(135deg, #ff6b35 0%, #e23744 100%)")
                .set("color", "white")
                .set("font-size", "11px")
                .set("font-weight", "bold")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("box-shadow", "0 2px 8px rgba(226, 55, 68, 0.4)")
                .set("border", "2px solid white");

        Button cartButton = new Button(new Icon(VaadinIcon.CART));
        cartButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cartButton.getStyle()
            .set("color", "white")
            .set("background", "rgba(255,255,255,0.1)")
            .set("border-radius", "8px")
            .set("padding", "8px")
            .set("transition", "all 0.3s ease");

        // Add hover effect for cart button
        cartButton.getElement().addEventListener("mouseenter", e ->
            cartButton.getStyle().set("background", "rgba(255,255,255,0.2)"));
        cartButton.getElement().addEventListener("mouseleave", e ->
            cartButton.getStyle().set("background", "rgba(255,255,255,0.1)"));

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

        // Apply professional drawer background
        navigation.getStyle()
            .set("background", "linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%)")
            .set("border-right", "3px solid #e23744");

        // Navigation links with professional styling
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

        // Create icon with professional red color
        Icon navIcon = new Icon(icon);
        navIcon.getStyle()
            .set("color", "#e23744")
            .set("margin-right", "12px")
            .set("font-size", "18px");

        link.addComponentAsFirst(navIcon);
        link.setTabIndex(-1);
        link.addClassNames(
            LumoUtility.Display.FLEX,
            LumoUtility.AlignItems.CENTER,
            LumoUtility.Padding.Horizontal.LARGE,
            LumoUtility.Padding.Vertical.MEDIUM,
            LumoUtility.TextColor.BODY,
            "nav-link"
        );

        // Apply professional navigation link styling
        link.getStyle()
            .set("text-decoration", "none")
            .set("color", "#2c3e50")
            .set("font-weight", "500")
            .set("font-size", "16px")
            .set("border-radius", "8px")
            .set("margin", "4px 8px")
            .set("transition", "all 0.3s ease");

        // Add professional hover effect
        link.getElement().addEventListener("mouseenter", e -> {
            link.getElement().getStyle()
                .set("background", "linear-gradient(135deg, #fff5f5 0%, #ffe8e8 100%)")
                .set("color", "#e23744")
                .set("transform", "translateX(8px)")
                .set("box-shadow", "0 4px 12px rgba(226, 55, 68, 0.15)");
            navIcon.getStyle().set("color", "#e23744");
        });

        link.getElement().addEventListener("mouseleave", e -> {
            link.getElement().getStyle()
                .remove("background")
                .remove("transform")
                .remove("box-shadow")
                .set("color", "#2c3e50");
            navIcon.getStyle().set("color", "#e23744");
        });

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
