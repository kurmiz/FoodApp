package com.quickbite.view.pages;

import com.quickbite.model.DeliveryAddress;
import com.quickbite.service.DeliveryService;
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
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Professional Delivery Address Management View
 */
@PageTitle("QuickBite - Delivery Addresses")
@Route(value = "delivery-addresses", layout = MainLayout.class)
public class DeliveryAddressView extends VerticalLayout {

    private final DeliveryService deliveryService;
    private Grid<DeliveryAddress> addressGrid;
    private Dialog addAddressDialog;

    @Autowired
    public DeliveryAddressView(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createHeader();
        createActionButtons();
        createAddressGrid();
        createAddAddressDialog();
        refreshGrid();
    }

    private void createHeader() {
        H1 title = new H1("📍 Delivery Addresses");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);
        
        Paragraph subtitle = new Paragraph("Manage your delivery locations for quick ordering");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY);
        
        add(title, subtitle);
    }

    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        buttonLayout.setWidthFull();

        Button addAddressButton = new Button("Add New Address", new Icon(VaadinIcon.PLUS));
        addAddressButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addAddressButton.addClickListener(e -> openAddAddressDialog());

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> refreshGrid());

        buttonLayout.add(addAddressButton, refreshButton);
        add(buttonLayout);
    }

    private void createAddressGrid() {
        addressGrid = new Grid<>(DeliveryAddress.class, false);
        addressGrid.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.MEDIUM);
        
        // Name column
        addressGrid.addColumn(DeliveryAddress::getFullName)
                .setHeader("Name")
                .setFlexGrow(1);

        // Address column
        addressGrid.addColumn(DeliveryAddress::getFormattedAddress)
                .setHeader("Address")
                .setFlexGrow(3);

        // Type column with badge
        addressGrid.addColumn(new ComponentRenderer<Span, DeliveryAddress>(this::createTypeBadge))
                .setHeader("Type")
                .setFlexGrow(1);

        // Default column
        addressGrid.addColumn(new ComponentRenderer<Span, DeliveryAddress>(this::createDefaultBadge))
                .setHeader("Default")
                .setFlexGrow(1);

        // Actions column
        addressGrid.addColumn(new ComponentRenderer<HorizontalLayout, DeliveryAddress>(address -> createActionButtons(address)))
                .setHeader("Actions")
                .setFlexGrow(1);

        addressGrid.setHeight("400px");
        add(addressGrid);
    }

    private Span createTypeBadge(DeliveryAddress address) {
        Span badge = new Span(address.getAddressType());
        badge.addClassNames(
            LumoUtility.FontSize.XSMALL,
            LumoUtility.Padding.Horizontal.SMALL,
            LumoUtility.Padding.Vertical.XSMALL,
            LumoUtility.BorderRadius.SMALL
        );
        
        switch (address.getAddressType().toUpperCase()) {
            case "HOME":
                badge.addClassNames(LumoUtility.Background.SUCCESS_10, LumoUtility.TextColor.SUCCESS);
                break;
            case "WORK":
                badge.addClassNames(LumoUtility.Background.PRIMARY_10, LumoUtility.TextColor.PRIMARY);
                break;
            default:
                badge.addClassNames(LumoUtility.Background.CONTRAST_10, LumoUtility.TextColor.SECONDARY);
        }
        
        return badge;
    }

    private Span createDefaultBadge(DeliveryAddress address) {
        if (address.isDefault()) {
            Span badge = new Span("✓ Default");
            badge.addClassNames(
                LumoUtility.Background.SUCCESS_10,
                LumoUtility.TextColor.SUCCESS,
                LumoUtility.FontSize.XSMALL,
                LumoUtility.Padding.Horizontal.SMALL,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.BorderRadius.SMALL
            );
            return badge;
        }
        return new Span("");
    }

    private HorizontalLayout createActionButtons(DeliveryAddress address) {
        Button setDefaultButton = new Button(new Icon(VaadinIcon.CHECK));
        setDefaultButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        setDefaultButton.getElement().setAttribute("aria-label", "Set as default");
        setDefaultButton.setEnabled(!address.isDefault());
        setDefaultButton.addClickListener(e -> setDefaultAddress(address));

        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editButton.getElement().setAttribute("aria-label", "Edit address");
        editButton.addClickListener(e -> editAddress(address));

        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.getElement().setAttribute("aria-label", "Delete address");
        deleteButton.addClickListener(e -> deleteAddress(address));

        HorizontalLayout actions = new HorizontalLayout(setDefaultButton, editButton, deleteButton);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        actions.setSpacing(true);
        return actions;
    }

    private void createAddAddressDialog() {
        addAddressDialog = new Dialog();
        addAddressDialog.setModal(true);
        addAddressDialog.setDraggable(false);
        addAddressDialog.setResizable(false);
        addAddressDialog.setWidth("600px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        H2 dialogTitle = new H2("Add New Delivery Address");
        dialogTitle.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        // Form fields
        TextField nameField = new TextField("Full Name");
        nameField.setRequired(true);
        nameField.setPlaceholder("Enter your full name");

        TextField phoneField = new TextField("Phone Number");
        phoneField.setRequired(true);
        phoneField.setPlaceholder("Enter phone number");

        TextField addressLine1Field = new TextField("Address Line 1");
        addressLine1Field.setRequired(true);
        addressLine1Field.setPlaceholder("Street address");

        TextField addressLine2Field = new TextField("Address Line 2");
        addressLine2Field.setPlaceholder("Apartment, suite, etc. (optional)");

        TextField cityField = new TextField("City");
        cityField.setRequired(true);
        cityField.setPlaceholder("Enter city");

        TextField stateField = new TextField("State");
        stateField.setRequired(true);
        stateField.setPlaceholder("Enter state");

        TextField zipField = new TextField("ZIP Code");
        zipField.setRequired(true);
        zipField.setPlaceholder("Enter ZIP code");

        ComboBox<String> typeField = new ComboBox<>("Address Type");
        typeField.setItems("HOME", "WORK", "OTHER");
        typeField.setValue("HOME");
        typeField.setRequired(true);

        TextField landmarkField = new TextField("Landmark");
        landmarkField.setPlaceholder("Nearby landmark (optional)");

        TextArea instructionsField = new TextArea("Delivery Instructions");
        instructionsField.setPlaceholder("Special delivery instructions (optional)");
        instructionsField.setHeight("80px");

        formLayout.add(nameField, phoneField);
        formLayout.add(addressLine1Field, 2);
        formLayout.add(addressLine2Field, 2);
        formLayout.add(cityField, stateField);
        formLayout.add(zipField, typeField);
        formLayout.add(landmarkField, 2);
        formLayout.add(instructionsField, 2);

        // Buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> {
            clearForm(nameField, phoneField, addressLine1Field, addressLine2Field, 
                     cityField, stateField, zipField, typeField, landmarkField, instructionsField);
            addAddressDialog.close();
        });

        Button saveButton = new Button("Save Address", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (validateAddressForm(nameField, phoneField, addressLine1Field, cityField, stateField, zipField)) {
                addAddress(nameField.getValue(), phoneField.getValue(), addressLine1Field.getValue(),
                          addressLine2Field.getValue(), cityField.getValue(), stateField.getValue(),
                          zipField.getValue(), typeField.getValue(), landmarkField.getValue(),
                          instructionsField.getValue());
                clearForm(nameField, phoneField, addressLine1Field, addressLine2Field, 
                         cityField, stateField, zipField, typeField, landmarkField, instructionsField);
                addAddressDialog.close();
            }
        });

        buttonLayout.add(cancelButton, saveButton);

        dialogLayout.add(dialogTitle, formLayout, buttonLayout);
        addAddressDialog.add(dialogLayout);
    }

    private boolean validateAddressForm(TextField nameField, TextField phoneField, TextField addressLine1Field,
                                       TextField cityField, TextField stateField, TextField zipField) {
        boolean isValid = true;

        if (nameField.getValue() == null || nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Name is required");
            isValid = false;
        } else {
            nameField.setInvalid(false);
        }

        if (phoneField.getValue() == null || phoneField.getValue().trim().isEmpty()) {
            phoneField.setInvalid(true);
            phoneField.setErrorMessage("Phone number is required");
            isValid = false;
        } else {
            phoneField.setInvalid(false);
        }

        if (addressLine1Field.getValue() == null || addressLine1Field.getValue().trim().isEmpty()) {
            addressLine1Field.setInvalid(true);
            addressLine1Field.setErrorMessage("Address is required");
            isValid = false;
        } else {
            addressLine1Field.setInvalid(false);
        }

        if (cityField.getValue() == null || cityField.getValue().trim().isEmpty()) {
            cityField.setInvalid(true);
            cityField.setErrorMessage("City is required");
            isValid = false;
        } else {
            cityField.setInvalid(false);
        }

        if (stateField.getValue() == null || stateField.getValue().trim().isEmpty()) {
            stateField.setInvalid(true);
            stateField.setErrorMessage("State is required");
            isValid = false;
        } else {
            stateField.setInvalid(false);
        }

        if (zipField.getValue() == null || zipField.getValue().trim().isEmpty()) {
            zipField.setInvalid(true);
            zipField.setErrorMessage("ZIP code is required");
            isValid = false;
        } else {
            zipField.setInvalid(false);
        }

        return isValid;
    }

    private void clearForm(TextField nameField, TextField phoneField, TextField addressLine1Field,
                          TextField addressLine2Field, TextField cityField, TextField stateField,
                          TextField zipField, ComboBox<String> typeField, TextField landmarkField,
                          TextArea instructionsField) {
        nameField.clear();
        nameField.setInvalid(false);
        phoneField.clear();
        phoneField.setInvalid(false);
        addressLine1Field.clear();
        addressLine1Field.setInvalid(false);
        addressLine2Field.clear();
        cityField.clear();
        cityField.setInvalid(false);
        stateField.clear();
        stateField.setInvalid(false);
        zipField.clear();
        zipField.setInvalid(false);
        typeField.setValue("HOME");
        landmarkField.clear();
        instructionsField.clear();
    }

    private void openAddAddressDialog() {
        addAddressDialog.open();
    }

    private void addAddress(String fullName, String phoneNumber, String addressLine1, String addressLine2,
                           String city, String state, String zipCode, String addressType,
                           String landmark, String deliveryInstructions) {
        try {
            DeliveryAddress newAddress = deliveryService.addAddress(fullName, phoneNumber, addressLine1,
                    addressLine2, city, state, zipCode, addressType, landmark, deliveryInstructions);
            refreshGrid();

            Notification notification = Notification.show(
                "Address added successfully!",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification notification = Notification.show(
                "Error adding address: " + e.getMessage(),
                5000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void setDefaultAddress(DeliveryAddress address) {
        boolean success = deliveryService.setDefaultAddress(address.getId());
        if (success) {
            refreshGrid();
            Notification notification = Notification.show(
                "Default address updated!",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            Notification notification = Notification.show(
                "Error updating default address",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editAddress(DeliveryAddress address) {
        // TODO: Implement edit functionality
        Notification.show("Edit functionality coming soon!", 3000, Notification.Position.TOP_END);
    }

    private void deleteAddress(DeliveryAddress address) {
        boolean deleted = deliveryService.removeAddress(address.getId());
        if (deleted) {
            refreshGrid();
            Notification notification = Notification.show(
                "Address deleted successfully!",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            Notification notification = Notification.show(
                "Error deleting address",
                3000,
                Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshGrid() {
        addressGrid.setItems(deliveryService.getAllAddresses());

        if (!deliveryService.hasAddresses()) {
            // Show empty state message
            // Could add an empty state component here
        }
    }
}
