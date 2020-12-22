package controller;

import base.CommonDialog;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.jfoenix.controls.JFXProgressBar;
import dao.FtlDao;
import dao.RuleDao;
import entity.FtlFileEntity;
import entity.OriginEntity;
import entity.RuleEntity;
import entity.SettingEntity;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import util.HibernateUtil;

import java.io.*;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static base.RichEdit.computeHighlighting;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class FtlFileController extends Scene {
    private static final boolean FOCUS_TRAVERSAL = false;

    private Session session = HibernateUtil.currentSession();

    private TableView<FtlFileEntity> ftlFileEntityTableView = new TableView<>();
    Button delBtn = new Button();
    Button editBtn = new Button();
    JFXProgressBar progressBar = new JFXProgressBar();

    public FtlFileController(Parent root, double width, double height) {
        super(root, width, height);
        BorderPane borderPane = (BorderPane) root;
        ((BorderPane) root).setPrefSize(width, height);
        borderPane.getStyleClass().add("background");
        delBtn.setDisable(true);
        editBtn.setDisable(true);
        Button uploadBtn = new Button();
        ImageView uploadIcon = new ImageView(OriginController.class.getResource("/images/icon/upload.png").toExternalForm());
        uploadIcon.setFitHeight(16);
        uploadIcon.setFitWidth(16);
        uploadBtn.setGraphic(uploadIcon);
        uploadBtn.setFocusTraversable(FOCUS_TRAVERSAL);
        uploadBtn.setOnMouseClicked((event -> {
        }));
        uploadBtn.setTooltip(new Tooltip("上传Ftl文件"));
        uploadBtn.setOnMouseClicked((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            List<File> list = fileChooser.showOpenMultipleDialog(uploadBtn.getScene().getWindow());
            if (list != null) {
                list.forEach(file -> {
                    try {
                        this.uploadFile(file);
                    } catch (IOException ioException) {
                        CommonDialog.showJMetroAlertWithExpandableContent(Style.DARK, "上传失败！", ioException);
                        ioException.printStackTrace();
                    }
                });
                this.refresh();
            }
        });
        ImageView editIcon = new ImageView(OriginController.class.getResource("/images/icon/edit.png").toExternalForm());
        editIcon.setFitHeight(16);
        editIcon.setFitWidth(16);
        editBtn.setGraphic(editIcon);
        editBtn.setFocusTraversable(FOCUS_TRAVERSAL);
        editBtn.setOnMouseClicked((event -> {
            FtlFileEntity ftlFileEntity = ftlFileEntityTableView.getSelectionModel().selectedItemProperty().getValue();
            String suffix = ftlFileEntity.getFileName().substring(ftlFileEntity.getFileName().lastIndexOf("."));
            FileReader fileReader = new FileReader(ftlFileEntity.getNowPath());
            String result = fileReader.readString();

            CodeArea codeArea = new CodeArea();
            codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

            codeArea.textProperty().addListener((obs, oldText, newText) -> {
                codeArea.setStyleSpans(0, computeHighlighting(newText));
            });
            Font font = Font.font("System",15);
            codeArea.setFont(font);
            codeArea.replaceText(0, 0, result);

            Scene scene = new Scene(new StackPane(codeArea), 600, 400);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.setTitle(ftlFileEntity.getFileName());
            primaryStage.setAlwaysOnTop(true);
            primaryStage.getIcons().add(new Image("/images/icon/logo.png"));

            primaryStage.show();

        }));
        editBtn.setTooltip(new Tooltip("编辑Ftl模板"));

        ImageView delIcon = new ImageView(OriginController.class.getResource("/images/icon/del.png").toExternalForm());
        delIcon.setFitHeight(16);
        delIcon.setFitWidth(16);
        delBtn.setGraphic(delIcon);
        delBtn.setFocusTraversable(FOCUS_TRAVERSAL);
        delBtn.setOnMouseClicked((event -> {
        }));
        delBtn.setTooltip(new Tooltip("删除选中行"));
        delBtn.setOnMouseClicked((e) -> {

            FtlFileEntity ftlFileEntity = ftlFileEntityTableView.getSelectionModel().selectedItemProperty().getValue();
            session.doWork(connection -> {
                Transaction transaction = session.beginTransaction();
                Statement st;
                String sql = "delete from ftl_file where id = " + ftlFileEntity.getId();
                st = connection.createStatement();
                st.executeUpdate(sql);
                st.close();
                transaction.commit();
                FileUtil.del(ftlFileEntity.getNowPath());
                this.refresh();
            });

        });
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(uploadBtn, delBtn, editBtn);
        VBox vBox = new VBox();
        progressBar.setMinWidth(this.getWidth());
        progressBar.setSecondaryProgress(-1);
        vBox.getChildren().addAll(toolBar);
        borderPane.setTop(vBox);
        borderPane.setCenter(ftlFileEntityTableView);
        initTable();
    }

    public void uploadFile(File file) throws IOException {
        FtlFileEntity ftlFileEntity = new FtlFileEntity();
        SettingEntity settingEntity = (SettingEntity) session.createQuery("from entity.SettingEntity").list().get(0);
        File ftlPath = new File(settingEntity.getFtlPath());
        if (!ftlPath.exists()) {
            ftlPath.mkdir();
        }
        FileInputStream fis = new FileInputStream(file);
        //创建新的文件，保存复制内容，文件名称与源文件名称一致
        String suffix = file.getName().substring(file.getName().lastIndexOf("."));

        File newFile = new File(ftlPath + "\\" + UUID.randomUUID() + suffix);
        ftlFileEntity.setFileName(file.getName());
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(newFile);
        byte[] b = new byte[1024];
        int len;
        while ((len = fis.read(b)) != -1) {
            fos.write(b, 0, len);
        }
        fos.close();
        fis.close();
        ftlFileEntity.setCreateTime(DateTime.now().toStringDefaultTimeZone());
        ftlFileEntity.setOriginPath(file.getAbsolutePath());
        ftlFileEntity.setNowPath(newFile.getAbsolutePath());
        ftlFileEntity.setNowName(newFile.getName());
        Transaction transaction;
        if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
            transaction = session.getTransaction();
        } else {
            transaction = session.beginTransaction();
        }
        session.clear();
        session.saveOrUpdate(ftlFileEntity);
        transaction.commit();
    }

    public void initTable() {
        ftlFileEntityTableView.setId("ftlTable");
        ftlFileEntityTableView.getStylesheets().add(RuleForm.class.getResource("/styles/application.css").toExternalForm());
        TableColumn<FtlFileEntity, String> fileNameColumn = new TableColumn<>("文件名");
        fileNameColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("fileName")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<FtlFileEntity, String> pathColumn = new TableColumn<>("路径");
        pathColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("originPath")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        TableColumn<FtlFileEntity, String> createTimeColumn = new TableColumn<>("创建时间");
        createTimeColumn.setCellValueFactory(cellData -> {
            try {
                return JavaBeanStringPropertyBuilder.create()
                        .bean(cellData.getValue())
                        .name("createTime")
                        .build();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        ftlFileEntityTableView.setItems(FtlDao.getFtls(session));
        ftlFileEntityTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        ftlFileEntityTableView.getSelectionModel().isCellSelectionEnabled();
        ftlFileEntityTableView.setEditable(true);
        ftlFileEntityTableView.getSelectionModel().setCellSelectionEnabled(false);
        ftlFileEntityTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ftlFileEntityTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FtlFileEntity>() {
            @Override
            public void changed(ObservableValue<? extends FtlFileEntity> observable, FtlFileEntity oldValue, FtlFileEntity newValue) {

                if (Objects.isNull(newValue)) {
                    delBtn.setDisable(true);
                    editBtn.setDisable(true);
                } else {
                    delBtn.setDisable(false);
                    editBtn.setDisable(false);
                }
            }
        });
        createTimeColumn.setStyle("-fx-alignment: CENTER;");
        fileNameColumn.setStyle("-fx-alignment: CENTER;");
        pathColumn.setStyle("-fx-alignment: CENTER;");
        ftlFileEntityTableView.getColumns().addAll(fileNameColumn, pathColumn, createTimeColumn);
    }

    public void refresh() {
        this.ftlFileEntityTableView.getItems().clear();
        this.ftlFileEntityTableView.getItems().addAll(FtlDao.getFtls(session));
        this.ftlFileEntityTableView.refresh();
    }

}
