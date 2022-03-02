package DAO;

import Factories.ConnectionFactory;
import Models.Pessoa;
import Models.Medico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {
    private String tableName = "medicos";
    private Connection connection = ConnectionFactory.getConnection();

    public MedicoDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS medico_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "medico_id BIGINT PRIMARY KEY DEFAULT nextval('medico_id_seq')," +
                "crm TEXT NOT NULL," +
                "porcentagem_participacao NUMERIC(50, 10)," +
                "consultorio TEXT NOT NULL," +
                "especialidade_id BIGINT," +
                "pessoa_id BIGINT," +
                "CONSTRAINT especialidade_id " +
                "FOREIGN KEY (especialidade_id)" +
                "REFERENCES especialidades(especialidade_id)," +
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

    public Medico getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE pessoa_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildMedico(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Medico createMedico(Medico medico) {
        if (medico != null) {
            String sql = "INSERT INTO " + tableName +
                    "(pessoa_id, especialidade_id, crm, porcentagem_participacao, consultorio) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                PessoaDAO pessoaDAO = new PessoaDAO();
                Long id = pessoaDAO.createPessoa(medico).getId();

                stmt.setLong(1, id);
                stmt.setLong(2, medico.getEspecialidade().getId());
                stmt.setString(3, medico.getCrm());
                stmt.setBigDecimal(4, medico.getPorcentagemParticipacao());
                stmt.setString(5, medico.getConsultorio());

                medico.setId(id);

                stmt.execute();

                return medico;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editMedico(Medico medico) {
        if (medico != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "especialidade_id = ?, crm = ?, porcentagem_participacao = ?, consultorio = ? " +
                    "WHERE pessoa_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                new PessoaDAO().editPessoa(medico);
                stmt.setLong(1, medico.getEspecialidade().getId());
                stmt.setString(2, medico.getCrm());
                stmt.setBigDecimal(3, medico.getPorcentagemParticipacao());
                stmt.setString(4, medico.getConsultorio());

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteById(Long id) {
        new PessoaDAO().deleteById(id);
    }

    public List<Medico> listMedicos() {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                medicos.add(buildMedico(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return medicos;
    }

    private Medico buildMedico(ResultSet resultSet) {
        try {
            Medico medico = (Medico) new PessoaDAO().getById(resultSet.getLong("pessoa_id"));
            medico.setCrm(resultSet.getString("crm"));
            medico.setPorcentagemParticipacao(resultSet.getBigDecimal("porcentagem_participacao"));
            medico.setConsultorio(resultSet.getString("consultorio"));
            medico.setEspecialidade(new EspecialidadeDAO().getById(resultSet.getLong("especialidade_id")));

            return medico;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
