package com.quickbite.view.pages;

import com.quickbite.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Home page view - Welcome page with navigation options
 */
@PageTitle("QuickBite - Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        createWelcomeSection();
        createNavigationSection();
        createFeaturesSection();
    }

    private void createWelcomeSection() {
        // Professional welcome section with red gradient
        Div welcomeSection = new Div();
        welcomeSection.addClassNames(
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.XLARGE,
            LumoUtility.Margin.LARGE
        );
        welcomeSection.setWidth("90%");
        welcomeSection.getStyle()
            .set("text-align", "center")
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("box-shadow", "0 8px 32px rgba(226, 55, 68, 0.3)")
            .set("position", "relative")
            .set("overflow", "hidden");

        // Add shimmer effect
        welcomeSection.getElement().getStyle()
            .set("background-size", "200% 200%")
            .set("animation", "gradient-shift 3s ease infinite");

        H1 title = new H1("🍕 Welcome to QuickBite!");
        title.addClassNames(
            LumoUtility.FontSize.XXXLARGE,
            LumoUtility.Margin.Bottom.MEDIUM
        );
        title.getStyle()
            .set("color", "white")
            .set("font-weight", "700")
            .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.3)")
            .set("margin", "0 0 20px 0");

        Paragraph subtitle = new Paragraph("🎨 PROFESSIONAL UI • Your favorite food, delivered fast and fresh!");
        subtitle.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.Bottom.LARGE
        );
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.95)")
            .set("font-weight", "500")
            .set("margin", "0")
            .set("line-height", "1.5");

        welcomeSection.add(title, subtitle);
        add(welcomeSection);
    }

    private void createNavigationSection() {
        H2 navTitle = new H2("What would you like to do?");
        navTitle.addClassNames(LumoUtility.TextColor.PRIMARY);

        // Create a responsive grid layout for navigation cards
        Div navigationGrid = new Div();
        navigationGrid.addClassNames(
            LumoUtility.Display.GRID,
            LumoUtility.Gap.LARGE,
            LumoUtility.Margin.Vertical.LARGE
        );
        navigationGrid.getStyle()
            .set("grid-template-columns", "repeat(auto-fit, minmax(250px, 1fr))")
            .set("width", "100%");

        // Menu card
        Div menuCard = createNavigationCard(
            VaadinIcon.MENU,
            "Browse Menu",
            "Explore our delicious food items",
            "primary",
            () -> getUI().ifPresent(ui -> ui.navigate(MenuView.class))
        );

        // Cart card
        Div cartCard = createNavigationCard(
            VaadinIcon.CART,
            "View Cart",
            "Review your selected items",
            "success",
            () -> getUI().ifPresent(ui -> ui.navigate(CartView.class))
        );

        // Delivery Addresses card
        Div addressCard = createNavigationCard(
            VaadinIcon.LOCATION_ARROW,
            "Delivery Addresses",
            "Manage your delivery locations",
            "primary",
            () -> getUI().ifPresent(ui -> ui.navigate(DeliveryAddressView.class))
        );

        // Menu Management card
        Div managementCard = createNavigationCard(
            VaadinIcon.COG,
            "Menu Management",
            "Add and manage menu items",
            "contrast",
            () -> getUI().ifPresent(ui -> ui.navigate(MenuManagementView.class))
        );

        // Admin card
        Div adminCard = createNavigationCard(
            VaadinIcon.USER_STAR,
            "Admin Panel",
            "View orders and statistics",
            "tertiary",
            () -> getUI().ifPresent(ui -> ui.navigate(AdminView.class))
        );

        navigationGrid.add(menuCard, cartCard, addressCard, managementCard, adminCard);
        add(navTitle, navigationGrid);
    }

    private Div createNavigationCard(VaadinIcon iconType, String title, String description,
                                    String theme, Runnable clickAction) {
        // Professional navigation card
        Div card = new Div();
        card.addClassNames(
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE,
            LumoUtility.BoxShadow.SMALL
        );
        card.getStyle()
            .set("cursor", "pointer")
            .set("transition", "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)")
            .set("text-align", "center")
            .set("background", "linear-gradient(135deg, #ffffff 0%, #fff8f8 100%)")
            .set("border", "2px solid transparent")
            .set("position", "relative")
            .set("overflow", "hidden");

        // Add red accent bar at top
        Div accentBar = new Div();
        accentBar.getStyle()
            .set("position", "absolute")
            .set("top", "0")
            .set("left", "0")
            .set("right", "0")
            .set("height", "4px")
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)");
        card.add(accentBar);

        // Add professional hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                .set("transform", "translateY(-8px) scale(1.02)")
                .set("box-shadow", "0 12px 40px rgba(226, 55, 68, 0.2)")
                .set("border-color", "#e23744");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                .remove("transform")
                .set("box-shadow", "0 4px 20px rgba(226, 55, 68, 0.1)")
                .set("border-color", "transparent");
        });

        card.addClickListener(e -> clickAction.run());

        Icon icon = new Icon(iconType);
        icon.addClassNames(LumoUtility.IconSize.LARGE);
        icon.getStyle()
            .set("font-size", "3.5rem")
            .set("margin-bottom", "1.5rem")
            .set("margin-top", "1rem")
            .set("color", "#e23744")
            .set("transition", "all 0.3s ease");

        // Add icon hover effect
        card.getElement().addEventListener("mouseenter", e ->
            icon.getStyle().set("transform", "scale(1.1)").set("color", "#cb1b28"));
        card.getElement().addEventListener("mouseleave", e ->
            icon.getStyle().remove("transform").set("color", "#e23744"));

        H3 cardTitle = new H3(title);
        cardTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Vertical.SMALL);
        cardTitle.getStyle()
            .set("color", "#2c3e50")
            .set("font-weight", "600")
            .set("margin", "0 0 12px 0");

        Paragraph cardDescription = new Paragraph(description);
        cardDescription.addClassNames(LumoUtility.FontSize.SMALL);
        cardDescription.getStyle()
            .set("color", "#6c757d")
            .set("line-height", "1.5")
            .set("margin", "0");

        card.add(icon, cardTitle, cardDescription);
        return card;
    }

    private void createFeaturesSection() {
        H2 featuresTitle = new H2("Why Choose QuickBite?");
        featuresTitle.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Top.LARGE);

        HorizontalLayout featuresLayout = new HorizontalLayout();
        featuresLayout.setSpacing(true);
        featuresLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        featuresLayout.setWidthFull();

        // Feature cards
        Div fastDelivery = createFeatureCard(
            VaadinIcon.CLOCK,
            "Fast Delivery",
            "Quick and reliable food delivery to your doorstep"
        );

        Div freshFood = createFeatureCard(
            VaadinIcon.HEART,
            "Fresh Ingredients",
            "Made with the freshest ingredients and lots of love"
        );

        Div easyOrdering = createFeatureCard(
            VaadinIcon.MOBILE,
            "Easy Ordering",
            "Simple and intuitive ordering process"
        );

        featuresLayout.add(fastDelivery, freshFood, easyOrdering);

        add(featuresTitle, featuresLayout);
    }

    private Div createFeatureCard(VaadinIcon iconType, String title, String description) {
        Div card = new Div();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.LARGE,
            LumoUtility.Margin.SMALL
        );
        card.setWidth("300px");
        card.getStyle().set("text-align", "center");

        Icon icon = new Icon(iconType);
        icon.addClassNames(LumoUtility.IconSize.LARGE, LumoUtility.TextColor.PRIMARY);

        H2 cardTitle = new H2(title);
        cardTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Vertical.SMALL);

        Paragraph cardDescription = new Paragraph(description);
        cardDescription.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        card.add(icon, cardTitle, cardDescription);
        return card;
    }
}
