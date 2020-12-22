package entity;

import javax.persistence.*;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

@Entity
@Table(name = "rule", schema = "main")
public class RuleEntity {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private short id;
    private String ruleName;
    private String language;
    private String tablePrefix;
    private String namespace;
    private String entityPrefix;
    private String entitySuffix;
    private String isUc;
    private String patentClass;

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
    @Column(name = "rule_name", nullable = true, length = -1)
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
        pcs.firePropertyChange("ruleName", ruleName, ruleName);
    }

    @Basic
    @Column(name = "language", nullable = true, length = -1)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        pcs.firePropertyChange("language", language, language);
    }

    @Basic
    @Column(name = "table_prefix", nullable = true, length = -1)
    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        pcs.firePropertyChange("tablePrefix", tablePrefix, tablePrefix);
    }

    @Basic
    @Column(name = "namespace", nullable = true, length = -1)
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
        pcs.firePropertyChange("namespace", namespace, namespace);

    }

    @Basic
    @Column(name = "entity_prefix", nullable = true, length = -1)
    public String getEntityPrefix() {
        return entityPrefix;
    }

    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
        pcs.firePropertyChange("entityPrefix", entityPrefix, entityPrefix);

    }

    @Basic
    @Column(name = "entity_suffix", nullable = true, length = -1)
    public String getEntitySuffix() {
        return entitySuffix;
    }

    public void setEntitySuffix(String entitySuffix) {
        this.entitySuffix = entitySuffix;
        pcs.firePropertyChange("entitySuffix", entitySuffix, entitySuffix);
    }

    @Basic
    @Column(name = "isUc", nullable = true, length = -1)
    public String getIsUc() {
        return isUc;
    }

    public void setIsUc(String isUc) {
        this.isUc = isUc;
        pcs.firePropertyChange("isUc", isUc, isUc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleEntity that = (RuleEntity) o;
        return id == that.id &&
                Objects.equals(ruleName, that.ruleName) &&
                Objects.equals(language, that.language) &&
                Objects.equals(tablePrefix, that.tablePrefix) &&
                Objects.equals(namespace, that.namespace) &&
                Objects.equals(entityPrefix, that.entityPrefix) &&
                Objects.equals(entitySuffix, that.entitySuffix) &&
                Objects.equals(isUc, that.isUc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ruleName, language, tablePrefix, namespace, entityPrefix, entitySuffix, isUc);
    }

    @Basic
    @Column(name = "patent_class", nullable = true, length = -1)
    public String getPatentClass() {
        return patentClass;
    }

    public void setPatentClass(String patentClass) {
        this.patentClass = patentClass;
    }
}
