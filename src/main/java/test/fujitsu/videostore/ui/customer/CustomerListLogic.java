package test.fujitsu.videostore.ui.customer;

import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.RentalStoreLogic;
import test.fujitsu.videostore.ui.database.CurrentDatabase;

public class CustomerListLogic extends RentalStoreLogic<Customer> {

    private DBTableRepository<Customer> customerDBTableRepository;

    public CustomerListLogic(CustomerList customerList) {
        super(customerList);
    }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }

        customerDBTableRepository = CurrentDatabase.get().getCustomerTable();

        view.setNewItemEnabled(true);
        view.setItems(customerDBTableRepository.getAll());
    }

    @Override
    public DBTableRepository<Customer> getRepo() {
        return customerDBTableRepository;
    }

    @Override
    public Customer createNewItem() {
        return new Customer();
    }

    @Override
    public Class getViewClass() {
        return CustomerList.class;
    }
}
