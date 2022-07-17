CREATE TABLE categories_history
(
    version  INTEGER          NOT NULL,
    inserted BOOLEAN DEFAULT FALSE,
    id   varchar(36),
    name     VARCHAR(50),
    PRIMARY KEY (version, id)
);

CREATE TABLE regions_history
(
    version  INTEGER          NOT NULL,
    inserted BOOLEAN DEFAULT FALSE,
    id   varchar(36),
    name     VARCHAR(25),
    PRIMARY KEY (version, id)
);

CREATE TABLE users_history
(
    version       INTEGER          NOT NULL,
    inserted      BOOLEAN DEFAULT FALSE,
    id   varchar(36),
    firstname     VARCHAR(20),
    lastname      VARCHAR(20),
    nickname      VARCHAR(20),
    password      VARCHAR(20),
    email         VARCHAR(50),
    rating        INTEGER,
    balance       FLOAT,
    creation_date DATETIME,
    region        varchar(36),
    PRIMARY KEY (version, id)
);

CREATE TABLE items_history
(
    version       INTEGER          NOT NULL,
    inserted      BOOLEAN          DEFAULT FALSE,
    id   varchar(36),
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT,
    quantity      INTEGER UNSIGNED,
    reserve_price FLOAT            DEFAULT 0,
    buy_now       FLOAT            DEFAULT 0,
    nb_of_bids    INTEGER UNSIGNED DEFAULT 0,
    max_bid       FLOAT            DEFAULT 0,
    start_date    DATETIME,
    end_date      DATETIME,
    seller        varchar(36),
    category      varchar(36),
    PRIMARY KEY (version, id)
);

CREATE TABLE old_items_history
(
    version       INTEGER          NOT NULL,
    inserted      BOOLEAN          DEFAULT FALSE,
    id   varchar(36),
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT,
    quantity      INTEGER UNSIGNED,
    reserve_price FLOAT            DEFAULT 0,
    buy_now       FLOAT            DEFAULT 0,
    nb_of_bids    INTEGER UNSIGNED DEFAULT 0,
    max_bid       FLOAT            DEFAULT 0,
    start_date    DATETIME,
    end_date      DATETIME,
    seller        varchar(36),
    category      varchar(36),
    PRIMARY KEY (version, id)
);

CREATE TABLE bids_history
(
    version  INTEGER          NOT NULL,
    inserted BOOLEAN DEFAULT FALSE,
    id   varchar(36),
    user_id  varchar(36),
    item_id  varchar(36),
    qty      INTEGER UNSIGNED,
    bid      FLOAT,
    max_bid  FLOAT,
    date     DATETIME,
    PRIMARY KEY (version, id)
);

CREATE TABLE comments_history
(
    version      INTEGER          NOT NULL,
    inserted     BOOLEAN DEFAULT FALSE,
    id   varchar(36),
    from_user_id varchar(36),
    to_user_id   varchar(36),
    item_id      varchar(36),
    rating       INTEGER,
    date         DATETIME,
    comment      TEXT,
    PRIMARY KEY (version, id)
);

CREATE TABLE buy_now_history
(
    version  INTEGER          NOT NULL,
    inserted BOOLEAN DEFAULT FALSE,
    id   varchar(36),
    buyer_id varchar(36),
    item_id  varchar(36),
    qty      INTEGER UNSIGNED,
    date     DATETIME,
    PRIMARY KEY (version, id)
);

CREATE TABLE ids_history
(
    version  INTEGER          NOT NULL,
    inserted BOOLEAN DEFAULT FALSE,
    id   varchar(36),
    category varchar(36),
    region   varchar(36),
    users    varchar(36),
    item     varchar(36),
    comment  varchar(36),
    bid      varchar(36),
    buyNow   varchar(36),
    PRIMARY KEY (version, id)
);