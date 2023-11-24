package main.store.Users;

public class UserManager {
     // Singleton status variable
    private static Boolean uniqueUserInstance = false;

    // current User details
    private static User currentUser;
    // private static UserManager instance;

    // // UserManager constructor
    // public static synchronized UserManager getInstance() {
    //     if (instance == null){
    //         instance = new UserManager();
    //     }
    //     return instance;
    // }

    // Creates a unique user instance for the system (ensuring Singleton method)
    public static void setCurrentUser (User myUser){
        if (!uniqueUserInstance){
            UserManager.currentUser = myUser;
            uniqueUserInstance = true;
            System.out.println("User created succesfully");
        }
        else {System.out.println("setCurrentUser failed. A User already exists in the system.");}
    }
    public static User getCurrentUser() {
        if (uniqueUserInstance){
            return UserManager.currentUser;
        } else {
            System.out.println("Current user does not exist");            return null;
        } 
    };

}
