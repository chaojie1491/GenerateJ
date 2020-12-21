package util;

public class GenUtil {

    public static String getGenType(String dbType) {
        if (dbType.contains("char")) {
            return "String";
        } else if (dbType.equals("int")) {
            return "int32";
        } else if (dbType.equals("datetime") || dbType.equals("date")) {
            return "DateTime";
        } else if (dbType.equals("bigint")) {
            return "Int64";
        }else if (dbType.equals("binary")) {
            return "Int64";
        } else {
            return null;
        }
    }
}
