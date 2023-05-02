package banking;

import java.util.HashSet;
import java.util.Random;

public class Card {
    private int id;
    private final String number;
    private String pin;
    private double balance;
    private final String bin = "400000"; // Bank Identification Number

    private static HashSet<String> cardNumbersList;


    public Card(int lastId) {
        this.id = lastId + 1;
        this.number = generateNumber();
        this.pin = generatePin();
        this.balance = 0;
    }

    public Card(int id, String number, String pin, double balance) {
        this.id = id;
        this.number = number;
        this.pin = pin;
        this.balance = balance;
    }
    public int getId() {
        return id;
    }
    public String getPin() {
        return pin;
    }

    public String getNumber() {
        return this.number;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static void setCardNumbersList(HashSet<String> cardNumbersList) {
        Card.cardNumbersList = cardNumbersList;
    }

    //  Custom methods for number, pin and checksum generation

    // This generates a card number: 6 digit bin, 9 random digits, 1 checksum
    private String generateNumber() {
        StringBuilder cardNumber = new StringBuilder(bin);
        for (int numDigits = 0; numDigits < 9; numDigits++) {
            Random random = new Random();
            int num = random.nextInt(10);
            cardNumber.append(num);
        }
        String generatedNumber = cardNumber.toString();
        int checksum = generateChecksum(generatedNumber);
        generatedNumber += checksum;

        if (cardNumbersList.contains(generatedNumber))
            return generateNumber();
        cardNumbersList.add(generatedNumber);
        return generatedNumber;
    }

    // Generate last digit according to Luhn algorithm
    static int generateChecksum(String accountNumber) {
        int[] numberArray = new int[accountNumber.length()]; // = 15
        int sum = 0;

        for (int i = 0; i < numberArray.length; i++) {
            numberArray[i] = (int)accountNumber.charAt(i) - 48; // deducting 48 to match char value
            // multiply even indexes * 2
            if (i % 2 == 0)
                numberArray[i] *= 2;
            // check if number is a digit
            if (numberArray[i] > 9)
                numberArray[i] -= 9;
            // add to total sum
            sum += numberArray[i];
        }
        int checksum = 10 - (sum % 10);
        if (checksum == 10)
            return 0;
        else
            return checksum;
    }


    private String generatePin() {
        Random random = new Random();
        String pin = "";
        for (int i = 0; i < 4; i++) {
            int num = (random.nextInt(10));
            pin += Integer.toString(num);
        }
        return pin;
    }

}
