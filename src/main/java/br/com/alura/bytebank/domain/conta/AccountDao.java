package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class AccountDao {

    private Connection connection;

    AccountDao(Connection connection) {
        this.connection = connection;
    }

    public void save(DadosAberturaConta data) {
        var client = new Cliente(data.dadosCliente());
        var account = new Conta(data.numero(), BigDecimal.ZERO, client, true);
        try {
            String sql = "INSERT INTO account (id, balance, client, document, email, active)" +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, account.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, data.dadosCliente().nome());
            preparedStatement.setString(4, data.dadosCliente().cpf());
            preparedStatement.setString(5, data.dadosCliente().email());
            preparedStatement.setBoolean(6, true);
            preparedStatement.execute();
            preparedStatement.close();
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> getAll() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Set<Conta> accounts = new HashSet<>();
        String sql = "SELECT * FROM account WHERE active = true";
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Integer num = resultSet.getInt(1);
                BigDecimal balance = resultSet.getBigDecimal(2);
                String name = resultSet.getString(3);
                String document = resultSet.getString(4);
                String email = resultSet.getString(5);
                boolean active = resultSet.getBoolean(6);
                DadosCadastroCliente data = new DadosCadastroCliente(name, document, email);
                Cliente client = new Cliente(data);
                accounts.add(new Conta(num, balance, client, active));
            }
            resultSet.close();
            preparedStatement.close();
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }

    public Conta getOne (Integer id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Conta account = null;
        String sql = "SELECT * FROM account WHERE id = ? and active = true";
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Integer accountId = resultSet.getInt(1);
                BigDecimal balance = resultSet.getBigDecimal(2);
                String name = resultSet.getString(3);
                String email = resultSet.getString(4);
                String document = resultSet.getString(5);
                boolean active = resultSet.getBoolean(6);
                DadosCadastroCliente data = new DadosCadastroCliente(name, document, email);
                Cliente client = new Cliente(data);
                account = new Conta(accountId, balance, client, active);
            }
            resultSet.close();
            preparedStatement.close();
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    public void update(Integer id, BigDecimal value) {
        PreparedStatement preparedStatement;
        String sql = "UPDATE account SET balance = ? WHERE id = ?";
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setBigDecimal(1, value);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            preparedStatement.close();
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Integer id) {
        PreparedStatement preparedStatement;
        String sql = "DELETE FROM account WHERE id = ?";
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            preparedStatement.close();
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void enable(Integer id) {
        PreparedStatement preparedStatement;
        String sql = "UPDATE account SET active = false WHERE id = ?";
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            preparedStatement.close();
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
