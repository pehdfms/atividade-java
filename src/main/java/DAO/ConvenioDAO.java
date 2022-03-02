package DAO;

import Factories.ConnectionFactory;
import Models.Convenio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConvenioDAO {
    private String tableName = "convenios";
    private Connection connection = ConnectionFactory.getConnection();

    public ConvenioDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS convenio_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "convenio_id BIGINT PRIMARY KEY DEFAULT nextval('convenio_id_seq')," +
                "nome TEXT NOT NULL," +
                "valor NUMERIC(50, 10)," +
                "cadastro TIMESTAMP," +
                "atualizado TIMESTAMP," +
                "desativado TIMESTAMP" +
                ");";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Convenio getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE convenio_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildConvenio(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Convenio createConvenio(Convenio convenio) {
        if (convenio != null) {
            String sql = "INSERT INTO " + tableName +
                    "(nome, valor," +
                    "cadastro, atualizado, desativado)" +
                    "VALUES (?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                stmt.setString(1, convenio.getNome());
                stmt.setBigDecimal(2, convenio.getValor());

                stmt.setTimestamp(3, Timestamp.valueOf(convenio.getCadastro()));
                stmt.setTimestamp(4, Timestamp.valueOf(convenio.getAtualizado()));
                stmt.setTimestamp(5, Timestamp.valueOf(convenio.getDesativado()));

                stmt.execute();

                ResultSet resultSet = stmt.getGeneratedKeys();

                while (resultSet.next()) {
                    convenio.setId(resultSet.getLong(1));
                }

                return convenio;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editConvenio(Convenio convenio) {
        if (convenio != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "nome = ?, valor = ?," +
                    "cadastro = ?, atualizado = ?, desativado = ? " +
                    "WHERE convenio_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                stmt.setString(1, convenio.getNome());
                stmt.setBigDecimal(2, convenio.getValor());

                stmt.setTimestamp(3, Timestamp.valueOf(convenio.getCadastro()));
                stmt.setTimestamp(4, Timestamp.valueOf(convenio.getAtualizado()));
                stmt.setTimestamp(5, Timestamp.valueOf(convenio.getDesativado()));

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteById(Long id) {
        if (id != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "desativado = ? " +
                    "WHERE convenio_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setLong(2, id);

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Convenio> listConvenios() {
        List<Convenio> convenios = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                convenios.add(buildConvenio(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return convenios;
    }

    private Convenio buildConvenio(ResultSet resultSet) {
        try {
            Convenio convenio = new Convenio();

            convenio.setId(resultSet.getLong("convenio_id"));

            convenio.setNome(resultSet.getString("nome"));
            convenio.setValor(resultSet.getBigDecimal("valor"));

            convenio.setCadastro(resultSet.getTimestamp("cadastro").toLocalDateTime());
            convenio.setAtualizado(resultSet.getTimestamp("atualizado").toLocalDateTime());
            convenio.setDesativado(resultSet.getTimestamp("desativado").toLocalDateTime());

            return convenio;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
