package test.fujitsu.videostore.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class RentalStoreLayout<O extends RentalStoreItem> extends HorizontalLayout {

    private TextField filter;
    private Button btn;

    public abstract ListDataProvider<O> getProvider();
    public abstract RentalStoreLogic<O> getLogic();
    public abstract Grid<O> getGrid();
    public abstract RentalStoreForm<O> getForm();

    public abstract String getBtnTitle();
    public abstract String getFilterPlaceholder();

    public HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setId("filter");
        filter.setPlaceholder(getFilterPlaceholder());
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> {
            getProvider().addFilter(item -> StringUtils.containsIgnoreCase(item.getName(),
                    filter.getValue()));
        });

        btn = new Button(getBtnTitle());
        btn.setId("new-item");
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btn.addClickListener(click -> getLogic().newItem());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(btn);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    public void setNewItemEnabled(boolean enabled) {
        btn.setEnabled(enabled);
    }

    public void clearSelection() {
        getGrid().getSelectionModel().deselectAll();
    }

    public void selectRow(O row) {
        getGrid().getSelectionModel().select(row);
    }

    public void addItem(O item) {
        getProvider().getItems().add(item);
        getGrid().getDataProvider().refreshAll();
    }

    public void updateItem(O item) {
        getProvider().refreshItem(item);
    }

    public void removeItem(O item) {
        getProvider().getItems().remove(item);
        getProvider().refreshAll();
    }

    public void editItem(O item) {
        showForm(item != null);
        getForm().editItem(item);
    }

    public void showForm(boolean show) {
        getForm().setVisible(show);
    }

    public void setItems(List<O> items) {
        getProvider().getItems().clear();
        getProvider().getItems().addAll(items);
        getProvider().refreshAll();
    }
}
