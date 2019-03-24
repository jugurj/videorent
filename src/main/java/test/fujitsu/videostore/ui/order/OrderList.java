package test.fujitsu.videostore.ui.order;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.RentalStoreForm;
import test.fujitsu.videostore.ui.RentalStoreLayout;
import test.fujitsu.videostore.ui.RentalStoreLogic;
import test.fujitsu.videostore.ui.order.components.OrderForm;
import test.fujitsu.videostore.ui.order.components.OrderGrid;

import java.util.ArrayList;

@Route(value = OrderList.VIEW_NAME, layout = MainLayout.class)
public class OrderList extends RentalStoreLayout<RentOrder> implements HasUrlParameter<String> {

    static final String VIEW_NAME = "OrderList";
    public static final String BTN_NAME = "New Order";
    public static final String FILTER_PLACEHOLDER = "Filter by order name";

    private OrderGrid grid;
    private OrderForm form;

    private ListDataProvider<RentOrder> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private OrderListLogic viewLogic = new OrderListLogic(this);

    public OrderList() {
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new OrderGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new OrderForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);
        setFlexGrow(0, barAndGridLayout);
        setFlexGrow(1, form);

        viewLogic.init();
    }

    @Override
    public ListDataProvider<RentOrder> getProvider() {
        return dataProvider;
    }

    @Override
    public RentalStoreLogic<RentOrder> getLogic() {
        return viewLogic;
    }

    @Override
    public Grid<RentOrder> getGrid() {
        return grid;
    }

    @Override
    public RentalStoreForm<RentOrder> getForm() {
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
