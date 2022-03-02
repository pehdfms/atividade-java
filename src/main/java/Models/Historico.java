package Models;

import java.time.LocalDateTime;

public class Historico extends AbstractEntity {
    private Agenda agenda;
    private String observacao;
    private Secretaria secretaria;
    private Paciente paciente;
    private LocalDateTime data;
    private StatusAgenda statusAgenda;

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Secretaria getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Secretaria secretaria) {
        this.secretaria = secretaria;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public StatusAgenda getStatusAgenda() {
        return statusAgenda;
    }

    public void setStatusAgenda(StatusAgenda statusAgenda) {
        this.statusAgenda = statusAgenda;
    }
}
