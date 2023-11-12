import main.db.*;
import java.sql.*;
import java.time.LocalDate;

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

public class User {

    int userID;
    Date joinDate;

    String name;
    String emailAddress;
    String password;
    String houseNumber;
    String roadName; 
    String cityName;
    String postcode;

    // Create constructor with all attributes
    public User(String fname, String sname, String emaiAddress, String password, 
    String houseNumber, String roadName, String cityName, String postcode) {
        // Transfer inputs from constructor
        this.name = fname + " " + sname;
        this.emailAddress = emailAddress;
        this.password = password; // TODO: Encrypt 'password'
        this.houseNumber = houseNumber;
        this.roadName = roadName;
        this.cityName = cityName;
        this.postcode = postcode;

        // System generated info
        LocalDate currentDate;
        currentDate = LocalDate.now();
        joinDate = java.sql.Date.valueOf(currentDate);

        // GenID idGenerator = new GenID();
        // userID = idGenerator.generateID();
    }
    // Get methods
    public Boolean signUp(){
        // START WORKING ON THIS FUNCTION
        Boolean flag = false;
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        try {
            Statement stmt = db.con.createStatement();
            PreparedStatement stmt = db.con.createStatement("INSERT INTO Users (user_id,name,hashed_password,email,isRegistered,houseNumber,"); // START BY FINISHING THIS FUNCTION UP


        }

        return flag;
    }
    private Object[] getAttributes(){
        Object[] attributes = 
        {
            Integer.toString(this.userID), 
            this.name, 
            this.password, 
            this.houseNumber, 
            this.roadName, 
            this.cityName,
            this.postcode, 
            this.joinDate
        };
        
        return attributes;
    }

    public Boolean exists(String emailAddress){
        // initiate Database Connections
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();
        Boolean flag = false;

        try{
            Statement stmt = db.con.createStatement();

            PreparedStatement pstmt = db.con.prepareStatement("SELECT EXISTS(SELECT * from Users WHERE email=?");
            pstmt.setString(1, emailAddress);
    
            ResultSet isExists = pstmt.executeQuery();
    
            if (isExists.next()){
                if (isExists.getString(1).equals("1")){
                    flag=true;
                }
            }
            else {
                System.out.println("Result set issue"); 
            }
            
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        return flag;
        
    }

    // Useless?
    // public String signIn(String name, String password, String email) {
    //     return "Well done you signed in";
    // }

    // public boolean signOut() {
    //     return false;
    // }

    // public String signUp(String name, String password, String email) {
    //     return "Well done you signed up";
    // }

}