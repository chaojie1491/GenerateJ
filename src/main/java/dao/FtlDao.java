package dao;

import entity.FtlFileEntity;
import entity.OriginEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;

import java.util.List;

public class FtlDao {

    public static ObservableList<FtlFileEntity> getFtls(Session session) {
        List<FtlFileEntity> ftlFileEntities = session.createQuery("from entity.FtlFileEntity").list();
        ObservableList<FtlFileEntity> strings = FXCollections.observableArrayList();
        strings.addAll(ftlFileEntities);
        return strings;
    }
}
