package banking;

import java.util.ArrayList;
import java.util.Random;

public class Card {
    private String number;
    private String pin;
    private double balance;
    private int id;

    private static ArrayList<String> listOfCardNumbers = new ArrayList<>();


    public Card(int lastId) {
        this.id = lastId + 1;
        this.number = generateNumber();
        this.pin = generatePin();
        this.balance = 0;
//        BankingSystem.listOfCards.put(number, this);
    }

    public Card(int id, String number, String pin, double balance) {
        this.id = id;
        this.number = number;
        this.pin = pin;
        this.balance = balance;
    }

    private String generateNumber() {
        String bin = "400000";
        String cardNumber = bin;
        for (int numDigits = 0; numDigits < 9; numDigits++) {
            Random random = new Random();
            int num = random.nextInt(10);
            cardNumber += num;
        }
        int checksum = generateChecksum(cardNumber);
        cardNumber += checksum;

        // add generateChecksum()
        if (listOfCardNumbers.contains(cardNumber))
            generateNumber();
        listOfCardNumbers.add(cardNumber);
        return cardNumber;
    }

    // Generate last digit according to Luhn algorithm
    static int generateChecksum(String accountNumber) {
        int[] numberArray = new int[accountNumber.length()]; // = 15
        int sum = 0;

        for (int i = 0; i < numberArray.length; i++) {
            numberArray[i] = (int)accountNumber.charAt(i) - 48; // deducting 48 to match char value
            // multiplying even indexes * 2
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
    public int getId() {
        return id;
    }
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNumber() {
        return this.number;
    }

    public double getBalance() {
        return this.balance;
    }
}
