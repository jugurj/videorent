package test.fujitsu.videostore.ui.inventory;

import com.vaadin.flow.component.UI;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.ui.RentalStoreItem;
import test.fujitsu.videostore.ui.RentalStoreLogic;
import test.fujitsu.videostore.ui.database.CurrentDatabase;

public class VideoStoreInventoryLogic extends RentalStoreLogic<Movie> {

    private DBTableRepository<Movie> movieDBTableRepository;

    public VideoStoreInventoryLogic(VideoStoreInventory videoStoreInventory) {
        super(videoStoreInventory);
    }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }
        movieDBTableRepository = CurrentDatabase.get().getMovieTable();

        view.setNewItemEnabled(true);
        view.setItems(movieDBTableRepository.getAll());
    }

    @Override
    public DBTableRepository<Movie> getRepo() {
        return movieDBTableRepository;
    }

    @Override
    public Movie createNewItem() {
        return new Movie();
    }

    @Override
    public Class getViewClass() {
        return VideoStoreInventory.class;
    }

}
