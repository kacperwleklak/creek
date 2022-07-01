CREATE TRIGGER log_users_change
    AFTER UPDATE
    ON users
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$UsersTrigger";

CREATE TRIGGER log_items_change
    AFTER UPDATE
    ON items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$ItemsTrigger";

CREATE TRIGGER log_old_items_change
    AFTER UPDATE
    ON old_items
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$OldItemsTrigger";

CREATE TRIGGER log_bids_change
    AFTER UPDATE
    ON bids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BidsTrigger";

CREATE TRIGGER log_comments_change
    AFTER UPDATE
    ON comments
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$CommentsTrigger";

CREATE TRIGGER log_buy_now_change
    AFTER UPDATE
    ON buy_now
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$BuyNowTrigger";

CREATE TRIGGER log_ids_change
    AFTER UPDATE
    ON ids
    FOR EACH ROW
CALL "pl.poznan.put.kacperwleklak.creek.sql.trigger.RubisDbTrigger$IdsTrigger";