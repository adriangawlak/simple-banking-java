package banking;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BankingSystem {

    private Scanner scan = new Scanner(System.in);
    private boolean programOn;
    private AccountsDatabase database;

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
                    int error = logIntoAccount();
                    if (error == 1) {
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
        System.out.println("\nYour card number:");
        printAccountNumber(card.getNumber());
        System.out.println("Your card PIN:");
        System.out.println(card.getPin());
    }

    public int logIntoAccount() throws SQLException {
        System.out.println("Enter your card number:");
        System.out.print("> ");
        scan.nextLine();
        String inputCardNumber = scan.nextLine().trim().replaceAll("\\s", "");

        System.out.println("Enter your PIN:");
        System.out.print("> ");
        String inputPin = scan.nextLine().trim().replaceAll("\\s", "");

        if (database.containsCard(inputCardNumber)) {
            Card card = database.getCard(inputCardNumber);

            if (card != null && card.getPin().equals(inputPin)) {
                System.out.println("\nYou have successfully logged in!");
                showAccountMenu(card); // moves to card view
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
                    System.out.println("\nBalance: " + card.getBalance());
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
                case 0:
                    loggedIn = false;
                    programOn = false;
                    System.out.println("\nBye!");
                    closeApplication();
                    break;
                default:
                    System.out.println("Please type the correct number and press Enter");
            }
        }
    }

    public void addBalance(Card card) {
        System.out.println("Enter income amount:");
        System.out.print("> ");
        double amountToAdd = scanDouble();
        // update card and database
        card.setBalance(card.getBalance() + amountToAdd);
        database.updateBalance(card);
        System.out.println("\nIncome was added!");
    }

    public void doTransfer(Card card) {
        System.out.println("\nEnter receivers card number:");
        System.out.print("> ");
        scan.nextLine();
        String receiversCard = scan.nextLine().trim().replaceAll("\\s", "");

        // Check if given number is a correct card number
        if (!CardValidator.isCardNumber(receiversCard)) {
            return;

        } else if (!database.containsCard(receiversCard)) {
            System.out.println("This card does not exist.");

        } else if (card.getNumber().equals(receiversCard)) {
            System.out.println("You can't transfer money to the same account!");
        // Proceed with the transfer
        } else {
            System.out.println("Enter the amount to transfer:");
            System.out.print("> ");
            double amount = scanDouble();
            if (amount > database.readBalance(card)) {
                System.out.println("Not enough money!");
            } else {
                card.setBalance(card.getBalance() - amount);
                database.sendTransfer(card, receiversCard, amount);
            }
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

    public double scanDouble() {
        double input = 0.0;
        boolean validInput = false;
        while (!validInput) {
            try {
                input = scan.nextDouble();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a correct number.");
                scan.next();
            }
        }
        return Math.round(input * 100.0) / 100.0;
    }

    private void printAccountNumber(String number) {
        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < number.length(); i+=4) {
                formattedNumber.append(number.substring(i, i + 4));
                formattedNumber.append(" ");
        }
        System.out.println(formattedNumber);
    }

}
