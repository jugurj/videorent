package test.fujitsu.videostore.ui;

import com.vaadin.flow.component.html.Div;

public abstract class RentalStoreForm<O extends RentalStoreItem> extends Div {

    public abstract void editItem(O item);
}
