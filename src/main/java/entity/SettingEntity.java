package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "setting", schema = "main", catalog = "")
public class SettingEntity {
    private short id;
    private String ftlPath;
    private String outPath;

    @Id
    @Column(name = "id", nullable = false)
    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ftl_path", nullable = true, length = -1)
    public String getFtlPath() {
        return ftlPath;
    }

    public void setFtlPath(String ftlPath) {
        this.ftlPath = ftlPath;
    }

    @Basic
    @Column(name = "out_path", nullable = true, length = -1)
    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingEntity that = (SettingEntity) o;
        return id == that.id &&
                Objects.equals(ftlPath, that.ftlPath) &&
                Objects.equals(outPath, that.outPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ftlPath, outPath);
    }
}
