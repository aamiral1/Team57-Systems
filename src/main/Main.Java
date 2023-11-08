import main.db.DatabaseConnectionHandler;


public class Main{
    public static void main(String args[]){
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();

        db.openConnection();        
    }
}

    