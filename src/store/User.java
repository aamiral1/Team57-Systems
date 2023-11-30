package store;

import db.*;
import java.sql.*;
import misc.*;

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
    private Boolean accountLocked = false;
    private String role;
    private String salt;

    // UserID
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    // Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getHashedPassword(){
        return this.hashedPassword;
    }
    public void setHashedPassword(String hashedPassword){
        this.hashedPassword = hashedPassword;
    }

    // EmailAddress
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    // HouseNumber
    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    // RoadName
    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    // CityName
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    // Postcode
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    // JoinDate
    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    // AccountLocked
    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    // Role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Salt
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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
        try {
            this.hashedPassword = Encryption.encrypt(hashedPassword, this.salt);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public boolean updateDetails(String userID, String username, String name, String enteredPassword,
            String emailString,
            String houseNumber, String cityName, String roadName, String postCode, String salt) {
        Boolean updateStatus;

        // open db connection
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        updateStatus = DatabaseOperations.saveUserEditDetails(db.con, userID, username, name, enteredPassword,
                emailString, houseNumber,
                cityName, roadName, postCode, salt);

        return updateStatus;
    }

}