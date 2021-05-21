-- Добавление колонок в журнальную таблицу
<changeSet id="CTP- ALTER TABLE ${tableName}_jrn" author="">
  <comment>DDL</comment>
  <addColumn tableName="${"${tableName}_JRN"?upper_case}">
      <#list columns as line>
        <column name="old_${line.name}" type="${line.type}"/>
        <column name="new_${line.name}" type="${line.type}"/>
      </#list>
  </addColumn>
</changeSet>

<changeSet id="CTP- ALTER ${tableName} TRIGGERS" author="">
  <sqlFile path="db/migration/liquibase/releases/release_<РЕЛИЗ>/sql/CREATE_${tableName}_JRN.sql" stripComments="true"
           splitStatements="false"/>
</changeSet>

-- Добавление колонок в функции
-- INSERT
--
<#list columns as column>
  NULL, NEW.${column.name}<#sep>, </#sep>
</#list>
--

-- UPDATE
--
<#list columns as column>
  (OLD.${column.name} IS NOT NULL AND NEW.${column.name} IS NOT NULL AND OLD.${column.name} = NEW.${column.name} OR OLD.${column.name} IS NULL AND NEW.${column.name} IS NULL)<#sep> AND </#sep>
</#list>
--
<#list columns as column>
  OLD.${column.name}, NEW.${column.name}<#sep>, </#sep>
</#list>
--

--DELETE
--
<#list columns as column>
  OLD.${column.name}, NULL<#sep>, </#sep>
</#list>
--
