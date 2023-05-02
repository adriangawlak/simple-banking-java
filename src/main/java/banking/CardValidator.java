package banking;

public class CardValidator {

    // This checks if a given value is a correct card number
    public static boolean isCardNumber(String cardNumber) {

        if (!cardNumber.matches("\\d{16}")) {
            System.out.println("Card number should be 16 digits. Please try again.");
            return false;
        }

        // check if a given checksum is correct according to Luhn algorithm
        int properChecksum = Card.generateChecksum(cardNumber.substring(0, 15));
        int givenChecksum = Integer.parseInt(cardNumber.substring(15));
        if (properChecksum != givenChecksum) {
            System.out.println("Incorrect card number. Please try again.");
            return false;
        }

        return true;
    }
}
