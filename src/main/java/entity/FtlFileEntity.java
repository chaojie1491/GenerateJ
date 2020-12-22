package entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

@Entity
@Table(name = "ftl_file", schema = "main")
public class FtlFileEntity {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Short id;
    private String fileName;
    private String createTime;
    private String originPath;
    private String nowPath;
    private StringProperty idProperty = new SimpleStringProperty();
    private StringProperty fileNameProperty = new SimpleStringProperty();

    private BooleanProperty ItemBool = new SimpleBooleanProperty(false);
    private String nowName;

    public boolean isItemBool() {
        return ItemBool.get();
    }

    public void setItemBool(boolean itemBool) {
        this.ItemBool.set(itemBool);
    }

    public BooleanProperty itemBoolProperty() {
        return ItemBool;
    }

    @Id
    @Column(name = "id", nullable = false)
    public Short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
        pcs.firePropertyChange("id", id, id);
        idProperty.setValue(String.valueOf(id));
    }

    public String getIdProperty() {
        return idProperty.get();
    }

    public void setIdProperty(String idProperty) {
        this.idProperty.set(idProperty);
    }

    public StringProperty idPropertyProperty() {
        return idProperty;
    }

    public String getFileNameProperty() {
        return fileNameProperty.get();
    }

    public void setFileNameProperty(String fileNameProperty) {
        this.fileNameProperty.set(fileNameProperty);
    }

    public StringProperty fileNamePropertyProperty() {
        return fileNameProperty;
    }

    @Basic
    @Column(name = "file_name", nullable = true, length = -1)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        pcs.firePropertyChange("fileName", fileName, fileName);
        idProperty.setValue(String.valueOf(fileName));
    }

    @Basic
    @Column(name = "create_time", nullable = true, length = -1)
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        pcs.firePropertyChange("createTime", createTime, createTime);
    }

    @Basic
    @Column(name = "origin_path", nullable = true, length = -1)
    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
        pcs.firePropertyChange("originPath", originPath, originPath);
    }

    @Basic
    @Column(name = "now_path", nullable = true, length = -1)
    public String getNowPath() {
        return nowPath;
    }

    public void setNowPath(String nowPath) {
        this.nowPath = nowPath;
        pcs.firePropertyChange("nowPath", nowPath, nowPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FtlFileEntity that = (FtlFileEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(originPath, that.originPath) &&
                Objects.equals(nowPath, that.nowPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, createTime, originPath, nowPath);
    }

    @Basic
    @Column(name = "now_name", nullable = true, length = -1)
    public String getNowName() {
        return nowName;
    }

    public void setNowName(String nowName) {
        this.nowName = nowName;
    }
}
