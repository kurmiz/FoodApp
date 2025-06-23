package com.quickbite.view.pages;

import com.quickbite.model.FoodItem;
import com.quickbite.service.ImageService;
import com.quickbite.service.MenuService;
import com.quickbite.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Professional Menu Management View for adding and managing food items
 */
@PageTitle("QuickBite - Menu Management")
@Route(value = "menu-management", layout = MainLayout.class)
public class MenuManagementView extends VerticalLayout {

    private final MenuService menuService;
    private final ImageService imageService;
    private Grid<FoodItem> menuGrid;
    private Dialog addItemDialog;

    @Autowired
    public MenuManagementView(MenuService menuService, ImageService imageService) {
        this.menuService = menuService;
        this.imageService = imageService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createHeader();
        createActionButtons();
        createMenuGrid();
        createAddItemDialog();
        refreshGrid();
    }

    private void createHeader() {
        H1 title = new H1("🍽️ Menu Management");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);
        
        add(title);
    }

    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        buttonLayout.setWidthFull();

        Button addItemButton = new Button("Add New Item", new Icon(VaadinIcon.PLUS));
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItemButton.addClickListener(e -> openAddItemDialog());

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> refreshGrid());

        buttonLayout.add(addItemButton, refreshButton);
        add(buttonLayout);
    }

    private void createMenuGrid() {
        menuGrid = new Grid<>(FoodItem.class, false);
        menuGrid.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.MEDIUM);
        
        // ID column
        menuGrid.addColumn(FoodItem::getId)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        // Name column
        menuGrid.addColumn(FoodItem::getName)
                .setHeader("Name")
                .setFlexGrow(2);

        // Description column
        menuGrid.addColumn(FoodItem::getDescription)
                .setHeader("Description")
                .setFlexGrow(3);

        // Category column
        menuGrid.addColumn(FoodItem::getCategory)
                .setHeader("Category")
                .setFlexGrow(1);

        // Price column
        menuGrid.addColumn(FoodItem::getFormattedPrice)
                .setHeader("Price")
                .setFlexGrow(1);

        // Actions column
        menuGrid.addColumn(new ComponentRenderer<HorizontalLayout, FoodItem>(item -> createActionButtons(item)))
                .setHeader("Actions")
                .setFlexGrow(1);

        menuGrid.setHeight("500px");
        add(menuGrid);
    }

    private HorizontalLayout createActionButtons(FoodItem item) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editButton.getElement().setAttribute("aria-label", "Edit item");
        editButton.addClickListener(e -> editItem(item));

        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.getElement().setAttribute("aria-label", "Delete item");
        deleteButton.addClickListener(e -> deleteItem(item));

        HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        actions.setSpacing(true);
        return actions;
    }

    private void createAddItemDialog() {
        addItemDialog = new Dialog();
        addItemDialog.setModal(true);
        addItemDialog.setDraggable(false);
        addItemDialog.setResizable(false);
        addItemDialog.setWidth("500px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        H2 dialogTitle = new H2("Add New Menu Item");
        dialogTitle.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField nameField = new TextField("Item Name");
        nameField.setRequired(true);
        nameField.setPlaceholder("Enter item name");

        TextArea descriptionField = new TextArea("Description");
        descriptionField.setRequired(true);
        descriptionField.setPlaceholder("Enter item description");
        descriptionField.setHeight("100px");

        ComboBox<String> categoryField = new ComboBox<>("Category");
        categoryField.setItems("Appetizers", "Main Courses", "Pasta", "Desserts", "Beverages", "Salads", "Soups");
        categoryField.setRequired(true);
        categoryField.setAllowCustomValue(true);
        categoryField.addCustomValueSetListener(e -> categoryField.setValue(e.getDetail()));

        NumberField priceField = new NumberField("Price ($)");
        priceField.setRequired(true);
        priceField.setMin(0.01);
        priceField.setStep(0.01);
        priceField.setValue(0.0);

        formLayout.add(nameField, descriptionField, categoryField, priceField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> {
            clearForm(nameField, descriptionField, categoryField, priceField);
            addItemDialog.close();
        });

        Button saveButton = new Button("Add Item", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (validateForm(nameField, descriptionField, categoryField, priceField)) {
                addMenuItem(nameField.getValue(), descriptionField.getValue(), 
                           categoryField.getValue(), priceField.getValue());
                clearForm(nameField, descriptionField, categoryField, priceField);
                addItemDialog.close();
            }
        });

        buttonLayout.add(cancelButton, saveButton);

        dialogLayout.add(dialogTitle, formLayout, buttonLayout);
        addItemDialog.add(dialogLayout);
    }

    private boolean validateForm(TextField nameField, TextArea descriptionField, 
                                ComboBox<String> categoryField, NumberField priceField) {
        boolean isValid = true;

        if (nameField.getValue() == null || nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Name is required");
            isValid = false;
        } else {
            nameField.setInvalid(false);
        }

        if (descriptionField.getValue() == null || descriptionField.getValue().trim().isEmpty()) {
            descriptionField.setInvalid(true);
            descriptionField.setErrorMessage("Description is required");
            isValid = false;
        } else {
            descriptionField.setInvalid(false);
        }

        if (categoryField.getValue() == null || categoryField.getValue().trim().isEmpty()) {
            categoryField.setInvalid(true);
            categoryField.setErrorMessage("Category is required");
            isValid = false;
        } else {
            categoryField.setInvalid(false);
        }

        if (priceField.getValue() == null || priceField.getValue() <= 0) {
            priceField.setInvalid(true);
            priceField.setErrorMessage("Price must be greater than 0");
            isValid = false;
        } else {
            priceField.setInvalid(false);
        }

        return isValid;
    }

    private void clearForm(TextField nameField, TextArea descriptionField, 
                          ComboBox<String> categoryField, NumberField priceField) {
        nameField.clear();
        nameField.setInvalid(false);
        descriptionField.clear();
        descriptionField.setInvalid(false);
        categoryField.clear();
        categoryField.setInvalid(false);
        priceField.setValue(0.0);
        priceField.setInvalid(false);
    }

    private void openAddItemDialog() {
        addItemDialog.open();
    }

    private void addMenuItem(String name, String description, String category, Double price) {
        try {
            FoodItem newItem = menuService.addMenuItem(name, description, price, category);
            refreshGrid();
            
            Notification notification = Notification.show(
                "Menu item '" + newItem.getName() + "' added successfully!",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification notification = Notification.show(
                "Error adding menu item: " + e.getMessage(),
                5000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editItem(FoodItem item) {
        // TODO: Implement edit functionality
        Notification.show("Edit functionality coming soon!", 3000, Notification.Position.TOP_END);
    }

    private void deleteItem(FoodItem item) {
        boolean deleted = menuService.removeMenuItem(item.getId());
        if (deleted) {
            refreshGrid();
            Notification notification = Notification.show(
                "Menu item '" + item.getName() + "' deleted successfully!",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            Notification notification = Notification.show(
                "Error deleting menu item",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshGrid() {
        menuGrid.setItems(menuService.getAllMenuItems());
    }
}
