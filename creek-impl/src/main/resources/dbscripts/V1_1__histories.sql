CREATE TABLE categories_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    name    VARCHAR(50),
    PRIMARY KEY (version, id)
);

CREATE TABLE regions_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    name    VARCHAR(25),
    PRIMARY KEY (version, id)
);

CREATE TABLE users_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    firstname     VARCHAR(20),
    lastname      VARCHAR(20),
    nickname      VARCHAR(20),
    password      VARCHAR(20),
    email         VARCHAR(50),
    rating        INTEGER,
    balance       FLOAT,
    creation_date TIMESTAMP,
    region        INTEGER,
    PRIMARY KEY (version, id)
);

CREATE TABLE items_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT,
    quantity      INTEGER,
    reserve_price FLOAT  DEFAULT 0,
    buy_now       FLOAT  DEFAULT 0,
    nb_of_bids    INTEGER  DEFAULT 0,
    max_bid       FLOAT  DEFAULT 0,
    start_date    TIMESTAMP,
    end_date      TIMESTAMP,
    seller        INTEGER,
    category      INTEGER,
    PRIMARY KEY (version, id)
);

CREATE TABLE old_items_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT,
    quantity      INTEGER,
    reserve_price FLOAT  DEFAULT 0,
    buy_now       FLOAT  DEFAULT 0,
    nb_of_bids    INTEGER  DEFAULT 0,
    max_bid       FLOAT  DEFAULT 0,
    start_date    TIMESTAMP,
    end_date      TIMESTAMP,
    seller        INTEGER,
    category      INTEGER,
    PRIMARY KEY (version, id)
);


CREATE TABLE bids_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    user_id INTEGER,
    item_id INTEGER,
    qty     INTEGER,
    bid     FLOAT,
    max_bid FLOAT,
    date    TIMESTAMP,
    PRIMARY KEY (version, id)
);

CREATE TABLE comments_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    from_user_id INTEGER,
    to_user_id   INTEGER,
    item_id      INTEGER,
    rating       INTEGER,
    date         TIMESTAMP,
    comment      TEXT,
    PRIMARY KEY (version, id)
);


CREATE TABLE buy_now_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    buyer_id INTEGER,
    item_id  INTEGER,
    qty      INTEGER,
    date     TIMESTAMP,
    PRIMARY KEY (version, id)
);

CREATE TABLE ids_history
(
    version integer  NOT NULL,
    inserted boolean DEFAULT FALSE,
    id      integer  NOT NULL,
    category INTEGER,
    region   INTEGER,
    users    INTEGER,
    item     INTEGER,
    comment  INTEGER,
    bid      INTEGER,
    buyNow   INTEGER,
    PRIMARY KEY (version, id)
);