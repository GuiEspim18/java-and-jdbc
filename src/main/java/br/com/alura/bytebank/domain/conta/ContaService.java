package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;
import br.com.alura.bytebank.domain.cliente.Cliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaService {

    private ConnectionFactory connection;

    public ContaService () {
        this.connection = new ConnectionFactory();
    }

    private Set<Conta> contas = new HashSet<>();

    public Set<Conta> listarContasAbertas() {
        Connection connection = this.connection.connect();
        return new AccountDao(connection).getAll();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta data) {
            Connection connection = this.connection.connect();
            new AccountDao(connection).save(data);
    }

    public void realizarSaque(Integer id, BigDecimal value) {
        Conta account = buscarContaPorNumero(id);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (value.compareTo(account.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }
        Connection connection = this.connection.connect();
        BigDecimal newValue = account.getSaldo().subtract(value);
        new AccountDao(connection).update(id, newValue);
    }

    public void realizarDeposito(Integer id, BigDecimal value) {
        var account = buscarContaPorNumero(id);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        Connection connection = this.connection.connect();
        BigDecimal newValue = account.getSaldo().add(value);
        new AccountDao(connection).update(account.getNumero(), newValue);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        contas.remove(conta);
    }

    private Conta buscarContaPorNumero(Integer id) {
       Connection connection = this.connection.connect();
       return new AccountDao(connection).getOne(id);
    }

    public void transference (Integer from, Integer to, BigDecimal value) {
        this.realizarSaque(from, value);
        this.realizarDeposito(to, value);
    }
}
