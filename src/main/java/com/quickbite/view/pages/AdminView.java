package com.quickbite.view.pages;

import com.quickbite.model.Order;
import com.quickbite.service.OrderService;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Admin page view - Displays all placed orders and business statistics
 */
@PageTitle("QuickBite - Admin Panel")
@Route(value = "admin", layout = MainLayout.class)
public class AdminView extends VerticalLayout {

    private final OrderService orderService;
    private Grid<Order> ordersGrid;
    private Div statisticsSection;

    @Autowired
    public AdminView(OrderService orderService) {
        this.orderService = orderService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createHeader();
        createStatisticsSection();
        createOrdersGrid();
        createActionButtons();
        refreshData();
    }

    private void createHeader() {
        H1 title = new H1("👨‍💼 Admin Panel");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);
        
        Paragraph subtitle = new Paragraph("Manage orders and view business statistics");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY);
        
        add(title, subtitle);
    }

    private void createStatisticsSection() {
        H2 statsTitle = new H2("📊 Business Statistics");
        statsTitle.addClassNames(LumoUtility.TextColor.PRIMARY);
        
        statisticsSection = new Div();
        statisticsSection.addClassNames(
            LumoUtility.Display.GRID,
            LumoUtility.Gap.MEDIUM,
            LumoUtility.Margin.Bottom.LARGE
        );
        statisticsSection.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(200px, 1fr))");
        
        add(statsTitle, statisticsSection);
    }

    private void createOrdersGrid() {
        H2 ordersTitle = new H2("📋 Recent Orders");
        ordersTitle.addClassNames(LumoUtility.TextColor.PRIMARY);
        
        ordersGrid = new Grid<>(Order.class, false);
        ordersGrid.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.MEDIUM);
        
        // Order ID column
        ordersGrid.addColumn(order -> "#" + order.getId())
                .setHeader("Order ID")
                .setFlexGrow(1);

        // Order time column
        ordersGrid.addColumn(Order::getFormattedOrderTime)
                .setHeader("Order Time")
                .setFlexGrow(2);

        // Item count column
        ordersGrid.addColumn(Order::getTotalItemCount)
                .setHeader("Items")
                .setFlexGrow(1);

        // Total amount column
        ordersGrid.addColumn(Order::getFormattedTotalAmount)
                .setHeader("Total Amount")
                .setFlexGrow(1);

        // Status column
        ordersGrid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setFlexGrow(1);

        ordersGrid.setHeight("400px");
        
        add(ordersTitle, ordersGrid);
    }

    private Span createStatusBadge(Order order) {
        Span statusBadge = new Span(order.getStatus());
        statusBadge.getElement().getThemeList().add("badge");
        
        switch (order.getStatus().toUpperCase()) {
            case "CONFIRMED":
                statusBadge.getElement().getThemeList().add("success");
                break;
            case "PREPARING":
                statusBadge.getElement().getThemeList().add("contrast");
                break;
            case "READY":
                statusBadge.getElement().getThemeList().add("primary");
                break;
            case "DELIVERED":
                statusBadge.getElement().getThemeList().add("success");
                break;
            default:
                statusBadge.getElement().getThemeList().add("contrast");
        }
        
        return statusBadge;
    }

    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> refreshData());

        Button backToMenuButton = new Button("Back to Menu", new Icon(VaadinIcon.ARROW_LEFT));
        backToMenuButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        backToMenuButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(MenuView.class)));

        buttonLayout.add(refreshButton, backToMenuButton);
        add(buttonLayout);
    }

    private void refreshData() {
        updateStatistics();
        updateOrdersGrid();
    }

    private void updateStatistics() {
        statisticsSection.removeAll();
        
        // Total orders card
        Div totalOrdersCard = createStatCard(
            "Total Orders",
            String.valueOf(orderService.getTotalOrderCount()),
            VaadinIcon.LIST,
            "primary"
        );

        // Total revenue card
        Div totalRevenueCard = createStatCard(
            "Total Revenue",
            orderService.getFormattedTotalRevenue(),
            VaadinIcon.DOLLAR,
            "success"
        );

        // Average order value card
        Div avgOrderCard = createStatCard(
            "Average Order",
            orderService.getFormattedAverageOrderValue(),
            VaadinIcon.CHART,
            "contrast"
        );

        statisticsSection.add(totalOrdersCard, totalRevenueCard, avgOrderCard);
    }

    private Div createStatCard(String title, String value, VaadinIcon iconType, String theme) {
        Div card = new Div();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL
        );

        HorizontalLayout cardLayout = new HorizontalLayout();
        cardLayout.setWidthFull();
        cardLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        cardLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(false);

        Paragraph cardTitle = new Paragraph(title);
        cardTitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.Margin.NONE);

        Span cardValue = new Span(value);
        cardValue.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD);
        
        switch (theme) {
            case "primary":
                cardValue.addClassNames(LumoUtility.TextColor.PRIMARY);
                break;
            case "success":
                cardValue.addClassNames(LumoUtility.TextColor.SUCCESS);
                break;
            case "contrast":
                cardValue.addClassNames(LumoUtility.TextColor.BODY);
                break;
        }

        textLayout.add(cardTitle, cardValue);

        Icon icon = new Icon(iconType);
        icon.addClassNames(LumoUtility.IconSize.LARGE, LumoUtility.TextColor.TERTIARY);

        cardLayout.add(textLayout, icon);
        card.add(cardLayout);

        return card;
    }

    private void updateOrdersGrid() {
        if (orderService.hasOrders()) {
            ordersGrid.setItems(orderService.getOrdersSortedByTime());
            ordersGrid.setVisible(true);
        } else {
            ordersGrid.setVisible(false);
            
            // Show empty state
            add(getEmptyStateMessage());
        }
    }

    private Div getEmptyStateMessage() {
        Div emptyState = new Div();
        emptyState.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.XLARGE
        );
        emptyState.getStyle().set("text-align", "center");

        Icon emptyIcon = new Icon(VaadinIcon.INBOX);
        emptyIcon.addClassNames(LumoUtility.IconSize.LARGE, LumoUtility.TextColor.TERTIARY);

        Paragraph emptyMessage = new Paragraph("No orders yet. Orders will appear here once customers start placing them.");
        emptyMessage.addClassNames(LumoUtility.TextColor.SECONDARY);

        emptyState.add(emptyIcon, emptyMessage);
        return emptyState;
    }
}
