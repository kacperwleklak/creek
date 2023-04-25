package pl.poznan.put.kacperwleklak.sql;

import org.h2.jdbc.JdbcBlob;
import org.h2.jdbc.JdbcConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.StringJoiner;

public abstract class DBTrigger {

    private static final String INSERT_VERSION_STATEMENT_FORMAT_MYSQL =
            "INSERT IGNORE INTO %s_history VALUES(%s)";
    private static final String INSERT_VERSION_STATEMENT_FORMAT_POSTGRES =
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

    private static String getVersionInsertStatement(Connection connection) {
        String mode = ((JdbcConnection) connection).getSession()
                .getDynamicSettings()
                .mode
                .toString();
        if (mode.equalsIgnoreCase("mysql")) {
            return INSERT_VERSION_STATEMENT_FORMAT_MYSQL;
        } else {
            return INSERT_VERSION_STATEMENT_FORMAT_POSTGRES;
        }
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
                String.format(getVersionInsertStatement(connection),
                        tableName, generateWildcards(objectToInsert.length + 2) //+2 is for version and inserted flag
                )
        );
        statement.setObject(1, getCurrentVersion(connection));
        statement.setObject(2, inserted); // it's updated/deleted, not inserted
        for (int i = 0; i < objectToInsert.length; i++) {
            Object val = objectToInsert[i];
            if (val instanceof JdbcBlob blob) {
                statement.setBlob(i+3, blob);
            } else {
                statement.setObject(i+3, val);
            }
        }
        statement.executeUpdate();
    }

}
