package base;

import com.alibaba.fastjson.JSONObject;
import controller.OriginController;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommonStage {
    public static List<Stage> stageStack = new ArrayList<>();
    public static JSONObject db_info = new JSONObject();

    public static Stage createStage(String title, Image image, Style style, Pane node, int w, int h) {
        if (stageStack.stream().noneMatch(stage -> stage.getTitle().equals(title))) {
            VBox mainContainer = new VBox();
            mainContainer.getStyleClass().add("background");
            Scene scene = new Scene(mainContainer, w, h);
            new JMetro(scene, style);
            Stage stage = new Stage();
            CustomJFXDecorator decorator = new CustomJFXDecorator(stage, node);
            scene.setRoot(decorator);
            stage.getIcons().add(image);
            stage.setScene(scene);
            stage.setTitle(title);
            stageStack.add(stage);
            return stage;
        } else {
            Stage stage = stageStack.stream().filter(s -> s.getTitle().equals(title)).collect(Collectors.toList()).get(0);
            stage.requestFocus();
            return stage;
        }
    }

    public static Stage createStageHaveScene(String title, Image image, Style style, Scene scene) {
        if (stageStack.stream().noneMatch(stage -> stage.getTitle().equals(title))) {
            VBox mainContainer = new VBox();
            mainContainer.getStyleClass().add("background");
            new JMetro(scene, style);
            Stage stage = new Stage();
            CustomJFXDecorator decorator = new CustomJFXDecorator(stage, scene.getRoot());
            scene.setRoot(decorator);
            stage.getIcons().add(image);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setAlwaysOnTop(true);
            stageStack.add(stage);

            return stage;
        } else {
            Stage stage = stageStack.stream().filter(s -> s.getTitle().equals(title)).collect(Collectors.toList()).get(0);
            stage.requestFocus();
            return stage;
        }
    }

    public static Stage createStageHaveSceneMax(String title, Image image, Style style, Scene scene) {
        if (stageStack.stream().noneMatch(stage -> stage.getTitle().equals(title))) {
            VBox mainContainer = new VBox();
            mainContainer.getStyleClass().add("background");
            new JMetro(scene, style);
            Stage stage = new Stage();
            CustomJFXDecorator decorator = new CustomJFXDecorator(stage, scene.getRoot());
            scene.setRoot(decorator);
            stage.getIcons().add(image);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setAlwaysOnTop(true);
            stage.setMaximized(true);
            stageStack.add(stage);
            return stage;
        } else {
            Stage stage = stageStack.stream().filter(s -> s.getTitle().equals(title)).collect(Collectors.toList()).get(0);
            stage.requestFocus();
            return stage;
        }
    }

    public static Stage createStage(String title, Image image, Style style, Scene scene) {
        VBox mainContainer = new VBox();
        mainContainer.getStyleClass().add("background");
        new JMetro(scene, style);
        Stage stage = new Stage();
        CustomJFXDecorator decorator = new CustomJFXDecorator(stage, scene.getRoot());
        scene.setRoot(decorator);
        stage.getIcons().add(image);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMaximized(true);
        stageStack.add(stage);
        return stage;
    }

}
