package controller;

import base.CommonDialog;
import base.CommonStage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dao.OriginDao;
import entity.OriginEntity;
import entity.TableInfoEntity;
import javafx.application.Platform;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import jfxtras.styles.jmetro.Style;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import util.GenUtil;
import util.HibernateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class OriginController extends BorderPane {
    private static final boolean FOCUS_TRAVERSAL = false;
    Session session = HibernateUtil.currentSession();
    private TableView<OriginEntity> table = new TableView<>();
    TableView.TableViewSelectionModel<OriginEntity> originEntityTableSelectionModel;

    Button delOrigin = new Button();
    SplitMenuButton run = new SplitMenuButton();
    ProgressBar progressBar;

    Service<Integer> service = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {

                @Override
                protected Integer call() throws Exception {
                    // TODO Auto-generated method stub
//            run.setDisable(true);
                    Thread.sleep(150);
                    double i = 0.0;
                    updateProgress(i, 100);
                    OriginEntity originEntity = table.getSelectionModel().selectedItemProperty().getValue();
                    if (originEntity == null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                            }
                        });
                        return null;
                    } else {
                        try {
                            JSONObject config = JSON.parseObject(originEntity.getConfig());
                            String url = String.format("jdbc:sqlserver://%s:%s;DatabaseName=%s", config.getString("host"), config.getString("port"), config.getString("database"));
                            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                            Connection con = DriverManager.getConnection(url, config.getString("username"), config.getString("password"));
                            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);
                            System.out.println("连接成功!");
                            i += 10;
                            updateProgress(i, 100);
                            String SQL = String.format("select * from %s..sysobjects where xtype = 'U'", config.getString("database"));
                            ResultSet rs = stmt.executeQuery(SQL);
                            rs.last();
                            JSONArray jsonArray = new JSONArray();
                            double progressItem = ((double) (100.0 - i) / rs.getRow());
                            rs.first();
                            Transaction transaction;
                            if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
                                transaction = session.getTransaction();
                            } else {
                                transaction = session.beginTransaction();
                            }

                            while (rs.next()) {
                                String columnsSql = String.format(" SELECT syscolumns.name,systypes.name as type,syscolumns.isnullable, syscolumns.length  FROM syscolumns, systypes  WHERE syscolumns.xusertype = systypes.xusertype  AND syscolumns.id = object_id('%s')", rs.getString("name"));
                                Statement st2 = con.createStatement();
                                ResultSet columnRes = st2.executeQuery(columnsSql);
                                String constraintKeySql = String.format("SELECT  TABLE_NAME,COLUMN_NAME  FROM  INFORMATION_SCHEMA.KEY_COLUMN_USAGE  WHERE  TABLE_NAME= '%s';", rs.getString("name"));

                                Statement st3 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY);
                                ResultSet keyList = st3.executeQuery(constraintKeySql);
                                List<TableInfoEntity> tableInfoEntities = null;
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
                                jsonArray.add(rs.getString("name"));
                                i += progressItem;
                                updateProgress(i, 100);
                            }
                            transaction.commit();
                            stmt.close();
                            CommonStage.db_info.clear();
                            CommonStage.db_info.put("db_table", jsonArray);
                            CommonStage.db_info.put("db_name", config.getString("database"));
                            CommonStage.db_info.put("config", config);
                            con.close();
                            updateProgress(100, 100);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Scene scene = new GenController(new BorderPane(), 800, 600);
                                    CommonStage.createStage("代码生成器", new Image("/images/icon/logo.png"), Style.DARK,
                                            scene).show();
                                    updateProgress(0, 100);
                                    hideProgress();
                                }
                            });
                        } catch (SQLServerException e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
//                            run.setDisable(false);
                                    CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "连接出错", e);
                                    hideProgress();
                                }
                            });
                            e.printStackTrace();
                        } catch (Exception e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
//                            run.setDisable(false);
                                    CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "程序异常", e);
                                    hideProgress();
                                }
                            });
                            e.printStackTrace();
                        }
                    }

                    return null;
                }

                @Override
                protected void running() {
                    // TODO Auto-generated method stub
                    super.running();
                }

                @Override
                protected void succeeded() {
                    // TODO Auto-generated method stub
                    super.succeeded();
                }

                @Override
                protected void failed() {
                    // TODO Auto-generated method stub
                    super.failed();
                }

                @Override
                protected void updateProgress(long workDone, long max) {
                    // TODO Auto-generated method stub
                    super.updateProgress(workDone, max);
                }

                @Override
                protected void updateMessage(String message) {
                    // TODO Auto-generated method stub
                    super.updateMessage(message);
                }

                @Override
                protected void updateTitle(String title) {
                    // TODO Auto-generated method stub
                    super.updateTitle(title);
                }

            };
        }
    };

    Service<Integer> service1 = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {

                @Override
                protected Integer call() throws Exception {
                    // TODO Auto-generated method stub
//            run.setDisable(true);
                    Thread.sleep(150);
                    double i = 0.0;
                    updateProgress(i, 100);
                    OriginEntity originEntity = table.getSelectionModel().selectedItemProperty().getValue();
                    if (originEntity == null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                            }
                        });
                        return null;
                    } else {
                        try {
                            JSONObject config = JSON.parseObject(originEntity.getConfig());
                            String url = String.format("jdbc:sqlserver://%s:%s;DatabaseName=%s", config.getString("host"), config.getString("port"), config.getString("database"));
                            System.out.println(url);
                            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                            Connection con = DriverManager.getConnection(url, config.getString("username"), config.getString("password"));
                            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);
                            System.out.println("连接成功!");
                            i += 10;
                            updateProgress(i, 100);
                            String SQL = String.format("select * from %s..sysobjects where xtype = 'U'", config.getString("database"));
                            ResultSet rs = stmt.executeQuery(SQL);
                            rs.last();
                            JSONArray jsonArray = new JSONArray();
                            double progressItem = ((double) (100.0 - i) / rs.getRow());
                            System.out.println(progressItem);
                            rs.beforeFirst();

                            while (rs.next()) {
                                jsonArray.add(rs.getString("name"));
                                i += progressItem;
                                updateProgress(i, 100);
                            }
                            stmt.close();
                            CommonStage.db_info.clear();
                            CommonStage.db_info.put("db_table", jsonArray);
                            CommonStage.db_info.put("db_name", config.getString("database"));
                            CommonStage.db_info.put("config", config);

                            con.close();
                            updateProgress(100, 100);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Scene scene = new GenController(new BorderPane(), 800, 600);
                                    CommonStage.createStage("代码生成器", new Image("/images/icon/logo.png"), Style.DARK,
                                            scene).show();
                                    updateProgress(0, 100);
                                    hideProgress();
                                }
                            });
                        } catch (SQLServerException e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
//                            run.setDisable(false);
                                    CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "连接出错", e);
                                    hideProgress();
                                }
                            });
                            e.printStackTrace();
                        } catch (Exception e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
//                            run.setDisable(false);
                                    CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "程序异常", e);
                                    hideProgress();
                                }
                            });
                            e.printStackTrace();
                        }
                    }

                    return null;
                }

                @Override
                protected void running() {
                    // TODO Auto-generated method stub
                    super.running();
                }

                @Override
                protected void succeeded() {
                    // TODO Auto-generated method stub
                    super.succeeded();
                }

                @Override
                protected void failed() {
                    // TODO Auto-generated method stub
                    super.failed();
                }

                @Override
                protected void updateProgress(long workDone, long max) {
                    // TODO Auto-generated method stub
                    super.updateProgress(workDone, max);
                }

                @Override
                protected void updateMessage(String message) {
                    // TODO Auto-generated method stub
                    super.updateMessage(message);
                }

                @Override
                protected void updateTitle(String title) {
                    // TODO Auto-generated method stub
                    super.updateTitle(title);
                }

            };
        }
    };

    public void initTable() {
        table.setId("originTable");
        table.getStylesheets().add(RuleForm.class.getResource("/styles/application.css").toExternalForm());

        TableColumn<OriginEntity, String> nameColumn = new TableColumn<>("名称");
        nameColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("name")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<OriginEntity, String> typeColumn = new TableColumn<>("类型");
        typeColumn.setCellValueFactory(cellData -> {
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

        TableColumn<OriginEntity, String> databaseColumn = new TableColumn<>("数据库");
        databaseColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("database")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        databaseColumn.setStyle("-fx-alignment: CENTER;");
        typeColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setStyle("-fx-alignment: CENTER;");

        table.setItems(OriginDao.getOrigins(session));
        table.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().isCellSelectionEnabled();
        table.setEditable(true);

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OriginEntity>() {
            @Override
            public void changed(ObservableValue<? extends OriginEntity> observable, OriginEntity oldValue, OriginEntity newValue) {

                if (Objects.isNull(newValue)) {
                    run.setDisable(true);
                    delOrigin.setDisable(true);
                } else {
                    run.setDisable(false);
                    delOrigin.setDisable(false);
                }
            }
        });

        table.getSelectionModel().setCellSelectionEnabled(false);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().addAll(nameColumn, typeColumn, databaseColumn);
    }

    public OriginController(Session session, int w, int h) {
        run.setDisable(true);
        delOrigin.setDisable(true);
        // --- Menu File
//        Menu menuFile = new Menu("数据源");
//        MenuItem add = new MenuItem("添加数据源",
//                new ImageView(new Image(OriginController.class.getResource("/images/icon/sqlserver.png").toExternalForm())));

        ToolBar toolBar = new ToolBar();
        Button addOrigin = new Button();
        ImageView imageView = new ImageView(OriginController.class.getResource("/images/icon/add.png").toExternalForm());
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        addOrigin.setGraphic(imageView);
        addOrigin.setFocusTraversable(FOCUS_TRAVERSAL);
        addOrigin.setOnMouseClicked((event -> {
            Scene scene = new OriginForm(new BorderPane(), 565, 300);
            CommonStage.createStageHaveScene("添加数据源", new Image("/images/icon/logo.png"), Style.DARK,
                    scene).show();

            if (!Objects.isNull(scene.getWindow())) {
                scene.getWindow().setOnHidden((event1 -> {
                    this.refresh();
                }));
            }
        }));

        ImageView delIcon = new ImageView(OriginController.class.getResource("/images/icon/del.png").toExternalForm());
        delIcon.setFitHeight(16);
        delIcon.setFitWidth(16);
        delOrigin.setGraphic(delIcon);
        delOrigin.setFocusTraversable(FOCUS_TRAVERSAL);
        delOrigin.setOnMouseClicked((event -> {
            OriginEntity originEntity = table.getSelectionModel().selectedItemProperty().getValue();
            session.doWork(connection -> {
                Transaction transaction = session.beginTransaction();
                Statement st;
                String sql = "delete from origin where id = " + originEntity.getId();
                st = connection.createStatement();
                st.executeUpdate(sql);
                st.close();
                transaction.commit();
                this.refresh();
            });
        }));

        MenuItem run1 = new MenuItem("运行");
        MenuItem run2 = new MenuItem("同步后运行");

        ImageView runIcon = new ImageView(OriginController.class.getResource("/images/icon/media_playback_start_256px_539991_easyicon.net.png").toExternalForm());
        runIcon.setFitHeight(16);
        runIcon.setFitWidth(16);
        run.getItems().addAll(run1, run2);
        run.setGraphic(runIcon);
        run.setFocusTraversable(FOCUS_TRAVERSAL);
        run1.setOnAction((event -> {
            this.getLoading1();
        }));
        run2.setOnAction((event -> {
            this.getLoading();
        }));
        MenuItem ftlFile = new MenuItem("模板");
        MenuItem rule = new MenuItem("规则");
        rule.setOnAction((e) -> {
            Scene scene = new RuleForm(new BorderPane(), 565, 485);
            CommonStage.createStageHaveScene("添加规则", new Image("/images/icon/logo.png"), Style.DARK,
                    scene).show();
        });
        ftlFile.setOnAction((e) -> {
            Scene scene = new FtlFileController(new BorderPane(), 425, 400);
            CommonStage.createStageHaveScene("代码模板", new Image("/images/icon/logo.png"), Style.DARK,
                    scene).show();
        });
        SplitMenuButton splitMenuButton = new SplitMenuButton();
        ImageView templateIcon = new ImageView(OriginController.class.getResource("/images/icon/code.png").toExternalForm());
        templateIcon.setFitHeight(20);
        templateIcon.setFitWidth(20);
        splitMenuButton.setGraphic(templateIcon);
        splitMenuButton.getItems().addAll(ftlFile, rule);
        splitMenuButton.setFocusTraversable(FOCUS_TRAVERSAL);


//        Button template = new Button();
//
//        template.setGraphic(templateIcon);
//        template.setFocusTraversable(FOCUS_TRAVERSAL);
//        template.setOnMouseClicked((event -> {
//
//        }));
        Button refresh = new Button();
        ImageView refreshIcon = new ImageView(OriginController.class.getResource("/images/icon/refresh.png").toExternalForm());
        refreshIcon.setFitHeight(16);
        refreshIcon.setFitWidth(16);
        refresh.setGraphic(refreshIcon);
        refresh.setFocusTraversable(FOCUS_TRAVERSAL);
        refresh.setOnMouseClicked((event -> {
            this.refresh();
        }));
        Button setting = new Button();
        ImageView settingIcon = new ImageView(OriginController.class.getResource("/images/icon/setting.png").toExternalForm());
        settingIcon.setFitHeight(16);
        settingIcon.setFitWidth(16);
        setting.setGraphic(settingIcon);
        setting.setFocusTraversable(FOCUS_TRAVERSAL);
        setting.setOnMouseClicked((event -> {
            Scene scene = new SettingController(new BorderPane(), 530, 300);
            CommonStage.createStageHaveScene("设置", new Image("/images/icon/logo.png"), Style.DARK,
                    scene).show();
        }));


        toolBar.getItems().addAll(addOrigin, delOrigin, refresh, new Separator(), splitMenuButton, setting, new Separator(), run);
        this.setTop(toolBar);
        initTable();
        if (OriginDao.getOrigins(session).size() == 0) {
            this.setCenter(getNoData(w, h));
        } else {
            this.setCenter(table);
        }
        this.getStyleClass().add("background");
    }

    private void refresh() {
        this.table.getItems().clear();
        this.table.getItems().addAll(OriginDao.getOrigins(session));
        this.table.refresh();
    }

    private StackPane getNoData(int w, int h) {
        Font font = Font.font("System", 15);
        Label noData = new Label("没有数据源");
        noData.setFont(font);
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(w, h);
        stackPane.getChildren().add(noData);
        stackPane.setPadding(new Insets(0, 0, 50, 0));
        StackPane.setAlignment(noData, Pos.CENTER);
        return stackPane;
    }

    private void hideProgress() {
        progressBar.setVisible(false);
    }

    private void getLoading() {
        progressBar = new ProgressBar();
        progressBar.progressProperty().bind(service.progressProperty());
        progressBar.setVisible(true);
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #252525;");
        hBox.getChildren().clear();
        hBox.getChildren().add(progressBar);
        hBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        this.setBottom(hBox);
        service.restart();
    }

    private void getLoading1() {
        progressBar = new ProgressBar();
        progressBar.progressProperty().bind(service1.progressProperty());
        progressBar.setVisible(true);
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #252525;");
        hBox.getChildren().clear();
        hBox.getChildren().add(progressBar);
        hBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        this.setBottom(hBox);
        service1.restart();
    }

    private static CheckMenuItem createMenuItem(String title) {
        CheckMenuItem cmi = new CheckMenuItem(title);
        cmi.setSelected(true);
        return cmi;
    }
}
