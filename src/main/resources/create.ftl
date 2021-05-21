-- Создание журнальной таблицы
<changeSet id="CTP- CREATE TABLE ${"${tableName}_JRN"?upper_case}" author="">
  <comment>DDL</comment>
  <createTable tableName="${"${tableName}_JRN"?upper_case}" remarks="Журнал для таблицы ${tableName}">
    <column name="id" type="INTEGER" remarks="Идентификатор записи"/>
    <column name="timestamp" type="TIMESTAMP" remarks="Дата и время создания записи"/>
    <column name="user_id" type="NUMBER(19)" remarks="Пользователь, создавший запись"/>
    <column name="operation" type="VARCHAR(3)" remarks="Тип операции. INS -вставка; UPD - убновление; DEL - удаление;"/>
    <column name="${"${tableName}_id"?lower_case}" type="NUMERIC(19)" remarks="Идентификатор записи в таблице ${tableName}"/>
      <#list columns as line>
        <column name="old_${line.name}" type="${line.type}"/>
        <column name="new_${line.name}" type="${line.type}"/>
      </#list>
  </createTable>
  <addAutoIncrement
      columnDataType="INTEGER"
      columnName="id"
      defaultOnNull="false"
      generationType="ALWAYS"
      incrementBy="1"
      startWith="1"
      tableName="${tableName}_jrn"/>
</changeSet>

<changeSet id="CTP- CREATE ${tableName} TRIGGERS" author="">
  <sqlFile path="db/migration/liquibase/releases/release_<РЕЛИЗ>/sql/CREATE_${tableName}_JRN.sql" stripComments="true"
           splitStatements="false"/>
</changeSet>

-- Создание функций
CREATE OR REPLACE FUNCTION jrn_${tableName}_ins() RETURNS TRIGGER AS
$$
BEGIN
INSERT INTO ${tableName}_jrn
VALUES (DEFAULT,
CURRENT_TIMESTAMP,
NEW.created_by_user_id,
'INS',
NEW.id,
<#list columns as column>
  NULL, NEW.${column.name}<#sep>, </#sep>
</#list>
);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION jrn_${tableName}_upd() RETURNS TRIGGER AS
$$
BEGIN
CASE WHEN (NOT(
<#list columns as column>
  (OLD.${column.name} IS NOT NULL AND NEW.${column.name} IS NOT NULL AND OLD.${column.name} = NEW.${column.name} OR OLD.${column.name} IS NULL AND NEW.${column.name} IS NULL)<#sep> AND </#sep>
</#list>
))
THEN
INSERT INTO ${tableName}_jrn
VALUES (DEFAULT,
CURRENT_TIMESTAMP,
NEW.last_upd_by_user_id,
'UPD',
NEW.id,
<#list columns as column>
  OLD.${column.name}, NEW.${column.name}<#sep>, </#sep>
</#list>
);
ELSE
END CASE;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION jrn_${tableName}_del() RETURNS TRIGGER AS
$$
BEGIN
INSERT INTO ${tableName}_jrn
VALUES (DEFAULT,
CURRENT_TIMESTAMP,
OLD.last_upd_by_user_id,
'DEL',
OLD.id,
<#list columns as column>
  OLD.${column.name}, NULL<#sep>, </#sep>
</#list>
);
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Создание тригеров
CREATE TRIGGER ${tableName}_ins_trigger
BEFORE INSERT
ON ${tableName}
FOR EACH ROW
EXECUTE PROCEDURE jrn_${tableName}_ins();

CREATE TRIGGER ${tableName}_upd_trigger
BEFORE UPDATE
ON ${tableName}
FOR EACH ROW
EXECUTE PROCEDURE jrn_${tableName}_upd();

CREATE TRIGGER ${tableName}_del_trigger
BEFORE DELETE
ON ${tableName}
FOR EACH ROW
EXECUTE PROCEDURE jrn_${tableName}_del();
