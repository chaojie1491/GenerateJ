package dao;

import entity.OriginEntity;
import entity.RuleEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;

import java.util.List;

public class RuleDao {

    public static ObservableList<RuleEntity> getRules(Session session) {
        List<RuleEntity> ruleEntities = session.createQuery("from entity.RuleEntity").list();
        ObservableList<RuleEntity> strings = FXCollections.observableArrayList();
        strings.addAll(ruleEntities);
        return strings;
    }
}
