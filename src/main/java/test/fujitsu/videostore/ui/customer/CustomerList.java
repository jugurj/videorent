package test.fujitsu.videostore.ui.customer;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.RentalStoreForm;
import test.fujitsu.videostore.ui.RentalStoreLayout;
import test.fujitsu.videostore.ui.RentalStoreLogic;
import test.fujitsu.videostore.ui.customer.components.CustomerForm;
import test.fujitsu.videostore.ui.customer.components.CustomerGrid;

import java.util.ArrayList;

@Route(value = CustomerList.VIEW_NAME, layout = MainLayout.class)
public class CustomerList extends RentalStoreLayout<Customer> implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "CustomerList";
    public static final String BTN_NAME = "New Customer";
    public static final String FILTER_PLACEHOLDER = "Filter by customer name";

    private CustomerGrid grid;
    private CustomerForm form;

    private ListDataProvider<Customer> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private CustomerListLogic viewLogic = new CustomerListLogic(this);

    public CustomerList() {
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new CustomerGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new CustomerForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init();
    }

    @Override
    public ListDataProvider<Customer> getProvider() {
        return dataProvider;
    }

    @Override
    public RentalStoreLogic<Customer> getLogic() {
        return viewLogic;
    }

    @Override
    public Grid<Customer> getGrid() {
        return grid;
    }

    @Override
    public RentalStoreForm<Customer> getForm() {
        return form;
    }

    @Override
    public String getBtnTitle() {
        return BTN_NAME;
    }

    @Override
    public String getFilterPlaceholder() {
        return FILTER_PLACEHOLDER;
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        viewLogic.enter(parameter);
    }
}
