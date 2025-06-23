package com.quickbite.view.pages;

import com.quickbite.model.FoodItem;
import com.quickbite.service.CartService;
import com.quickbite.service.MenuService;
import com.quickbite.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * PROFESSIONAL ZOMATO-STYLE MENU VIEW
 */
@PageTitle("QuickBite - Professional Menu")
@Route(value = "menu-new", layout = MainLayout.class)
public class MenuViewNew extends VerticalLayout {

    private final MenuService menuService;
    private final CartService cartService;
    private final Div menuContainer;

    @Autowired
    public MenuViewNew(MenuService menuService, CartService cartService) {
        this.menuService = menuService;
        this.cartService = cartService;
        this.menuContainer = new Div();
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createProfessionalHeader();
        createMenuContainer();
        loadMenuItems();
    }

    private void createProfessionalHeader() {
        // PROFESSIONAL TRANSFORMATION BANNER
        Div banner = new Div();
        banner.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("padding", "30px")
            .set("text-align", "center")
            .set("font-size", "2rem")
            .set("font-weight", "bold")
            .set("margin-bottom", "30px")
            .set("border-radius", "15px")
            .set("box-shadow", "0 8px 32px rgba(226, 55, 68, 0.3)")
            .set("animation", "pulse 2s infinite");
        
        H1 title = new H1("🎨 PROFESSIONAL UI TRANSFORMATION COMPLETE!");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0 0 10px 0")
            .set("font-size", "2.5rem");
        
        Paragraph subtitle = new Paragraph("✨ Zomato/Swiggy Style Food Cards with Enhanced Features ✨");
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.9)")
            .set("margin", "0")
            .set("font-size", "1.2rem");
        
        banner.add(title, subtitle);
        add(banner);
    }

    private void createMenuContainer() {
        menuContainer.addClassNames(
            LumoUtility.Display.GRID,
            LumoUtility.Gap.LARGE,
            LumoUtility.Padding.LARGE
        );
        menuContainer.getStyle()
            .set("grid-template-columns", "repeat(auto-fill, minmax(350px, 1fr))")
            .set("width", "100%")
            .set("background", "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)")
            .set("border-radius", "15px")
            .set("min-height", "200px");
        
        add(menuContainer);
    }

    private void loadMenuItems() {
        List<FoodItem> items = menuService.getAllMenuItems();
        displayMenuItems(items);
    }

    private void displayMenuItems(List<FoodItem> items) {
        menuContainer.removeAll();

        if (items.isEmpty()) {
            createEmptyStateMessage();
            return;
        }

        items.forEach(this::createProfessionalFoodCard);
    }

    private void createEmptyStateMessage() {
        Div emptyState = new Div();
        emptyState.getStyle()
            .set("text-align", "center")
            .set("padding", "60px")
            .set("background", "white")
            .set("border-radius", "15px")
            .set("border", "3px dashed #e23744")
            .set("color", "#e23744");

        H3 emptyTitle = new H3("🍽️ No Menu Items Yet");
        emptyTitle.getStyle().set("color", "#e23744");

        Paragraph emptyMessage = new Paragraph("Add some delicious items to see the professional food cards!");
        
        Button addItemsButton = new Button("Go to Menu Management", new Icon(VaadinIcon.PLUS));
        addItemsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItemsButton.getStyle()
            .set("background", "#e23744")
            .set("margin-top", "20px");
        addItemsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("menu-management")));

        emptyState.add(emptyTitle, emptyMessage, addItemsButton);
        menuContainer.add(emptyState);
    }

    private void createProfessionalFoodCard(FoodItem item) {
        // PROFESSIONAL ZOMATO-STYLE FOOD CARD
        Div card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "20px")
            .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
            .set("border", "2px solid #e23744")
            .set("overflow", "hidden")
            .set("transition", "all 0.3s ease")
            .set("cursor", "pointer")
            .set("min-height", "350px")
            .set("position", "relative");

        // Hover effect
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                .set("transform", "translateY(-8px)")
                .set("box-shadow", "0 20px 40px rgba(226, 55, 68, 0.2)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                .set("transform", "translateY(0)")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        });

        // Image section with gradient overlay
        Div imageSection = new Div();
        imageSection.getStyle()
            .set("height", "200px")
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("position", "relative")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("color", "white")
            .set("font-size", "3rem");
        
        imageSection.add(new Span("🍽️"));

        // Content section
        Div contentSection = new Div();
        contentSection.getStyle().set("padding", "20px");

        // Item name with professional styling
        H3 itemName = new H3(item.getName());
        itemName.getStyle()
            .set("color", "#2c3e50")
            .set("margin", "0 0 10px 0")
            .set("font-size", "1.5rem")
            .set("font-weight", "bold");

        // Description
        Paragraph description = new Paragraph(item.getDescription());
        description.getStyle()
            .set("color", "#6c757d")
            .set("margin", "0 0 15px 0")
            .set("line-height", "1.5");

        // Price and action section
        HorizontalLayout bottomSection = new HorizontalLayout();
        bottomSection.setWidthFull();
        bottomSection.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bottomSection.setAlignItems(FlexComponent.Alignment.CENTER);

        // Professional price display
        Span price = new Span(item.getFormattedPrice());
        price.getStyle()
            .set("font-size", "1.8rem")
            .set("font-weight", "bold")
            .set("color", "#28a745")
            .set("text-shadow", "1px 1px 2px rgba(0,0,0,0.1)");

        // Professional add to cart button
        Button addToCartButton = new Button("Add to Cart", new Icon(VaadinIcon.CART_O));
        addToCartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addToCartButton.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("border", "none")
            .set("border-radius", "25px")
            .set("padding", "10px 20px")
            .set("font-weight", "bold")
            .set("box-shadow", "0 4px 15px rgba(226, 55, 68, 0.3)")
            .set("transition", "all 0.3s ease");
        
        addToCartButton.addClickListener(e -> addToCart(item));

        bottomSection.add(price, addToCartButton);

        contentSection.add(itemName, description, bottomSection);
        card.add(imageSection, contentSection);
        menuContainer.add(card);
    }

    private void addToCart(FoodItem item) {
        cartService.addToCart(item);

        // Show professional success notification
        Notification notification = Notification.show(
            "✅ " + item.getName() + " added to cart!",
            3000,
            Notification.Position.TOP_END
        );
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
