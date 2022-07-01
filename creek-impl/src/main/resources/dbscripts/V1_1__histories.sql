CREATE TABLE categories_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    name    VARCHAR(50),
    PRIMARY KEY (version, id)
);

CREATE TABLE regions_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    name    VARCHAR(25),
    PRIMARY KEY (version, id)
);

CREATE TABLE users_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    firstname     VARCHAR(20),
    lastname      VARCHAR(20),
    nickname      VARCHAR(20) NOT NULL UNIQUE,
    password      VARCHAR(20) NOT NULL,
    email         VARCHAR(50) NOT NULL,
    rating        INTEGER,
    balance       FLOAT,
    creation_date TIMESTAMP,
    region        INTEGER  NOT NULL,
    PRIMARY KEY (version, id)
);

CREATE TABLE items_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT  NOT NULL,
    quantity      INTEGER  NOT NULL,
    reserve_price FLOAT  DEFAULT 0,
    buy_now       FLOAT  DEFAULT 0,
    nb_of_bids    INTEGER  DEFAULT 0,
    max_bid       FLOAT  DEFAULT 0,
    start_date    TIMESTAMP,
    end_date      TIMESTAMP,
    seller        INTEGER  NOT NULL,
    category      INTEGER  NOT NULL,
    PRIMARY KEY (version, id)
);

CREATE TABLE old_items_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT  NOT NULL,
    quantity      INTEGER  NOT NULL,
    reserve_price FLOAT  DEFAULT 0,
    buy_now       FLOAT  DEFAULT 0,
    nb_of_bids    INTEGER  DEFAULT 0,
    max_bid       FLOAT  DEFAULT 0,
    start_date    TIMESTAMP,
    end_date      TIMESTAMP,
    seller        INTEGER  NOT NULL,
    category      INTEGER  NOT NULL,
    PRIMARY KEY (version, id)
);


CREATE TABLE bids_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    user_id INTEGER  NOT NULL,
    item_id INTEGER  NOT NULL,
    qty     INTEGER  NOT NULL,
    bid     FLOAT  NOT NULL,
    max_bid FLOAT  NOT NULL,
    date    TIMESTAMP,
    PRIMARY KEY (version, id)
);

CREATE TABLE comments_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    from_user_id INTEGER  NOT NULL,
    to_user_id   INTEGER  NOT NULL,
    item_id      INTEGER  NOT NULL,
    rating       INTEGER,
    date         TIMESTAMP,
    comment      TEXT,
    PRIMARY KEY (version, id)
);


CREATE TABLE buy_now_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    buyer_id INTEGER  NOT NULL,
    item_id  INTEGER  NOT NULL,
    qty      INTEGER  NOT NULL,
    date     TIMESTAMP,
    PRIMARY KEY (version, id)
);

CREATE TABLE ids_history
(
    version integer  NOT NULL,
    id      integer  NOT NULL,
    category INTEGER  NOT NULL,
    region   INTEGER  NOT NULL,
    users    INTEGER  NOT NULL,
    item     INTEGER  NOT NULL,
    comment  INTEGER  NOT NULL,
    bid      INTEGER  NOT NULL,
    buyNow   INTEGER  NOT NULL,
    PRIMARY KEY (version, id)
);