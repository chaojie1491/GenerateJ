package controller;

import base.CommonDialog;
import com.jfoenix.controls.JFXButton;
import dao.RuleDao;
import entity.RuleEntity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.util.Callback;
import jfxtras.styles.jmetro.Style;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import util.HibernateUtil;

import java.io.IOException;
import java.sql.Statement;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class RuleForm extends Scene {
    TextField name;
    TextField namespace;
    TextField entityPrefix;
    TextField entitySuffix;
    TextField tablePrefix;
    TextField parentClass;
    ChoiceBox<String> language;
    CheckBox uc;

    ObservableList<String> languages = FXCollections.observableArrayList("C#", "Java");
    Session session = HibernateUtil.currentSession();

    TableView<RuleEntity> ruleEntityTableView;

    private void initTable() {
        ruleEntityTableView.getStylesheets().add(RuleForm.class.getResource("/styles/application.css").toExternalForm());
        TableColumn<RuleEntity, String> ruleNameColumn = new TableColumn<>("名称");
        ruleNameColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("ruleName")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<RuleEntity, String> ruleLanguageColumn = new TableColumn<>("语言");
        ruleLanguageColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("language")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<RuleEntity, String> tablePrefixColumn = new TableColumn<>("表前缀移除");
        tablePrefixColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("tablePrefix")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<RuleEntity, String> namespaceColumn = new TableColumn<>("命名空间");
        namespaceColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("namespace")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<RuleEntity, String> entityPrefixColumn = new TableColumn<>("实体前缀");
        namespaceColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("tablePrefix")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<RuleEntity, String> entitySuffixColumn = new TableColumn<>("实体后缀");
        namespaceColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("entitySuffix")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<RuleEntity, String> optionColumn = new TableColumn<>("操作");
        Callback<TableColumn<RuleEntity, String>, TableCell<RuleEntity, String>> cellCallback = new Callback<TableColumn<RuleEntity, String>, TableCell<RuleEntity, String>>() {
            @Override
            public TableCell<RuleEntity, String> call(TableColumn<RuleEntity, String> param) {
                return new TableCell<RuleEntity, String>() {

                    final Button btn = new Button();
                    final ImageView delIcon = new ImageView(OriginController.class.getResource("/images/icon/del.png").toExternalForm());

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        delIcon.setFitHeight(16);
                        delIcon.setFitWidth(16);
                        btn.setGraphic(delIcon);
                        btn.setFocusTraversable(false);

                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                RuleEntity ruleEntity = getTableView().getItems().get(getIndex());
                                session.doWork(connection -> {
                                    Transaction transaction = session.beginTransaction();
                                    Statement st;
                                    String sql = "delete from rule where id = " + ruleEntity.getId();
                                    System.out.println("删除:" + sql);
                                    st = connection.createStatement();
                                    st.executeUpdate(sql);
                                    st.close();
                                    transaction.commit();
                                    refresh();
                                });
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
        optionColumn.setCellFactory(cellCallback);
        ruleEntityTableView.setItems(RuleDao.getRules(session));
        ruleEntityTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        ruleEntityTableView.getSelectionModel().isCellSelectionEnabled();
        ruleEntityTableView.setEditable(true);
        ruleEntityTableView.getSelectionModel().setCellSelectionEnabled(false);
        ruleEntityTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ruleNameColumn.setStyle("-fx-alignment: CENTER;");
        ruleLanguageColumn.setStyle("-fx-alignment: CENTER;");
        namespaceColumn.setStyle("-fx-alignment: CENTER;");
        tablePrefixColumn.setStyle("-fx-alignment: CENTER;");
        optionColumn.setStyle("-fx-alignment: CENTER;");
        ruleEntityTableView.getColumns().addAll(ruleNameColumn, ruleLanguageColumn, namespaceColumn, tablePrefixColumn, optionColumn);
    }

    private void refresh() {
        this.ruleEntityTableView.getItems().clear();
        this.ruleEntityTableView.getItems().addAll(RuleDao.getRules(session));
        this.ruleEntityTableView.refresh();
    }

    public RuleForm(Parent root, double width, double height) {
        super(root, width, height);
        BorderPane borderPane = (BorderPane) root;
        ((BorderPane) root).setPrefSize(width, height);
        borderPane.getStyleClass().add("background");
        AnchorPane anchorPane = null;
        try {
            anchorPane = FXMLLoader.load(getClass().getResource("/fxml/rule/RuleForm.fxml"));
            borderPane.setCenter(anchorPane);
            name = (TextField) this.lookup("#name");
            namespace = (TextField) this.lookup("#namespace");
            tablePrefix = (TextField) this.lookup("#tablePrefix");
            entityPrefix = (TextField) this.lookup("#entityPrefix");
            entitySuffix = (TextField) this.lookup("#entitySuffix");
            parentClass = (TextField) this.lookup("#parentClass");
            uc = (CheckBox) this.lookup("#uc");
            ruleEntityTableView = (TableView<RuleEntity>) this.lookup("#ruleTable");
            initTable();
            Button save = (Button) this.lookup("#save");
            save.setOnMouseClicked((e) -> {
                if (this.name.getText().isEmpty() ||
                        this.namespace.getText().isEmpty() ||
                        this.language.getValue().isEmpty()) {
                    CommonDialog.showError(Style.DARK, "错误", "请检查表单");
                } else {
                    RuleEntity ruleEntity = new RuleEntity();
                    ruleEntity.setEntityPrefix(entityPrefix.getText());
                    ruleEntity.setEntitySuffix(entitySuffix.getText());
                    ruleEntity.setTablePrefix(tablePrefix.getText());
                    ruleEntity.setLanguage(language.getValue());
                    ruleEntity.setIsUc(uc.selectedProperty().getValue() ? "Y" : "N");
                    ruleEntity.setNamespace(namespace.getText());
                    ruleEntity.setRuleName(name.getText());
                    ruleEntity.setPatentClass(parentClass.getText());
                    Session session = HibernateUtil.currentSession();
                    Transaction transaction;
                    if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
                        transaction = session.getTransaction();
                    } else {
                        transaction = session.beginTransaction();
                    }
                    session.clear();
                    session.saveOrUpdate(ruleEntity);
                    transaction.commit();

                    this.refresh();
                }
            });
            language = (ChoiceBox) this.lookup("#language");
            language.setItems(languages);
            language.setValue(languages.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
