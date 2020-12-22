package util;

import entity.FtlFileEntity;
import entity.RuleEntity;
import entity.SettingEntity;
import entity.TableInfoEntity;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.hibernate.Session;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenUtil {

    public static String getGenType(String dbType) {
        if (dbType.contains("char")) {
            return "string";
        } else if (dbType.equals("int")) {
            return "Int32";
        } else if (dbType.equals("datetime") || dbType.equals("date")) {
            return "DateTime";
        } else if (dbType.equals("bigint")) {
            return "Int64";
        } else if (dbType.equals("binary")) {
            return "Int64";
        } else if (dbType.equals("bit")) {
            return "Boolean";
        } else if (dbType.equals("decimal")) {
            return "Decimal";
        } else if (dbType.equals("float")) {
            return "Double";
        } else if (dbType.equals("image")) {
            return "Byte[]";
        } else if (dbType.equals("numeric")) {
            return "Decimal";
        } else if (dbType.equals("nvarchar")) {
            return "Decimal";
        } else if (dbType.equals("text")) {
            return "string";
        } else if (dbType.equals("timestamp")) {
            return "Byte[]";
        } else if (dbType.equals("tinyint")) {
            return "Byte[]";
        } else if (dbType.equals("varchar")) {
            return "string";
        } else {
            return null;
        }
    }


    public static void genFile(RuleEntity ruleEntity, List<TableInfoEntity> entities, List<FtlFileEntity> ftlFileEntities, SettingEntity settingEntity) throws IOException, TemplateException {
        Configuration configuration = new Configuration();

        // step2 获取模版路径
        configuration.setDirectoryForTemplateLoading(new File(settingEntity.getFtlPath()));
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("packageName", ruleEntity.getNamespace());
        dataMap.put("className", entities.get(0).getTableName().replaceAll("_", ""));
        dataMap.put("tableName", entities.get(0).getTableName());
        dataMap.put("fields", entities);
        if (!ruleEntity.getPatentClass().isEmpty()) {
            dataMap.put("extendsClass", " : " + ruleEntity.getPatentClass());
        }
        Writer out = null;

        for (int i = 0; i < ftlFileEntities.size(); i++) {
            Template template = configuration.getTemplate(ftlFileEntities.get(i).getNowName());
            File docFile;
            if (ftlFileEntities.get(i).getFileName().contains("cs")) {
                docFile = new File(settingEntity.getOutPath() + "\\" + dataMap.get("className") + ".cs");
            } else {
                docFile = new File(settingEntity.getOutPath() + "\\" + dataMap.get("className") + ".hbm" + ".xml");
            }
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            template.process(dataMap, out);
            out.flush();
        }

    }
}
