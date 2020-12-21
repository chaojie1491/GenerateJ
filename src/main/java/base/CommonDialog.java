package base;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.FlatAlert;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonDialog {

    public static void showError(Style style, String title,String content) {
        FlatAlert alert = new FlatAlert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(title);
        alert.setContentText(content);
        JMetro jMetro = new JMetro(style);
        jMetro.setScene(alert.getDialogPane().getScene());
        alert.showAndWait();
    }

    public static void showJMetroAlertWithExpandableContent(Style style, String content, Exception e) {
        JMetro jMetro = new JMetro(style);
        FlatAlert alert = new FlatAlert(Alert.AlertType.ERROR);
        alert.setHeaderText("异常!");
        alert.setContentText(content);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

//        textArea.setMaxWidth(Double.MAX_VALUE);
//        textArea.setMaxHeight(Double.MAX_VALUE);
//        GridPane.setVgrow(textArea, Priority.ALWAYS);
//        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        jMetro.setScene(alert.getDialogPane().getScene());

        alert.showAndWait();
    }
}
