package test.fujitsu.videostore.backend.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import org.apache.commons.io.FilenameUtils;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Database Factory.
 */
public class DatabaseFactory {

    /**
     * Creates database "connection"/opens database from path.
     * <p>
     * Two example files, /db-examples/database.json and /db-examples/database.yaml.
     * Hint: MovieType.databaseId == type field in database files.
     *
     * @param filePath file path to database
     * @return database proxy for different tables
     */

    public static Database from(String filePath) {

        return new Database() {
            @Override
            public DBTableRepository<Movie> getMovieTable() {

                final List<Movie> movieList = new ArrayList<>();
                JSONObject databaseJsonObj = parseData(filePath);
                JSONArray movieJsonArray = databaseJsonObj.optJSONArray("movie");

                try {
                    for (int i = 0; i < movieJsonArray.length(); i++) {
                        JSONObject movieJsonObj = movieJsonArray.getJSONObject(i);

                        Movie movie = new Movie();
                        movie.setId(movieJsonObj.getInt("id"));
                        movie.setName(movieJsonObj.getString("name"));
                        movie.setStockCount(movieJsonObj.getInt("stockCount"));
                        movie.setType(movieJsonObj.getInt("type"));

                        movieList.add(movie);
                    }
                    // Sorting objects in list by id to enable faster id generation.
                    movieList.sort(Comparator.comparing(Movie::getId));
                } catch(JSONException e){
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    System.out.println("Loaded file has inappropriate structure!");
                }



                return new DBTableRepository<Movie>() {

                    @Override
                    public List<Movie> getAll() {
                        return movieList;
                    }

                    @Override
                    public Movie findById(int id) {
                        return getAll().stream().filter(movie -> movie.getId() == id).findFirst().get();
                    }

                    @Override
                    public int getIndex(Movie object) {
                        try {
                            for (int index = 0; index < movieJsonArray.length(); index++) {
                                JSONObject movieJsonObj = movieJsonArray.getJSONObject(index);
                                if (movieJsonObj.getInt("id") == object.getId()) {
                                    return index;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } return 0;
                    }

                    @Override
                    public boolean remove(Movie object) {
                        if (object == null) return false;

                        movieJsonArray.remove(this.getIndex(object));
                        writeData(filePath, databaseJsonObj);

                        return movieList.remove(object);
                    }

                    @Override
                    public Movie createOrUpdate(Movie object) {
                        if (object == null) return null;

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            JSONObject movieJsonObj = toJSON(object);

                            movieJsonArray.put(movieJsonObj);
                            writeData(filePath, databaseJsonObj);

                            movieList.add(object);
                            movieList.sort(Comparator.comparing(Movie::getId));

                            return object;
                        }

                        Movie movie = findById(object.getId());
                        movie.setName(object.getName());
                        movie.setStockCount(object.getStockCount());
                        movie.setType(object.getType());
                        JSONObject movieJsonObj = toJSON(movie);

                        try {
                            movieJsonArray.put(getIndex(movie), movieJsonObj);
                            writeData(filePath, databaseJsonObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return movie;
                    }

                    @Override
                    public int generateNextId() {
                        int uniId = 1;

                        for(Movie movie : movieList) {
                            if (uniId != movie.getId()) break;
                            uniId++;
                        } return uniId;
                    }

                    @Override
                    public JSONObject toJSON(Movie object) {
                        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        try {
                            String jsonString = objectWriter.writeValueAsString(object);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            jsonObject.put("type", object.getType().getDatabaseId());
                            jsonObject.remove("newObject");

                            return jsonObject;
                        } catch (JsonProcessingException | JSONException e) {
                            e.printStackTrace();
                        } return null;
                    }
                };
            }

            @Override
            public DBTableRepository<Customer> getCustomerTable() {
                final List<Customer> customerList = new ArrayList<>();

                JSONObject databaseJsonObj = parseData(filePath);
                JSONArray customerJsonArray = databaseJsonObj.optJSONArray("customer");

                try {
                    for (int i = 0; i < customerJsonArray.length(); i++) {
                        JSONObject customerJsonObj = customerJsonArray.getJSONObject(i);

                        Customer customer = new Customer();
                        customer.setId(customerJsonObj.getInt("id"));
                        customer.setName(customerJsonObj.getString("name"));
                        customer.setPoints(customerJsonObj.getInt("points"));

                        customerList.add(customer);
                    }
                    // Sorting objects in list by id to enable faster id generation.
                    customerList.sort(Comparator.comparing(Customer::getId));
                } catch(JSONException e){
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    System.out.println("Loaded file has inappropriate structure!");
                }

                return new DBTableRepository<Customer>() {
                    @Override
                    public List<Customer> getAll() {
                        return customerList;
                    }

                    @Override
                    public Customer findById(int id) {
                        return getAll().stream().filter(customer -> customer.getId() == id).findFirst().get();
                    }

                    @Override
                    public int getIndex(Customer object) {
                        try {
                            for (int index = 0; index < customerJsonArray.length(); index++) {
                                JSONObject movieJsonObj = customerJsonArray.getJSONObject(index);
                                if (movieJsonObj.getInt("id") == object.getId()) {
                                    return index;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } return 0;
                    }

                    @Override
                    public boolean remove(Customer object) {
                        if (object == null) return false;

                        customerJsonArray.remove(this.getIndex(object));
                        writeData(filePath, databaseJsonObj);

                        return customerList.remove(object);
                    }

                    @Override
                    public Customer createOrUpdate(Customer object) {
                        if (object == null) return null;

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            JSONObject movieJsonObj = toJSON(object);

                            customerJsonArray.put(movieJsonObj);
                            writeData(filePath, databaseJsonObj);

                            customerList.add(object);
                            customerList.sort(Comparator.comparing(Customer::getId));

                            return object;
                        }

                        Customer customer = findById(object.getId());
                        customer.setName(object.getName());
                        customer.setPoints(object.getPoints());
                        JSONObject customerJsonObj = toJSON(customer);

                        try {
                            customerJsonArray.put(getIndex(customer), customerJsonObj);
                            writeData(filePath, databaseJsonObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return customer;
                    }

                    @Override
                    public int generateNextId() {
                        int uniId = 1;

                        for(Customer customer : customerList) {
                            if (uniId != customer.getId()) break;
                            uniId++;
                        } return uniId;
                    }

                    @Override
                    public JSONObject toJSON(Customer object) {
                        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        try {
                            String jsonString = objectWriter.writeValueAsString(object);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            jsonObject.remove("newObject");

                            return jsonObject;
                        } catch (JsonProcessingException | JSONException e) {
                            e.printStackTrace();
                        } return null;
                    }
                };
            }

            @Override
            public DBTableRepository<RentOrder> getOrderTable() {

                final List<RentOrder> orderList = new ArrayList<>();

                JSONObject databaseJsonObj = parseData(filePath);
                JSONArray orderJsonArray = databaseJsonObj.optJSONArray("order");

                try {
                    for (int i = 0; i < orderJsonArray.length(); i++) {
                        JSONObject orderJsonObj = orderJsonArray.getJSONObject(i);
                        JSONArray orderItemsJsonArray = orderJsonObj.getJSONArray("items");

                        RentOrder order = new RentOrder();
                        order.setId(orderJsonObj.getInt("id"));
                        order.setCustomer(getCustomerTable().findById(orderJsonObj.getInt("customer")));
                        order.setOrderDate(LocalDate.parse(orderJsonObj.get("orderDate").toString()));

                        List<RentOrder.Item> orderItems = new ArrayList<>();

                        for (int j = 0; j < orderItemsJsonArray.length(); j++) {
                            JSONObject orderItemJsonObj = orderItemsJsonArray.getJSONObject(j);

                            RentOrder.Item item = new RentOrder.Item();
                            item.setMovie(getMovieTable().findById(orderItemJsonObj.getInt("movie")));
                            item.setMovieType(orderItemJsonObj.getInt("type"));
                            item.setPaidByBonus(orderItemJsonObj.getBoolean("paidByBonus"));
                            item.setDays(orderItemJsonObj.getInt("days"));

                            orderItems.add(item);
                        }

                        order.setItems(orderItems);
                        orderList.add(order);
                    }
                    // Sorting objects in list by id to enable faster id generation.
                    orderList.sort(Comparator.comparing(RentOrder::getId));
                } catch(JSONException e){
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    System.out.println("Loaded file has inappropriate structure!");
                }

                return new DBTableRepository<RentOrder>() {
                    @Override
                    public List<RentOrder> getAll() {
                        return orderList;
                    }

                    @Override
                    public RentOrder findById(int id) {
                        return getAll().stream().filter(order -> order.getId() == id).findFirst().get();
                    }

                    @Override
                    public int getIndex(RentOrder object) {
                        try {
                            for (int index = 0; index < orderJsonArray.length(); index++) {
                                JSONObject movieJsonObj = orderJsonArray.getJSONObject(index);
                                if (movieJsonObj.getInt("id") == object.getId()) {
                                    return index;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } return 0;
                    }

                    @Override
                    public boolean remove(RentOrder object) {
                        if (object == null) return false;

                        orderJsonArray.remove(this.getIndex(object));
                        writeData(filePath, databaseJsonObj);

                        return orderList.remove(object);
                    }

                    @Override
                    public RentOrder createOrUpdate(RentOrder object) {

                        if (object == null) return null;

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            JSONObject orderJsonObj = toJSON(object);

                            orderJsonArray.put(orderJsonObj);
                            writeData(filePath, databaseJsonObj);

                            orderList.add(object);
                            orderList.sort(Comparator.comparing(RentOrder::getId));

                            return object;
                        }

                        RentOrder order = findById(object.getId());

                        order.setCustomer(object.getCustomer());
                        order.setOrderDate(order.getOrderDate());
                        order.setItems(object.getItems());
                        JSONObject orderJsonObj = toJSON(order);

                        try {
                            orderJsonArray.put(getIndex(order), orderJsonObj);
                            writeData(filePath, databaseJsonObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return order;
                    }

                    @Override
                    public int generateNextId() {
                        int uniId = 1;

                        for(RentOrder order : orderList) {
                            if (uniId != order.getId()) break;
                            uniId++;
                        } return uniId;
                    }

                    @Override
                    public JSONObject toJSON(RentOrder object) {
                        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        try {
                            String jsonString = objectWriter.writeValueAsString(object);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            jsonObject.remove("newObject");
                            jsonObject.remove("name");
                            jsonObject.put("customer", object.getCustomer().getId());
                            jsonObject.put("orderDate", object.getOrderDate().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));

                            JSONArray orderItemsJsonArray = jsonObject.getJSONArray("items");
                            for(int index = 0; index < orderItemsJsonArray.length(); index++) {
                                JSONObject orderItemJsonObj = orderItemsJsonArray.getJSONObject(index);
                                orderItemJsonObj.put("movie", object.getItems().get(index).getMovie().getId());
                                orderItemJsonObj.put("type", object.getItems().get(index).getMovie().getType().getDatabaseId());
                                if(object.getItems().get(index).getReturnedDay() != null) {
                                    orderItemJsonObj.put("returnedDay",  object.getItems().get(index).getReturnedDay().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
                                }
                            }

                            return jsonObject;
                        } catch (JsonProcessingException | JSONException e) {
                            e.printStackTrace();
                        } catch (NoSuchElementException e) {
                            System.out.println("Array of items is empty.");
                        } return null;
                    }
                };
            }
        };
    }

    /**
     *
     * @param path path to file in database
     * @return string of content for given path file
     * @throws IOException error occurs when there are problems with file reading/access
     */
    private static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    /**
     * Parse file content into JSONObject. If it is a YAML format transform it into JSON format.
     *
     * @param filePath path to currently opened database file
     * @return JSONObject of database root
     */
    private static JSONObject parseData(String filePath) {

        try {
            String content = readFile(filePath);
            Map<String, Object> map = null;

            if (!FilenameUtils.getExtension(filePath).equals("json") && !FilenameUtils.getExtension(filePath).equals("yaml")) {
                throw new NoSuchFileException(filePath);
            } else if (FilenameUtils.getExtension(filePath).equals("yaml")) {
                Yaml yaml = new Yaml();
                map = (Map<String, Object>) yaml.load(content);
            }

            return (map == null) ?  new JSONObject(content) : new JSONObject(map);

        } catch (NoSuchFileException e) {
            System.out.println("That file extension is not allowed. " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("General I/O exception: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    /**
     * Rewrite file of opened database with updated values. If file was opened as YAML transform into YAML format.
     *
     * @param filePath path to currently opened database file
     * @param jsonObject database root in JSONObject format
     */
    private static void writeData(String filePath, JSONObject jsonObject) {
        try {
            if (FilenameUtils.getExtension(filePath).equals("yaml")) {
                Yaml yaml = new Yaml();
                Map<String,Object> map = (Map<String, Object>) yaml.load(jsonObject.toString(2));
                Files.write(Paths.get(filePath), yaml.dump(map).getBytes());
            } else {
                Files.write(Paths.get(filePath), jsonObject.toString(2).getBytes());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}
