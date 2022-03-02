package DAO;

import Factories.ConnectionFactory;
import Models.Especialidade;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeDAO {
    private String tableName = "especialidades";
    private Connection connection = ConnectionFactory.getConnection();

    public EspecialidadeDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS especialidade_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "especialidade_id BIGINT PRIMARY KEY DEFAULT nextval('especialidade_id_seq')," +
                "nome TEXT NOT NULL," +
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

    public Especialidade getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE especialidade_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildEspecialidade(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Especialidade createEspecialidade(Especialidade especialidade) {
        if (especialidade != null) {
            String sql = "INSERT INTO " + tableName +
                    "(nome, cadastro, atualizado, desativado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                stmt.setString(1, especialidade.getNome());
                stmt.setTimestamp(2, Timestamp.valueOf(especialidade.getCadastro()));
                stmt.setTimestamp(3, Timestamp.valueOf(especialidade.getAtualizado()));
                stmt.setTimestamp(4, Timestamp.valueOf(especialidade.getDesativado()));

                stmt.execute();

                ResultSet resultSet = stmt.getGeneratedKeys();

                while (resultSet.next()) {
                    especialidade.setId(resultSet.getLong(1));
                }

                return especialidade;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editEspecialidade(Especialidade especialidade) {
        if (especialidade != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "nome = ?, cadastro = ?, atualizado = ?, desativado = ? " +
                    "WHERE especialidade_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                stmt.setString(1, especialidade.getNome());
                stmt.setTimestamp(2, Timestamp.valueOf(especialidade.getCadastro()));
                stmt.setTimestamp(3, Timestamp.valueOf(especialidade.getAtualizado()));
                stmt.setTimestamp(4, Timestamp.valueOf(especialidade.getDesativado()));

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
                    "WHERE especialidade_id = ?";

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

    public List<Especialidade> listEspecialidades() {
        List<Especialidade> especialidades = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                especialidades.add(buildEspecialidade(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return especialidades;
    }

    private Especialidade buildEspecialidade(ResultSet resultSet) {
        try {
            Especialidade especialidade = new Especialidade();

            especialidade.setId(resultSet.getLong("especialidade_id"));
            especialidade.setNome(resultSet.getString("nome"));
            especialidade.setCadastro(resultSet.getTimestamp("cadastro").toLocalDateTime());
            especialidade.setAtualizado(resultSet.getTimestamp("atualizado").toLocalDateTime());
            especialidade.setDesativado(resultSet.getTimestamp("desativado").toLocalDateTime());

            return especialidade;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
