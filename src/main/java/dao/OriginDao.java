package dao;

import entity.OriginEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class OriginDao {

    public static ObservableList<OriginEntity> getOrigins(Session session) {
        List<OriginEntity> originEntities = session.createQuery("from entity.OriginEntity").list();
        ObservableList<OriginEntity> strings = FXCollections.observableArrayList();
        strings.addAll(originEntities);
        return strings;
    }
}
