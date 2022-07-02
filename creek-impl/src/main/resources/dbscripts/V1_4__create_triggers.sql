CREATE TRIGGER on_users_update
    AFTER UPDATE
    ON users
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$UsersTrigger";

CREATE TRIGGER on_items_update
    AFTER UPDATE
    ON items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$ItemsTrigger";

CREATE TRIGGER on_old_items_update
    AFTER UPDATE
    ON old_items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$OldItemsTrigger";

CREATE TRIGGER on_bids_update
    AFTER UPDATE
    ON bids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BidsTrigger";

CREATE TRIGGER on_comments_update
    AFTER UPDATE
    ON comments
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$CommentsTrigger";

CREATE TRIGGER on_buy_now_update
    AFTER UPDATE
    ON buy_now
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BuyNowTrigger";

CREATE TRIGGER on_ids_update
    AFTER UPDATE
    ON ids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$IdsTrigger";

CREATE TRIGGER on_users_insert
    AFTER INSERT
    ON users
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$UsersTrigger";

CREATE TRIGGER on_items_insert
    AFTER INSERT
    ON items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$ItemsTrigger";

CREATE TRIGGER on_old_items_insert
    AFTER INSERT
    ON old_items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$OldItemsTrigger";

CREATE TRIGGER on_bids_insert
    AFTER INSERT
    ON bids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BidsTrigger";

CREATE TRIGGER on_comments_insert
    AFTER INSERT
    ON comments
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$CommentsTrigger";

CREATE TRIGGER on_buy_now_insert
    AFTER INSERT
    ON buy_now
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BuyNowTrigger";

CREATE TRIGGER on_ids_insert
    AFTER INSERT
    ON ids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$IdsTrigger";

CREATE TRIGGER on_users_delete
    AFTER DELETE
    ON users
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$UsersTrigger";

CREATE TRIGGER on_items_delete
    AFTER DELETE
    ON items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$ItemsTrigger";

CREATE TRIGGER on_old_items_delete
    AFTER DELETE
    ON old_items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$OldItemsTrigger";

CREATE TRIGGER on_bids_delete
    AFTER DELETE
    ON bids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BidsTrigger";

CREATE TRIGGER on_comments_delete
    AFTER DELETE
    ON comments
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$CommentsTrigger";

CREATE TRIGGER on_buy_now_delete
    AFTER DELETE
    ON buy_now
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BuyNowTrigger";

CREATE TRIGGER on_ids_delete
    AFTER DELETE
    ON ids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$IdsTrigger";