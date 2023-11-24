package main.store.Users;

import main.db.*;
import java.sql.*;
import java.time.LocalDate;
import main.misc.*;

public class User {

    private String userID;
    private String username;
    private String name;
    private String hashedPassword;
    private String emailAddress;
    private String houseNumber;
    private String roadName;
    private String cityName;
    private String postcode;
    private Date joinDate;
    private Boolean accountLocked=false;
    private String role;
    private String salt;

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

    public String getUsername() {
        return this.username;
    }
    public String getUserID() {
        return this.userID;
    }
    public String getSalt(){
        return this.salt;
    }

    public Boolean lockUser() {
        this.accountLocked = true;
        return true;
    }

    // Create constructor with all attributes
    public User(String userID, String username, String name, String hashedPassword, String emailAddress,
            String houseNumber, String cityName, String roadName, String postcode, Date joinDate,
            String role, String salt) {
        // Transfer inputs from constructor
        this.userID = userID;
        this.username = username;
        this.name = name;
        this.salt = salt; // assign user-specific salt
        this.emailAddress = emailAddress;
        try{this.hashedPassword = Encryption.encrypt(hashedPassword, this.salt);}
        catch (Exception e) {e.printStackTrace();}
        this.houseNumber = houseNumber;
        this.roadName = roadName;
        this.cityName = cityName;
        this.postcode = postcode;
        this.joinDate = joinDate;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
            "userID=" + userID +
            ", username='" + username + '\'' +
            ", name='" + name + '\'' +
            ", hashedPassword='" + hashedPassword + '\'' +
            ", emailAddress='" + emailAddress + '\'' +
            ", houseNumber='" + houseNumber + '\'' +
            ", cityName='" + cityName + '\'' +
            ", roadName='" + roadName + '\'' +
            ", postcode='" + postcode + '\'' +
            ", joinDate=" + joinDate +
            ", accountLocked=" + accountLocked +
            ", role='" + role + '\'' +
            ", salt='" + salt + '\'' +
            '}';
    }

    public Object[] getAttributes() {
        // compute attributes into an easily accessible array
        Object[] attributes = {
            this.userID,
            this.username,
            this.name,
            this.hashedPassword,
            this.emailAddress,
            this.houseNumber,
            this.cityName,
            this.roadName,
            this.postcode,
            this.joinDate,
            this.accountLocked,
            this.role,
            this.salt
        };

        return attributes;
    }

    // Update user details in database
    public boolean updateDetails(String userID, String username, String name, String enteredPassword, String emailString,
    String houseNumber, String cityName, String roadName, String postCode, String salt){
        Boolean updateStatus;
        
        // open db connection
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        updateStatus = DatabaseOperations.saveUserEditDetails(db.con, userID, username, name, enteredPassword, emailString, houseNumber,
        cityName, roadName, postCode, salt);

        return updateStatus;
    }

}