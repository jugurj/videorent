package test.fujitsu.videostore.ui.inventory;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.RentalStoreForm;
import test.fujitsu.videostore.ui.RentalStoreLayout;
import test.fujitsu.videostore.ui.RentalStoreLogic;
import test.fujitsu.videostore.ui.inventory.components.MovieForm;
import test.fujitsu.videostore.ui.inventory.components.MovieGrid;

import java.util.ArrayList;

@Route(value = VideoStoreInventory.VIEW_NAME, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class VideoStoreInventory extends RentalStoreLayout<Movie> implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "Inventory";
    public static final String BTN_NAME = "New Movie";
    public static final String FILTER_PLACEHOLDER = "Filter by movie name";

    private MovieGrid grid;
    private MovieForm form;

    private ListDataProvider<Movie> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private VideoStoreInventoryLogic viewLogic = new VideoStoreInventoryLogic(this);

    public VideoStoreInventory() {
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new MovieGrid();
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));
        grid.setDataProvider(dataProvider);

        form = new MovieForm(viewLogic);

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
    public ListDataProvider<Movie> getProvider() {
        return dataProvider;
    }

    @Override
    public RentalStoreLogic<Movie> getLogic() {
        return viewLogic;
    }

    @Override
    public Grid<Movie> getGrid() {
        return grid;
    }

    @Override
    public RentalStoreForm getForm() {
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
