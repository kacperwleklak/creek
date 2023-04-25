package pl.poznan.put.kacperwleklak.sql.rubis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RubisProcedures {

    public static void processBid(Connection connection, String bidId, String itemId, float maxBid, String userId, int qty, float bid) throws SQLException {
        PreparedStatement selectStmt = connection.prepareStatement("SELECT max_bid FROM items WHERE id=?");
        selectStmt.setString(1, itemId);
        ResultSet selectResultSet = selectStmt.executeQuery();

        if (selectResultSet.next() && maxBid > selectResultSet.getFloat("max_bid")) {
            PreparedStatement updateStmt = connection.prepareStatement("UPDATE items SET max_bid=? WHERE id=?");
            updateStmt.setFloat(1, maxBid);
            updateStmt.setString(2, itemId);
            updateStmt.executeUpdate();
        }

        PreparedStatement insertBidsStmt = connection.prepareStatement("INSERT INTO bids VALUES (?, ?, ?, ?, ?, ?, NOW())");
        insertBidsStmt.setString(1, bidId);
        insertBidsStmt.setString(2, userId);
        insertBidsStmt.setString(3, itemId);
        insertBidsStmt.setInt(4, qty);
        insertBidsStmt.setFloat(5, bid);
        insertBidsStmt.setFloat(6, maxBid);
        insertBidsStmt.executeUpdate();

        PreparedStatement updateItemsStmt = connection.prepareStatement("UPDATE items SET nb_of_bids=nb_of_bids+1 WHERE id=?");
        updateItemsStmt.setString(1, itemId);
        updateItemsStmt.executeUpdate();
    }

    public static void buyNow(Connection connection, String buyNowId, String itemId, int qty, String userId) throws SQLException {
        PreparedStatement selectStmt = connection.prepareStatement("SELECT * FROM items WHERE id=?");
        selectStmt.setString(1, itemId);
        ResultSet selectResultSet = selectStmt.executeQuery();

        if (!selectResultSet.next()) {
            return;
        }

        PreparedStatement updateItemStmt;
        int newQty = selectResultSet.getInt("quantity") - qty;
        if (newQty <= 0) {
            updateItemStmt = connection.prepareStatement("UPDATE items SET end_date=NOW(), quantity=? WHERE id=?");
        } else {
            updateItemStmt = connection.prepareStatement("UPDATE items SET quantity=? WHERE id=?");
        }
        updateItemStmt.setInt(1, newQty);
        updateItemStmt.setString(2, itemId);
        updateItemStmt.executeUpdate();

        PreparedStatement insertBuyNow = connection.prepareStatement("INSERT INTO buy_now VALUES (?, ?, ?, ?, NOW())");
        insertBuyNow.setString(1, buyNowId);
        insertBuyNow.setString(2, userId);
        insertBuyNow.setString(3, itemId);
        insertBuyNow.setInt(4, qty);
        insertBuyNow.executeUpdate();
    }

    public static void processComment(Connection connection, String commentId, String from, String to, String itemId, int rating, String comment) throws SQLException {
        PreparedStatement selectStmt = connection.prepareStatement("SELECT count(*) AS count FROM users WHERE id=?");
        selectStmt.setString(1, itemId);
        ResultSet selectResultSet = selectStmt.executeQuery();

        if (!selectResultSet.next() && selectResultSet.getInt("count") == 0) {
            return;
        }

        PreparedStatement updateStmt = connection.prepareStatement("UPDATE users SET rating=rating+? WHERE id=?");
        updateStmt.setInt(1, rating);
        updateStmt.setString(2, to);
        updateStmt.executeUpdate();

        PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO comments VALUES (?, ?, ?, ?, ?, NOW(), ?)");
        insertStmt.setString(1, commentId);
        insertStmt.setString(2, from);
        insertStmt.setString(3, to);
        insertStmt.setString(4, itemId);
        insertStmt.setInt(5, rating);
        insertStmt.setString(6, comment);
        insertStmt.executeUpdate();
    }
}
