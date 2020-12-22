<?xml version="1.0" encoding="utf-8" ?>
<hibernate-mapping xmlns="urn:nhibernate-mapping-2.2">
    <class name="${packageName}.${tableName}" table="${tableName}" lazy="false" dynamic-update="true">

        <#list fields as field>
            <#if field.constraintKey == "Y">
                <id name="${field.columnName}" column="${field.columnName}" type="${field.genType}"
                    unsaved-value="0"></id>
            <#else >
                <property name="${field.columnName}" column="${field.columnName}" type="${field.genType}"
                          length="${field.len}"/>
            </#if>
        </#list>
    </class>
</hibernate-mapping>