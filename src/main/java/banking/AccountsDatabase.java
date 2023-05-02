package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.HashSet;

// This class handles all database operations and SQL queries
public class AccountsDatabase {

    private final String dbUrl = "jdbc:sqlite:./src/database/";
    private final String defaultName = "accountsDatabase";
    private final Connection connection;

    // Create a default database or connect to an existing one
    public AccountsDatabase () throws SQLException {
        String fullUrl = dbUrl + defaultName;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(fullUrl);
        this.connection = dataSource.getConnection();
        createTable();
        Card.setCardNumbersList(getAllCardNumbers());
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
            System.out.println("Couldn't create a table in the database");
        }
    }

    public void addCard(Card card) {
        String cardInfo = "INSERT INTO card (id, number, pin, balance) " +
                "VALUES (?, ?, ?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(cardInfo)) {
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
        String selectCardSQL = "SELECT * FROM card WHERE number = ?";

        try (PreparedStatement ps = connection.prepareStatement(selectCardSQL)) {
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

    public HashSet<String> getAllCardNumbers() {
        HashSet<String> allNumbers = new HashSet<>();
        String allCardsSQL = "SELECT number FROM card";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(allCardsSQL);
            while (resultSet.next()) {
                String cardNum = resultSet.getString("number");
                allNumbers.add(cardNum);
            }
        } catch (SQLException e) {
            System.out.println("Couldn't get allCards list");
        }
        return allNumbers;
    }

    public int checkLastId() {
        int lastId = 0;
        String checkIdSQL = "SELECT * FROM card " +
                "ORDER BY id DESC LIMIT 1;";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(checkIdSQL);
            while (resultSet.next()) {
                lastId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("I couldn't read last ID");
        }
        return lastId;
    }

    public double readBalance(Card card) {
        double balance = 0;
        String checkBalanceSQL = "SELECT balance FROM card " +
                "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(checkBalanceSQL)) {
            ps.setInt(1, card.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong during reading a balance. Please try again.");
        }
        return balance;
    }

    public void updateBalance(Card card) {
        String addBalanceSQL = "UPDATE card " +
                "SET balance = ?" +
                "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(addBalanceSQL)) {
            ps.setDouble(1, card.getBalance());
            ps.setInt(2, card.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Something went wrong during adding balance");
        }
    }

    public void deleteAccount(Card card) {
        String deleteAccountSQL = "DELETE FROM card " +
                "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(deleteAccountSQL)) {
            ps.setInt(1, card.getId());
            ps.executeUpdate();
            System.out.println("\nThe account has been closed!");
        } catch (SQLException e) {
            System.out.println("Something went wrong during closing the account.");
        }
    }

    // returns true if card exists in the database
    public boolean containsCard(String cardNumber) {
        boolean cardExists = true;
        // Check if there are any results of the query
        String checkCardSQL = "SELECT * FROM card " +
                "WHERE number = ?";

        try (PreparedStatement ps = connection.prepareStatement(checkCardSQL)) {
            ps.setString(1, cardNumber);
            ResultSet resultSet = ps.executeQuery();
            cardExists = resultSet.next();
        } catch (SQLException e) {
            System.out.println("We couldn't find this account number. Please try again.");
        }
        return cardExists;
    }

    public void sendTransfer(Card card, String receiversCardNumber, double amount) {
        String deductBalanceSQL = "UPDATE card SET balance = balance - ?" +
                "WHERE id = ?";
        String addBalanceSQL = "UPDATE card SET balance = balance + ?" +
                "WHERE number = ?";

        try (PreparedStatement ps1 = connection.prepareStatement(deductBalanceSQL);
             PreparedStatement ps2 = connection.prepareStatement(addBalanceSQL)) {
            ps1.setDouble(1, amount);
            ps1.setInt(2, card.getId());
            ps1.executeUpdate();
            ps2.setDouble(1, amount);
            ps2.setString(2, receiversCardNumber);
            ps2.executeUpdate();
            System.out.println("\nTransfer completed successfully!");
        } catch (SQLException e) {
            System.out.println("Something went wrong during a transfer");
        }
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

}