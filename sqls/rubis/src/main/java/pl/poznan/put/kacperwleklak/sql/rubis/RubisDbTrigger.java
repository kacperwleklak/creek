package pl.poznan.put.kacperwleklak.sql.rubis;

import org.h2.api.Trigger;
import pl.poznan.put.kacperwleklak.sql.DBTrigger;

import java.sql.Connection;
import java.sql.SQLException;

public class RubisDbTrigger extends DBTrigger {

    public static class UsersTrigger implements Trigger {
        private static final String TABLE_NAME = "users";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }

    public static class ItemsTrigger implements Trigger {
        private static final String TABLE_NAME = "items";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }

    public static class OldItemsTrigger implements Trigger {
        private static final String TABLE_NAME = "old_items";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }

    public static class BidsTrigger implements Trigger {
        private static final String TABLE_NAME = "bids";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }

    public static class CommentsTrigger implements Trigger {
        private static final String TABLE_NAME = "comments";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }

    public static class BuyNowTrigger implements Trigger {
        private static final String TABLE_NAME = "buy_now";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }

    public static class IdsTrigger implements Trigger {
        private static final String TABLE_NAME = "ids";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }
}
