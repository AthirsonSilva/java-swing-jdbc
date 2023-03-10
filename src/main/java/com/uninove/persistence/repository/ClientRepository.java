package com.uninove.persistence.repository;

import com.uninove.persistence.entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientRepository {
    String username = "root";
    String url = "jdbc:mysql://localhost:3306/uninove?useTimezone=true&serverTimezone=UTC";
    String password = "@Potter77";

    Connection connection;
    PreparedStatement preparedStatement;
    private ResultSet resultSet;

    /**
     * @return 1 if the connection was established successfully, 2 if there was an error loading the driver, 3 if there was an error connecting to the database
     */
    public int connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            return 1;
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            return 3;
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading driver: " + e.getMessage());
            return 2;
        }
    }

    /**
     * Disconnects from the database
     */
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error disconnecting from database: " + e.getMessage());
        }
    }

    /**
     * @param client the client to be saved
     * @return 1 if the client was saved successfully, 2 if the client already exists, 3 if there was an error
     */
    public int save(Client client) {
        try {
            preparedStatement = connection.prepareStatement("""
                    INSERT INTO clients (id, name, phone, gender, income)
                    VALUES (?, ?, ?, ?, ?)
                       """);

            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, client.getName());
            preparedStatement.setString(3, client.getPhone());
            preparedStatement.setString(4, client.getGender());
            preparedStatement.setDouble(5, client.getIncome());

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            int code = e.getErrorCode();
            if (code == 1062) {
                return 2;
            } else {
                return 3;
            }
        }
    }


    /**
     * @param id the id of the client to be searched
     * @return the client with the id informed or null if not found
     */
    public Client search(String id) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM clients WHERE id = ? ");
            preparedStatement.setString(1, String.valueOf(UUID.fromString(id)));
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Client
                        .builder()
                        .id(UUID.fromString(resultSet.getString("id")))
                        .name(resultSet.getString("name"))
                        .phone(resultSet.getString("phone"))
                        .gender(resultSet.getString("gender"))
                        .income(resultSet.getDouble("income"))
                        .build();
            } else {
                return null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }


    /**
     * @return a list of all clients or null if there was an error
     */
    public ArrayList<Client> fetchClients() {
        ArrayList<Client> clients = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM clients");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(List.of(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone"),
                        resultSet.getString("gender"),
                        resultSet.getDouble("income")
                ));
                Client client = Client
                        .builder()
                        .id(UUID.fromString(resultSet.getString("id")))
                        .name(resultSet.getString("name"))
                        .phone(resultSet.getString("phone"))
                        .gender(resultSet.getString("gender"))
                        .income(resultSet.getDouble("income"))
                        .build();

                clients.add(client);
            }

            return clients;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

            return null;
        }
    }

    /**
     * @param id the id of the client to be deleted
     * @return 1 if the client was deleted successfully, 0 if there was an error
     */
    public int delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM clients WHERE id = ? ");
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    /**
     * @param client the client to be updated
     * @return 1 if the client was updated successfully, 0 if there was an error
     */
    public int update(Client client) {
        try {

            preparedStatement = connection.prepareStatement("""
                        UPDATE clients
                        SET name = ?, phone = ?, gender = ?, income = ?
                        WHERE id=?
                    """);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPhone());
            preparedStatement.setString(3, client.getGender());
            preparedStatement.setDouble(4, client.getIncome());
            preparedStatement.setString(5, resultSet.getString("id"));

            return preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }
}
