package DAO;

import Factories.ConnectionFactory;
import Models.Pessoa;
import Models.Paciente;
import Models.TipoAtendimento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {
    private String tableName = "pacientes";
    private Connection connection = ConnectionFactory.getConnection();

    public PacienteDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS paciente_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "paciente_id BIGINT PRIMARY KEY DEFAULT nextval('paciente_id_seq')," +
                "tipo_atendimento TEXT NOT NULL," +
                "numero_cartao_convenio TEXT," +
                "data_vencimento TIMESTAMP," +
                "convenio_id BIGINT," +
                "pessoa_id BIGINT," +
                "CONSTRAINT convenio_id " +
                "FOREIGN KEY (convenio_id) " +
                "REFERENCES convenios(convenio_id), " +
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

    public Paciente getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE pessoa_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildPaciente(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Paciente createPaciente(Paciente paciente) {
        if (paciente != null) {
            String sql = "INSERT INTO " + tableName +
                    "(pessoa_id, convenio_id, tipo_atendimento, numero_cartao_convenio, data_vencimento) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                PessoaDAO pessoaDAO = new PessoaDAO();
                Long id = pessoaDAO.createPessoa(paciente).getId();

                stmt.setLong(1, id);
                stmt.setLong(2, paciente.getConvenio().getId());
                stmt.setString(3, paciente.getTipoAtendimento().name());
                stmt.setString(4, paciente.getNumeroCartaoConvenio());
                stmt.setTimestamp(5, Timestamp.valueOf(paciente.getDataVencimento()));

                paciente.setId(id);

                stmt.execute();

                return paciente;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editPaciente(Paciente paciente) {
        if (paciente != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "convenio_id = ?, tipo_atendimento = ?, numero_cartao_convenio = ?, data_vencimento = ? " +
                    "WHERE pessoa_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                new PessoaDAO().editPessoa(paciente);

                stmt.setLong(1, paciente.getConvenio().getId());
                stmt.setString(2, paciente.getTipoAtendimento().name());
                stmt.setString(3, paciente.getNumeroCartaoConvenio());
                stmt.setTimestamp(4, Timestamp.valueOf(paciente.getDataVencimento()));

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteById(Long id) {
        new PessoaDAO().deleteById(id);
    }

    public List<Paciente> listPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                pacientes.add(buildPaciente(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pacientes;
    }

    private Paciente buildPaciente(ResultSet resultSet) {
        try {
            Paciente paciente = (Paciente) new PessoaDAO().getById(resultSet.getLong("pessoa_id"));

            paciente.setTipoAtendimento(TipoAtendimento.valueOf(resultSet.getString("tipo_atendimento")));
            paciente.setConvenio(new ConvenioDAO().getById(resultSet.getLong("convenio_id")));
            paciente.setNumeroCartaoConvenio(resultSet.getString("numero_cartao_convenio"));
            paciente.setDataVencimento(resultSet.getTimestamp("data_vencimento").toLocalDateTime());

            return paciente;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
