package main.db;
import java.sql.*;

public class DatabaseConnectionHandler {
    Connection con = null;

    // Open a connection
    public void openConnection(){
        try {
            con = DriverManager.getConnection("jdbc:mysql://stusql.dcs.shef.ac.uk/team043", "team057", "iY2loowu9");
            System.out.println("Connection Success!")
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // Close connection
    public void closeConnection() {
        try{
            if (con != null) con.close();
        }

        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

