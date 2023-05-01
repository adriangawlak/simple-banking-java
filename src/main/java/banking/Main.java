package banking;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        BankingSystem bankingSystem = new BankingSystem();
        bankingSystem.showMenu();

    }
}