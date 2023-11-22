package main.store.Users;

import main.db.*;
import java.sql.*;
import java.time.LocalDate;
import main.misc.*;

public class User {

    int userID;
    Date joinDate;
    Boolean accountLocked;

    String userName;
    String emailAddress;
    String password;
    String houseNumber;
    String roadName;
    String cityName;
    String postcode;



    public static String salt = "team057";

    // // Singleton status variable
    // private static Boolean uniqueInstance = null;

    // // current User details
    // private static String currentUsername;
    // private static String currentUserID;

    // // form a unique instance of the User class
    // public Boolean createUniqueUser

    // // getter and setter methods
    // public void setCurrentUser (String username, String userID){
    //     User.currentUsername = username;
    //     User.currentUserID = userID;
    // }
    // public String[] getCurrentUser;

    public String getName() {
        return this.userName;
    }

    // Create constructor with all attributes
    public User(String userID, String username, String emailAddress, String password,
            String houseNumber, String roadName, String cityName, String postcode, Date joinDate, Boolean accountLocked) {
        // Transfer inputs from constructor
        this.userID = Integer.valueOf(userID);
        this.userName = username;
        this.emailAddress = emailAddress;
        try{this.password = Encryption.encrypt(password, salt);}
        catch (Exception e) {e.printStackTrace();}
        this.houseNumber = houseNumber;
        this.roadName = roadName;
        this.cityName = cityName;
        this.postcode = postcode;
        this.joinDate = joinDate;
        this.accountLocked = accountLocked;
    

        // System generated info
        // LocalDate currentDate;
        // currentDate = LocalDate.now();
        // joinDate = java.sql.Date.valueOf(currentDate);
        // isRegistered = true;
        // // DO THIS TOO
        // GenID idGenerator = new GenID();
        // userID = 00000001;
    }

    @Override
    public String toString() {
        return "User{" +
            "userID='" + userID + '\'' +
            ", userName='" + userName + '\'' +
            ", emailAddress='" + emailAddress + '\'' +
            // Don't print the password, even if it's encrypted, for security reasons
            ", houseNumber='" + houseNumber + '\'' +
            ", roadName='" + roadName + '\'' +
            ", cityName='" + cityName + '\'' +
            ", postcode='" + postcode + '\'' +
            ", joinDate=" + joinDate +
            ", accountLocked=" + accountLocked +
            '}';
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
            pstmt.setString(2, userName);
            pstmt.setString(3, password);
            pstmt.setString(4, emailAddress);
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

//     public Boolean exists() {
//         // initiate Database Connections
//         DatabaseConnectionHandler db = new DatabaseConnectionHandler();
//         db.openConnection();
//         Boolean flag = false;

//         try {
//             Statement stmt = db.con.createStatement();

//             PreparedStatement pstmt = db.con.prepareStatement("SELECT EXISTS(SELECT * from Users WHERE email=?)");
//             pstmt.setString(1, emailAddress);

//             ResultSet isExists = pstmt.executeQuery();

//             if (isExists.next()) {
//                 if (isExists.getString(1).equals("1")) {
//                     flag = true;
//                 }
//             } else {
//                 System.out.println("Result set issue");
//             }

//         } catch (SQLException ex) {
//             ex.printStackTrace();
//         }

//         return flag;

//     }

//     // Useless?
//     // public String signIn(String name, String password, String email) {
//     // return "Well done you signed in";
//     // }

//     // public boolean signOut() {
//     // return false;
//     // }

}