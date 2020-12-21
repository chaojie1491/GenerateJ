package controller;

import base.CommonStage;
import com.alibaba.fastjson.JSONArray;
import dao.FtlDao;
import dao.RuleDao;
import dao.TableInfoDao;
import entity.FtlFileEntity;
import entity.OriginEntity;
import entity.RuleEntity;
import entity.TableInfoEntity;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.controlsfx.control.CheckListView;
import org.hibernate.Session;
import sun.reflect.generics.tree.Tree;
import util.HibernateUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class GenController extends Scene {

    private CheckListView<String> ftls;
    final ObservableList<String> ftlsFiles = FXCollections.observableArrayList();
    final ObservableList<String> rules = FXCollections.observableArrayList();
    Session session = HibernateUtil.currentSession();
    TableView<TableInfoEntity> fieldTable;
    TreeView<String> dbList;

    public GenController(Parent root, double width, double height) {
        super(root, width, height);
        BorderPane borderPane = (BorderPane) root;
        ((BorderPane) root).setPrefSize(width, height);
        borderPane.getStyleClass().add("background");
        AnchorPane anchorPane = null;
        try {
            anchorPane = FXMLLoader.load(getClass().getResource("/fxml/gen/Gen.fxml"));
            borderPane.setCenter(anchorPane);
            dbList = (TreeView<String>) this.lookup("#dbList");
            ListView<String> rulesView = (ListView<String>) this.lookup("#rules");
            fieldTable = (TableView<TableInfoEntity>) this.lookup("#fieldTable");
            ftls = (CheckListView<String>) this.lookup("#ftls");
            TreeItem<String> rootNode = new TreeItem<>();
            rootNode.setValue(CommonStage.db_info.getString("db_name"));
            JSONArray tables = CommonStage.db_info.getJSONArray("db_table");
            tables.forEach(t -> {
                TreeItem<String> stringTreeItem = new TreeItem<>();
                stringTreeItem.setValue(t.toString());
                rootNode.getChildren().add(stringTreeItem);
            });
            FtlDao.getFtls(session).forEach((f) -> {
                ftlsFiles.add(f.getFileName());
            });
            RuleDao.getRules(session).forEach((r) -> {
                rules.add(r.getRuleName());
            });
            rulesView.getItems().addAll(rules);
            ftls.setItems(ftlsFiles);
            initTable();
            dbList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
                @Override
                public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
                    if (!Objects.isNull(newValue)) {
                        refresh(newValue.getValue());
                    }
                }
            });


            dbList.setRoot(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initTable() {
        TableColumn<TableInfoEntity, String> fieldName = new TableColumn<>("字段名");
        fieldName.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("columnName")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<TableInfoEntity, String> fieldType = new TableColumn<>("字段类型");
        fieldType.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("type")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<TableInfoEntity, Boolean> fieldKey = new TableColumn<>("主键");
        fieldKey.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableInfoEntity, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<TableInfoEntity, Boolean> param) {
                return param.getValue().isKeyProperty();
            }
        });
        fieldKey.setCellFactory(CheckBoxTableCell.forTableColumn(fieldKey) );
        TableColumn<TableInfoEntity, Boolean> nullable = new TableColumn<>("Null");
        nullable.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableInfoEntity, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<TableInfoEntity, Boolean> param) {
                return param.getValue().isNullableProperty();
            }
        });
        nullable.setCellFactory(CheckBoxTableCell.forTableColumn(nullable) );
        TableColumn<TableInfoEntity, String> len = new TableColumn<>("长度");
        len.setCellValueFactory(new PropertyValueFactory<TableInfoEntity,String>("lenProper"));
        TableColumn<TableInfoEntity, String> genType = new TableColumn<>("生成类型");
        genType.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("genType")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });


        genType.setCellFactory(ComboBoxTableCell.forTableColumn("String", "int32", "int64","Double","Date","Datetime"));


        fieldTable.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        fieldTable.getSelectionModel().isCellSelectionEnabled();
        fieldTable.setEditable(true);
        fieldTable.getSelectionModel().setCellSelectionEnabled(false);
        fieldTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fieldTable.getColumns().addAll(fieldName, fieldType, fieldKey,nullable, genType, len);
    }
    public static class Contact {
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty category = new SimpleStringProperty();

        public Contact(String name, String category) {
            setName(name);
            setCategory(category);
        }

        public final StringProperty nameProperty() {
            return this.name;
        }


        public final String getName() {
            return this.nameProperty().get();
        }


        public final void setName(final String name) {
            this.nameProperty().set(name);
        }


        public final StringProperty categoryProperty() {
            return this.category;
        }


        public final String getCategory() {
            return this.categoryProperty().get();
        }


        public final void setCategory(final String category) {
            this.categoryProperty().set(category);
        }

    }

    public void refresh(String tbName) {
        this.fieldTable.getItems().clear();
        this.fieldTable.getItems().addAll(TableInfoDao.getRules(session, tbName));
        this.fieldTable.refresh();
    }
}
