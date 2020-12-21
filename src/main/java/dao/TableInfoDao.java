package dao;

import entity.RuleEntity;
import entity.TableInfoEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class TableInfoDao {
    public static ObservableList<TableInfoEntity> getRules(Session session, String tbName) {
        Query query = session.createQuery("from entity.TableInfoEntity where tableName = :tbName");
        query.setString("tbName", tbName);
        List<TableInfoEntity> ruleEntities = query.list();
        ObservableList<TableInfoEntity> strings = FXCollections.observableArrayList();
        strings.addAll(ruleEntities);
        return strings;
    }
}
