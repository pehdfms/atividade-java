package DAO;

import Factories.ConnectionFactory;
import Models.Pessoa;
import Models.Sexo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {
    private String tableName = "pessoas";
    private Connection connection = ConnectionFactory.getConnection();

    public PessoaDAO() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS pessoa_id_seq;";

        sql += "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "pessoa_id BIGINT PRIMARY KEY DEFAULT nextval('pessoa_id_seq')," +
                "nome TEXT NOT NULL," +
                "login TEXT NOT NULL," +
                "senha TEXT NOT NULL," +
                "telefone TEXT NOT NULL," +
                "celular TEXT NOT NULL," +
                "nacionalidade TEXT NOT NULL," +
                "cpf TEXT NOT NULL," +
                "rg TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "sexo TEXT NOT NULL," +
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

    public Pessoa getById(Long id) {
        String sql = "SELECT * FROM " + tableName +
                " WHERE pessoa_id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            return buildPessoa(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Pessoa createPessoa(Pessoa pessoa) {
        if (pessoa != null) {
            String sql = "INSERT INTO " + tableName +
                    "(nome, login, senha," +
                    " telefone, celular, nacionalidade," +
                    " cpf, rg, email," +
                    " cadastro, atualizado, desativado," +
                    " sexo)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                stmt.setString(1, pessoa.getNome());
                stmt.setString(2, pessoa.getLogin());
                stmt.setString(3, pessoa.getSenha());
                stmt.setString(4, pessoa.getTelefone());
                stmt.setString(5, pessoa.getCelular());
                stmt.setString(6, pessoa.getNacionalidade());
                stmt.setString(7, pessoa.getCpf());
                stmt.setString(8, pessoa.getRg());
                stmt.setString(9, pessoa.getEmail());

                stmt.setTimestamp(10, Timestamp.valueOf(pessoa.getCadastro()));
                stmt.setTimestamp(11, Timestamp.valueOf(pessoa.getAtualizado()));
                stmt.setTimestamp(12, Timestamp.valueOf(pessoa.getDesativado()));

                stmt.setString(13, pessoa.getSexo().name());

                stmt.execute();

                ResultSet resultSet = stmt.getGeneratedKeys();

                while (resultSet.next()) {
                    pessoa.setId(resultSet.getLong(1));
                }

                return pessoa;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void editPessoa(Pessoa pessoa) {
        if (pessoa != null) {
            String sql = "UPDATE " + tableName + " SET " +
                    "nome = ?, login = ?, senha = ?," +
                    "telefone = ?, celular = ?, nacionalidade = ?," +
                    "cpf = ?, rg = ?, email = ?," +
                    "cadastro = ?, atualizado = ?, desativado = ?," +
                    "sexo = ?" +
                    "WHERE pessoa_id = ?";

            try {
                PreparedStatement stmt = connection.prepareStatement(sql);

                stmt.setString(1, pessoa.getNome());
                stmt.setString(2, pessoa.getLogin());
                stmt.setString(3, pessoa.getSenha());
                stmt.setString(4, pessoa.getTelefone());
                stmt.setString(5, pessoa.getCelular());
                stmt.setString(6, pessoa.getNacionalidade());
                stmt.setString(7, pessoa.getCpf());
                stmt.setString(8, pessoa.getRg());
                stmt.setString(9, pessoa.getEmail());

                stmt.setTimestamp(10, Timestamp.valueOf(pessoa.getCadastro()));
                stmt.setTimestamp(11, Timestamp.valueOf(pessoa.getAtualizado()));
                stmt.setTimestamp(12, Timestamp.valueOf(pessoa.getDesativado()));

                stmt.setString(13, pessoa.getSexo().name());

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
                    "WHERE pessoa_id = ?";

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

    public List<Pessoa> listPessoas() {
        List<Pessoa> pessoas = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                pessoas.add(buildPessoa(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pessoas;
    }

    public Pessoa buildPessoa(ResultSet resultSet) {
        try {
            Pessoa pessoa = new Pessoa();

            pessoa.setId(resultSet.getLong("pessoa_id"));

            pessoa.setNome(resultSet.getString("nome"));
            pessoa.setLogin(resultSet.getString("login"));
            pessoa.setSenha(resultSet.getString("senha"));
            pessoa.setTelefone(resultSet.getString("telefone"));
            pessoa.setCelular(resultSet.getString("celular"));
            pessoa.setNacionalidade(resultSet.getString("nacionalidade"));
            pessoa.setCpf(resultSet.getString("cpf"));
            pessoa.setRg(resultSet.getString("rg"));
            pessoa.setEmail(resultSet.getString("email"));
            pessoa.setSexo(Sexo.valueOf(resultSet.getString("sexo")));

            pessoa.setCadastro(resultSet.getTimestamp("cadastro").toLocalDateTime());
            pessoa.setAtualizado(resultSet.getTimestamp("atualizado").toLocalDateTime());
            pessoa.setDesativado(resultSet.getTimestamp("desativado").toLocalDateTime());

            return pessoa;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
