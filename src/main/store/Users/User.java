package main.store.Users;

import main.db.*;
import java.sql.*;
import java.time.LocalDate;
import main.misc.*;

public class User {

    int userID;
    Date joinDate;
    Boolean isRegistered;

    String name;
    String emailAddress;
    String password;
    String houseNumber;
    String roadName;
    String cityName;
    String postcode;

    public static String cryptoPassword = "team057";

    // Create constructor with all attributes
    public User(String fname, String sname, String emailAddress, String password,
            String houseNumber, String roadName, String cityName, String postcode) {
        // Transfer inputs from constructor
        this.name = fname + " " + sname;
        this.emailAddress = emailAddress;
        try{this.password = Encryption.encrypt(password, cryptoPassword);}
        catch (Exception e) {e.printStackTrace();}
        this.houseNumber = houseNumber;
        this.roadName = roadName;
        this.cityName = cityName;
        this.postcode = postcode;

        // System generated info
        LocalDate currentDate;
        currentDate = LocalDate.now();
        joinDate = java.sql.Date.valueOf(currentDate);
        isRegistered = true;
        // DO THIS TOO
        // GenID idGenerator = new GenID();
        userID = 00000001;
    }

    // Get methods
    public Boolean signUp(){
        Boolean flag = false;
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        try {
            // Pass in sql query to sign up user
            String query = "INSERT INTO User VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = db.con.prepareStatement(query);

            // Pass in parameters to SQL Query
            pstmt.setInt(1, userID);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.setString(4, emailAddress);
            pstmt.setBoolean(5, isRegistered);
            pstmt.setString(6, houseNumber);
            pstmt.setString(7, cityName);
            pstmt.setString(8, roadName);
            pstmt.setString(9, postcode);
            pstmt.setDate(10, joinDate);

            // execute Query
            pstmt.executeUpdate();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }

        return flag;
    }

    public Boolean exists() {
        // initiate Database Connections
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
        Boolean flag = false;

        try {
            Statement stmt = db.con.createStatement();

            PreparedStatement pstmt = db.con.prepareStatement("SELECT EXISTS(SELECT * from Users WHERE email=?)");
            pstmt.setString(1, emailAddress);

            ResultSet isExists = pstmt.executeQuery();

            if (isExists.next()) {
                if (isExists.getString(1).equals("1")) {
                    flag = true;
                }
            } else {
                System.out.println("Result set issue");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return flag;

    }

    // Useless?
    // public String signIn(String name, String password, String email) {
    // return "Well done you signed in";
    // }

    // public boolean signOut() {
    // return false;
    // }

}