CREATE TABLE usertable (YCSB_KEY VARCHAR(255) PRIMARY KEY not NULL, YCSB_VALUE LONGBLOB not NULL);

CREATE ALIAS REVERSE_CASE FOR "pl.poznan.put.kacperwleklak.sql.ycsb.YcsbStoredProcedure.reverseCase";