package controller;

import base.CommonDialog;
import base.CommonStage;
import base.CustomJFXDecorator;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import entity.OriginEntity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import util.HibernateUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class OriginForm extends Scene {


    ObservableList<String> types = FXCollections.observableArrayList("SqlServer", "MySql", "SqLite", "Oracle");

    public BooleanProperty isPass = new SimpleBooleanProperty(true);
    TextField name;
    TextField host;
    TextField port;
    TextField username;
    TextField password;
    TextField database;
    ChoiceBox<String> choiceBox;

    public OriginForm(Parent root, double width, double height) {
        super(root);
        BorderPane borderPane = (BorderPane) root;
        ((BorderPane) root).setPrefSize(width, height);
        borderPane.getStyleClass().add("background");
        AnchorPane anchorPane = null;
        try {
            anchorPane = FXMLLoader.load(getClass().getResource("/fxml/origin/Form.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        borderPane.setCenter(anchorPane);
        name = (TextField) this.lookup("#name");
        host = (TextField) this.lookup("#host");
        port = (TextField) this.lookup("#port");
        username = (TextField) this.lookup("#username");
        password = (TextField) this.lookup("#password");
        database = (TextField) this.lookup("#database");
        Text msg = (Text) this.lookup("#msg");
        Button button = (Button) this.lookup("#testBtn");
        button.setOnMouseClicked((event -> {
            this.getConnection(host.getText(), port.getText(), database.getText(), username.getText(), password.getText(), msg);
        }));
        Button finish = (Button) this.lookup("#finish");
        finish.setOnMouseClicked(event -> {
            if (name.getText().isEmpty() || host.getText().isEmpty() || host.getText().isEmpty() ||
                    port.getText().isEmpty() || database.getText().isEmpty()) {
                CommonDialog.showError(Style.DARK, "错误", "请检查表单");
            } else {
                OriginEntity originEntity = new OriginEntity();
                JSONObject object = new JSONObject();
                object.put("host", host.getText());
                object.put("port", port.getText());
                object.put("database", database.getText());
                object.put("username", username.getText());
                object.put("password", password.getText());
                originEntity.setId(null);
                originEntity.setConfig(object.toJSONString());
                originEntity.setName(name.getText());
                originEntity.setType(choiceBox.getValue());
                originEntity.setDatabase(database.getText());
                Session session = HibernateUtil.currentSession();
                Transaction transaction;
                if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
                    transaction = session.getTransaction();
                } else {
                    transaction = session.beginTransaction();
                }
                session.save(originEntity);
                transaction.commit();
                ((Stage)this.getWindow()).close();
                session.clear();
            }
        });
        finish.disableProperty().bindBidirectional(isPass);
        choiceBox = (ChoiceBox<String>) this.lookup("#type");
        choiceBox.setItems(types);
        choiceBox.setValue(types.get(0));
    }


    public void getConnection(String host, String port, String database, String username, String password, Text msg) {
        String url = String.format("jdbc:sqlserver://%s:%s;DatabaseName=%s", host, port, database);
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            con.createStatement();
            msg.setText("连接成功");
            msg.setFill(Color.GREEN);
            this.isPass.setValue(false);
        } catch (SQLException e) {
            msg.setText(e.getMessage());
            msg.setFill(Color.RED);
            e.printStackTrace();
            this.isPass.setValue(true);
        }


    }
}
