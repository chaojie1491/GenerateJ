package entity;

import javax.persistence.*;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

@Entity
@Table(name = "ftl_file", schema = "main")
public class FtlFileEntity {
    private Short id;
    private String fileName;
    private String createTime;
    private String originPath;
    private String nowPath;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    @Id
    @Column(name = "id", nullable = false)
    public Short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
        pcs.firePropertyChange("id", id, id);
    }


    @Basic
    @Column(name = "file_name", nullable = true, length = -1)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        pcs.firePropertyChange("fileName", fileName, fileName);
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
}
