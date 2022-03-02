package DAO;

import Factories.ConnectionFactory;
import Models.Pessoa;
import Models.Secretaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecretariaDAO {
    private String tableName = "secretarias";
    private Connection connection = ConnectionFactory.getConnection();

    public SecretariaDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS secretaria_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "secretaria_id BIGINT PRIMARY KEY DEFAULT nextval('secretaria_id_seq')," +
                "salario NUMERIC(50, 10)," +
                "data_contratacao TIMESTAMP," +
                "pis TEXT NOT NULL," +
                "pessoa_id BIGINT," +
                "CONSTRAINT pessoa_id " +
                "FOREIGN KEY (pessoa_id)" +
                "REFERENCES pessoas(pessoa_id)" +
                ");";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Secretaria getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE pessoa_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildSecretaria(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Secretaria createSecretaria(Secretaria secretaria) {
        if (secretaria != null) {
            String sql = "INSERT INTO " + tableName +
                    "(pessoa_id, salario, data_contratacao, pis) " +
                    "VALUES (?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                PessoaDAO pessoaDAO = new PessoaDAO();
                Long id = pessoaDAO.createPessoa(secretaria).getId();

                stmt.setLong(1, id);
                stmt.setBigDecimal(2, secretaria.getSalario());
                stmt.setTimestamp(3, Timestamp.valueOf(secretaria.getDataContratacao()));
                stmt.setString(4, secretaria.getPis());

                secretaria.setId(id);

                stmt.execute();

                return secretaria;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editSecretaria(Secretaria secretaria) {
        if (secretaria != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "salario = ?, data_contratacao = ?, pis = ? " +
                    "WHERE pessoa_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                new PessoaDAO().editPessoa(secretaria);
                stmt.setBigDecimal(1, secretaria.getSalario());
                stmt.setTimestamp(2, Timestamp.valueOf(secretaria.getDataContratacao()));
                stmt.setString(3, secretaria.getPis());

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteById(Long id) {
        new PessoaDAO().deleteById(id);
    }

    public List<Secretaria> listSecretarias() {
        List<Secretaria> secretarias = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                secretarias.add(buildSecretaria(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return secretarias;
    }

    private Secretaria buildSecretaria(ResultSet resultSet) {
        try {
            Secretaria secretaria = (Secretaria) new PessoaDAO().getById(resultSet.getLong("pessoa_id"));
            secretaria.setSalario(resultSet.getBigDecimal("salario"));
            secretaria.setDataContratacao(resultSet.getTimestamp("data_contratacao").toLocalDateTime());
            secretaria.setPis(resultSet.getString("pis"));

            return secretaria;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
