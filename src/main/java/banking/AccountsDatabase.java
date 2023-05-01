package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

// This class handles all database operations and SQL queries
public class AccountsDatabase {

    private final String dbUrl = "jdbc:sqlite:./src/database/";
    private final String defaultName = "accountsDatabase";
    private final Connection connection;

    // This constructor
    public AccountsDatabase () throws SQLException {
        String fullUrl = dbUrl + defaultName;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(fullUrl);
        this.connection = dataSource.getConnection();
        createTable();
    }

    private void createTable() {
        try {
            if (connection.isValid(5)) {
                Statement statement = connection.createStatement();
                String createTable = "CREATE TABLE IF NOT EXISTS card (" +
                        "id INT PRIMARY KEY NOT NULL, " +
                        "number TEXT, " +
                        "pin TEXT, " +
                        "balance INT DEFAULT 0)";
                statement.execute(createTable);
            }
        } catch (SQLException e) {
            System.out.println("Table already exists");
        }
    }

    public void addCard(Card card) {
        try {
            String cardInfo = "INSERT INTO card (id, number, pin, balance) " +
                    "VALUES (?, ?, ?, ?);";
//                    "VALUES (" + card.getId() + ", '" + card.getNumber() + "', '" + card.getPin() + "', " + card.getBalance() +");";
            PreparedStatement preparedStatement = connection.prepareStatement(cardInfo);
            preparedStatement.setInt(1, card.getId());
            preparedStatement.setString(2, card.getNumber());
            preparedStatement.setString(3, card.getPin());
            preparedStatement.setDouble(4, card.getBalance());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Something went wrong during table update.");
        }
    }

    public Card getCard(String cardNumber) {
        Card card = null;
        try {
            String selectCardSQL = "SELECT * FROM card WHERE number = ?";
            PreparedStatement ps = connection.prepareStatement(selectCardSQL);
            ps.setString(1, cardNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String pin = rs.getString("pin");
                double balance = rs.getDouble("balance");
                int id = rs.getInt("id");
                card = new Card(id, cardNumber, pin, balance);
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong during getting a card");;
        }
        return card;
    }

    public int checkLastId() {
        int lastId = 0;
        try {
            Statement statement = connection.createStatement();
            String checkIdSQL = "SELECT * FROM card " +
                    "ORDER BY id DESC LIMIT 1;";
            ResultSet resultSet = statement.executeQuery(checkIdSQL);
            while (resultSet.next()) {
                lastId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("I couldn't read last ID");
        }
        return lastId;
    }

    public int readBalance(Card card) throws SQLException {
        Statement statement = connection.createStatement();
        String checkBalanceSQL = "SELECT balance FROM card " +
                "WHERE id = " + card.getId();
        int balance = 0;
        try (ResultSet resultSet = statement.executeQuery(checkBalanceSQL)) {
            while (resultSet.next()) {
                balance = resultSet.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong during reading a balance. Please try again.");
        }
        return balance;
    }

//    public int readBalance(String cardNum) throws SQLException {
//        Statement statement = connection.createStatement();
//        String checkBalanceSQL = "SELECT balance FROM card " +
//                "WHERE number = " + cardNum + ";";
//        int balance = 0;
//        try (ResultSet resultSet = statement.executeQuery(checkBalanceSQL)) {
//            while (resultSet.next()) {
//                balance = resultSet.getInt("balance");
//            }
//        } catch (SQLException e) {
//            System.out.println("Something went wrong during reading a balance. Please try again.");
//        }
//        return balance;
//    }

    public String readPin(String accountNumber) throws SQLException {
        Statement statement = connection.createStatement();
        String checkBalanceSQL = "SELECT pin FROM card " +
                "WHERE number = " + accountNumber;
        String pin = "";
        try (ResultSet resultSet = statement.executeQuery(checkBalanceSQL)) {
            while (resultSet.next()) {
                pin = resultSet.getString("pin");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong during reading a pin. Please try again.");
        }
        return pin;
    }

    public int readId(String accountNumber) throws SQLException {
        Statement statement = connection.createStatement();
        String checkBalanceSQL = "SELECT id FROM card " +
                "WHERE number = " + accountNumber;
        int id = 0;
        try (ResultSet resultSet = statement.executeQuery(checkBalanceSQL)) {
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong during reading a pin. Please try again.");
        }
        return id;
    }

    public void updateBalance(Card card, int newBalance) {
        String addBalanceSQL = "UPDATE card " +
                "SET balance = " + newBalance + " " +
                "WHERE id = " + card.getId() + ";";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(addBalanceSQL);
        } catch (SQLException e) {
            System.out.println("Something went wrong during adding balance");
        }
    }

    public void deleteAccount(Card card) {
        String deleteAccountSQL = "DELETE FROM card " +
                "WHERE id = " + card.getId() + ";";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteAccountSQL);
            System.out.println("\nThe account has been closed!");
        } catch (SQLException e) {
            System.out.println("Something went wrong during closing the account.");
        }
    }

    // returns true if card exists in the database
    public boolean containsCard(String cardNumber) throws SQLException {
        boolean cardExists = true;
        Statement statement = connection.createStatement();

        // Check if there are any results of the query
        String checkCardSQL = "SELECT * FROM card " +
                "WHERE number = " + cardNumber;
        try (ResultSet resultSet = statement.executeQuery(checkCardSQL)) {
            cardExists = resultSet.next();
        } catch (SQLException e) {
            System.out.println("We couldn't find this account number. Please try again.");
        }
        return cardExists;
    }

    public void doTransfer(Card card, String receiversCardNumber, int amount) {
        String deductBalanceSQL = "UPDATE card SET balance = balance - ?" +
                "WHERE id = ?";
        String addBalanceSQL = "UPDATE card SET balance = balance + ?" +
                "WHERE number = ?";
        try (PreparedStatement ps1 = connection.prepareStatement(deductBalanceSQL);
             PreparedStatement ps2 = connection.prepareStatement(addBalanceSQL)) {

            ps1.setInt(1, amount);
            ps1.setInt(2, card.getId());
            ps1.executeUpdate();
            ps2.setInt(1, amount);
            ps2.setString(2, receiversCardNumber);
            ps2.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Something went wrong during a transfer");
        }
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

}