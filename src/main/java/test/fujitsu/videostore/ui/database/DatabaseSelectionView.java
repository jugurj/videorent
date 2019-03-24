package test.fujitsu.videostore.ui.database;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.File;

@Route("DatabaseSelection")
@PageTitle("Database Selection")
@HtmlImport("css/shared-styles.html")
public class DatabaseSelectionView extends FlexLayout {

    private TextField databasePath;
    private Button selectDatabaseButton;
    private final int NOTIFICATION_DURATION = 2000;

    public DatabaseSelectionView() {
        setSizeFull();
        setClassName("database-selection-screen");

        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(buildLoginForm());

        add(centeringLayout);
    }

    private Component buildLoginForm() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("310px");

        databasePath = new TextField("Enter database file path");
        databasePath.setId("database-path");
        databasePath.setRequired(true);

        databasePath.setValue("db-examples/");

        verticalLayout.add(databasePath);

        HorizontalLayout buttons = new HorizontalLayout();
        verticalLayout.add(buttons);

        selectDatabaseButton = new Button("Select database");
        selectDatabaseButton.setId("database-select");
        selectDatabaseButton.addClickListener(event -> selectDatabase());
        selectDatabaseButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        buttons.add(selectDatabaseButton);

        return verticalLayout;
    }

    private void selectDatabase() {
        selectDatabaseButton.setEnabled(false);
        try {
            File databaseFile = new File(databasePath.getValue());

            if (databaseFile.exists()) {
                if (databaseFile.length() > 0) {
                    CurrentDatabase.set(databasePath.getValue());
                    getUI().get().navigate("");
                } else {
                    databasePath.setValue("");
                    Notification notify = Notification.show("Oops, looks like the file is empty!", NOTIFICATION_DURATION, Notification.Position. TOP_CENTER);
                    notify.setId("error-notification");
                }
            } else {
                databasePath.setValue("");
                Notification notify = Notification.show("There is no such file!", NOTIFICATION_DURATION, Notification.Position. TOP_CENTER);
                notify.setId("error-notification");
            }
        } finally {
            selectDatabaseButton.setEnabled(true);
        }
    }



}
