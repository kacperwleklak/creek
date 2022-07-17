CREATE TABLE sequence
(
    name VARCHAR(50) NOT NULL UNIQUE,
    val INTEGER,
    PRIMARY KEY (name)
);

INSERT INTO sequence values ('global_version', 1);