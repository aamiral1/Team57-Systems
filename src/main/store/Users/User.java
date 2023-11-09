public class User {

    int userID;
    String name;
    String password;
    String email;
    Boolean isRegistered;

    // Create constructor with all attributes
    public newUser (String name, String pass, String email) {
        if(type(name) == String){
            // VALIDATE AND ESCAPE INPUTS
            this.name = name;
        this.password = pass;
        this.email = email;
        this.isRegistered = True;
        this.userID = // Generate some random 8 digit number INT
        }
        
    }

    // Get methods

    public String signIn(String name, String password, String email) {
        return "Well done you signed in";
    }

    public boolean signOut() {
        return false;
    }

    public String signUp(String name, String password, String email) {
        return "Well done you signed up";
    }

}