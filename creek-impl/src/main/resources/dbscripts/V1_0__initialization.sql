CREATE TABLE categories
(
    id   INTEGER UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    name VARCHAR(50),
    PRIMARY KEY (id)
);

CREATE TABLE regions
(
    id   INTEGER UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    name VARCHAR(25),
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    id            varchar(36) DEFAULT(UUID()) NOT NULL,
    firstname     VARCHAR(20),
    lastname      VARCHAR(20),
    nickname      VARCHAR(20) NOT NULL UNIQUE,
    password      VARCHAR(20) NOT NULL,
    email         VARCHAR(50) NOT NULL,
    rating        INTEGER,
    balance       FLOAT,
    creation_date DATETIME,
    region        INTEGER NOT NULL,
    PRIMARY KEY (id),
    INDEX         users_auth (nickname,password),
    INDEX         users_region_id (region)
);

CREATE TABLE items
(
    id            varchar(36) DEFAULT(UUID()) NOT NULL,
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT NOT NULL,
    quantity      INTEGER UNSIGNED NOT NULL,
    reserve_price FLOAT DEFAULT 0,
    buy_now       FLOAT DEFAULT 0,
    nb_of_bids    INTEGER UNSIGNED DEFAULT 0,
    max_bid       FLOAT DEFAULT 0,
    start_date    DATETIME,
    end_date      DATETIME,
    seller        varchar(36) NOT NULL,
    category      INTEGER NOT NULL,
    PRIMARY KEY (id),
    INDEX         items_seller_id (seller),
    INDEX         items_category_id (category)
);

CREATE TABLE old_items
(
    id            varchar(36) DEFAULT(UUID()) NOT NULL,
    name          VARCHAR(100),
    description   TEXT,
    initial_price FLOAT NOT NULL,
    quantity      INTEGER UNSIGNED NOT NULL,
    reserve_price FLOAT DEFAULT 0,
    buy_now       FLOAT DEFAULT 0,
    nb_of_bids    INTEGER UNSIGNED DEFAULT 0,
    max_bid       FLOAT DEFAULT 0,
    start_date    DATETIME,
    end_date      DATETIME,
    seller        varchar(36) NOT NULL,
    category      INTEGER NOT NULL,
    PRIMARY KEY (id),
    INDEX         old_items_seller_id (seller),
    INDEX         old_items_category_id (category)
);

CREATE TABLE bids
(
    id            varchar(36) DEFAULT(UUID()) NOT NULL,
    user_id varchar(36) NOT NULL,
    item_id varchar(36) NOT NULL,
    qty     INTEGER UNSIGNED NOT NULL,
    bid     FLOAT NOT NULL,
    max_bid FLOAT NOT NULL,
    date    DATETIME,
    PRIMARY KEY (id),
    INDEX   bids_item (item_id),
    INDEX   bids_user (user_id)
);

CREATE TABLE comments
(
    id            varchar(36) DEFAULT(UUID()) NOT NULL,
    from_user_id varchar(36) NOT NULL,
    to_user_id   varchar(36) NOT NULL,
    item_id      varchar(36) NOT NULL,
    rating       INTEGER,
    date         DATETIME,
    comment      TEXT,
    PRIMARY KEY (id),
    INDEX        comments_from_user (from_user_id),
    INDEX        comments_to_user (to_user_id),
    INDEX        comments_item (item_id)
);

CREATE TABLE buy_now
(
    id            varchar(36) DEFAULT(UUID()) NOT NULL,
    buyer_id varchar(36) NOT NULL,
    item_id  varchar(36) NOT NULL,
    qty      INTEGER UNSIGNED NOT NULL,
    date     DATETIME,
    PRIMARY KEY (id),
    INDEX    buy_now_buyer (buyer_id),
    INDEX    buy_now_item (item_id)
);

CREATE TABLE ids
(
    id            varchar(36) DEFAULT(UUID()) NOT NULL,
    category INTEGER NOT NULL,
    region        INTEGER NOT NULL,
    users    varchar(36) NOT NULL,
    item     varchar(36) NOT NULL,
    comment  varchar(36) NOT NULL,
    bid      varchar(36) NOT NULL,
    buyNow   varchar(36) NOT NULL,
    PRIMARY KEY (id)
);
