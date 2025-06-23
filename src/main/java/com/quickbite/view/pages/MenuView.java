package com.quickbite.view.pages;

import com.quickbite.model.FoodItem;
import com.quickbite.service.CartService;
import com.quickbite.service.MenuService;
import com.quickbite.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Menu page view - Displays food items with add to cart functionality
 */
@PageTitle("QuickBite - Menu")
@Route(value = "menu", layout = MainLayout.class)
public class MenuView extends VerticalLayout {

    private final MenuService menuService;
    private final CartService cartService;
    private final Div menuContainer;
    private List<FoodItem> currentMenuItems;

    @Autowired
    public MenuView(MenuService menuService, CartService cartService) {
        this.menuService = menuService;
        this.cartService = cartService;
        this.menuContainer = new Div();
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Add professional transformation banner
        createTransformationBanner();

        createHeader();
        createFilters();
        createMenuContainer();
        loadMenuItems();
    }

    private void createTransformationBanner() {
        Div banner = new Div();
        banner.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("padding", "20px")
            .set("text-align", "center")
            .set("font-size", "1.5rem")
            .set("font-weight", "bold")
            .set("margin-bottom", "20px")
            .set("border-radius", "10px")
            .set("box-shadow", "0 4px 15px rgba(226, 55, 68, 0.3)");

        banner.setText("🎨 PROFESSIONAL UI TRANSFORMATION ACTIVE - MODERN STYLE MENU");
        add(banner);
    }

    private void createHeader() {
        // Create professional header with gradient background
        Div headerSection = new Div();
        headerSection.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("padding", "30px")
            .set("border-radius", "15px")
            .set("margin-bottom", "30px")
            .set("box-shadow", "0 8px 32px rgba(226, 55, 68, 0.3)");

        H1 title = new H1("🍽️ PROFESSIONAL MENU - MODERN STYLE");
        title.getStyle()
            .set("color", "white")
            .set("font-size", "2.5rem")
            .set("font-weight", "bold")
            .set("margin", "0 0 10px 0")
            .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.3)");

