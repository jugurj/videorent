package test.fujitsu.videostore.ui;

import com.vaadin.flow.component.UI;
import test.fujitsu.videostore.backend.database.DBTableRepository;

public abstract class RentalStoreLogic<T extends RentalStoreItem> {

    protected RentalStoreLayout view;

    public abstract void init();

    public abstract DBTableRepository<T> getRepo();

    public abstract T createNewItem();

    public abstract Class getViewClass();

    public RentalStoreLogic(RentalStoreLayout viewRef) {
        view = viewRef;
    }

    public void cancelItem() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String itemId) {
        String fragmentParameter;
        if (itemId == null || itemId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = itemId;
        }

        UI.getCurrent().navigate(getViewClass(), fragmentParameter);
    }

    public void enter(String itemId) {
        if (itemId != null && !itemId.isEmpty()) {
            if (itemId.equals("new")) {
                newItem();
            } else {
                try {
                    int pid = Integer.parseInt(itemId);
                    T item = findItem(pid);
                    view.selectRow(item);
                } catch (NumberFormatException ex) {
                    // Ignored
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private T findItem(int itemId) {
        return getRepo().findById(itemId);
    }

    public void saveItem(T item) {
        boolean isNew = item.isNewObject();

        T updatedItemObject = getRepo().createOrUpdate(item);

        if (isNew) {
            view.addItem(updatedItemObject);
        } else {
            view.updateItem(item);
        }

        view.clearSelection();
        setFragmentParameter("");
        view.showSaveNotification(item.getName() + (isNew ? " created" : " updated"));
    }

    public void deleteItem(T item) {
        getRepo().remove(item);

        view.clearSelection();
        view.removeItem(item);

        setFragmentParameter("");
        view.showSaveNotification(item.getName() + " removed");
    }

    /**
     * Method fired when user selects item which he want to edit.
     *
     * @param item T object
     */
    public void editItem(T item) {
        if (item == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(item.getId() + "");
        }
        view.editItem(item);
    }

    public void newItem() {
        view.editItem(createNewItem());
        view.clearSelection();
        setFragmentParameter("new");
    }

    public void rowSelected(T item) {
        editItem(item);
    }

}
