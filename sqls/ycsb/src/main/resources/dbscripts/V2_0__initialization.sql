CREATE TABLE usertable (YCSB_KEY VARCHAR(255) PRIMARY KEY not NULL, YCSB_VALUE LONGBLOB not NULL);

CREATE TABLE usertable_history
(
    version       INTEGER          NOT NULL,
    inserted      BOOLEAN DEFAULT FALSE,
    YCSB_KEY VARCHAR(255) not NULL,
    YCSB_VALUE LONGBLOB,
    PRIMARY KEY (version, YCSB_KEY)
);


CREATE TRIGGER on_usertable_update
    AFTER UPDATE
    ON usertable
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.sql.ycsb.YcsbDbTrigger$UsertableTrigger";

CREATE TRIGGER on_usertable_insert
    AFTER INSERT
    ON usertable
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.sql.ycsb.YcsbDbTrigger$UsertableTrigger";

CREATE TRIGGER on_usertable_delete
    AFTER DELETE
    ON usertable
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.sql.ycsb.YcsbDbTrigger$UsertableTrigger";