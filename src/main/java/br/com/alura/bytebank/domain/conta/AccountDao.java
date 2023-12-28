package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountDao {

    private Connection connection;

    AccountDao (Connection connection) {
        this.connection = connection;
    }

    public void save(DadosAberturaConta data) {
        var client = new Cliente(data.dadosCliente());
        var account = new Conta(data.numero(), client);

        try {
            String sql = "INSERT INTO account (id, balance, client, document, email)" +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, account.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, data.dadosCliente().nome());
            preparedStatement.setString(4, data.dadosCliente().cpf());
            preparedStatement.setString(5, data.dadosCliente().email());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
