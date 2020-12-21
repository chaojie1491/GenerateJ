import base.CommonStage;
import base.CustomJFXDecorator;
import base.FxAppPreloader;
import com.sun.javafx.application.LauncherImpl;
import controller.OriginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.hibernate.Session;
import util.HibernateUtil;


public class EnterApplication extends Application {

    private static final Style STYLE = Style.DARK;

    Session session;


    @Override
    public void start(Stage stage) throws Exception {
//        VBox mainContainer = new VBox();
//        mainContainer.getStyleClass().add("background");
//        Scene scene = new Scene(mainContainer, 400, 350);
//        new JMetro(scene, STYLE);
//        CustomJFXDecorator decorator = new CustomJFXDecorator(stage, new OriginController(session, scene));
//        scene.setRoot(decorator);
//        stage.getIcons().add(new Image("/images/icon/logo.png"));
//        stage.setScene(scene);
//        stage.setTitle("Code Generate Tool");
//        stage.show();
        CommonStage.createStage("Code Generate Tool", new Image("/images/icon/logo.png"), STYLE,
                new OriginController(session, 400, 350), 400, 350).show();
    }

    public static void main(String[] args) {
        run(EnterApplication.class, args);
    }


    @Override
    public void init() throws Exception {
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(20));
        session = HibernateUtil.currentSession();
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(40));
        Thread.sleep(1000);
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(100));
        Thread.sleep(1000);
    }

    public static void run(final Class<? extends Application> appClass,
                           final String[] args) {
        LauncherImpl.launchApplication(appClass, FxAppPreloader.class, args);
    }


}



