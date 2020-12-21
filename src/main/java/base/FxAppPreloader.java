package base;

import com.jfoenix.controls.JFXProgressBar;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.io.IOException;

@Slf4j
public class FxAppPreloader extends Preloader {

    private JFXProgressBar progressBar;
    private Parent view;
    private Stage stage;
    int progress;
    Session session;

    @Override
    public void init() {
        try {
            view = FXMLLoader.load(getClass().getResource("/fxml/loader/loader.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage primary) {
        stage = primary;
        primary.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(view);
        scene.setFill(Color.TRANSPARENT);
        progressBar = (JFXProgressBar) scene.lookup("#progressBar");
        primary.setScene(scene);
        primary.setAlwaysOnTop(true);
        primary.show();
    }

    @SneakyThrows
    @Override
    public synchronized void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            double x = ((ProgressNotification) info).getProgress();
            double percent = x / 100f;
            progressBar.progressProperty().set(percent > 1 ? 1 : percent);
        }
    }


    @Override
    synchronized public void handleStateChangeNotification(StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_LOAD:
                break;
            case BEFORE_INIT:
                break;
            case BEFORE_START:
                stage.close();
        }
    }

    private synchronized void notifyLoader() {
        progress += 100f / 3;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        LauncherImpl.notifyPreloader(this, new ProgressNotification(progress));
    }

}
