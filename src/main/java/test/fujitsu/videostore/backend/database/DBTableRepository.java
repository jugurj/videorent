package test.fujitsu.videostore.backend.database;

import org.json.JSONObject;

import java.util.List;

/**
 * Database repository interface
 *
 * @param <T> Stored data type
 */
public interface DBTableRepository<T> {

    /**
     * Fetches all object from table
     *
     * @return list of object
     */
    List<T> getAll();

    /**
     * Finds specific object from table using ID field
     *
     * @param id object id
     * @return found object
     */
    T findById(int id);

    /**
     * Finds specific object's index in JSON array
     *
     * @param object object to find index
     * @return index of object in JSON array
     */
    int getIndex(T object);

    /**
     * Removes object
     *
     * @param object object for removal
     * @return object removed or not
     */
    boolean remove (T object);

    /**
     * Creates or updates object.
     * <p>
     * If object without ID or ID is -1, then it will be object creation. In case of creation ID should be set to provided object
     * If updating existing object, then returning object which was updated from database
     *
     * @param object object to create or update
     * @return updated object
     */
    T createOrUpdate (T object);

    /**
     * New ID generation for table. Should be always unique
     *
     * @return next id sequence
     */
    int generateNextId();

    /**
     * Parse Java object to JSON object
     *
     * @param object object to parser to JSON object
     * @return JSON object from given Java object
     */
    JSONObject toJSON(T object);

}
