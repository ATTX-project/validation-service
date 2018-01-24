package org.uh.hulib.attx.services.validation;

import java.sql.*;

public class SQLiteConnection {
    /**
     * Connect to the data.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:data.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createNewTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS reports (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	reportid real NOT NULL,\n"
                + "	result text\n"
                + ");";
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    /**
     * Insert a new row into the reports table
     *
     * @param reportid
     * @param result
     */
    public void insert(double reportid, String result) {
        String sql = "INSERT INTO reports(reportid,result) VALUES(?,?)";
        Connection conn = this.connect();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, reportid);
            pstmt.setString(2, result);
            pstmt.executeUpdate();
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = stmt.executeQuery("select * from reports");
            while(rs.next())
            {
                // read the result set
                System.out.println("result = " + rs.getString("result"));
                System.out.println("reportid = " + rs.getDouble("reportid"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                // conn close failed.
                System.err.println(e);
            }
        }
    }

    public static SQLiteConnection main() {
        SQLiteConnection app = new SQLiteConnection();
        app.createNewTable();
        return app;
    }
}