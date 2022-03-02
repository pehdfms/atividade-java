import DAO.*;

public class Main {
    public static void main(String[] args) {
        new PessoaDAO().createTable();
        new ConvenioDAO().createTable();
        new EspecialidadeDAO().createTable();
        new PacienteDAO().createTable();
        new MedicoDAO().createTable();
        new SecretariaDAO().createTable();
        new AgendaDAO().createTable();
        new HistoricoDAO().createTable();
    }
}
