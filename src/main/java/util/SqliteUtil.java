package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 连接 Sqlite
 */
public class SqliteUtil {

    private static Connection connection;

    public synchronized static Connection getConnection() throws SQLException {
        //如果 当前练
        if (connection == null) {
            try {
                String driverClass = "org.sqlite.JDBC";
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = "jdbc:sqlite:Gen.db";
            return connection = DriverManager.getConnection(url);
        } else {
            return connection;
        }
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new NullPointerException("连接未开启！");
        }
    }


    private static final String FTL_FILE = "create table ftl_file" +
            "(" +
            "    id          INTEGER not null" +
            "        constraint ftl_file_pk" +
            "            primary key autoincrement," +
            "    file_name   TEXT," +
            "    create_time TEXT," +
            "    origin_path TEXT," +
            "    now_path    TEXT," +
            "    now_name    TEXT" +
            ");";

    private static final String ORIGIN = "create table origin" +
            "(" +
            "    id       INTEGER not null" +
            "        constraint origin_pk" +
            "            primary key autoincrement," +
            "    name     TEXT," +
            "    type     TEXT," +
            "    config   TEXT," +
            "    database TEXT" +
            ");";

    public static final String RULE = "create table rule" +
            "(" +
            "    id            integer not null" +
            "        constraint rule_pk" +
            "            primary key autoincrement," +
            "    rule_name     TEXT," +
            "    language      TEXT," +
            "    table_prefix  TEXT," +
            "    namespace     TEXT," +
            "    entity_prefix TEXT," +
            "    entity_suffix TEXT," +
            "    isUc          TEXT," +
            "    patent_class  TEXT," +
            "    model         TEXT" +
            ");";
    public static final String SETTING = "create table setting" +
            "(" +
            "    id       INTEGER not null" +
            "        constraint setting_pk" +
            "            primary key autoincrement," +
            "    ftl_path TEXT," +
            "    out_path TEXT" +
            ");";
    public static final String TABLE_INFO = "create table table_info" +
            "(" +
            "    id             INTEGER not null" +
            "        constraint table_info_pk" +
            "            primary key autoincrement," +
            "    table_id       INTEGER," +
            "    table_name     TEXT," +
            "    column_name    TEXT," +
            "    constraint_key TEXT," +
            "    is_null        TEXT," +
            "    type           TEXT," +
            "    gen_type       text," +
            "    len            INTEGER," +
            "    tb_desc        TEXT," +
            "    field_desc     text," +
            "    identity       TEXT" +
            ");";

    public static final String unique = "create unique index table_info_id_uindex" +
            "    on table_info (id);" +
            "create unique index setting_id_uindex" +
            "    on setting (id);"
            + "create unique index rule_id_uindex" +
            "    on rule (id);"
            + "create unique index origin_id_uindex" +
            "    on origin (id);"
            + "create unique index ftl_file_id_uindex" +
            "    on ftl_file (id);";

    static String INIT = "insert into main.setting (id, ftl_path, out_path) values (1000, null, null);\n";

    public static void createDatabases() throws SQLException, IOException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        System.out.println(FTL_FILE);
        statement.execute(FTL_FILE);
        statement.execute(ORIGIN);
        statement.execute(RULE);
        statement.execute(TABLE_INFO);
        statement.execute(SETTING);
        statement.execute(unique);
        statement.execute(INIT);
        close();
    }

}
