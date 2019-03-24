package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple receipt creation service
 * <p>
 * Note! All calculations should be in another place. Here we just setting already calculated data. Feel free to refactor.
 */
public class OrderToReceiptService {

    private static final int PREMIUM_PRICE = 4;
    private static final int BASIC_PRICE = 3;
    private static final int REGULAR_DAYS_BEFORE_PENALTY = 3;
    private static final int OLD_DAYS_BEFORE_PENALTY = 5;
    private static final int BONUS_POINTS_PER_MOVIE = 25;

    /**
     * Converts rent order to printable receipt
     *
     * @param order rent object
     * @return Printable receipt object
     */
    public PrintableOrderReceipt convertRentOrderToReceipt(RentOrder order) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalBonusPoints = 0;
        int addedBonusPoints = 0;

        PrintableOrderReceipt printableOrderReceipt = new PrintableOrderReceipt();

        printableOrderReceipt.setOrderId(order.isNewObject() ? "new" : Integer.toString(order.getId()));
        printableOrderReceipt.setOrderDate(order.getOrderDate());
        printableOrderReceipt.setCustomerName(order.getCustomer().getName());

        List<PrintableOrderReceipt.Item> itemList = new ArrayList<>();
        printableOrderReceipt.setOrderItems(itemList);

        for (RentOrder.Item orderItem : order.getItems()) {
            PrintableOrderReceipt.Item item = new PrintableOrderReceipt.Item();
            item.setDays(orderItem.getDays());
            item.setMovieName(orderItem.getMovie().getName());
            item.setMovieType(orderItem.getMovieType());

            // Get 2 points for New Release and 1 for each other.
            addedBonusPoints += ((item.getMovieType().getDatabaseId() == 1) ? 2 : 1);

            if (orderItem.isPaidByBonus()) {
                totalBonusPoints += (BONUS_POINTS_PER_MOVIE * orderItem.getDays());
                item.setPaidBonus(BONUS_POINTS_PER_MOVIE * orderItem.getDays());
            } else {
                BigDecimal itemPrice = calcRentalPrice(item.getMovieType(), item.getDays());
                totalPrice = totalPrice.add(itemPrice);
                item.setPaidMoney(itemPrice);
            }

            itemList.add(item);
        }

        printableOrderReceipt.setTotalPrice(totalPrice);

        printableOrderReceipt.setRemainingBonusPoints(order.getCustomer().getPoints() - totalBonusPoints + addedBonusPoints);

        return printableOrderReceipt;
    }

    private static BigDecimal calcRentalPrice(MovieType movieType, int days) {
        switch(movieType) {
            case NEW: return BigDecimal.valueOf(PREMIUM_PRICE * days);
            case REGULAR: {
                if (days <= REGULAR_DAYS_BEFORE_PENALTY) return BigDecimal.valueOf(BASIC_PRICE);
                else return BigDecimal.valueOf(BASIC_PRICE + (BASIC_PRICE * (days - REGULAR_DAYS_BEFORE_PENALTY)));
            }
            case OLD: {
                if (days <= OLD_DAYS_BEFORE_PENALTY) return BigDecimal.valueOf(BASIC_PRICE);
                else return BigDecimal.valueOf(BASIC_PRICE + (BASIC_PRICE * (days - OLD_DAYS_BEFORE_PENALTY)));
            }
        } return BigDecimal.ZERO;
    }

    private static BigDecimal calcReturnPrice(MovieType movieType, int days, int extraDays) {
        switch(movieType) {
            case NEW: return BigDecimal.valueOf(PREMIUM_PRICE * extraDays);
            case REGULAR: {
                if (days + extraDays <= REGULAR_DAYS_BEFORE_PENALTY) return BigDecimal.ZERO;
                else return BigDecimal.valueOf(BASIC_PRICE * (extraDays + days - REGULAR_DAYS_BEFORE_PENALTY));
            }
            case OLD: {
                if (days + extraDays <= OLD_DAYS_BEFORE_PENALTY) return BigDecimal.ZERO;
                else return BigDecimal.valueOf(BASIC_PRICE * extraDays + days - OLD_DAYS_BEFORE_PENALTY);
            }
        } return BigDecimal.ZERO;
    }

    /**
     * Converts return order to printable receipt
     *
     * @param order return object
     * @return Printable receipt object
     */
    public PrintableReturnReceipt convertRentOrderToReceipt(ReturnOrder order) {
        BigDecimal totalExtraPrice = BigDecimal.ZERO;
        PrintableReturnReceipt receipt = new PrintableReturnReceipt();

        receipt.setOrderId(Integer.toString(order.getRentOrder().getId()));
        receipt.setCustomerName(order.getRentOrder().getCustomer().getName());
        receipt.setRentDate(order.getRentOrder().getOrderDate());
        receipt.setReturnDate(order.getReturnDate());

        List<PrintableReturnReceipt.Item> returnedItems = new ArrayList<>();
        if (order.getItems() != null) {
            for (RentOrder.Item rentedItem : order.getItems()) {
                PrintableReturnReceipt.Item item = new PrintableReturnReceipt.Item();
                item.setMovieName(rentedItem.getMovie().getName());
                item.setMovieType(rentedItem.getMovieType());

                LocalDate orderDate = order.getRentOrder().getOrderDate();
                LocalDate dayToReturn = orderDate.plusDays(rentedItem.getDays());
                int extraDays = Math.toIntExact(ChronoUnit.DAYS.between(dayToReturn, LocalDate.now()));
                item.setExtraDays(extraDays > 0 ? extraDays : 0);

                item.setExtraPrice(calcReturnPrice(item.getMovieType(), rentedItem.getDays(), item.getExtraDays()));
                totalExtraPrice = totalExtraPrice.add(item.getExtraPrice());
                returnedItems.add(item);
            }
        }
        receipt.setReturnedItems(returnedItems);

        receipt.setTotalCharge(totalExtraPrice);

        return receipt;
    }

}
