package DAO;

import Factories.ConnectionFactory;
import Models.Agenda;
import Models.StatusAgenda;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {
    private String tableName = "agendas";
    private Connection connection = ConnectionFactory.getConnection();

    public AgendaDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS agenda_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "agenda_id BIGINT PRIMARY KEY DEFAULT nextval('agenda_id_seq')," +
                "encaixe BOOLEAN," +
                "data_agendamento TIMESTAMP," +
                "status_agenda TEXT NOT NULL," +
                "cadastro TIMESTAMP," +
                "atualizado TIMESTAMP," +
                "desativado TIMESTAMP," +
                "paciente_id BIGINT," +
                "medico_id BIGINT," +
                "CONSTRAINT paciente_id " +
                "FOREIGN KEY (paciente_id)" +
                "REFERENCES pacientes(paciente_id), " +
                "CONSTRAINT medico_id " +
                "FOREIGN KEY (medico_id)" +
                "REFERENCES medicos(medico_id)" +
                ");";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Agenda getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE agenda_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildAgenda(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Agenda createAgenda(Agenda agenda) {
        if (agenda != null) {
            String sql = "INSERT INTO " + tableName +
                    "(encaixe, data_agendamento, status_agendamento," +
                    " cadastro, atualizado, desativado," +
                    " paciente_id, medico_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                stmt.setBoolean(1, agenda.getEncaixe());
                stmt.setTimestamp(2, Timestamp.valueOf(agenda.getDataAgendamento()));
                stmt.setString(3, agenda.getStatusAgenda().name());
                stmt.setTimestamp(4, Timestamp.valueOf(agenda.getCadastro()));
                stmt.setTimestamp(5, Timestamp.valueOf(agenda.getAtualizado()));
                stmt.setTimestamp(6, Timestamp.valueOf(agenda.getDesativado()));
                stmt.setLong(7, agenda.getPaciente().getId());
                stmt.setLong(8, agenda.getMedico().getId());

                stmt.execute();

                ResultSet resultSet = stmt.getGeneratedKeys();

                while (resultSet.next()) {
                    agenda.setId(resultSet.getLong(1));
                }

                return agenda;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editAgenda(Agenda agenda) {
        if (agenda != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "encaixe = ?, data_agendamento = ?, status_agendamento = ?," +
                    "cadastro = ?, atualizado = ?, desativado = ?," +
                    "paciente_id = ?, medico_id = ? " +
                    "WHERE agenda_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                stmt.setBoolean(1, agenda.getEncaixe());
                stmt.setTimestamp(2, Timestamp.valueOf(agenda.getDataAgendamento()));
                stmt.setString(3, agenda.getStatusAgenda().name());
                stmt.setTimestamp(4, Timestamp.valueOf(agenda.getCadastro()));
                stmt.setTimestamp(5, Timestamp.valueOf(agenda.getAtualizado()));
                stmt.setTimestamp(6, Timestamp.valueOf(agenda.getDesativado()));
                stmt.setLong(7, agenda.getPaciente().getId());
                stmt.setLong(8, agenda.getMedico().getId());

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
                    "WHERE agenda_id = ?";

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

    public List<Agenda> listAgendas() {
        List<Agenda> agendas = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                agendas.add(buildAgenda(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return agendas;
    }

    private Agenda buildAgenda(ResultSet resultSet) {
        try {
            Agenda agenda = new Agenda();

            agenda.setId(resultSet.getLong("agenda_id"));

            agenda.setPaciente(new PacienteDAO().getById(resultSet.getLong("paciente_id")));
            agenda.setMedico(new MedicoDAO().getById(resultSet.getLong("medico_id")));

            agenda.setStatusAgenda(StatusAgenda.valueOf(resultSet.getString("status_agenda")));
            agenda.setDataAgendamento(resultSet.getTimestamp("data_agendamento").toLocalDateTime());
            agenda.setEncaixe(resultSet.getBoolean("encaixe"));

            agenda.setCadastro(resultSet.getTimestamp("cadastro").toLocalDateTime());
            agenda.setAtualizado(resultSet.getTimestamp("atualizado").toLocalDateTime());
            agenda.setDesativado(resultSet.getTimestamp("desativado").toLocalDateTime());

            return agenda;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
