package banking;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BankingSystem {

    private Scanner scan = new Scanner(System.in);
//    public static HashMap<String, Card> listOfCards = new HashMap<>();
    private boolean programOn;
    AccountsDatabase database;

    public BankingSystem() throws SQLException {
            database = new AccountsDatabase();
    }

    public void showMenu() throws SQLException {
        programOn = true;
        System.out.println("\nWelcome to the Simple Banking System!");
        System.out.println("Choose action by typing a digit and press Enter");
        while (programOn) {
            System.out.println("\n1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            System.out.print("> ");
            int choice = scanNumber();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    int result = logIntoAccount();
                    if (result == 1) {
                        programOn = false;
                        closeApplication();
                    }
                    break;
                case 0:
                    System.out.println("\nBye!");
                    programOn = false;
                    closeApplication();
                    break;
                default:
                    System.out.println("Please type a correct digit and press Enter");
            }
        }
    }

    private void closeApplication() throws SQLException {
        database.closeConnection();
        scan.close();
        System.exit(0);
    }

    public void createAccount() {
        Card card = new Card(database.checkLastId());
        database.addCard(card);
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(card.getNumber());
        System.out.println("Your card PIN:");
        System.out.println(card.getPin());
    }

    public int logIntoAccount() throws SQLException {
        System.out.println("Enter your card number:");
        System.out.print("> ");
        String cardNumber = scan.next();

        System.out.println("Enter your PIN:");
        System.out.print("> ");
        String inputPin = scan.next();

//        if (listOfCards.containsKey(cardNumber)) {
        if (database.containsCard(cardNumber)) {
            Card card = database.getCard(cardNumber);
//            Card card = listOfCards.get(cardNumber);
//            int id = database.readId(cardNumber);
//            int balance = database.readBalance(cardNumber);
//            String pin = database.readPin(cardNumber);
//            Card card = new Card(id, cardNumber, pin, balance);

            if (card != null && card.getPin().equals(inputPin)) {
                System.out.println("\nYou have successfully logged in!");
                showAccountMenu(card); // -> create cardView
                return 0;
            }
        } else {
            System.out.println("Wrong card number or PIN!");
            if (inputPin.equals("0"))
                return 1;
        }
        return 0;
    }

    public void showAccountMenu(Card card) throws SQLException {
        boolean loggedIn = true;
        while(loggedIn) {
            System.out.println("\n1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            System.out.print("> ");
            int choice = scanNumber();

            switch (choice) {
                case 1:
//                    System.out.println("\nBalance: " + card.getBalance());
                    System.out.println("\nBalance: " + database.readBalance(card));
                    break;
                case 2:
                    addBalance(card);
                    break;
                case 3:
                    doTransfer(card);
                    break;
                case 4:
                    database.deleteAccount(card);
                    loggedIn = false;
                    break;
                case 5:
                    System.out.println("\nYou have successfully logged out!");
                    loggedIn = false;
                    break;
//                case 6:
//                    System.out.println(database.checkLastId());
                case 0:
                    loggedIn = false;
                    programOn = false;
                    System.out.println("\nBye!");
                    closeApplication();
                    break;
            }
        }
    }

    public void addBalance(Card card) throws SQLException {
        System.out.println("Enter income amount:");
        System.out.print(">");
        int currentBalance = database.readBalance(card);
        int amountToAdd = scan.nextInt();
        int newBalance = currentBalance + amountToAdd;
        database.updateBalance(card, newBalance);
        System.out.println("\nIncome was added!");
    }

    public void doTransfer(Card card) throws SQLException {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        System.out.print(">");
        scan.nextLine();
        String receiversAccount = scan.nextLine();

        int properChecksum = Card.generateChecksum(receiversAccount.substring(0, 15));
        int givenChecksum = Integer.parseInt(receiversAccount.substring(15));
//        System.out.println("Proper checksum is " + properChecksum);

        // check if receivers card number is correct
        if (properChecksum != givenChecksum) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (database.containsCard(receiversAccount) == false) {
            System.out.println("Such a card does not exist.");
        } else if (card.getNumber().equals(receiversAccount)) {
            System.out.println("You can't transfer money to the same account!");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            System.out.print(">");
            int amount = scan.nextInt();
            if (amount > database.readBalance(card)){
                System.out.println("Not enough money!");
            } else
                database.doTransfer(card, receiversAccount, amount);
        }

    }

    public int scanNumber() {
        int choice = 0;
        boolean validInput = false;
        while (!validInput) {
            try {
                choice = scan.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a correct number");
                scan.next();
            }
        }
        return choice;
    }

    public long scanNumber(boolean isLong) {
        long choice = 0;
        boolean validInput = false;
        while (!validInput) {
            try {
                choice = scan.nextLong();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a correct number");
                scan.next();
            }
        }
        return choice;
    }

}
