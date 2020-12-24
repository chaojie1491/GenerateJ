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


    public static String getSql(String tbName) {
        String sql = "SELECT cast(case when a.colorder = 1 then d.name else '' end as varchar(500)) as tbName,\n" +
                "       cast(case when a.colorder = 1 then isnull(f.value, '') else '' end as varchar(500)) as tbComment,\n" +
                "       cast(a.colorder as varchar(500)) as fieldNo,\n" +
                "       cast(a.name as varchar(500)) as fieldName,\n" +
                "       cast(a.id as varchar(500)) as id,\n" +
                "       cast(case when COLUMNPROPERTY(a.id, a.name, 'IsIdentity') = 1 then 'Y' else 'N' end as varchar(500)) as autoAdd,\n" +
                "       cast(case\n" +
                "                         when exists(SELECT 1\n" +
                "                                     FROM sysobjects\n" +
                "                                     where xtype = 'PK'\n" +
                "                                       and name in (\n" +
                "                                         SELECT name\n" +
                "                                         FROM sysindexes\n" +
                "                                         WHERE indid in (\n" +
                "                                             SELECT indid\n" +
                "                                             FROM sysindexkeys\n" +
                "                                             WHERE id = a.id\n" +
                "                                               AND colid = a.colid\n" +
                "                                         ))) then 'Y'\n" +
                "                         else 'N' end as varchar(500)) as constrain_key,\n" +
                "       cast(b.name as varchar(500)) as type,\n" +
                "       cast(COLUMNPROPERTY(a.id, a.name, 'PRECISION') as varchar(500)) as len,\n" +
                "       cast(case when a.isnullable = 1 then 'Y' else 'N' end as varchar(500)) as nullable,\n" +
                "       cast(isnull(e.text, '')  as varchar(500)) as defaultVal,\n" +
                "       cast(isnull(g.[value], '') as varchar(500)) as fieldComment\n" +
                "FROM syscolumns a\n" +
                "         left join systypes b on a.xusertype = b.xusertype\n" +
                "         inner join sysobjects d on a.id = d.id and d.xtype = 'U' and d.name <> 'dtproperties'\n" +
                "         left join syscomments e on a.cdefault = e.id\n" +
                "         left join sys.extended_properties g on a.id = g.major_id and a.colid = g.minor_id\n" +
                "         left join sys.extended_properties f on d.id = f.major_id and f.minor_id = 0\n" +
                String.format("where d.name = '%s'order by a.id, a.colorder", tbName);
        return sql;
    }

    public static void genFile(RuleEntity ruleEntity, List<TableInfoEntity> entities, List<FtlFileEntity> ftlFileEntities, SettingEntity settingEntity) throws IOException, TemplateException {
        Configuration configuration = new Configuration();

        // step2 获取模版路径
        configuration.setDirectoryForTemplateLoading(new File(settingEntity.getFtlPath()));
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("packageName", ruleEntity.getNamespace());
        dataMap.put("model", ruleEntity.getModel());
        dataMap.put("className", entities.get(0).getTableName().replaceAll("_", ""));
        dataMap.put("tableName", entities.get(0).getTableName());
        dataMap.put("fields", entities);
        if (!ruleEntity.getPatentClass().isEmpty()) {
            dataMap.put("extendsClass", " : " + ruleEntity.getPatentClass());
        } else {
            dataMap.put("extendsClass", "");
        }
        Writer out = null;

        for (int i = 0; i < ftlFileEntities.size(); i++) {
            Template template = configuration.getTemplate(ftlFileEntities.get(i).getNowName());
            File docFile;
            String path = settingEntity.getOutPath() + "\\" + dataMap.get("className");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            if (ftlFileEntities.get(i).getFileName().contains("cs")) {
                docFile = new File(path + "\\" + dataMap.get("className") + ".cs");
            } else if (ftlFileEntities.get(i).getFileName().contains(".ascx.ftl")) {
                docFile = new File(path + "\\" + "Main" + ".ascx");
            } else {
                docFile = new File(path + "\\" + dataMap.get("className") + ".hbm" + ".xml");
            }
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            template.process(dataMap, out);
            out.flush();
        }

    }
}
