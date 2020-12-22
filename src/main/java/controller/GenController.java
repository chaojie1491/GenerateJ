package controller;

import base.CommonDialog;
import base.CommonStage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dao.FtlDao;
import dao.RuleDao;
import dao.TableInfoDao;
import entity.*;
import freemarker.template.TemplateException;
import javafx.application.Platform;
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
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import jfxtras.styles.jmetro.Style;
import org.controlsfx.control.CheckListView;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import sun.reflect.generics.tree.Tree;
import util.GenUtil;
import util.HibernateUtil;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Objects;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class GenController extends Scene {

    private CheckListView<FtlFileEntity> ftls;
    private ObservableList<FtlFileEntity> ftlChecks;
    private ObservableList<FtlFileEntity> checked;
    final ObservableList<String> dbs = FXCollections.observableArrayList();
    final ObservableList<String> rules = FXCollections.observableArrayList();
    ListView<RuleEntity> rulesView;
    Session session = HibernateUtil.currentSession();
    TableView<TableInfoEntity> fieldTable;
    CheckListView<String> dbList;
    ProgressBar progressBar;
    HBox progressBox;
    Button generate;

    Service<Integer> service = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    JSONObject config = CommonStage.db_info.getJSONObject("config");
                    String tbName = dbList.getSelectionModel().selectedItemProperty().getValue();
                    if (tbName == null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                            }

                        });
                        return null;
                    } else {
                        double i = 10.0;
                        updateProgress(i, 100);
                        String url = String.format("jdbc:sqlserver://%s:%s;DatabaseName=%s", config.getString("host"), config.getString("port"), config.getString("database"));
                        try {
                            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                            Connection con = DriverManager.getConnection(url, config.getString("username"), config.getString("password"));
                            String tableInfoSql = String.format("select * from %s..sysobjects where xtype = 'U' and name = '%s'", config.getString("database"), tbName);
                            String constraintKeySql = String.format("SELECT  TABLE_NAME,COLUMN_NAME  FROM  INFORMATION_SCHEMA.KEY_COLUMN_USAGE  WHERE  TABLE_NAME= '%s';", tbName);
                            String columnsSql = String.format(" SELECT syscolumns.name,systypes.name as type,syscolumns.isnullable, syscolumns.length  FROM syscolumns, systypes  WHERE syscolumns.xusertype = systypes.xusertype  AND syscolumns.id = object_id('%s')", tbName);
                            Statement st2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);
                            ResultSet columnRes = st2.executeQuery(columnsSql);
                            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);
                            ResultSet rs = stmt.executeQuery(tableInfoSql);
                            Statement st3 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);
                            ResultSet keyList = st3.executeQuery(constraintKeySql);
                            List<TableInfoEntity> tableInfoEntities = null;
                            Transaction transaction;
                            if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
                                transaction = session.getTransaction();
                            } else {
                                transaction = session.beginTransaction();
                            }
                            columnRes.last();
                            double progressItem = ((double) (100.0 - i) / columnRes.getRow());
                            columnRes.beforeFirst();
                            while (rs.next()) {
                                tableInfoEntities = session.createQuery("from entity.TableInfoEntity where tableId = " + rs.getString("id")).list();
                                while (columnRes.next()) {


                                    TableInfoEntity tableInfoEntity;
                                    if (tableInfoEntities == null || tableInfoEntities.size() == 0) {
                                        tableInfoEntity = new TableInfoEntity();
                                    } else {
                                        tableInfoEntity = tableInfoEntities.get(0);
                                    }

                                    if (Objects.isNull(tableInfoEntity)) {
                                        tableInfoEntity = new TableInfoEntity();
                                    }
                                    tableInfoEntity.setTableId(rs.getShort("id"));
                                    tableInfoEntity.setTableName(rs.getString("name"));
                                    tableInfoEntity.setColumnName(columnRes.getString("name"));
                                    if (columnRes.getInt("isnullable") == 0) {
                                        tableInfoEntity.setIsNull("N");
                                    } else {
                                        tableInfoEntity.setIsNull("Y");
                                    }
                                    keyList.last();
                                    if (keyList.getRow() > 0) {
                                        keyList.beforeFirst();
                                        while (keyList.next()) {
                                            if (keyList.getString("COLUMN_NAME").equals(columnRes.getString("name"))) {
                                                tableInfoEntity.setConstraintKey("Y");
                                            } else {
                                                tableInfoEntity.setConstraintKey("N");
                                            }
                                        }
                                    } else {
                                        tableInfoEntity.setConstraintKey("N");
                                    }
                                    tableInfoEntity.setLen((short) columnRes.getInt("length"));
                                    tableInfoEntity.setType(columnRes.getString("type"));
                                    tableInfoEntity.setGenType(GenUtil.getGenType(columnRes.getString("type")));
                                    session.saveOrUpdate(tableInfoEntity);
                                }
                                st2.close();
                                st3.close();
                                i += progressItem;
                                updateProgress(i, 100);
                            }
                            stmt.close();
                            con.close();
                            transaction.commit();
                            updateProgress(100, 100);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    updateProgress(0, 100);
                                    hideProgress();
                                }
                            });
                        } catch (ClassNotFoundException | SQLException e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "错误！", e);
                                }
                            });
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return null;
                }

                @Override
                protected void succeeded() {
                    String tbName = dbList.getSelectionModel().selectedItemProperty().getValue();
                    refresh(tbName);
                }

                private void hideProgress() {
                    progressBox.setVisible(false);
                }
            };
        }
    };

    void initRulesView() {
        rulesView.setItems(RuleDao.getRules(session));
        rulesView.setCellFactory(param -> new ListCell<RuleEntity>() {
            @Override
            protected void updateItem(RuleEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getRuleName() == null) {
                    setText(null);
                } else {
                    setText(item.getRuleName());
                }
            }
        });
    }

    void initFtlCheckView() {
        ftlChecks = FtlDao.getFtls(session);
        ftls.setItems(ftlChecks);
        ftls.setCellFactory(lv -> new CheckBoxListCell<FtlFileEntity>(ftls::getItemBooleanProperty) {
            @Override
            public void updateItem(FtlFileEntity ftlFileEntity, boolean empty) {
                super.updateItem(ftlFileEntity, empty);
                setText(ftlFileEntity == null ? "" : ftlFileEntity.getFileName());
            }
        });

        ftls.getCheckModel().getCheckedItems().addListener(new ListChangeListener<FtlFileEntity>() {
            @Override
            public void onChanged(Change<? extends FtlFileEntity> c) {
                checked = (ObservableList<FtlFileEntity>) c.getList();
            }
        });


    }

    public GenController(Parent root, double width, double height) {
        super(root, width, height);
        BorderPane borderPane = (BorderPane) root;
        ((BorderPane) root).setPrefSize(width, height);
        borderPane.getStyleClass().add("background");
        AnchorPane anchorPane = null;
        try {
            anchorPane = FXMLLoader.load(getClass().getResource("/fxml/gen/Gen.fxml"));
            borderPane.setCenter(anchorPane);
            dbList = (CheckListView<String>) this.lookup("#dbList");
            rulesView = (ListView<RuleEntity>) this.lookup("#rules");
            fieldTable = (TableView<TableInfoEntity>) this.lookup("#fieldTable");
            ftls = (CheckListView<FtlFileEntity>) this.lookup("#ftls");
            progressBar = (ProgressBar) this.lookup("#progressBar");
            progressBox = (HBox) this.lookup("#progressBox");
            generate = (Button) this.lookup("#generate");


            JSONArray tables = CommonStage.db_info.getJSONArray("db_table");
            tables.forEach(t -> {
                dbs.add(t.toString());
            });

            dbList.setItems(dbs);

            dbList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!Objects.isNull(newValue)) {
//                        refresh(newValue.getValue());
                        JSONObject config = CommonStage.db_info.getJSONObject("config");

                        if (!newValue.equals(config.getString("database"))) {
                            progressBox.setVisible(true);
                            progressBar.progressProperty().bind(service.progressProperty());
                            HBox.setHgrow(progressBar, Priority.ALWAYS);
                            service.restart();
                        }
                    }
                }
            });

            progressBox.setVisible(false);

            initRulesView();
            initTable();

            initFtlCheckView();
            //生成代码
            generate.setOnMouseClicked(event -> {
                String tbName = dbList.getSelectionModel().selectedItemProperty().getValue();
                List<TableInfoEntity> tableInfoEntities = TableInfoDao.getRules(session, tbName);
                if (Objects.isNull(checked) || checked.size() == 0) {
                    CommonDialog.showError(Style.DARK, "错误", "请选择模板文件");
                } else {
                    checked.forEach(c -> {
                        System.out.println(c.getFileName());
                    });
                }
                RuleEntity ruleEntity = rulesView.getSelectionModel().selectedItemProperty().getValue();
                if (Objects.isNull(ruleEntity)) {
                    CommonDialog.showError(Style.DARK, "错误", "请选择生成规则");
                } else {
                    System.out.println(ruleEntity.getEntityPrefix());
                }

                SettingEntity settingEntity = (SettingEntity) session.createQuery("from entity.SettingEntity").list().get(0);

                try {
                    GenUtil.genFile(ruleEntity, tableInfoEntities, checked, settingEntity);
                    CommonDialog.showError(Style.DARK,"OK","生成完成");
                } catch (IOException e) {
                    e.printStackTrace();
                    CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "错误", e);

                } catch (TemplateException e) {
                    e.printStackTrace();
                    CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "错误", e);
                }
            });
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
        fieldKey.setCellFactory(CheckBoxTableCell.forTableColumn(fieldKey));
        TableColumn<TableInfoEntity, Boolean> nullable = new TableColumn<>("Null");
        nullable.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableInfoEntity, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<TableInfoEntity, Boolean> param) {
                return param.getValue().isNullableProperty();
            }
        });
        nullable.setCellFactory(CheckBoxTableCell.forTableColumn(nullable));
        TableColumn<TableInfoEntity, String> len = new TableColumn<>("长度");
        len.setCellValueFactory(new PropertyValueFactory<TableInfoEntity, String>("lenProper"));
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


        genType.setCellFactory(ComboBoxTableCell.forTableColumn("String", "int32", "int64", "Double", "Date", "Datetime"));


        fieldTable.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        fieldTable.getSelectionModel().isCellSelectionEnabled();
        fieldTable.setEditable(true);
        fieldTable.getSelectionModel().setCellSelectionEnabled(false);
        fieldTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fieldTable.getColumns().addAll(fieldName, fieldType, fieldKey, nullable, genType, len);
    }


    public void refresh(String tbName) {
        this.fieldTable.getItems().clear();
        this.fieldTable.getItems().addAll(TableInfoDao.getRules(session, tbName));
        this.fieldTable.refresh();
    }
}
