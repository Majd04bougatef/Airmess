package services;

import interfaces.GlobalInterface;
import models.Expense;
import util.MyDatabase;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseService implements GlobalInterface<Expense> {

    private final Connection con;

    public ExpenseService() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Expense expense) {
        String sql = "INSERT INTO expense (id_U, nameEX, amount, description, category, dateE) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, expense.getId_U());
            preparedStatement.setString(2, expense.getNameEX());
            preparedStatement.setDouble(3, expense.getAmount());
            preparedStatement.setString(4, expense.getDescription());
            preparedStatement.setString(5, expense.getCategory());
            preparedStatement.setDate(6, Date.valueOf(expense.getDateE()));
            preparedStatement.executeUpdate();
            System.out.println("Expense added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
        }
    }

    @Override
    public void update(Expense expense) {
        String sql = "UPDATE expense SET id_U = ?, nameEX = ?, amount = ?, description = ?, category = ?, dateE = ? WHERE idE = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, expense.getId_U());
            preparedStatement.setString(2, expense.getNameEX());
            preparedStatement.setDouble(3, expense.getAmount());
            preparedStatement.setString(4, expense.getDescription());
            preparedStatement.setString(5, expense.getCategory());
            preparedStatement.setDate(6, Date.valueOf(expense.getDateE()));
            preparedStatement.setInt(7, expense.getIdE());
            preparedStatement.executeUpdate();
            System.out.println("Expense updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
        }
    }

    @Override
    public void delete(Expense expense) {
        String sql = "DELETE FROM expense WHERE idE = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, expense.getIdE());
            preparedStatement.executeUpdate();
            System.out.println("Expense deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
        }
    }

    @Override
    public List<Expense> getAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expense";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Expense expense = new Expense();
                expense.setIdE(resultSet.getInt("idE"));
                expense.setId_U(resultSet.getInt("id_U"));
                expense.setNameEX(resultSet.getString("nameEX"));
                expense.setAmount(resultSet.getDouble("amount"));
                expense.setDescription(resultSet.getString("description"));
                expense.setCategory(resultSet.getString("category"));
                expense.setDateE(resultSet.getDate("dateE").toLocalDate());
                expenses.add(expense);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses: " + e.getMessage());
        }
        return expenses;
    }


    @Override
    public List<Expense> getById(int id) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expense WHERE idE = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Expense expense = new Expense();
                    expense.setIdE(resultSet.getInt("idE"));
                    expense.setId_U(resultSet.getInt("id_U"));
                    expense.setNameEX(resultSet.getString("nameEX"));
                    expense.setAmount(resultSet.getDouble("amount"));
                    expense.setDescription(resultSet.getString("description"));
                    expense.setCategory(resultSet.getString("category"));
                    expense.setDateE(resultSet.getDate("dateE").toLocalDate());
                    expenses.add(expense);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses by ID: " + e.getMessage());
        }
        return expenses;
    }
}
