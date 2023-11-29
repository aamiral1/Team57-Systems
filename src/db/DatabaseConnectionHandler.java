package db;

import java.sql.*;

public class DatabaseConnectionHandler {
    public Connection con = null;

    // Open a connection
    public void openConnection() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://stusql.dcs.shef.ac.uk/team057", "team057", "iY2loowu9");
            System.out.println("Connection Success!");
            
        }

        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Connection Failed!");
        }
    }

    // Close connection
    public void closeConnection() {
        try {
            if (con != null)
                con.close();
        }

        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
