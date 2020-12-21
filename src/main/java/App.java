import base.CustomJFXDecorator;
import com.sun.javafx.application.LauncherImpl;
import dao.OriginDao;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static javafx.application.Application.launch;

public class App extends EnterApplication {
    private static final Style STYLE = Style.DARK;
    private static final boolean FOCUS_TRAVERSAL = false;

    private static Logger logger = LoggerFactory.getLogger(App.class);

    BorderPane borderPane;

    Scene scene;

    private Session session;


    public static void main(String[] args) {
        run(App.class,args);
    }


    private StackPane getNoData() {
        Font font = Font.font("System", 15);
        Label noData = new Label("没有数据源");
        noData.setFont(font);
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(scene.getWidth(), scene.getHeight());
        stackPane.getChildren().add(noData);
        stackPane.setPadding(new Insets(0, 0, 50, 0));
        StackPane.setAlignment(noData, Pos.CENTER);
        return stackPane;
    }

    private static HBox getLoading() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().setValue(-1);
        progressBar.prefWidth(200);

        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #252525;");
        hBox.getChildren().add(progressBar);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }


    private static CheckMenuItem createMenuItem(String title) {
        CheckMenuItem cmi = new CheckMenuItem(title);
        cmi.setSelected(true);
        return cmi;
    }
}
