public class User {

    int userID;
    String name;
    String password;
    String email;
    Boolean isRegistered;

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