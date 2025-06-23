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
        // Main welcome section
        Div welcomeSection = new Div();
        welcomeSection.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.XLARGE,
            LumoUtility.Margin.LARGE
        );
        welcomeSection.setWidth("80%");
        welcomeSection.getStyle().set("text-align", "center");

        H1 title = new H1("🍕 Welcome to QuickBite!");
        title.addClassNames(
            LumoUtility.FontSize.XXXLARGE,
            LumoUtility.TextColor.PRIMARY,
            LumoUtility.Margin.Bottom.MEDIUM
        );

        Paragraph subtitle = new Paragraph("Your favorite food, delivered fast and fresh!");
        subtitle.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.Margin.Bottom.LARGE
        );

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
        Div card = new Div();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE,
            LumoUtility.BoxShadow.SMALL
        );
        card.getStyle()
            .set("cursor", "pointer")
            .set("transition", "all 0.2s ease")
            .set("text-align", "center");

        // Add hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateY(-4px)");
            card.addClassNames(LumoUtility.BoxShadow.MEDIUM);
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().remove("transform");
            card.removeClassNames(LumoUtility.BoxShadow.MEDIUM);
        });

        card.addClickListener(e -> clickAction.run());

        Icon icon = new Icon(iconType);
        icon.addClassNames(LumoUtility.IconSize.LARGE);
        icon.getStyle().set("font-size", "3rem").set("margin-bottom", "1rem");

        // Apply theme colors
        switch (theme) {
            case "primary":
                icon.addClassNames(LumoUtility.TextColor.PRIMARY);
                break;
            case "success":
                icon.addClassNames(LumoUtility.TextColor.SUCCESS);
                break;
            case "contrast":
                icon.addClassNames(LumoUtility.TextColor.TERTIARY);
                break;
            case "tertiary":
                icon.addClassNames(LumoUtility.TextColor.SECONDARY);
                break;
        }

        H3 cardTitle = new H3(title);
        cardTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Vertical.SMALL);

        Paragraph cardDescription = new Paragraph(description);
        cardDescription.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

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
