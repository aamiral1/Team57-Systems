package main.store.Users;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import main.misc.Encryption;

public class BankDetail {
    private String cardName;
    private String cardNumber;
    private Date expiryDate;
    private String cvv;
    private String salt = Encryption.generateSalt();

    public BankDetail(String cardName, String cardNumber, String expiryDate, String cvv) {
        try {
            // Encrypt card details
            this.cardName = Encryption.encrypt(cardName, salt);
            this.cardNumber = Encryption.encrypt(cardNumber.strip().replace(" ",""), salt);
            this.cvv = cvv;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Encryption for card details failed");
            e.printStackTrace();
        }

        // convert Date String to Date object
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsedDate = null;
        try {
            parsedDate = dateFormatter.parse(expiryDate);
            this.expiryDate = new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            System.out.println("INVALID DATE");
            e.printStackTrace();
            this.expiryDate = null;
        }
    }

    // Getter Methods
    public String getCardName() {
        return this.cardName;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public String getCVV() {
        return this.cvv;
    }

    public String getBankSalt() {
        return this.salt;
    }
}
