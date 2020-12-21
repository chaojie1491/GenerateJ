package entity;

import javax.persistence.*;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

@Entity
@Table(name = "origin", schema = "main")
public class OriginEntity {
    private Short id;
    private String name;
    private String type;
    private String config;
    private String database;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    @Id
    @Column(name = "id", nullable = false)
    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
        pcs.firePropertyChange("id", id, id);
    }

    @Basic
    @Column(name = "name", nullable = true, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        pcs.firePropertyChange("name", name, name);
    }
    @Basic
    @Column(name = "database", nullable = true, length = -1)
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
        pcs.firePropertyChange("database", database, database);
    }

    @Basic
    @Column(name = "type", nullable = true, length = -1)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        pcs.firePropertyChange("type", type, type);
    }

    @Basic
    @Column(name = "config", nullable = true, length = -1)
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OriginEntity that = (OriginEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, config);
    }
}
