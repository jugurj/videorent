package test.fujitsu.videostore.ui.order;

import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.reciept.OrderToReceiptService;
import test.fujitsu.videostore.ui.RentalStoreLogic;
import test.fujitsu.videostore.ui.database.CurrentDatabase;

public class OrderListLogic extends RentalStoreLogic<RentOrder> {

    private DBTableRepository<RentOrder> repository;
    private OrderToReceiptService orderToReceiptService;

    public OrderListLogic(OrderList orderList) {
        super(orderList);
        orderToReceiptService = new OrderToReceiptService();
    }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }
        repository = CurrentDatabase.get().getOrderTable();
        view.setNewItemEnabled(true);
        view.setItems(repository.getAll());
    }

    @Override
    public DBTableRepository<RentOrder> getRepo() {
        return repository;
    }

    @Override
    public RentOrder createNewItem() {
        return new RentOrder();
    }

    @Override
    public Class getViewClass() {
        return OrderList.class;
    }

    public OrderToReceiptService getOrderToReceiptService() {
        return orderToReceiptService;
    }
}