        Paragraph subtitle = new Paragraph("🎨 ENHANCED UI TRANSFORMATION - Professional Food Cards with Ratings, Badges & Animations");
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.9)")
            .set("font-size", "1.2rem")
            .set("margin", "0")
            .set("font-weight", "500");

        headerSection.add(title, subtitle);
        add(headerSection);
    }

    private void createFilters() {
        HorizontalLayout filtersLayout = new HorizontalLayout();
        filtersLayout.setWidthFull();
        filtersLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        filtersLayout.setAlignItems(FlexComponent.Alignment.END);

        // Search field
        TextField searchField = new TextField("Search Menu");
        searchField.setPlaceholder("Search for dishes...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterMenuItems(e.getValue(), null));

        // Category filter
        ComboBox<String> categoryFilter = new ComboBox<>("Category");
        categoryFilter.setItems(menuService.getCategories());
        categoryFilter.setPlaceholder("All Categories");
        categoryFilter.addValueChangeListener(e -> filterMenuItems(searchField.getValue(), e.getValue()));

        // Clear filters button
        Button clearFilters = new Button("Clear Filters", new Icon(VaadinIcon.REFRESH));
        clearFilters.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearFilters.addClickListener(e -> {
            searchField.clear();
            categoryFilter.clear();
            loadMenuItems();
        });

        filtersLayout.add(searchField, categoryFilter, clearFilters);
        add(filtersLayout);
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
            .set("background", "#f8f9fa");

        add(menuContainer);
    }

    private void loadMenuItems() {
        currentMenuItems = menuService.getAllMenuItems();
        displayMenuItems(currentMenuItems);
    }

    private void filterMenuItems(String searchTerm, String category) {
        List<FoodItem> filteredItems = menuService.getAllMenuItems();

        // Apply search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            filteredItems = filteredItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               item.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
        }

        // Apply category filter
        if (category != null && !category.trim().isEmpty()) {
            filteredItems = filteredItems.stream()
                .filter(item -> item.getCategory().equals(category))
                .toList();
        }

        currentMenuItems = filteredItems;
        displayMenuItems(currentMenuItems);
    }

    private void displayMenuItems(List<FoodItem> items) {
        menuContainer.removeAll();

        if (items.isEmpty()) {
            createEmptyStateMessage();
            return;
        }

        items.forEach(this::createMenuItemCard);
    }

    private void createEmptyStateMessage() {
        Div emptyState = new Div();
        emptyState.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.XLARGE,
            LumoUtility.Margin.LARGE
        );
        emptyState.getStyle().set("text-align", "center");

        Icon emptyIcon = new Icon(VaadinIcon.CUTLERY);
        emptyIcon.addClassNames(LumoUtility.IconSize.LARGE, LumoUtility.TextColor.TERTIARY);
        emptyIcon.getStyle().set("font-size", "4rem");

        H3 emptyTitle = new H3("No Menu Items Available");
        emptyTitle.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.SMALL);

        Paragraph emptyMessage = new Paragraph("The menu is currently empty. Add some delicious items to get started!");
        emptyMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Bottom.MEDIUM);

        Button addItemsButton = new Button("Add Menu Items", new Icon(VaadinIcon.PLUS));
        addItemsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItemsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(MenuManagementView.class)));

        emptyState.add(emptyIcon, emptyTitle, emptyMessage, addItemsButton);
        menuContainer.add(emptyState);
    }

    private void createMenuItemCard(FoodItem item) {
        // PROFESSIONAL MODERN-STYLE FOOD CARD
        Div card = new Div();
        card.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.BoxShadow.LARGE
        );
        card.getStyle()
            .set("border", "3px solid #e23744")
            .set("transition", "all 0.3s ease")
            .set("cursor", "pointer")
            .set("overflow", "hidden")
            .set("min-height", "300px")
            .set("background", "linear-gradient(135deg, #fff5f5 0%, #ffffff 100%)")
            .set("transform", "scale(1.02)")
            .set("box-shadow", "0 8px 25px rgba(226, 55, 68, 0.2)");

        // Add hover effect
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateY(-4px)");
            card.addClassNames(LumoUtility.BoxShadow.LARGE);
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().remove("transform");
            card.removeClassNames(LumoUtility.BoxShadow.LARGE);
        });

        // Image section
        Div imageSection = createImageSection(item);

        // Content section
        Div contentSection = new Div();
        contentSection.addClassNames(LumoUtility.Padding.MEDIUM);

        // Header with badges
        HorizontalLayout headerLayout = createCardHeader(item);

        // Item name and rating
        Div titleSection = createTitleSection(item);

        // Description
        Paragraph description = new Paragraph(item.getDescription());
        description.addClassNames(
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.FontSize.SMALL,
            LumoUtility.Margin.Vertical.SMALL
        );
        description.getStyle().set("line-height", "1.4");

        // Tags
        Div tagsSection = createTagsSection(item);

        // Price and action section
        HorizontalLayout bottomSection = createBottomSection(item);

        contentSection.add(headerLayout, titleSection, description, tagsSection, bottomSection);
        card.add(imageSection, contentSection);
        menuContainer.add(card);
    }

    private Div createImageSection(FoodItem item) {
        Div imageSection = new Div();
        imageSection.addClassNames(LumoUtility.Position.RELATIVE);
        imageSection.getStyle()
            .set("height", "200px")
            .set("background-size", "cover")
            .set("background-position", "center")
            .set("background-repeat", "no-repeat");

        // Set background image
        String imageUrl = item.getImageUrl() != null ? item.getImageUrl() :
            "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='200' viewBox='0 0 300 200'%3E%3Crect width='300' height='200' fill='%23f5f5f5'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' fill='%23999' font-family='Arial' font-size='16'%3ENo Image%3C/text%3E%3C/svg%3E";
        imageSection.getStyle().set("background-image", "url('" + imageUrl + "')");

        // Popular badge
        if (item.isPopular()) {
            Span popularBadge = new Span("🔥 Popular");
            popularBadge.addClassNames(
                LumoUtility.Background.ERROR,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.FontSize.XSMALL,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Padding.Horizontal.SMALL,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.BorderRadius.MEDIUM
            );
            popularBadge.getStyle()
                .set("position", "absolute")
                .set("top", "10px")
                .set("left", "10px");
            imageSection.add(popularBadge);
        }

        // Discount badge
        if (item.hasDiscount()) {
            Span discountBadge = new Span(item.getDiscountPercentage() + "% OFF");
            discountBadge.addClassNames(
                LumoUtility.Background.SUCCESS,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.FontSize.XSMALL,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Padding.Horizontal.SMALL,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.BorderRadius.MEDIUM
            );
            discountBadge.getStyle()
                .set("position", "absolute")
                .set("top", "10px")
                .set("right", "10px");
            imageSection.add(discountBadge);
        }

        return imageSection;
    }

    private HorizontalLayout createCardHeader(FoodItem item) {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setWidthFull();

        // Category badge
        Span categoryBadge = new Span(item.getCategory());
        categoryBadge.addClassNames(
            LumoUtility.Background.CONTRAST_10,
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.FontSize.XSMALL,
            LumoUtility.Padding.Horizontal.SMALL,
            LumoUtility.Padding.Vertical.XSMALL,
            LumoUtility.BorderRadius.SMALL
        );

        // Diet badges
        HorizontalLayout dietBadges = new HorizontalLayout();
        dietBadges.setSpacing(false);

        if (item.isVegetarian()) {
            Span vegBadge = new Span("🟢");
            vegBadge.getElement().setAttribute("title", "Vegetarian");
            dietBadges.add(vegBadge);
        }

        if (item.isVegan()) {
            Span veganBadge = new Span("🌱");
            veganBadge.getElement().setAttribute("title", "Vegan");
            dietBadges.add(veganBadge);
        }

        if (item.isSpicy()) {
            Span spicyBadge = new Span("🌶️");
            spicyBadge.getElement().setAttribute("title", "Spicy");
            dietBadges.add(spicyBadge);
        }

        headerLayout.add(categoryBadge, dietBadges);
        return headerLayout;
    }

    private Div createTitleSection(FoodItem item) {
        Div titleSection = new Div();

        // Item name
        H3 itemName = new H3(item.getName());
        itemName.addClassNames(
            LumoUtility.Margin.NONE,
            LumoUtility.TextColor.PRIMARY,
            LumoUtility.FontSize.LARGE,
            LumoUtility.FontWeight.BOLD
        );

        // Rating and time section
        HorizontalLayout ratingTimeLayout = new HorizontalLayout();
        ratingTimeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        ratingTimeLayout.setSpacing(true);

        // Rating
        HorizontalLayout ratingLayout = new HorizontalLayout();
        ratingLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        ratingLayout.setSpacing(false);

        Icon starIcon = new Icon(VaadinIcon.STAR);
        starIcon.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.TextColor.WARNING);

        Span rating = new Span(item.getFormattedRating());
        rating.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.BOLD);

        Span reviewCount = new Span("(" + item.getReviewCount() + ")");
        reviewCount.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

        ratingLayout.add(starIcon, rating, reviewCount);

        // Preparation time
        HorizontalLayout timeLayout = new HorizontalLayout();
        timeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        timeLayout.setSpacing(false);

        Icon clockIcon = new Icon(VaadinIcon.CLOCK);
        clockIcon.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Span prepTime = new Span(item.getPreparationTime());
        prepTime.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        timeLayout.add(clockIcon, prepTime);

        ratingTimeLayout.add(ratingLayout, timeLayout);

        titleSection.add(itemName, ratingTimeLayout);
        return titleSection;
    }

    private Div createTagsSection(FoodItem item) {
        Div tagsSection = new Div();

        if (item.getTags() != null && !item.getTags().trim().isEmpty()) {
            HorizontalLayout tagsLayout = new HorizontalLayout();
            tagsLayout.setSpacing(true);

            String[] tags = item.getTags().split(",");
            for (String tag : tags) {
                if (tag.trim().length() > 0) {
                    Span tagSpan = new Span(tag.trim());
                    tagSpan.addClassNames(
                        LumoUtility.Background.PRIMARY_10,
                        LumoUtility.TextColor.PRIMARY,
                        LumoUtility.FontSize.XSMALL,
                        LumoUtility.Padding.Horizontal.SMALL,
                        LumoUtility.Padding.Vertical.XSMALL,
                        LumoUtility.BorderRadius.SMALL
                    );
                    tagsLayout.add(tagSpan);
                }
            }

            tagsSection.add(tagsLayout);
        }

        return tagsSection;
    }

    private HorizontalLayout createBottomSection(FoodItem item) {
        HorizontalLayout bottomSection = new HorizontalLayout();
        bottomSection.setWidthFull();
        bottomSection.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bottomSection.setAlignItems(FlexComponent.Alignment.CENTER);
        bottomSection.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        // Price section
        Div priceSection = new Div();

        if (item.hasDiscount()) {
            HorizontalLayout priceLayout = new HorizontalLayout();
            priceLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            priceLayout.setSpacing(true);

            Span currentPrice = new Span(item.getFormattedPrice());
            currentPrice.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.SUCCESS
            );

            Span originalPrice = new Span(item.getFormattedOriginalPrice());
            originalPrice.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY
            );
            originalPrice.getStyle().set("text-decoration", "line-through");

            priceLayout.add(currentPrice, originalPrice);
            priceSection.add(priceLayout);
        } else {
            Span price = new Span(item.getFormattedPrice());
            price.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.SUCCESS
            );
            priceSection.add(price);
        }

        // Professional Add to Cart button
        Button addToCartButton = new Button("Add to Cart", new Icon(VaadinIcon.CART_O));
        addToCartButton.getStyle()
            .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "8px")
            .set("padding", "10px 20px")
            .set("font-weight", "600")
            .set("font-size", "14px")
            .set("text-transform", "uppercase")
            .set("letter-spacing", "0.5px")
            .set("transition", "all 0.3s ease")
            .set("box-shadow", "0 3px 12px rgba(226, 55, 68, 0.3)");

        // Add hover effect
        addToCartButton.getElement().addEventListener("mouseenter", e ->
            addToCartButton.getStyle()
                .set("background", "linear-gradient(135deg, #cb1b28 0%, #e23744 100%)")
                .set("transform", "translateY(-2px)")
                .set("box-shadow", "0 5px 16px rgba(226, 55, 68, 0.4)"));
        addToCartButton.getElement().addEventListener("mouseleave", e ->
            addToCartButton.getStyle()
                .set("background", "linear-gradient(135deg, #e23744 0%, #ff6b35 100%)")
                .remove("transform")
                .set("box-shadow", "0 3px 12px rgba(226, 55, 68, 0.3)"));

        addToCartButton.addClickListener(e -> addToCart(item));

        bottomSection.add(priceSection, addToCartButton);
        return bottomSection;
    }

    private void addToCart(FoodItem item) {
        cartService.addToCart(item);

        // Update cart badge in main layout
        if (getParent().isPresent() && getParent().get() instanceof MainLayout mainLayout) {
            mainLayout.updateCartBadge();
        }

        // Show success notification
        Notification notification = Notification.show(
            item.getName() + " added to cart!",
            3000,
            Notification.Position.TOP_END
        );
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
