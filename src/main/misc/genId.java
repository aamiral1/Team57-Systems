package main.misc;
import main.db.*;

public class genId {
    
    public Integer generateID(){
        // Open a database connection
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();
        db.openConnection();

        // Pass in SQL Query
        String query = "SELECT * FROM User";
        '''
        user 1:
        user 2:
        user 3:
        user 4:
        return 5
        '''
        // Get the id of the last user in the result set 
        // Add +1 to that id and return the value


        return null;

    }
}
