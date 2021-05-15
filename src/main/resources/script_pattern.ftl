CREATE TABLE ${tableName}_jrn
(
ID                                SERIAL,
TIMESTAMP                         TIMESTAMP,
USER_ID                           NUMERIC(19),
OPERATION                         VARCHAR(3),
${tableName}_ID                   NUMERIC(19),
<#list columnsWithTypes as line>
    OLD_${line},
    NEW_${line}<#sep>, </#sep>
</#list>
);

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
  NULL, NEW.${column}<#sep>, </#sep>
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
  (OLD.${column} IS NOT NULL AND NEW.${column} IS NOT NULL AND OLD.${column} = NEW.${column} OR OLD.${column} IS NULL AND NEW.${column} IS NULL)<#sep> AND </#sep>
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
  OLD.${column}, NEW.${column}<#sep>, </#sep>
</#list>
);
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
  OLD.${column}, NULL<#sep>, </#sep>
</#list>
);
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

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
