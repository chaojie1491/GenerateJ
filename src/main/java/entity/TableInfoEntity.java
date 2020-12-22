package entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

@Entity
@Table(name = "table_info", schema = "main")
public class TableInfoEntity {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private short id;
    private Short tableId;
    private String tableName;
    private String columnName;
    private String constraintKey;
    private String isNull;
    private String type;
    private String genType;
    private Short len;
    private StringProperty lenProper = new SimpleStringProperty();
    private BooleanProperty isKey = new SimpleBooleanProperty(true);
    private BooleanProperty isNullable = new SimpleBooleanProperty(true);
    private String tbDesc;
    private String fieldDesc;

    public String getLenProper() {
        return lenProper.get();
    }

    public void setLenProper(String lenProper) {
        this.lenProper.set(lenProper);
    }

    public StringProperty lenProperProperty() {
        return lenProper;
    }

    public boolean isIsNullable() {
        return isNullable.get();
    }

    public void setIsNullable(boolean isNullable) {
        this.isNullable.set(isNullable);
    }

    public BooleanProperty isNullableProperty() {
        return isNullable;
    }

    public boolean isIsKey() {
        return isKey.get();
    }

    public void setIsKey(boolean isKey) {
        this.isKey.set(isKey);
    }

    public BooleanProperty isKeyProperty() {
        return isKey;
    }

    @Id
    @Column(name = "id", nullable = false)
    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
        pcs.firePropertyChange("id", id, id);
    }

    @Basic
    @Column(name = "table_id", nullable = true)
    public Short getTableId() {
        return tableId;
    }

    public void setTableId(Short tableId) {
        this.tableId = tableId;
        pcs.firePropertyChange("tableId", tableId, tableId);
    }

    @Basic
    @Column(name = "table_name", nullable = true, length = -1)
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        pcs.firePropertyChange("tableName", tableName, tableName);
    }

    @Basic
    @Column(name = "column_name", nullable = true, length = -1)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        pcs.firePropertyChange("columnName", columnName, columnName);
    }

    @Basic
    @Column(name = "constraint_key", nullable = true, length = -1)
    public String getConstraintKey() {
        return constraintKey;
    }

    public void setConstraintKey(String constraintKey) {
        if (constraintKey.equals("N")) {
            isKey.setValue(false);
        } else {
            isKey.setValue(true);
        }
        this.constraintKey = constraintKey;
        pcs.firePropertyChange("constraintKey", constraintKey, constraintKey);
    }

    @Basic
    @Column(name = "is_null", nullable = true, length = -1)
    public String getIsNull() {
        if (isNull.equals("N")) {
            isNullable.setValue(false);
        } else {
            isNullable.setValue(true);
        }
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
        pcs.firePropertyChange("isNull", isNull, isNull);
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
    @Column(name = "gen_type", nullable = true, length = -1)
    public String getGenType() {
        return genType;
    }

    public void setGenType(String genType) {
        this.genType = genType;
        pcs.firePropertyChange("genType", genType, genType);
    }

    @Basic
    @Column(name = "len", nullable = true)
    public Short getLen() {
        return len;
    }

    public void setLen(Short len) {
        this.len = len;
        lenProper.setValue(String.valueOf(len));
        pcs.firePropertyChange("len", String.valueOf(len), String.valueOf(len));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableInfoEntity that = (TableInfoEntity) o;
        return id == that.id &&
                Objects.equals(tableId, that.tableId) &&
                Objects.equals(tableName, that.tableName) &&
                Objects.equals(columnName, that.columnName) &&
                Objects.equals(constraintKey, that.constraintKey) &&
                Objects.equals(isNull, that.isNull) &&
                Objects.equals(type, that.type) &&
                Objects.equals(genType, that.genType) &&
                Objects.equals(len, that.len);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tableId, tableName, columnName, constraintKey, isNull, type, genType, len);
    }


    @Basic
    @Column(name = "tb_desc", nullable = true, length = -1)
    public String getTbDesc() {
        return tbDesc;
    }

    public void setTbDesc(String tbDesc) {
        this.tbDesc = tbDesc;
    }

    @Basic
    @Column(name = "field_desc", nullable = true, length = -1)
    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }
}
