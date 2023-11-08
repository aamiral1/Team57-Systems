import java.sql.Date;

public class BankingDetails {
    String bankCardName;
    String bankCardNumber;
    Date cardExpiryDate;
    int CVV;
    Boolean validStatus;

    public Boolean updateBankDetails(String bankCardName, String bankCardNumber, Date cardExpiryDate, int CVV, Boolean validStatus) {
        if (validStatus) {
            this.bankCardName = bankCardName;
            this.bankCardNumber = bankCardNumber;
            this.cardExpiryDate = cardExpiryDate;
            this.CVV = CVV;
            this.validStatus = validStatus;
            return true;
        }
        return false;
    }
}