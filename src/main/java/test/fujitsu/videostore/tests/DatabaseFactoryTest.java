package test.fujitsu.videostore.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.database.Database;
import test.fujitsu.videostore.backend.database.DatabaseFactory;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseFactoryTest {

    private DBTableRepository<Movie> moviesRepository;
    private DBTableRepository<Customer> customersRepository;
    private DBTableRepository<RentOrder> ordersRepository;

    private Movie testMovie;
    private Customer testCustomer;
    private RentOrder testOrder;

    @Before
    public void setUp() throws Exception {
        Database database = DatabaseFactory.from("db-examples/database_test.json");
        moviesRepository = database.getMovieTable();
        customersRepository = database.getCustomerTable();
        ordersRepository = database.getOrderTable();

        testMovie = new Movie();
        testMovie.setName("Test Movie");
        testMovie.setType(1);
        testMovie.setStockCount(10);

        testCustomer = new Customer();
        testCustomer.setName("Tester Test");
        testCustomer.setPoints(20);

        testOrder = new RentOrder();
        testOrder.setCustomer(customersRepository.getAll().get(0));
        testOrder.setOrderDate(LocalDate.of(2019, 3, 24));
        testOrder.setItems(ordersRepository.getAll().get(0).getItems());
    }

    @Test
    public void testMovieRepositoryNotEmpty() {
        Assert.assertFalse(moviesRepository.getAll().isEmpty());
    }

    @Test
    public void testCustomerRepositoryNotEmpty() {
        Assert.assertFalse(customersRepository.getAll().isEmpty());
    }

    @Test
    public void testOrderRepositoryNotEmpty() {
        Assert.assertFalse(ordersRepository.getAll().isEmpty());
    }

    @Test
    public void testFirstMovieCorrect() {
        Assert.assertEquals(moviesRepository.getAll().get(0).getId(), 1);
        Assert.assertEquals(moviesRepository.getAll().get(0).getName(), "The Avengers");
        Assert.assertEquals(moviesRepository.getAll().get(0).getStockCount(), 25);
        Assert.assertEquals(moviesRepository.getAll().get(0).getType(), MovieType.REGULAR);
    }

    @Test
    public void testFirstCustomerCorrect() {
        Assert.assertEquals(customersRepository.getAll().get(0).getId(), 1);
        Assert.assertEquals(customersRepository.getAll().get(0).getName(), "Maria Kusk");
        Assert.assertEquals(customersRepository.getAll().get(0).getPoints(), 32);
    }

    @Test
    public void testFirstOrderCorrect() {
        Assert.assertEquals(ordersRepository.getAll().get(0).getId(), 1);
        Assert.assertEquals(ordersRepository.getAll().get(0).getCustomer().getId(), 2);
        Assert.assertEquals(ordersRepository.getAll().get(0).getOrderDate().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")), "2019-01-20");
    }

    @Test
    public void testFindMovieById() {
        Assert.assertEquals(moviesRepository.findById(214).getName(), "The Upside");
        Assert.assertEquals(moviesRepository.findById(214).getStockCount(), 5);
        Assert.assertEquals(moviesRepository.findById(214).getType(), MovieType.NEW);
    }

    @Test
    public void testFindCustomerById() {
        Assert.assertEquals(customersRepository.findById(3).getName(), "Irina Tamm");
        Assert.assertEquals(customersRepository.findById(3).getPoints(), 455);
    }

    @Test
    public void testFindOrderById() {
        Assert.assertEquals(ordersRepository.findById(1).getCustomer().getId(), 2);
        Assert.assertEquals(ordersRepository.findById(1).getOrderDate().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")), "2019-01-20");
    }

    @Test
    public void testCreateAndRemoveNewMovie() {
        Movie movie = moviesRepository.createOrUpdate(testMovie);

        Assert.assertEquals(movie.getName(), "Test Movie");
        Assert.assertEquals(movie.getStockCount(), 10);
        Assert.assertEquals(movie.getType(), MovieType.NEW);

        Assert.assertTrue(moviesRepository.remove(testMovie));
    }

    @Test
    public void testCreateAndRemoveNewCustomer() {
        Customer customer = customersRepository.createOrUpdate(testCustomer);

        Assert.assertEquals(customer.getName(), "Tester Test");
        Assert.assertEquals(customer.getPoints(), 20);

        Assert.assertTrue(customersRepository.remove(testCustomer));
    }

    @Test
    public void testCreateAndRemoveNewOrder() {
        RentOrder order = ordersRepository.createOrUpdate(testOrder);

        Assert.assertEquals(order.getCustomer(), customersRepository.getAll().get(0));
        Assert.assertEquals(order.getOrderDate(), LocalDate.of(2019, 3, 24));
        Assert.assertEquals(order.getItems(), ordersRepository.getAll().get(0).getItems());

        Assert.assertTrue(ordersRepository.remove(testOrder));
    }

}