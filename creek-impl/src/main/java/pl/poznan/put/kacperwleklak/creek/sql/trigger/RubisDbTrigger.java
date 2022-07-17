package pl.poznan.put.kacperwleklak.creek.sql.trigger;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;

public class RubisDbTrigger {

    private static final String INSERT_VERSION_STATEMENT_FORMAT =
            "INSERT INTO %s_history VALUES(%s) ON CONFLICT DO NOTHING";

    private static int getCurrentVersion(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT val from sequence where name='global_version'");
        ResultSet resultSet = stmt.executeQuery();
        resultSet.next();
        return resultSet.getInt("val");
    }

    private static String generateWildcards(int size) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (int i = 0; i < size; i++) {
            stringJoiner.add("?");
        }
        return stringJoiner.toString();
    }

    protected static void saveVersion(Connection connection, Object[] oldObject, Object[] newObject, String tableName)
            throws SQLException {
        boolean inserted;
        Object[] objectToInsert;
        if (oldObject == null) {    // callback when inserted new value
            objectToInsert = new Object[newObject.length];
            objectToInsert[0] = newObject[0]; // save ID
            inserted = true;
        } else {                    // when update/delete
            objectToInsert = oldObject;
            inserted = false;
        }
        PreparedStatement statement = connection.prepareStatement(
                String.format(INSERT_VERSION_STATEMENT_FORMAT,
                        tableName, generateWildcards(objectToInsert.length + 2) //+2 is for version and inserted flag
                )
        );
        statement.setObject(1, getCurrentVersion(connection));
        statement.setObject(2, inserted); // it's updated/deleted, not inserted
        for (int i = 0; i < objectToInsert.length; i++) {
            statement.setObject(i + 3, objectToInsert[i]);
        }
        statement.executeUpdate();
    }

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
