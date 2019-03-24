package test.fujitsu.videostore.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import test.fujitsu.videostore.ui.RentalStoreItem;

/**
 * Movie domain object
 */
public class Movie implements RentalStoreItem {

    /**
     * Movie ID
     */
    private int id = -1;

    /**
     * Movie name
     */
    private String name;

    /**
     * Movies in stock
     */
    private int stockCount = 0;

    /**
     * Movie type
     */
    private MovieType type = MovieType.NEW;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public MovieType getType() {
        return type;
    }

    @JsonIgnore
    public void setType(MovieType type) {
        this.type = type;
    }

    public void setType(int type) {
        this.type = MovieType.values()[type - 1];
    }

    /**
     * New object for database or not
     *
     * @return boolean
     */
    public boolean isNewObject() {
        return id == -1;
    }
}