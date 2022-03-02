package DAO;

import Factories.ConnectionFactory;
import Models.Historico;
import Models.StatusAgenda;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoricoDAO {
    private String tableName = "historicos";
    private Connection connection = ConnectionFactory.getConnection();

    public HistoricoDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS historico_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "historico_id BIGINT PRIMARY KEY DEFAULT nextval('historico_id_seq')," +
                "observacao TEXT," +
                "data TIMESTAMP," +
                "status_agenda TEXT NOT NULL," +
                "cadastro TIMESTAMP," +
                "atualizado TIMESTAMP," +
                "desativado TIMESTAMP," +
                "agenda_id BIGINT," +
                "secretaria_id BIGINT," +
                "paciente_id BIGINT," +
                "CONSTRAINT agenda_id " +
                "FOREIGN KEY (agenda_id)" +
                "REFERENCES agendas(agenda_id), " +
                "CONSTRAINT paciente_id " +
                "FOREIGN KEY (paciente_id)" +
                "REFERENCES pacientes(paciente_id), " +
                "CONSTRAINT secretaria_id " +
                "FOREIGN KEY (secretaria_id)" +
                "REFERENCES secretarias(secretaria_id)" +
                ");";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Historico getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE historico_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildHistorico(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Historico createHistorico(Historico historico) {
        if (historico != null) {
            String sql = "INSERT INTO " + tableName +
                    "(observacao, data, status_agenda," +
                    " cadastro, atualizado, desativado," +
                    " agenda_id, paciente_id, secretaria_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                stmt.setString(1, historico.getObservacao());
                stmt.setTimestamp(2, Timestamp.valueOf(historico.getData()));
                stmt.setString(3, historico.getStatusAgenda().name());

                stmt.setTimestamp(4, Timestamp.valueOf(historico.getCadastro()));
                stmt.setTimestamp(5, Timestamp.valueOf(historico.getAtualizado()));
                stmt.setTimestamp(6, Timestamp.valueOf(historico.getDesativado()));

                stmt.setLong(7, historico.getAgenda().getId());
                stmt.setLong(8, historico.getPaciente().getId());
                stmt.setLong(9, historico.getSecretaria().getId());

                stmt.execute();

                ResultSet resultSet = stmt.getGeneratedKeys();

                while (resultSet.next()) {
                    historico.setId(resultSet.getLong(1));
                }

                return historico;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editHistorico(Historico historico) {
        if (historico != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "observacao = ?, data = ?, status_agenda = ?," +
                    "cadastro = ?, atualizado = ?, desativado = ?," +
                    "agenda_id = ?, paciente_id = ?, secretaria_id = ? " +
                    "WHERE historico_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                stmt.setString(1, historico.getObservacao());
                stmt.setTimestamp(2, Timestamp.valueOf(historico.getData()));
                stmt.setString(3, historico.getStatusAgenda().name());

                stmt.setTimestamp(4, Timestamp.valueOf(historico.getCadastro()));
                stmt.setTimestamp(5, Timestamp.valueOf(historico.getAtualizado()));
                stmt.setTimestamp(6, Timestamp.valueOf(historico.getDesativado()));

                stmt.setLong(7, historico.getAgenda().getId());
                stmt.setLong(8, historico.getPaciente().getId());
                stmt.setLong(9, historico.getSecretaria().getId());

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
                    "WHERE historico_id = ?";

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

    public List<Historico> listHistoricos() {
        List<Historico> historicos = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                historicos.add(buildHistorico(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return historicos;
    }

    private Historico buildHistorico(ResultSet resultSet) {
        try {
            Historico historico = new Historico();

            historico.setId(resultSet.getLong("historico_id"));

            historico.setAgenda(new AgendaDAO().getById(resultSet.getLong("agenda_id")));
            historico.setPaciente(new PacienteDAO().getById(resultSet.getLong("paciente_id")));
            historico.setSecretaria(new SecretariaDAO().getById(resultSet.getLong("secretaria_id")));

            historico.setObservacao(resultSet.getString("observacao"));
            historico.setData(resultSet.getTimestamp("data").toLocalDateTime());
            historico.setStatusAgenda(StatusAgenda.valueOf(resultSet.getString("status_agenda")));

            historico.setCadastro(resultSet.getTimestamp("cadastro").toLocalDateTime());
            historico.setAtualizado(resultSet.getTimestamp("atualizado").toLocalDateTime());
            historico.setDesativado(resultSet.getTimestamp("desativado").toLocalDateTime());

            return historico;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
