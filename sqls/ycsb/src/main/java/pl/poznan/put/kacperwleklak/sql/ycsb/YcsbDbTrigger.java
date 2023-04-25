package pl.poznan.put.kacperwleklak.sql.ycsb;

import org.h2.api.Trigger;
import pl.poznan.put.kacperwleklak.sql.DBTrigger;

import java.sql.Connection;
import java.sql.SQLException;

public class YcsbDbTrigger extends DBTrigger {

    public static class UsertableTrigger implements Trigger {
        private static final String TABLE_NAME = "usertable";

        @Override
        public void fire(Connection connection, Object[] oldObject, Object[] newObject) throws SQLException {
            saveVersion(connection, oldObject, newObject, TABLE_NAME);
        }
    }

}