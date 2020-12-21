package controller;

import base.CommonDialog;
import dao.RuleDao;
import entity.RuleEntity;
import entity.SettingEntity;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.styles.jmetro.Style;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import util.HibernateUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Statement;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class SettingController extends Scene {
    TextField ftlPath;
    TextField outFilePath;
    Button save;
    Session session = HibernateUtil.currentSession();

    public SettingController(Parent root, double width, double height) {
        super(root, width, height);
        BorderPane borderPane = (BorderPane) root;
        ((BorderPane) root).setPrefSize(width, height);
        borderPane.getStyleClass().add("background");
        AnchorPane anchorPane = null;

        try {
            anchorPane = FXMLLoader.load(getClass().getResource("/fxml/setting/Setting.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        borderPane.setCenter(anchorPane);
        ftlPath = (TextField) this.lookup("#ftlPath");
        outFilePath = (TextField) this.lookup("#outFilePath");

        SettingEntity settingEntity = (SettingEntity) session.createQuery("from entity.SettingEntity").list().get(0);
        ftlPath.setText(settingEntity.getFtlPath());
        outFilePath.setText(settingEntity.getOutPath());
        save = (Button) this.lookup("#save");
        ftlPath.setOnMouseClicked((e) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(ftlPath.getScene().getWindow());
            String path = file.getPath();//选择的文件夹路径
            ftlPath.setText(path);
        });
        outFilePath.setOnMouseClicked((e) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(outFilePath.getScene().getWindow());
            String path = file.getPath();//选择的文件夹路径
            outFilePath.setText(path);
        });
        save.setOnMouseClicked((e) -> {
            SettingEntity newEntity = new SettingEntity();
            newEntity.setId((short) 1000);
            newEntity.setFtlPath(ftlPath.getText());
            newEntity.setOutPath(outFilePath.getText());
            Transaction transaction;
            if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
                transaction = session.getTransaction();
            } else {
                transaction = session.beginTransaction();
            }
            session.merge(newEntity);
            transaction.commit();
            ((Stage) this.getWindow()).close();
        });
    }

}
